package com.coderdream.util.whisper; // 确认包名是否正确

import io.github.givimad.whisperjni.WhisperContext;
import io.github.givimad.whisperjni.WhisperFullParams;
import io.github.givimad.whisperjni.WhisperJNI;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.UnsupportedAudioFileException; // 保留，以防将来需要特定错误类型
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 使用 whisper.jni (io.github.givimad:whisper-jni) v1.7.1 进行语音转文本的工具类。
 * <p>
 * 重要: 此版本依赖外部工具 FFmpeg 来支持 WAV 之外的音频格式 (如 MP3, Ogg, FLAC 等)。
 * 请确保 FFmpeg 已安装并在系统的 PATH 环境变量中，或者通过 FFMPEG_PATH 环境变量或
 * ffmpeg.path Java 系统属性指定其可执行文件路径。
 * <p>
 * 音频会被自动转换为 Whisper 所需的格式: 16kHz 采样率, 单声道, 16位 PCM。
 * <p>
 * 依赖:
 * - io.github.givimad:whisper-jni:1.7.1
 * - SLF4J API
 * - Lombok (或手动实现 Getter/Setter/Logger)
 * - FFmpeg (外部程序)
 */
@Slf4j
public class WhisperUtil_V1_7_1_ffmpeg { // 类名加后缀以示区别

    // 优先从环境变量 FFMPEG_PATH 读取，其次从系统属性 ffmpeg.path 读取，最后默认为 "ffmpeg"
    private static final String FFMPEG_EXECUTABLE = System.getenv("FFMPEG_PATH") != null ?
            System.getenv("FFMPEG_PATH") :
            System.getProperty("ffmpeg.path", "ffmpeg");

    // 静态初始化块，用于加载本地库和检查 FFmpeg
    static {
        try {
            log.info("正在加载 Whisper JNI 本地库 (v1.7.1)...");
            WhisperJNI.loadLibrary();
            log.info("Whisper JNI 本地库加载成功。");
            // WhisperJNI.setLibraryLogger(null); // 如需禁用 C++ 日志
        } catch (Exception e) {
            log.error("加载 Whisper JNI 本地库失败!", e);
            throw new RuntimeException("无法加载 Whisper JNI 本地库", e);
        }

        // 检查 FFmpeg 是否可用
        checkFFmpegAvailability();
    }

