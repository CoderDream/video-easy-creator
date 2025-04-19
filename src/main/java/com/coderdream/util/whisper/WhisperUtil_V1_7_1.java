package com.coderdream.util.whisper; // 确认包名是否正确

import io.github.givimad.whisperjni.WhisperContext;
import io.github.givimad.whisperjni.WhisperFullParams;
// import io.github.givimad.whisperjni.WhisperGrammar; // Grammar 可能在 1.7.1 中接口不同或不存在
import io.github.givimad.whisperjni.WhisperJNI;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * 使用 whisper.jni (io.github.givimad:whisper-jni) v1.7.1 进行语音转文本的工具类。
 * 依赖已发布到 Maven Central。
 * 需要 SLF4J API、Lombok 及 SLF4J 实现 (如 Logback)。
 * 会尝试将输入音频转换为 16kHz 采样率、单声道、16位 PCM 编码。
 */
@Slf4j
public class WhisperUtil_V1_7_1 { // 类名加上版本号以示区别

    // 静态初始化块，用于加载本地库和设置日志（可选）
    static {
        try {
            log.info("正在加载 Whisper JNI 本地库 (v1.7.1)...");
            WhisperJNI.loadLibrary();
            log.info("Whisper JNI 本地库加载成功。");
            // WhisperJNI.setLibraryLogger(null); // 如需禁用 C++ 日志，取消此行注释
        } catch (Exception e) {
            log.error("加载 Whisper JNI 本地库失败!", e);
            throw new RuntimeException("无法加载 Whisper JNI 本地库", e);
        }
    }

    /**
     * 转录过程的配置 (适配 v1.7.1)。
     */
    public static class TranscriptionConfig {
        private String language = "en";
        private boolean translate = false;
        private int threads = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
        // v1.7.1 的 Grammar 支持可能不同或不存在，暂时移除相关配置
        // private Path grammarPath = null;
        // private float grammarPenalty = 100.0f;

        public TranscriptionConfig setLanguage(String language) { this.language = language; return this; }
        public TranscriptionConfig setTranslate(boolean translate) { this.translate = translate; return this; }
        public TranscriptionConfig setThreads(int threads) { this.threads = Math.max(1, threads); return this; }
        // public TranscriptionConfig setGrammarPath(Path grammarPath) { this.grammarPath = grammarPath; return this; }
        // public TranscriptionConfig setGrammarPenalty(float grammarPenalty) { this.grammarPenalty = grammarPenalty; return this; }

        public String getLanguage() { return language; }
        public boolean isTranslate() { return translate; }
        public int getThreads() { return threads; }
        // public Path getGrammarPath() { return grammarPath; }
        // public float getGrammarPenalty() { return grammarPenalty; }
    }

    /**
     * 使用指定的模型和默认配置转录给定的音频文件 (适配 v1.7.1)。
     */
    public static String transcribe(Path modelPath, Path audioPath)
            throws IOException, UnsupportedAudioFileException, RuntimeException {
        return transcribe(modelPath, audioPath, new TranscriptionConfig());
    }

