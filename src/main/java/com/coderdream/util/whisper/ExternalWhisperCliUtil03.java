package com.coderdream.util.whisper; // 请确保包名正确

import lombok.extern.slf4j.Slf4j;

// ... (其他 imports 保持不变) ...
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Slf4j
public class ExternalWhisperCliUtil03 {

    // --- 配置区域 ---
    private static final String WHISPER_CLI_EXECUTABLE_PATH = "D:\\00_Green\\WhisperDesktop\\cli\\main.exe";
    private static final String ARG_MODEL = "-m";
    // private static final String ARG_INPUT_FILE = "-f"; // 不再需要，因为文件放最后
    private static final String ARG_OUTPUT_SRT = "-osrt";
    private static final String ARG_LANGUAGE = "-l";
    private static final String ARG_TRANSLATE = "-tr";
    private static final String ARG_THREADS = "-t";
    // --- 配置结束 ---

    public static class ExternalTranscriptionConfig {
        private String language = null; // null 表示使用 CLI 默认语言
        private boolean translate = false;
        private int threads = 8;

        public ExternalTranscriptionConfig setLanguage(String language) {
            if ("auto".equalsIgnoreCase(language)) {
                log.warn("CLI 不支持 'auto' 语言检测，将不传递 -l 参数，使用 CLI 默认语言。");
                this.language = null;
            } else {
                this.language = language;
            }
            return this;
        }
        public ExternalTranscriptionConfig setTranslate(boolean translate) { this.translate = translate; return this; }
        public ExternalTranscriptionConfig setThreads(int threads) { this.threads = Math.max(1, threads); return this; }

        public String getLanguage() { return language; }
        public boolean isTranslate() { return translate; }
        public int getThreads() { return threads; }
    }

    public static Path transcribeToSrtExternalCli(Path modelPath, Path audioPath,
                                                 ExternalTranscriptionConfig config, long timeoutSeconds)
            throws IOException, InterruptedException, RuntimeException {

        // ... (校验逻辑不变) ...
        Objects.requireNonNull(modelPath, "模型路径不能为空");
        Objects.requireNonNull(audioPath, "音频路径不能为空");
        Objects.requireNonNull(config, "配置不能为空");
        // ... (文件存在性校验不变)

        // --- 构建命令行参数列表 ---
        List<String> command = new ArrayList<>();
        command.add(WHISPER_CLI_EXECUTABLE_PATH);

        command.add(ARG_MODEL); command.add(modelPath.toAbsolutePath().toString());
        command.add(ARG_THREADS); command.add(String.valueOf(config.getThreads()));
        if (config.getLanguage() != null && !config.getLanguage().trim().isEmpty()) {
             if ("auto".equalsIgnoreCase(config.getLanguage())) {
                 log.warn("跳过无效的 'auto' 语言参数。");
             } else {
                 command.add(ARG_LANGUAGE); command.add(config.getLanguage());
             }
        } else {
             log.info("未指定语言，将使用 CLI 默认语言。");
        }
        if (config.isTranslate()) {
            command.add(ARG_TRANSLATE);
        }
        command.add(ARG_OUTPUT_SRT); // 输出 SRT 开关

        // **修改点：直接添加音频文件路径到最后，不再需要 -f 参数**
        command.add(audioPath.toAbsolutePath().toString());


        log.info("准备执行外部 Whisper CLI 命令:");
        // 打印完整命令，方便调试
        log.info("{}", String.join(" ", command));


        // --- 计算预期的 SRT 输出路径 (逻辑不变) ---
        String audioFileName = audioPath.getFileName().toString();
        String expectedSrtFileName = audioFileName + ".srt";
        Path expectedSrtPath = audioPath.resolveSibling(expectedSrtFileName);
        log.info("预期生成的 SRT 文件路径: {}", expectedSrtPath);

        // --- 执行外部进程 (逻辑不变) ---
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = null;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        StringBuilder processOutput = new StringBuilder();

        try {
            process = processBuilder.start();
            final Process pFinal = process;
            executor.submit(() -> { /* ... 异步读取输出 ... */
                 try (BufferedReader reader = new BufferedReader(new InputStreamReader(pFinal.getInputStream(), StandardCharsets.UTF_8))) {
                     String line;
                     while ((line = reader.readLine()) != null) { processOutput.append(line).append(System.lineSeparator()); log.info("Whisper CLI: {}", line); }
                 } catch (IOException e) { log.error("读取外部 Whisper CLI 输出时出错: {}", e.getMessage()); }
            });
            log.info("外部 Whisper CLI 已启动，等待其完成...");
            boolean finished;
            if (timeoutSeconds > 0) finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            else { process.waitFor(); finished = true; }
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
            if (!finished) { /* ... 超时 ... */ process.destroyForcibly(); return null; }
            int exitCode = process.exitValue();
            log.info("外部 Whisper CLI 已结束，退出码: {}", exitCode);
            log.debug("外部 Whisper CLI 完整输出:\n{}", processOutput);
            if (exitCode == 0) {
                if (Files.exists(expectedSrtPath) && Files.size(expectedSrtPath) > 0) return expectedSrtPath;
                else { log.error("CLI 退出码为 0，但 SRT 文件无效 {}", expectedSrtPath); return null; }
            } else { log.error("CLI 执行失败，退出码: {}", exitCode); return null; }
        } finally { /* ... 清理 ... */
             if (process != null && process.isAlive()) process.destroyForcibly();
             if (!executor.isTerminated()) executor.shutdownNow();
        }
    }


