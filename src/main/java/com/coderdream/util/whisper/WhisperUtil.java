//package com.coderdream.util.whisper;
//
//import io.github.givimad.whisperjni.WhisperContext;
//import io.github.givimad.whisperjni.WhisperFullParams;
//import io.github.givimad.whisperjni.WhisperGrammar;
//import io.github.givimad.whisperjni.WhisperJNI;
//import lombok.extern.slf4j.Slf4j;
//
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioInputStream;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.UnsupportedAudioFileException;
//import java.io.File;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Objects;
//
///**
// * 使用 whisper.jni (io.github.givimad:whisper-jni) 进行语音转文本的工具类。
// * 依赖已发布到 Maven Central。
// * 需要 SLF4J API、Lombok 及 SLF4J 实现 (如 Logback)。
// * 假设输入音频是 16kHz 采样率、单声道、16位 PCM 编码的 WAV 文件。
// */
//@Slf4j
//public class WhisperUtil {
//
//    // 静态初始化块，用于加载本地库和设置日志（可选）
//    static {
//        try {
//            log.info("正在加载 Whisper JNI 本地库...");
//            // 根据 README，这会加载 JAR 包中包含的或系统路径中的本地库
//            WhisperJNI.loadLibrary();
//            log.info("Whisper JNI 本地库加载成功。");
//
//            // 根据需要取消注释下一行来禁用 whisper.cpp 内部的 C++ 日志输出
//            // WhisperJNI.setLibraryLogger(null);
//            // log.info("已禁用 whisper.cpp 内部日志输出。");
//
//        } catch (Exception e) {
//            log.error("加载 Whisper JNI 本地库失败!", e);
//            // 让后续操作失败，因为库没加载成功
//            throw new RuntimeException("无法加载 Whisper JNI 本地库", e);
//        }
//    }
//
//    // --- 配置内部类 ---
//
//    /**
//     * 转录过程的配置。
//     */
//    public static class TranscriptionConfig {
//        private String language = "en"; // 默认为英语
//        private boolean translate = false; // 默认不翻译
//        private int threads = Math.max(1, Runtime.getRuntime().availableProcessors() / 2); // 默认线程数
//        private Path grammarPath = null; // GBNF 语法文件路径 (可选)
//        private float grammarPenalty = 100.0f; // 语法惩罚系数 (当使用 grammar 时)
//        // 可以根据 WhisperFullParams 添加更多配置项，如 temperature, beam_size 等
//
//        public TranscriptionConfig setLanguage(String language) {
//            this.language = language;
//            return this;
//        }
//
//        public TranscriptionConfig setTranslate(boolean translate) {
//            this.translate = translate;
//            return this;
//        }
//
//        public TranscriptionConfig setThreads(int threads) {
//            this.threads = Math.max(1, threads); // 确保至少1个线程
//            return this;
//        }
//
//        public TranscriptionConfig setGrammarPath(Path grammarPath) {
//            this.grammarPath = grammarPath;
//            return this;
//        }
//
//        public TranscriptionConfig setGrammarPenalty(float grammarPenalty) {
//            this.grammarPenalty = grammarPenalty;
//            return this;
//        }
//
//        public String getLanguage() { return language; }
//        public boolean isTranslate() { return translate; }
//        public int getThreads() { return threads; }
//        public Path getGrammarPath() { return grammarPath; }
//        public float getGrammarPenalty() { return grammarPenalty; }
//    }
//
//    // --- 公共转录方法 ---
//
//    /**
//     * 使用指定的模型和默认配置转录给定的音频文件。
//     *
//     * @param modelPath Whisper GGML 模型文件 (.bin) 的路径。
//     * @param audioPath WAV 音频文件的路径 (必须是 16kHz, 单声道, 16位 PCM)。
//     * @return 转录后的文本。
//     * @throws IOException                 如果读取文件或初始化 Whisper 时出错。
//     * @throws UnsupportedAudioFileException 如果音频文件格式不支持或不正确。
//     * @throws RuntimeException            如果 Whisper JNI 初始化或转录失败。
//     */
//    public static String transcribe(Path modelPath, Path audioPath)
//            throws IOException, UnsupportedAudioFileException, RuntimeException {
//        return transcribe(modelPath, audioPath, new TranscriptionConfig());
//    }
//
//     /**
//     * 使用指定的模型和配置转录给定的音频文件。
//     *
//     * @param modelPath Whisper GGML 模型文件 (.bin) 的路径。
//     * @param audioPath WAV 音频文件的路径 (必须是 16kHz, 单声道, 16位 PCM)。
//     * @param config    转录配置。
//     * @return 转录后的文本。
//     * @throws IOException                 如果读取文件或初始化 Whisper 时出错。
//     * @throws UnsupportedAudioFileException 如果音频文件格式不支持或不正确。
//     * @throws RuntimeException            如果 Whisper JNI 初始化或转录失败。
//     */
//    public static String transcribe(Path modelPath, Path audioPath, TranscriptionConfig config)
//            throws IOException, UnsupportedAudioFileException, RuntimeException {
//
//        Objects.requireNonNull(modelPath, "模型路径不能为空");
//        Objects.requireNonNull(audioPath, "音频路径不能为空");
//        Objects.requireNonNull(config, "转录配置不能为空");
//
//        File modelFile = modelPath.toFile();
//        File audioFile = audioPath.toFile();
//
//        if (!modelFile.exists() || !modelFile.isFile()) {
//            throw new IOException("模型文件未找到或不是有效文件: " + modelPath);
//        }
//        if (!audioFile.exists() || !audioFile.isFile()) {
//            throw new IOException("音频文件未找到或不是有效文件: " + audioPath);
//        }
//        if (config.getGrammarPath() != null && (!config.getGrammarPath().toFile().exists() || !config.getGrammarPath().toFile().isFile())) {
//             throw new IOException("语法文件未找到或不是有效文件: " + config.getGrammarPath());
//        }
//
//        // WhisperJNI 实例用于初始化上下文和解析语法
//        WhisperJNI whisper = new WhisperJNI();
//
//        // 使用 try-with-resources 管理 WhisperContext 和 WhisperGrammar (如果使用) 的生命周期
//        // WhisperContext 和 WhisperGrammar 都实现了 AutoCloseable
//        try (WhisperContext ctx = whisper.init(modelPath.toString())) {
//            log.info("Whisper 上下文初始化成功 (模型: {}).", modelPath.getFileName());
//
//            // 加载音频数据
//            log.info("正在加载音频: {}", audioPath.getFileName());
//            float[] audioData = loadAudioData(audioFile);
//            log.info("音频加载完成 ({} 个样本).", audioData.length);
//
//            // 配置转录参数
//            WhisperFullParams whisperParams = new WhisperFullParams(WhisperJNI.SamplingStrategy.WHISPER_SAMPLING_GREEDY);
//            whisperParams.language = config.getLanguage();
//            whisperParams.translate = config.isTranslate();
//            whisperParams.nThreads = config.getThreads();
//            // ... 设置其他参数 ...
//
//            log.info("转录参数: language={}, translate={}, threads={}",
//                    whisperParams.language, whisperParams.translate, whisperParams.nThreads);
//
//            // 处理 Grammar (如果配置了)
//            WhisperGrammar grammar = null;
//            try {
//                 if (config.getGrammarPath() != null) {
//                    log.info("正在解析语法文件: {}", config.getGrammarPath());
//                    // WhisperGrammar 也需要管理资源，但 WhisperJNI v1.5.1 的 parseGrammar 返回值
//                    // 可能并未实现 AutoCloseable，需要确认库的具体实现。
//                    // 如果库版本支持 AutoCloseable，应将其放入外部 try-with-resources。
//                    // 假设它不是 AutoCloseable 或需要在 params 中传递引用：
//                    grammar = whisper.parseGrammar(config.getGrammarPath());
//                    whisperParams.grammar = grammar; // 将解析后的 grammar 设置到参数中
//                    whisperParams.grammarPenalty = config.getGrammarPenalty();
//                    log.info("语法文件解析成功，设置惩罚系数为: {}", whisperParams.grammarPenalty);
//                 }
//
//                // 执行转录
//                log.info("开始转录...");
//                long startTime = System.currentTimeMillis();
//                // 调用 full 方法，它返回一个整数状态码
//                int result = whisper.full(ctx, whisperParams, audioData, audioData.length);
//                long endTime = System.currentTimeMillis();
//                log.info("Whisper full() 方法执行完成，返回码: {} (耗时: {} ms).", result, (endTime - startTime));
//
//                // 检查转录是否成功
//                if (result != 0) {
//                    throw new RuntimeException("Whisper 转录失败，错误码: " + result);
//                }
//
//                // 获取转录结果
//                int numSegments = whisper.fullNSegments(ctx);
//                if (numSegments == 0) {
//                    log.warn("未检测到任何语音片段。");
//                    return "";
//                }
//
//                log.info("检测到 {} 个语音片段，正在组合文本...", numSegments);
//                StringBuilder transcription = new StringBuilder();
//                for (int i = 0; i < numSegments; i++) {
//                    transcription.append(whisper.fullGetSegmentText(ctx, i));
//                    // 可以在这里获取更多片段信息，如时间戳：
//                    // long start = whisper.fullGetSegmentTimestamp0(ctx, i);
//                    // long end = whisper.fullGetSegmentTimestamp1(ctx, i);
//                    // log.debug("  片段 {}: [{}ms - {}ms] {}", i, start, end, whisper.fullGetSegmentText(ctx, i));
//                }
//                log.info("文本组合完成.");
//                return transcription.toString();
//
//            } finally {
//                 // 手动释放 grammar 资源 (如果它没有实现 AutoCloseable)
//                 // 注意：需要查阅 whisper-jni 文档确认 WhisperGrammar 是否需要手动释放
//                 // 如果 parseGrammar 返回的对象需要释放，且没有实现 AutoCloseable，
//                 // 可能需要调用类似 grammar.free() 或 whisper.freeGrammar(grammar) 的方法。
//                 // 假设这里不需要显式释放，或者由 WhisperContext 关闭时隐式处理。
//                 // if (grammar != null) { /* call free method if exists */ }
//                 log.debug("完成转录处理流程（Grammar 资源管理需根据库文档确认）。");
//            }
//
//        } catch (WhisperJNI.WhisperException e) {
//            log.error("初始化 Whisper 上下文或解析语法时发生错误: {}", e.getMessage(), e);
//            throw new RuntimeException("Whisper 初始化或语法解析失败: " + e.getMessage(), e);
//        }
//        // WhisperContext 会在此处通过 try-with-resources 自动关闭并释放资源
//    }
//
//    // --- 私有辅助方法 ---
//
//    /**
//     * 加载 WAV 音频文件并将其转换为 float 数组 (PCM 32位浮点数)。
//     * 重要: 假设输入的 WAV 是 16kHz, 单声道, 16位有符号 PCM。
//     *
//     * @param wavFile 要加载的 WAV 文件。
//     * @return 包含音频数据的 float 数组，值在 -1.0 到 1.0 之间归一化。
//     * @throws IOException                 如果发生 I/O 错误。
//     * @throws UnsupportedAudioFileException 如果音频格式不支持 (或不是 16kHz/单声道/16位)。
//     */
//    private static float[] loadAudioData(File wavFile) throws IOException, UnsupportedAudioFileException {
//        AudioInputStream audioInputStream = null;
//        try {
//            audioInputStream = AudioSystem.getAudioInputStream(wavFile);
//            AudioFormat format = audioInputStream.getFormat();
//
//            // --- 关键格式检查 ---
//            if (format.getSampleRate() != 16000) {
//                throw new UnsupportedAudioFileException("不支持的采样率: " + format.getSampleRate() + " Hz. 需要: 16000 Hz");
//            }
//            if (format.getChannels() != 1) {
//                throw new UnsupportedAudioFileException("不支持的声道数: " + format.getChannels() + ". 需要: 1 (单声道)");
//            }
//             if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
//                 throw new UnsupportedAudioFileException("不支持的编码: " + format.getEncoding() + ". 需要: PCM_SIGNED");
//             }
//             if (format.getSampleSizeInBits() != 16) {
//                 throw new UnsupportedAudioFileException("不支持的采样位深: " + format.getSampleSizeInBits() + " bits. 需要: 16 bits");
//             }
//            // --- 格式检查结束 ---
//
//            byte[] audioBytes = audioInputStream.readAllBytes();
//            float[] audioFloats = new float[audioBytes.length / 2];
//            ByteBuffer buffer = ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN);
//
//            for (int i = 0; i < audioFloats.length; i++) {
//                short pcmSample = buffer.getShort();
//                audioFloats[i] = (float) pcmSample / 32768.0f;
//            }
//            return audioFloats;
//
//        } finally {
//            if (audioInputStream != null) {
//                try {
//                    audioInputStream.close();
//                } catch (IOException e) {
//                    log.warn("警告: 关闭音频输入流失败: {}", e.getMessage());
//                }
//            }
//        }
//    }
//
//    // --- 示例用法 Main 方法 ---
//    public static void main(String[] args) {
//        // --- !! 重要: 修改这些路径 !! ---
//        // 模型文件路径 (例如: 使用 tiny 或 base 模型进行快速测试)
//        String modelFilePath = "path/to/your/ggml-model-whisper-tiny.en.bin"; // 或 ggml-base.bin 等
//        // 测试音频文件路径 (必须是 16kHz, 单声道, 16位 PCM WAV)
//        // 你可以从 whisper.cpp 的 samples 目录找到或自己准备
//        String audioFilePathEN = "path/to/your/english_audio_16k_mono.wav";
//        String audioFilePathNonEN = "path/to/your/non_english_audio_16k_mono.wav"; // 例如: 西班牙语或中文
//        // 语法文件路径 (可选, 用于 Grammar 测试)
//        String grammarFilePath = "path/to/your/grammar.gbnf"; // 创建一个简单的 .gbnf 文件用于测试
//        // --- 路径修改结束 ---
//
//        Path modelPath = Paths.get(modelFilePath);
//        Path audioPathEN = Paths.get(audioFilePathEN);
//        Path audioPathNonEN = Paths.get(audioFilePathNonEN);
//        Path grammarPath = Paths.get(grammarFilePath);
//
//        // --- 测试用例 ---
//
//        // 测试 1: 基本英语转录 (默认配置)
//        runTest("测试 1: 基本英语转录 (默认配置)", modelPath, audioPathEN, new TranscriptionConfig());
//
//        // 测试 2: 自动语言检测 (使用非英语音频)
//        if (Files.exists(audioPathNonEN)) {
//            runTest("测试 2: 自动语言检测", modelPath, audioPathNonEN,
//                    new TranscriptionConfig().setLanguage("auto"));
//        } else {
//            log.warn("跳过测试 2: 未找到非英语音频文件 {}", audioPathNonEN);
//        }
//
//        // 测试 3: 翻译为英语 (使用非英语音频)
//        if (Files.exists(audioPathNonEN)) {
//            runTest("测试 3: 翻译为英语", modelPath, audioPathNonEN,
//                    new TranscriptionConfig().setLanguage("auto").setTranslate(true)); // 假设模型支持翻译
//        } else {
//            log.warn("跳过测试 3: 未找到非英语音频文件 {}", audioPathNonEN);
//        }
//
//        // 测试 4: 指定线程数
//        runTest("测试 4: 指定线程数 (例如 2)", modelPath, audioPathEN,
//                new TranscriptionConfig().setThreads(2));
//
//        // 测试 5: 使用 GBNF 语法文件 (如果文件存在)
//        if (Files.exists(grammarPath)) {
//            // 确保 grammar.gbnf 文件内容有效，例如:
//            // root ::= "hello" | "world"
//            runTest("测试 5: 使用 GBNF 语法", modelPath, audioPathEN, // 可能需要特定匹配语法的音频
//                    new TranscriptionConfig().setGrammarPath(grammarPath).setGrammarPenalty(100f));
//        } else {
//            log.warn("跳过测试 5: 未找到语法文件 {}", grammarPath);
//        }
//
//        // 测试 6: 文件不存在错误处理 (模型文件)
//        log.info("\n--- 测试 6: 模型文件不存在 ---");
//        try {
//            WhisperUtil.transcribe(Paths.get("non/existent/model.bin"), audioPathEN);
//        } catch (Exception e) {
//            log.error("预期错误: {}", e.getMessage()); // 应该捕获 IOException
//        }
//
//        // 测试 7: 文件不存在错误处理 (音频文件)
//         log.info("\n--- 测试 7: 音频文件不存在 ---");
//        try {
//            WhisperUtil.transcribe(modelPath, Paths.get("non/existent/audio.wav"));
//        } catch (Exception e) {
//            log.error("预期错误: {}", e.getMessage()); // 应该捕获 IOException
//        }
//
//        // 测试 8: 音频格式错误 (如果能找到一个非 16k/mono/16bit 的 WAV 文件)
//        // Path wrongFormatAudioPath = Paths.get("path/to/wrong_format.wav");
//        // if(Files.exists(wrongFormatAudioPath)) {
//        //     runTest("测试 8: 不支持的音频格式", modelPath, wrongFormatAudioPath, new TranscriptionConfig()); // 预期 UnsupportedAudioFileException
//        // } else {
//        //     log.warn("跳过测试 8: 未找到格式错误的音频文件");
//        // }
//    }
//
//    // 辅助方法，用于运行单个测试用例并打印结果/错误
//    private static void runTest(String testName, Path modelPath, Path audioPath, TranscriptionConfig config) {
//        log.info("\n--- {} ---", testName);
//        if (!Files.exists(modelPath)) {
//            log.error("模型文件不存在，跳过测试: {}", modelPath);
//            return;
//        }
//         if (!Files.exists(audioPath)) {
//            log.error("音频文件不存在，跳过测试: {}", audioPath);
//            return;
//        }
//         if (config.getGrammarPath() != null && !Files.exists(config.getGrammarPath())) {
//              log.error("语法文件不存在，跳过测试: {}", config.getGrammarPath());
//            return;
//         }
//
//        try {
//            String result = WhisperUtil.transcribe(modelPath, audioPath, config);
//            log.info("转录结果:\n{}", result);
//        } catch (IOException | UnsupportedAudioFileException | RuntimeException e) {
//            log.error("测试 '{}' 执行失败: {}", testName, e.getMessage(), e);
//        }
//    }
//
//}