     /**
     * 使用指定的模型和配置转录给定的音频文件 (适配 v1.7.1)。
     */
    public static String transcribe(Path modelPath, Path audioPath, TranscriptionConfig config)
            throws IOException, UnsupportedAudioFileException, RuntimeException {

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
        // 移除 Grammar 文件检查

        WhisperJNI whisper = new WhisperJNI();
        WhisperContext ctx = null;
        try {
            log.info("正在初始化 Whisper 上下文 (模型: {})...", modelPath.getFileName());
            ctx = whisper.init(modelPath); // 直接传递 Path 对象
            log.info("Whisper 上下文初始化成功.");

            log.info("正在加载并转换音频: {}", audioPath.getFileName());
            // 调用包含格式转换逻辑的 loadAudioData
            float[] audioData = loadAudioData(audioFile);
            log.info("音频加载和转换完成 ({} 个样本).", audioData.length);

            WhisperFullParams whisperParams = new WhisperFullParams(); // v1.7.1 使用无参构造
            whisperParams.language = config.getLanguage();
            whisperParams.translate = config.isTranslate();
            whisperParams.nThreads = config.getThreads();

            log.info("转录参数: language={}, translate={}, threads={}",
                    whisperParams.language, whisperParams.translate, whisperParams.nThreads);

            // 移除 Grammar 相关逻辑

            log.info("开始转录...");
            long startTime = System.currentTimeMillis();
            int result = whisper.full(ctx, whisperParams, audioData, audioData.length);
            long endTime = System.currentTimeMillis();
            log.info("Whisper full() 方法执行完成，返回码: {} (耗时: {} ms).", result, (endTime - startTime));

            if (result != 0) {
                throw new RuntimeException("Whisper 转录失败，错误码: " + result);
            }

            int numSegments = whisper.fullNSegments(ctx);
            if (numSegments == 0) {
                log.warn("未检测到任何语音片段。");
                return "";
            }

            log.info("检测到 {} 个语音片段，正在组合文本...", numSegments);
            StringBuilder transcription = new StringBuilder();
            for (int i = 0; i < numSegments; i++) {
                transcription.append(whisper.fullGetSegmentText(ctx, i));
            }
            log.info("文本组合完成.");
            return transcription.toString();

        } catch (RuntimeException e) {
            log.error("Whisper JNI 操作失败: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (ctx != null) {
                try {
                    log.info("正在手动释放 Whisper 上下文...");
                    ctx.close(); // 手动释放资源
                    log.info("Whisper 上下文已释放.");
                } catch (Exception closeEx) {
                    log.error("释放 Whisper 上下文时出错: {}", closeEx.getMessage(), closeEx);
                }
            }
        }
    }