    // --- 示例用法 Main 方法 ---
    public static void main(String[] args) {
        log.info("--- 测试调用外部 Whisper CLI (main.exe) ---");
        String modelFilePath = "D:\\00_Green\\WhisperDesktop\\models\\ggml-model-whisper-medium.bin";
        String audioFilePath = "D:\\0000\\0003_PressBriefings\\20250415\\20250415.mp3";
        audioFilePath = "D:\\14_LearnEnglish\\6MinuteEnglish\\2017\\171005\\171005_adult_exercise.mp3";

        log.info("模型文件路径: {}", modelFilePath);
        log.info("音频文件路径: {}", audioFilePath);
        log.info("CLI 程序路径: {}", WHISPER_CLI_EXECUTABLE_PATH);

        Path modelPath = Paths.get(modelFilePath);
        Path audioPath = Paths.get(audioFilePath);

        if (!Files.exists(modelPath)) { log.error("模型文件未找到"); return; }
        if (!Files.exists(audioPath)) { log.error("音频文件未找到"); return; }
        log.info("文件检查通过。");

        ExternalTranscriptionConfig config = new ExternalTranscriptionConfig()
                .setLanguage(null) // 使用默认语言 (通常是英语)
                .setTranslate(false)
                .setThreads(8);     // 使用 8 线程

        long timeoutInSeconds = 1800; // 30 分钟超时

        log.info("准备调用外部 Whisper CLI (语言: {}, 翻译: {}, 线程: {}, 超时: {} 秒)...",
                 config.getLanguage() == null ? "[默认]" : config.getLanguage(),
                 config.isTranslate(), config.getThreads(), timeoutInSeconds > 0 ? timeoutInSeconds : "无");

        try {
            long startTime = System.currentTimeMillis();
            Path generatedSrtPath = transcribeToSrtExternalCli(modelPath, audioPath, config, timeoutInSeconds);
            long endTime = System.currentTimeMillis();
            double durationMinutes = (endTime - startTime) / 60000.0;
            if (generatedSrtPath != null) {
                 log.info("外部 CLI 调用成功！SRT 文件已生成: {}", generatedSrtPath);
                 log.info("总耗时: {} ms (~= {} 分钟)", (endTime - startTime), String.format("%.2f", durationMinutes));
                 // ... (预览代码) ...
                 try {
                     List<String> lines = Files.readAllLines(generatedSrtPath, StandardCharsets.UTF_8);
                     log.info("生成的 SRT 文件 '{}' 前 {} 行预览:", generatedSrtPath.getFileName(), Math.min(lines.size(), 10)); // 预览10行
                     for(int i = 0; i < Math.min(lines.size(), 10); i++) log.info("  {}", lines.get(i));
                 } catch (IOException readEx) { log.warn("读取生成的 SRT 文件预览时出错: {}", readEx.getMessage()); }
            } else {
                 log.error("外部 CLI 调用失败或超时。总耗时: {} ms (~= {} 分钟)", (endTime - startTime), String.format("%.2f", durationMinutes));
            }
        } catch (Exception e) { log.error("调用外部 CLI 时发生错误: {}", e.getMessage(), e); }

        log.info("--- CLI 调用测试结束 ---");
    }

    // formatTimestamp 方法保持不变
    private static String formatTimestamp(long totalMillis) {
        if (totalMillis < 0) totalMillis = 0;
        long milliseconds = totalMillis % 1000; long totalSeconds = totalMillis / 1000;
        long seconds = totalSeconds % 60; long totalMinutes = totalSeconds / 60;
        long minutes = totalMinutes % 60; long hours = totalMinutes / 60;
        return String.format(Locale.ROOT, "%02d:%02d:%02d,%03d", hours, minutes, seconds, milliseconds);
    }
}
