package com.coderdream.util.whisper;

import io.github.givimad.whisperjni.WhisperContext;
import io.github.givimad.whisperjni.WhisperFullParams;
import io.github.givimad.whisperjni.WhisperJNI;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
// import java.util.concurrent.atomic.AtomicInteger; // 不再需要
// import java.util.function.Consumer; // 不再需要

/**
 * 使用 whisper.jni (io.github.givimad:whisper-jni) v1.7.1 进行语音转文本的工具类。
 * <p>
 * 功能：
 * - 依赖外部 FFmpeg 支持多种音频格式。
 * - [新增] 生成 SRT 格式的字幕文件。
 * <p>
 * 注意：根据提供的 whisper-jni 源码，此版本不支持进度回调功能。
 * <p>
 * 使用前提：
 * - FFmpeg 已安装并在 PATH 中，或通过 FFMPEG_PATH/ffmpeg.path 指定路径。
 * - Whisper 模型文件 (.bin) 存在。
 */
@Slf4j
// 可以考虑重命名类，去掉 progress
public class WhisperUtil_V1_7_1_ffmpeg_progress_srt_02 {

    private static final String FFMPEG_EXECUTABLE = System.getenv("FFMPEG_PATH") != null ?
            System.getenv("FFMPEG_PATH") :
            System.getProperty("ffmpeg.path", "ffmpeg");

    // 静态初始化块 (保持不变)
    static {
        try {
            log.info("正在加载 Whisper JNI 本地库 (v1.7.1)...");
            WhisperJNI.loadLibrary();
            log.info("Whisper JNI 本地库加载成功。");
        } catch (Exception e) {
            log.error("加载 Whisper JNI 本地库失败!", e);
            throw new RuntimeException("无法加载 Whisper JNI 本地库", e);
        }
        checkFFmpegAvailability();
    }