    /**
     * 检查 FFmpeg 是否可执行。
     */
    private static void checkFFmpegAvailability() {
        log.info("检查 FFmpeg 可用性 (路径: {})...", FFMPEG_EXECUTABLE);
        ProcessBuilder pb = new ProcessBuilder(FFMPEG_EXECUTABLE, "-version");
        pb.redirectErrorStream(true); // 合并错误流和输出流
        try {
            Process process = pb.start();
            // 读取少量输出即可确认程序启动
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line == null || !line.toLowerCase().contains("ffmpeg version")) {
                     log.warn("FFmpeg 可能未正确配置或未找到。尝试执行 '{} -version' 未返回预期输出。", FFMPEG_EXECUTABLE);
                } else {
                     log.info("FFmpeg 看起来可用。");
                }
            }
            // 等待进程结束，避免资源泄露 (设置超时)
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                 process.destroyForcibly();
                 log.warn("FFmpeg 版本检查进程超时。");
            }
            if (process.exitValue() != 0) {
                 log.warn("FFmpeg 版本检查退出代码非 0 (可能是警告或错误)。");
            }
        } catch (IOException e) {
            log.error("无法执行 FFmpeg 命令 '{}'。请确保 FFmpeg 已安装并在 PATH 中，" +
                      "或者通过 FFMPEG_PATH 环境变量 / ffmpeg.path 系统属性指定了正确路径。", FFMPEG_EXECUTABLE, e);
            throw new RuntimeException("FFmpeg 不可用或配置错误: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("等待 FFmpeg 版本检查进程时被中断。", e);
            throw new RuntimeException("FFmpeg 检查中断", e);
        }
    }

    /**
     * 转录过程的配置 (适配 v1.7.1, 与原版一致)。
     */
    public static class TranscriptionConfig {
        private String language = "en";
        private boolean translate = false;
        private int threads = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
        // grammar 相关已移除

        public TranscriptionConfig setLanguage(String language) { this.language = language; return this; }
        public TranscriptionConfig setTranslate(boolean translate) { this.translate = translate; return this; }
        public TranscriptionConfig setThreads(int threads) { this.threads = Math.max(1, threads); return this; }

        public String getLanguage() { return language; }
        public boolean isTranslate() { return translate; }
        public int getThreads() { return threads; }
    }

    /**
     * 使用指定的模型和默认配置转录给定的音频文件 (支持多种格式)。
     */
    public static String transcribe(Path modelPath, Path audioPath)
            throws IOException, RuntimeException {
        // 注意：UnsupportedAudioFileException 不再从此方法直接抛出，因为 FFmpeg 处理格式
        return transcribe(modelPath, audioPath, new TranscriptionConfig());
    }

    /**
     * 使用指定的模型和配置转录给定的音频文件 (支持多种格式)。
     */
    public static String transcribe(Path modelPath, Path audioPath, TranscriptionConfig config)
            throws IOException, RuntimeException {

        Objects.requireNonNull(modelPath, "模型路径不能为空");
        Objects.requireNonNull(audioPath, "音频路径不能为空");
        Objects.requireNonNull(config, "转录配置不能为空");

        File modelFile = modelPath.toFile();
        File audioFile = audioPath.toFile();

        if (!modelFile.exists() || !modelFile.isFile()) {
            throw new IOException("模型文件未找到或不是有效文件: " + modelPath);
        }
        if (!audioFile.exists() || !audioFile.isFile()) {
            throw new IOException("音频文件未找到或不是有效文件: " + audioPath);
        }

        WhisperJNI whisper = new WhisperJNI();
        WhisperContext ctx = null;
        try {
            log.info("正在初始化 Whisper 上下文 (模型: {})...", modelPath.getFileName());
            ctx = whisper.init(modelPath);
            log.info("Whisper 上下文初始化成功.");

            log.info("正在使用 FFmpeg 加载并转换音频: {}", audioPath.getFileName());
            // 调用新的基于 FFmpeg 的 loadAudioData
            float[] audioData = loadAudioDataWithFFmpeg(audioFile);
            log.info("音频加载和转换完成 ({} 个样本).", audioData.length);

            if (audioData.length == 0) {
                log.warn("警告: 从音频文件加载的数据为空。可能是文件问题或 FFmpeg 处理错误。");
                return ""; // 或者抛出异常，取决于期望行为
            }

            WhisperFullParams whisperParams = new WhisperFullParams();
            whisperParams.language = config.getLanguage();
            whisperParams.translate = config.isTranslate();
            whisperParams.nThreads = config.getThreads();

            log.info("转录参数: language={}, translate={}, threads={}",
                    whisperParams.language, whisperParams.translate, whisperParams.nThreads);

            log.info("开始转录...");
            long startTime = System.currentTimeMillis();
            // 调用 whisper.full
            int result = whisper.full(ctx, whisperParams, audioData, audioData.length);
            long endTime = System.currentTimeMillis();
            log.info("Whisper full() 方法执行完成，返回码: {} (耗时: {} ms).", result, (endTime - startTime));

            if (result != 0) {
                throw new RuntimeException("Whisper 转录失败，错误码: " + result + ". 请检查模型、音频数据和参数。");
            }

            int numSegments = whisper.fullNSegments(ctx);
            if (numSegments < 0) {
                 // 根据 whisper.cpp 的 C API，负值表示错误
                 throw new RuntimeException("获取片段数量失败，Whisper 返回错误码: " + numSegments);
            }
            if (numSegments == 0) {
                log.warn("未检测到任何语音片段。");
                return "";
            }

            log.info("检测到 {} 个语音片段，正在组合文本...", numSegments);
            StringBuilder transcription = new StringBuilder();
            for (int i = 0; i < numSegments; i++) {
                String segmentText = whisper.fullGetSegmentText(ctx, i);
                if (segmentText == null) {
                    log.warn("警告: 获取第 {} 个片段的文本时返回 null。", i);
                    // 可以选择跳过或记录错误
                    continue;
                }
                transcription.append(segmentText);
            }
            log.info("文本组合完成.");
            return transcription.toString();

        } catch (IOException | RuntimeException e) { // 捕获更广泛的异常
            log.error("转录过程中发生错误: {}", e.getMessage(), e);
            throw e; // 重新抛出，让调用者知道失败了
        } finally {
            if (ctx != null) {
                try {
                    log.info("正在释放 Whisper 上下文...");
                    ctx.close(); // 手动释放资源
                    log.info("Whisper 上下文已释放.");
                } catch (Exception closeEx) {
                    // 这里捕获 Exception 而不是更具体的，因为 close() 可能抛出检查型或运行时异常
                    log.error("释放 Whisper 上下文时出错: {}", closeEx.getMessage(), closeEx);
                    // 不建议在这里重新抛出异常，以免覆盖原始的转录异常
                }
            }
        }
    }

    /**
     * 使用 FFmpeg 加载音频文件，将其转换为 16kHz 单声道 16-bit PCM，
     * 并返回 float 数组 (值在 -1.0 到 1.0 之间)。
     *
     * @param audioFile 要加载的音频文件 (任何 FFmpeg 支持的格式)。
     * @return 包含音频数据的 float 数组。
     * @throws IOException 如果 FFmpeg 执行失败或读取数据时发生 I/O 错误。
     * @throws RuntimeException 如果 FFmpeg 进程中断或返回非零退出码。
     */
    private static float[] loadAudioDataWithFFmpeg(File audioFile) throws IOException {
        // FFmpeg 命令参数:
        // -i <input file>  : 指定输入文件
        // -nostdin         : 避免 FFmpeg 从 stdin 读取，防止潜在问题
        // -ar 16000        : 设置音频采样率为 16kHz
        // -ac 1            : 设置音频通道数为 1 (单声道)
        // -f s16le         : 设置输出格式为 signed 16-bit little-endian PCM
        // -acodec pcm_s16le: 明确指定 PCM 编解码器
        // -loglevel error  : 只输出错误信息，保持 stdout 清洁
        // -                   : 将输出写入 stdout
        List<String> command = Arrays.asList(
                FFMPEG_EXECUTABLE,
                "-i", audioFile.getAbsolutePath(),
                "-nostdin",
                "-ar", "16000",
                "-ac", "1",
                "-f", "s16le",
                "-acodec", "pcm_s16le",
                "-loglevel", "error",
                "-"
        );

        log.debug("执行 FFmpeg 命令: {}", String.join(" ", command));
        ProcessBuilder pb = new ProcessBuilder(command);
        // pb.redirectError(ProcessBuilder.Redirect.INHERIT); // 将 FFmpeg 的错误流直接输出到 Java 进程的错误流
        pb.redirectErrorStream(true); // 合并 stderr 到 stdout 流，方便读取错误信息

        Process process = null;
        byte[] pcmBytes = null;
        String ffmpegOutput = ""; // 存储 FFmpeg 的输出（包括错误信息）

        try {
            process = pb.start();

            // 使用 try-with-resources 确保输入流被关闭
            try (InputStream ffmpegInputStream = process.getInputStream();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = ffmpegInputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                pcmBytes = baos.toByteArray();
            }

            // 等待 FFmpeg 进程完成
            boolean finished = process.waitFor(60, TimeUnit.SECONDS); // 设置超时，例如 60 秒
            if (!finished) {
                process.destroyForcibly();
                throw new IOException("FFmpeg 处理超时: " + audioFile.getName());
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                // 如果 ffmpeg 输出了错误信息到合并后的流，pcmBytes 可能包含这些文本信息
                // 尝试将其解码为字符串以获取错误详情
                String errorDetails = new String(pcmBytes).trim(); // 尝试解码整个输出
                 if (errorDetails.isEmpty()) {
                    // 如果 ByteArrayOutputStream 为空，但退出码非0，可能是启动就失败了
                    errorDetails = "无法获取 FFmpeg 错误输出。";
                }
                log.error("FFmpeg 转换失败，退出码: {}。文件: {}. FFmpeg 输出:\n{}", exitCode, audioFile.getAbsolutePath(), errorDetails);
                throw new IOException("FFmpeg failed with exit code " + exitCode + " for file: " + audioFile.getName() + ". Details: " + errorDetails);
            }

            log.debug("FFmpeg 转换成功，读取了 {} 字节的 PCM 数据。", pcmBytes.length);

            // --- 将 16-bit little-endian PCM 字节转换为 float 数组 ---
            if (pcmBytes.length % 2 != 0) {
                 // 这通常不应该发生，除非 FFmpeg 输出损坏或读取不完整
                log.warn("警告: 读取的 PCM 数据字节数不是偶数 ({})。可能导致最后一个样本丢失或转换错误。", pcmBytes.length);
            }

            int numSamples = pcmBytes.length / 2;
            float[] audioFloats = new float[numSamples];
            ByteBuffer buffer = ByteBuffer.wrap(pcmBytes).order(ByteOrder.LITTLE_ENDIAN);

            for (int i = 0; i < numSamples; i++) {
                // 读取 16 位有符号整数 (short)
                short pcmSample = buffer.getShort();
                // 归一化到 -1.0 到 1.0 范围
                audioFloats[i] = (float) pcmSample / 32768.0f;
            }

            return audioFloats;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("等待 FFmpeg 进程时被中断。", e);
            if (process != null) {
                process.destroyForcibly();
            }
            throw new RuntimeException("FFmpeg process interrupted", e);
        } catch (IOException e) {
             // 捕获启动进程或读取流时可能发生的 IOException
             log.error("执行 FFmpeg 或读取其输出时发生 I/O 错误: {}", e.getMessage(), e);
             if (process != null) {
                process.destroyForcibly();
             }
             throw e; // 重新抛出原始的 IOException
        } finally {
            // 确保进程资源在任何情况下都被尝试关闭 (尽管 waitFor 后理论上已结束)
            if (process != null && process.isAlive()) {
                log.warn("FFmpeg 进程在方法退出时仍然存活，强制销毁。");
                process.destroyForcibly();
            }
        }
    }

    // --- 示例用法 Main 方法 (需要修改路径) ---
    public static void main(String[] args) {
        // --- 配置你的路径 ---
        // 模型文件路径 (保持不变)
        String modelFilePath = "D:\\00_Green\\WhisperDesktop\\models" + File.separator + "ggml-model-whisper-tiny.bin";
        // !! 修改为你的 MP3 文件或其他非 WAV 格式的音频文件路径 !!
        String audioFilePathMP3 = "D:\\0000\\0003_PressBriefings\\20250415\\20250415.mp3";
        // 可以添加其他格式的测试文件路径
        String audioFilePathWAV = "D:\\0000\\bgmusic\\eng.wav"; // 之前的 WAV 文件，也可以测试
        // --- 路径设置结束 ---

        Path modelPath = Paths.get(modelFilePath);
        Path audioPathMP3 = Paths.get(audioFilePathMP3);
        Path audioPathWAV = Paths.get(audioFilePathWAV);

        // --- 测试用例 ---
        if (Files.exists(audioPathMP3)) {
            runTest("测试 MP3 (FFmpeg): 基本英语转录", modelPath, audioPathMP3, new TranscriptionConfig());
        } else {
            log.warn("跳过 MP3 测试: 未找到音频文件 {}", audioPathMP3);
        }

        if (Files.exists(audioPathWAV)) {
            runTest("测试 WAV (FFmpeg): 基本英语转录", modelPath, audioPathWAV, new TranscriptionConfig());
            // 如果需要测试其他语言或翻译，取消下面的注释并确保有合适的音频文件
            // runTest("测试 WAV (FFmpeg): 自动语言检测 (假设是中文)", modelPath, Paths.get("path/to/your/chinese.wav"), new TranscriptionConfig().setLanguage("auto"));
            // runTest("测试 WAV (FFmpeg): 翻译为英语 (假设是中文)", modelPath, Paths.get("path/to/your/chinese.wav"), new TranscriptionConfig().setLanguage("auto").setTranslate(true));
        } else {
            log.warn("跳过 WAV 测试: 未找到音频文件 {}", audioPathWAV);
        }

        // 可以添加更多测试，例如不同线程数
        if (Files.exists(audioPathMP3)) {
            runTest("测试 MP3 (FFmpeg): 指定线程数", modelPath, audioPathMP3, new TranscriptionConfig().setThreads(4));
        }
    }

    // 辅助方法 runTest (使用新的类名)
    private static void runTest(String testName, Path modelPath, Path audioPath, TranscriptionConfig config) {
        log.info("\n--- {} ---", testName);
        if (!Files.exists(modelPath)) { log.error("模型文件不存在，跳过测试: {}", modelPath); return; }
        if (!Files.exists(audioPath)) { log.error("音频文件不存在，跳过测试: {}", audioPath); return; }

        try {
            // 调用包含 FFmpeg 支持的类的 transcribe 方法
            String result = WhisperUtil_V1_7_1_ffmpeg.transcribe(modelPath, audioPath, config);
            log.info("转录结果:\n{}", result);
        } catch (IOException | RuntimeException e) { // 捕获可能由 FFmpeg 或 Whisper 抛出的异常
            log.error("测试 '{}' 执行失败: {}", testName, e.getMessage(), e);
        }
    }
}