    // --- 私有辅助方法 (包含格式转换逻辑) ---
    /**
     * 加载 WAV 音频文件，进行必要的格式转换 (重采样到 16kHz, 转为单声道, 16位 PCM)，
     * 并将其转换为 float 数组 (PCM 32位浮点数)。
     *
     * @param wavFile 要加载的 WAV 文件。
     * @return 包含音频数据的 float 数组，值在 -1.0 到 1.0 之间归一化。
     * @throws IOException                 如果发生 I/O 错误。
     * @throws UnsupportedAudioFileException 如果无法进行必要的格式转换。
     */
    private static float[] loadAudioData(File wavFile) throws IOException, UnsupportedAudioFileException {
        AudioInputStream sourceInputStream = null;
        AudioInputStream convertedInputStream = null;
        try {
            sourceInputStream = AudioSystem.getAudioInputStream(wavFile);
            AudioFormat sourceFormat = sourceInputStream.getFormat();
            log.debug("源音频格式: {}", sourceFormat);

            // 定义目标格式: 16kHz, 16bit, 单声道, signed PCM
            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, sourceFormat.isBigEndian());
            log.debug("目标音频格式: {}", targetFormat);

            // 检查是否需要转换，以及 JRE 是否支持转换
            if (!sourceFormat.matches(targetFormat)) {
                if (AudioSystem.isConversionSupported(targetFormat, sourceFormat)) {
                    log.info("需要进行音频格式转换 (例如，重采样到 16kHz)...");
                    convertedInputStream = AudioSystem.getAudioInputStream(targetFormat, sourceInputStream);
                    log.info("音频格式转换成功.");
                } else {
                    log.error("JRE 不支持从 {} 转换为 {}", sourceFormat, targetFormat);
                    throw new UnsupportedAudioFileException(
                            "无法将源音频格式 " + sourceFormat + " 转换为所需的目标格式 " + targetFormat);
                }
            } else {
                log.info("音频格式已满足要求，无需转换.");
                convertedInputStream = sourceInputStream;
            }

            log.debug("正在读取音频字节流...");
            byte[] audioBytes = convertedInputStream.readAllBytes();
            log.debug("读取完成，共 {} 字节.", audioBytes.length);

            if (targetFormat.getSampleSizeInBits() != 16 || targetFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                 throw new IllegalStateException("转换后的音频数据不是预期的 16-bit signed PCM 格式!");
            }

            float[] audioFloats = new float[audioBytes.length / 2];
            ByteOrder byteOrder = targetFormat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
            ByteBuffer buffer = ByteBuffer.wrap(audioBytes).order(byteOrder);

            for (int i = 0; i < audioFloats.length; i++) {
                short pcmSample = buffer.getShort();
                audioFloats[i] = (float) pcmSample / 32768.0f;
            }
            return audioFloats;

        } finally {
            if (convertedInputStream != null && convertedInputStream != sourceInputStream) {
                try { convertedInputStream.close(); log.debug("已关闭转换后的音频流."); }
                catch (IOException e) { log.warn("警告: 关闭转换后的音频输入流失败: {}", e.getMessage()); }
            }
            if (sourceInputStream != null) {
                 try { sourceInputStream.close(); log.debug("已关闭源音频流."); }
                catch (IOException e) { log.warn("警告: 关闭源音频输入流失败: {}", e.getMessage()); }
            }
        }
    }


    // --- 示例用法 Main 方法 (使用你的路径) ---
    public static void main(String[] args) {
        // --- 使用你提供的路径 ---
        String modelFilePath = "D:\\00_Green\\WhisperDesktop\\models" + File.separator + "ggml-model-whisper-tiny.bin";
        String audioFilePathEN = "D:\\0000\\bgmusic\\eng.wav";
//        String audioFilePathNonEN = "D:\\0000\\bgmusic\\chn.wav";
        audioFilePathEN = "D:\\0000\\0003_PressBriefings\\20250415\\20250415.mp3";
        // String grammarFilePath = "path/to/your/grammar.gbnf"; // 移除 Grammar
        // --- 路径设置结束 ---

        Path modelPath = Paths.get(modelFilePath);
        Path audioPathEN = Paths.get(audioFilePathEN);
//        Path audioPathNonEN = Paths.get(audioFilePathNonEN);
        // Path grammarPath = Paths.get(grammarFilePath); // 移除 Grammar

        // --- 测试用例 ---
        runTest("测试 1 (v1.7.1): 基本英语转录", modelPath, audioPathEN, new TranscriptionConfig());

//        if (Files.exists(audioPathNonEN)) {
//            runTest("测试 2 (v1.7.1): 自动语言检测", modelPath, audioPathNonEN, new TranscriptionConfig().setLanguage("auto"));
//            runTest("测试 3 (v1.7.1): 翻译为英语", modelPath, audioPathNonEN, new TranscriptionConfig().setLanguage("auto").setTranslate(true));
//        } else {
//            log.warn("跳过测试 2 & 3: 未找到非英语音频文件 {}", audioPathNonEN);
//        }

        runTest("测试 4 (v1.7.1): 指定线程数", modelPath, audioPathEN, new TranscriptionConfig().setThreads(2));

        // 移除 Grammar 测试
        // if (Files.exists(grammarPath)) { ... }

        // 注释掉文件不存在的测试，如果你不需要它们
//        log.info("\n--- 测试 6 (v1.7.1): 模型文件不存在 ---");
//        try { WhisperUtil_V1_7_1.transcribe(Paths.get("non/existent/model.bin"), audioPathEN); }
//        catch (Exception e) { log.error("预期错误: {}", e.getMessage()); }
//
//        log.info("\n--- 测试 7 (v1.7.1): 音频文件不存在 ---");
//        try { WhisperUtil_V1_7_1.transcribe(modelPath, Paths.get("non/existent/audio.wav")); }
//        catch (Exception e) { log.error("预期错误: {}", e.getMessage()); }
    }

    // 辅助方法 runTest (保持不变)
    private static void runTest(String testName, Path modelPath, Path audioPath, TranscriptionConfig config) {
        log.info("\n--- {} ---", testName);
        if (!Files.exists(modelPath)) { log.error("模型文件不存在，跳过测试: {}", modelPath); return; }
        if (!Files.exists(audioPath)) { log.error("音频文件不存在，跳过测试: {}", audioPath); return; }
        // 移除 grammarPath 检查
        try {
            String result = WhisperUtil_V1_7_1.transcribe(modelPath, audioPath, config); // 调用适配后的类
            log.info("转录结果:\n{}", result);
        } catch (IOException | UnsupportedAudioFileException | RuntimeException e) {
            log.error("测试 '{}' 执行失败: {}", testName, e.getMessage(), e);
        }
    }
}