    // checkFFmpegAvailability 方法 (保持不变)
    private static void checkFFmpegAvailability() {
        // ... (代码同上一个版本)
         log.info("检查 FFmpeg 可用性 (路径: {})...", FFMPEG_EXECUTABLE);
         ProcessBuilder pb = new ProcessBuilder(FFMPEG_EXECUTABLE, "-version");
         pb.redirectErrorStream(true);
         try {
             Process process = pb.start();
             try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                 String line = reader.readLine();
                 if (line == null || !line.toLowerCase().contains("ffmpeg version")) {
                      log.warn("FFmpeg 可能未正确配置或未找到。执行 '{} -version' 未返回预期输出。", FFMPEG_EXECUTABLE);
                 } else {
                      log.info("FFmpeg 看起来可用。");
                 }
             }
             if (!process.waitFor(5, TimeUnit.SECONDS)) {
                  process.destroyForcibly();
                  log.warn("FFmpeg 版本检查进程超时。");
             }
             if (process.exitValue() != 0) {
                  log.warn("FFmpeg 版本检查退出代码非 0 (可能是警告或错误)。");
             }
         } catch (IOException e) {
             log.error("无法执行 FFmpeg 命令 '{}'。请确保 FFmpeg 已安装并在 PATH 中，" +
                       "或通过 FFMPEG_PATH / ffmpeg.path 指定路径。", FFMPEG_EXECUTABLE, e);
             throw new RuntimeException("FFmpeg 不可用或配置错误: " + e.getMessage(), e);
         } catch (InterruptedException e) {
             Thread.currentThread().interrupt();
             log.error("等待 FFmpeg 版本检查时被中断。", e);
             throw new RuntimeException("FFmpeg 检查中断", e);
         }
    }

    /**
     * 转录过程的配置类 (移除了进度回调)。
     */
    public static class TranscriptionConfig {
        private String language = "en";
        private boolean translate = false;
        private int threads = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
        // private Consumer<Integer> progressCallback = null; // 移除

        public TranscriptionConfig setLanguage(String language) { this.language = language; return this; }
        public TranscriptionConfig setTranslate(boolean translate) { this.translate = translate; return this; }
        public TranscriptionConfig setThreads(int threads) { this.threads = Math.max(1, threads); return this; }
        // public TranscriptionConfig setProgressCallback(Consumer<Integer> progressCallback) { // 移除
        //     this.progressCallback = progressCallback;
        //     return this;
        // }

        public String getLanguage() { return language; }
        public boolean isTranslate() { return translate; }
        public int getThreads() { return threads; }
        // public Consumer<Integer> getProgressCallback() { return progressCallback; } // 移除
    }

    // --- 公开 API 方法 ---

    /**
     * 将音频文件转录为纯文本字符串 (使用默认配置)。
     */
    public static String transcribe(Path modelPath, Path audioPath)
            throws IOException, RuntimeException {
        return transcribe(modelPath, audioPath, new TranscriptionConfig());
    }

    /**
     * 将音频文件转录为纯文本字符串 (使用指定配置)。
     */
    public static String transcribe(Path modelPath, Path audioPath, TranscriptionConfig config)
            throws IOException, RuntimeException {
        try (TranscriptionResult result = runWhisperTranscription(modelPath, audioPath, config)) {
            return result.getAsPlainText();
        }
    }

    /**
     * 将音频文件转录并生成 SRT 字幕文件 (使用默认配置)。
     */
    public static Path transcribeToSrt(Path modelPath, Path audioPath)
            throws IOException, RuntimeException {
        return transcribeToSrt(modelPath, audioPath, new TranscriptionConfig());
    }

    /**
     * 将音频文件转录并生成 SRT 字幕文件 (使用指定配置)。
     */
    public static Path transcribeToSrt(Path modelPath, Path audioPath, TranscriptionConfig config)
            throws IOException, RuntimeException {
        try (TranscriptionResult result = runWhisperTranscription(modelPath, audioPath, config)) {
            if (result.getNumSegments() <= 0) {
                log.warn("未检测到任何语音片段，无法生成 SRT 文件。音频: {}", audioPath);
                throw new RuntimeException("No speech segments detected, cannot generate SRT file for: " + audioPath.getFileName());
            }

            Path srtPath = determineSrtPath(audioPath, config);
            log.info("准备将字幕写入文件: {}", srtPath);

            try (BufferedWriter writer = Files.newBufferedWriter(srtPath, StandardCharsets.UTF_8,
                                                                StandardOpenOption.CREATE,
                                                                StandardOpenOption.WRITE,
                                                                StandardOpenOption.TRUNCATE_EXISTING)) {
                for (int i = 0; i < result.getNumSegments(); i++) {
                    long t0 = result.getSegmentStartTimestamp(i); // 使用修正后的方法名
                    long t1 = result.getSegmentEndTimestamp(i);   // 使用修正后的方法名
                    String text = result.getSegmentText(i);

                    if (text == null) {
                        log.warn("警告: 第 {} 段(索引 {}) 的文本为 null，跳过此段。", i + 1, i);
                        continue;
                    }

                    writer.write(String.valueOf(i + 1));
                    writer.newLine();
                    writer.write(formatTimestamp(t0) + " --> " + formatTimestamp(t1));
                    writer.newLine();
                    writer.write(text.trim());
                    writer.newLine();
                    writer.newLine();
                }
                log.info("SRT 字幕文件生成成功: {}", srtPath);
                return srtPath;
            } catch (IOException e) {
                log.error("写入 SRT 文件时出错: {}", srtPath, e);
                throw e;
            }
        }
    }

    // --- 内部核心转录逻辑 ---

    /**
     * 封装了调用 Whisper JNI 进行转录的核心过程。
     */
    private static TranscriptionResult runWhisperTranscription(Path modelPath, Path audioPath, TranscriptionConfig config)
            throws IOException, RuntimeException {

        // ... (文件检查等保持不变) ...
        Objects.requireNonNull(modelPath, "模型路径不能为空");
        Objects.requireNonNull(audioPath, "音频路径不能为空");
        Objects.requireNonNull(config, "转录配置不能为空");

        File modelFile = modelPath.toFile();
        File audioFile = audioPath.toFile();
        if (!modelFile.exists() || !modelFile.isFile()) { throw new IOException("模型文件未找到: " + modelPath); }
        if (!audioFile.exists() || !audioFile.isFile()) { throw new IOException("音频文件未找到: " + audioPath); }


        WhisperJNI whisper = null;
        WhisperContext ctx = null;
        // boolean progressReported = false; // 移除
        boolean transcriptionSuccess = false;

        try {
            whisper = new WhisperJNI();
            log.info("正在初始化 Whisper 上下文 (模型: {})...", modelPath.getFileName());
            ctx = whisper.init(modelPath);
            log.info("Whisper 上下文初始化成功.");

            log.info("正在使用 FFmpeg 加载并转换音频: {}", audioPath.getFileName());
            float[] audioData = loadAudioDataWithFFmpeg(audioFile);
            log.info("音频加载和转换完成 ({} 个样本).", audioData.length);
            if (audioData.length == 0) {
                log.warn("警告: 从 FFmpeg 获取的音频数据为空。音频文件: {}", audioPath);
                return new TranscriptionResult(ctx, whisper, 0);
            }

            WhisperFullParams whisperParams = new WhisperFullParams(); // 使用构造函数，无需设置 strategy
            whisperParams.language = config.getLanguage();
            whisperParams.translate = config.isTranslate();
            whisperParams.nThreads = config.getThreads();
            // whisperParams.printProgress = false; // 可以选择关闭 C++ 端的进度打印

            // 移除设置进度回调的逻辑
            // final Consumer<Integer> userCallback = config.getProgressCallback(); // 移除
            // if (userCallback != null) { // 移除
            //    ...
            // } else { // 移除
            //     log.debug("未配置进度回调 (当前版本不支持)。"); // 修改日志
            // }

            log.info("转录参数: language={}, translate={}, threads={}",
                    whisperParams.language, whisperParams.translate, whisperParams.nThreads);

            log.info("开始转录 (无实时进度更新)..."); // 修改日志
            long startTime = System.currentTimeMillis();
            int result = whisper.full(ctx, whisperParams, audioData, audioData.length);
            long endTime = System.currentTimeMillis();

            // 移除进度回调后的换行逻辑
            // if (progressReported && userCallback != null) {
            //     System.out.println();
            // }

            log.info("Whisper full() 方法执行完成，返回码: {} (耗时: {} ms).", result, (endTime - startTime));

            if (result != 0) {
                throw new RuntimeException("Whisper 转录失败，错误码: " + result);
            }

            int numSegments = whisper.fullNSegments(ctx);
            if (numSegments < 0) {
                throw new RuntimeException("获取分段数量失败，Whisper 返回错误码: " + numSegments);
            }
            log.info("检测到 {} 个语音片段。", numSegments);
            transcriptionSuccess = true;

            return new TranscriptionResult(ctx, whisper, numSegments);

        } catch (IOException | RuntimeException e) {
            log.error("转录过程中发生错误: {}", e.getMessage(), e);
            // 移除进度条后的换行
            // if (progressReported) System.out.println();
            if (ctx != null && !transcriptionSuccess) {
                 try {
                     log.warn("转录异常，正在尝试关闭 Whisper 上下文...");
                     ctx.close();
                     log.warn("Whisper 上下文已关闭。");
                 } catch (Exception ce) {
                     log.error("转录异常后关闭 Whisper 上下文失败", ce);
                     e.addSuppressed(ce);
                 }
            }
            throw e;
        }
    }


    // --- 辅助类：封装转录结果，管理资源 ---

    /**
     * 用于封装 Whisper 转录的结果信息，并负责管理 WhisperContext 的生命周期。
     * **重要:** 必须在使用完毕后调用 close() 方法 (或通过 try-with-resources)。
     */
    private static class TranscriptionResult implements AutoCloseable {
        private final WhisperContext context;
        private final WhisperJNI whisperInterface;
        private final int numSegments;
        private boolean closed = false;

        public TranscriptionResult(WhisperContext context, WhisperJNI whisperInterface, int numSegments) {
            this.context = context;
            this.whisperInterface = Objects.requireNonNull(whisperInterface, "WhisperJNI interface cannot be null");
            this.numSegments = Math.max(0, numSegments);
        }

        public int getNumSegments() {
            checkClosed();
            return numSegments;
        }

        // 获取第 i 段的开始时间 (毫秒) - **修正方法名**
        public long getSegmentStartTimestamp(int segmentIndex) {
            checkReadyForAccess("获取开始时间戳");
            if (segmentIndex < 0 || segmentIndex >= numSegments) throw new IndexOutOfBoundsException("无效的段索引: " + segmentIndex + " (总段数: " + numSegments + ")");
            // 使用正确的方法名
            return whisperInterface.fullGetSegmentTimestamp0(context, segmentIndex);
        }

        // 获取第 i 段的结束时间 (毫秒) - **修正方法名**
        public long getSegmentEndTimestamp(int segmentIndex) {
            checkReadyForAccess("获取结束时间戳");
            if (segmentIndex < 0 || segmentIndex >= numSegments) throw new IndexOutOfBoundsException("无效的段索引: " + segmentIndex + " (总段数: " + numSegments + ")");
            // 使用正确的方法名
            return whisperInterface.fullGetSegmentTimestamp1(context, segmentIndex);
        }

        // 获取第 i 段的文本 (保持不变)
        public String getSegmentText(int segmentIndex) {
            checkReadyForAccess("获取段落文本");
            if (segmentIndex < 0 || segmentIndex >= numSegments) throw new IndexOutOfBoundsException("无效的段索引: " + segmentIndex + " (总段数: " + numSegments + ")");
            return whisperInterface.fullGetSegmentText(context, segmentIndex);
        }

        // 获取所有分段拼接成的纯文本 (保持不变)
        public String getAsPlainText() {
            checkReadyForAccess("获取纯文本");
            if (numSegments == 0) return "";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numSegments; i++) {
                String text = getSegmentText(i);
                if (text != null) {
                    sb.append(text);
                }
            }
            return sb.toString();
        }

        /**
         * 关闭资源 (主要是 WhisperContext)。
         */
        @Override
        public void close() {
            if (!closed && context != null) {
                try {
                    log.debug("正在关闭 TranscriptionResult 中的 WhisperContext...");
                    context.close();
                    log.debug("TranscriptionResult 中的 WhisperContext 已关闭。");
                } catch (Exception e) {
                    log.error("关闭 TranscriptionResult 中的 WhisperContext 时出错: {}", e.getMessage(), e);
                } finally {
                     closed = true;
                }
            } else if (closed) {
                log.trace("TranscriptionResult 资源已关闭，跳过重复关闭。");
            } else {
                log.trace("TranscriptionResult 中的 WhisperContext 为 null，无需关闭。");
            }
        }

        private void checkClosed() {
            if (closed) {
                throw new IllegalStateException("操作失败：TranscriptionResult 资源已被关闭。");
            }
        }

        private void checkReadyForAccess(String operation) {
             checkClosed();
             if (context == null) {
                 throw new IllegalStateException("操作失败 (" + operation + ")：WhisperContext 为 null，可能由于初始化失败或无音频数据。");
             }
        }
    }


    // --- 内部辅助方法 ---

    // loadAudioDataWithFFmpeg 方法 (保持不变)
    private static float[] loadAudioDataWithFFmpeg(File audioFile) throws IOException {
        // ... (代码同上一个版本)
        List<String> command = Arrays.asList(
                FFMPEG_EXECUTABLE,
                "-i", audioFile.getAbsolutePath(),
                "-nostdin", "-ar", "16000", "-ac", "1",
                "-f", "s16le", "-acodec", "pcm_s16le",
                "-loglevel", "error", "-"
        );
        log.debug("执行 FFmpeg 命令: {}", String.join(" ", command));
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = null;
        byte[] pcmBytes = null;
        try {
            process = pb.start();
            try (InputStream ffmpegInputStream = process.getInputStream();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = ffmpegInputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                pcmBytes = baos.toByteArray();
            }
            boolean finished = process.waitFor(1800, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new IOException("FFmpeg 处理超时 (超过 30 分钟): " + audioFile.getName());
            }
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                String errorDetails = "(无法获取 FFmpeg 输出)";
                if (pcmBytes != null && pcmBytes.length > 0) {
                   errorDetails = new String(pcmBytes, StandardCharsets.UTF_8).trim();
                }
                log.error("FFmpeg 转换失败，退出码: {}。文件: {}. FFmpeg 输出:\n{}", exitCode, audioFile.getAbsolutePath(), errorDetails);
                throw new IOException("FFmpeg failed with exit code " + exitCode + " for file: " + audioFile.getName() + ". Details: " + errorDetails);
            }
            if (pcmBytes == null || pcmBytes.length == 0) {
                 log.warn("FFmpeg 成功执行但输出了 0 字节的 PCM 数据: {}", audioFile.getName());
                 return new float[0];
            }
            log.debug("FFmpeg 转换成功，读取了 {} 字节的 PCM 数据。", pcmBytes.length);
            if (pcmBytes.length % 2 != 0) {
                log.warn("警告: 读取的 PCM 数据字节数 ({}) 不是偶数。", pcmBytes.length);
                pcmBytes = Arrays.copyOf(pcmBytes, pcmBytes.length - 1);
            }
            int numSamples = pcmBytes.length / 2;
            float[] audioFloats = new float[numSamples];
            ByteBuffer buffer = ByteBuffer.wrap(pcmBytes).order(ByteOrder.LITTLE_ENDIAN);
            for (int i = 0; i < numSamples; i++) {
                short pcmSample = buffer.getShort();
                audioFloats[i] = (float) pcmSample / 32768.0f;
            }
            return audioFloats;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("等待 FFmpeg 进程时被中断。", e);
            if (process != null) process.destroyForcibly();
            throw new RuntimeException("FFmpeg process interrupted", e);
        } catch (IOException e) {
             log.error("执行 FFmpeg 或读取其输出时发生 I/O 错误: {}", e.getMessage(), e);
             if (process != null) process.destroyForcibly();
             throw e;
        } finally {
            if (process != null && process.isAlive()) {
                log.warn("FFmpeg 进程在方法退出时仍存活，强制销毁。");
                process.destroyForcibly();
            }
        }
    }

    // formatTimestamp 方法 (保持不变)
    private static String formatTimestamp(long totalMillis) {
        if (totalMillis < 0) totalMillis = 0;
        long milliseconds = totalMillis % 1000;
        long totalSeconds = totalMillis / 1000;
        long seconds = totalSeconds % 60;
        long totalMinutes = totalSeconds / 60;
        long minutes = totalMinutes % 60;
        long hours = totalMinutes / 60;
        return String.format(Locale.ROOT, "%02d:%02d:%02d,%03d", hours, minutes, seconds, milliseconds);
    }

    // determineSrtPath 方法 (保持不变)
    private static Path determineSrtPath(Path audioPath, TranscriptionConfig config) {
        Path parentDir = audioPath.getParent();
        if (parentDir == null) parentDir = Paths.get("");

        String originalFileName = audioPath.getFileName().toString();
        String baseName;
        int lastDotIndex = originalFileName.lastIndexOf('.');
        baseName = (lastDotIndex > 0) ? originalFileName.substring(0, lastDotIndex) : originalFileName;

        String langSuffix = config.isTranslate() ? "en"
                : (config.getLanguage() == null || config.getLanguage().isEmpty() || "auto".equalsIgnoreCase(config.getLanguage()) ? "auto" : config.getLanguage());

        String srtFileName = baseName + "." + langSuffix + ".srt";
        return parentDir.resolve(srtFileName);
    }


    // --- 示例用法 Main 方法 ---
    public static void main(String[] args) {
        // --- 配置你的路径 ---
        String modelFilePath = "D:\\00_Green\\WhisperDesktop\\models" + File.separator + "ggml-model-whisper-tiny.bin";
        String audioFilePath = "D:\\0000\\0003_PressBriefings\\20250415\\20250415.mp3";
        // --- 路径设置结束 ---

        Path modelPath = Paths.get(modelFilePath);
        Path audioPath = Paths.get(audioFilePath);

        if (!Files.exists(modelPath)) { log.error("错误：模型文件未找到: {}", modelPath); return; }
        if (!Files.exists(audioPath)) { log.error("错误：音频文件未找到: {}", audioPath); return; }

        // --- 测试 1: 转录为纯文本 (无进度回调) ---
        log.info("\n--- 测试 1: 转录为纯文本 (语言: 自动检测) ---");
        // 移除了进度回调的设置
        TranscriptionConfig configText = new TranscriptionConfig().setLanguage("auto");
        try {
            System.out.println("开始转录文本..."); // 添加开始提示
            String plainText = transcribe(modelPath, audioPath, configText);
            System.out.println("文本转录完成。"); // 添加完成提示
            log.info("纯文本转录结果:\n{}", plainText);
        } catch (IOException | RuntimeException e) {
            log.error("纯文本转录测试失败: {}", e.getMessage(), e);
        }

        // --- 测试 2: 生成 SRT 字幕文件 (指定中文，无进度回调) ---
        log.info("\n--- 测试 2: 生成 SRT 字幕文件 (语言: 中文) ---");
        // 移除了进度回调的设置
        TranscriptionConfig configSrtZh = new TranscriptionConfig().setLanguage("zh");
        try {
            System.out.println("开始生成 SRT (中文)..."); // 添加开始提示
            Path srtPath = transcribeToSrt(modelPath, audioPath, configSrtZh);
            System.out.println("SRT (中文) 生成完成。"); // 添加完成提示
            log.info("SRT (中文) 文件已生成: {}", srtPath);
        } catch (IOException | RuntimeException e) {
            log.error("SRT (中文) 生成测试失败: {}", e.getMessage(), e);
        }

        // --- 测试 3: 生成 SRT 字幕文件 (自动检测语言，无进度回调) ---
        log.info("\n--- 测试 3: 生成 SRT 字幕文件 (语言: 自动检测) ---");
        // 移除了进度回调的设置
        TranscriptionConfig configSrtAuto = new TranscriptionConfig().setLanguage("auto");
        try {
            System.out.println("开始生成 SRT (自动)..."); // 添加开始提示
            Path srtPathAuto = transcribeToSrt(modelPath, audioPath, configSrtAuto);
            System.out.println("SRT (自动) 生成完成。"); // 添加完成提示
            log.info("SRT (自动检测语言) 文件已生成: {}", srtPathAuto);
        } catch (IOException | RuntimeException e) {
            log.error("SRT (自动) 生成测试失败: {}", e.getMessage(), e);
        }

        // --- 测试 4: 将音频翻译为英文并生成 SRT (无进度回调) ---
        log.info("\n--- 测试 4: 翻译为英文并生成 SRT ---");
        // 移除了进度回调的设置
        TranscriptionConfig configTranslate = new TranscriptionConfig().setLanguage("auto").setTranslate(true);
        try {
            System.out.println("开始翻译为 SRT (英文)..."); // 添加开始提示
            Path srtPathEn = transcribeToSrt(modelPath, audioPath, configTranslate);
            System.out.println("翻译为 SRT (英文) 完成。"); // 添加完成提示
            log.info("翻译后的 SRT (英文) 文件已生成: {}", srtPathEn);
        } catch (IOException | RuntimeException e) {
            log.error("翻译为 SRT (英文) 测试失败: {}", e.getMessage(), e);
        }
    }
}
