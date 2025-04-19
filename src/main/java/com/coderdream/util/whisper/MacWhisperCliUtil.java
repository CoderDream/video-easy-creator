package com.coderdream.util.whisper; // 请确保包名正确

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*; // 导入 Files, Path, Paths, StandardOpenOption
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 使用 macOS 原生编译的 whisper.cpp CLI 进程 (whisper-cli)
 * 来执行高性能的语音转文本和 SRT 生成。
 * <p>
 * 通过捕获 whisper-cli 的标准输出并解析，来生成 SRT 文件。
 * 支持 Metal / Core ML 加速（取决于编译时是否启用）。
 */
@Slf4j
public class MacWhisperCliUtil {

    // --- ==================== 配置与默认值区域 ==================== ---

    /** 【已根据用户提供信息修改】whisper.cpp 基础路径。 */
    private static final String WHISPER_CPP_BASE_PATH = "/Volumes/System/04_GitHub/whisper.cpp";

    /** 【确认】外部 Whisper CLI 可执行文件的完整路径。 */
    private static final String WHISPER_CLI_EXECUTABLE_PATH = WHISPER_CPP_BASE_PATH + "/build/bin/whisper-cli";

    /** 【确认】默认使用的 Whisper 模型文件的完整路径 (假设是 medium)。 */
    private static final String DEFAULT_MODEL_PATH = WHISPER_CPP_BASE_PATH + "/models/ggml-medium.bin";

    /** 【确认】命令行参数常量 (根据 whisper-cli --help 结果)。 */
    private static final String ARG_MODEL = "-m";
    private static final String ARG_INPUT_FILE = "-f";
    private static final String ARG_LANGUAGE = "-l";
    private static final String ARG_TRANSLATE = "-tr";
    private static final String ARG_THREADS = "-t";
    private static final String ARG_PRINT_PROGRESS = "-pp";
    private static final String ARG_NO_GPU = "-ng";

    /** 默认使用的 CPU 线程数。 */
    private static final int DEFAULT_THREADS = calculateDefaultThreads();

    /** 默认的进程执行超时时间（秒）。0 或负数表示不超时。 */
    private static final long DEFAULT_TIMEOUT_SECONDS = 1800; // 默认 30 分钟

    // --- ================= 配置区域结束 ================== ---

    // 静态初始化块
    static {
        Path defaultModel = Paths.get(DEFAULT_MODEL_PATH);
        if (!Files.exists(defaultModel)) {
            log.warn("警告：默认模型文件路径 '{}' 不存在！使用简化接口时可能会失败。", DEFAULT_MODEL_PATH);
        }
        checkCliExecutable();
    }

    /** 检查 CLI 可执行文件是否存在且有效。 */
    private static void checkCliExecutable() {
        File executable = new File(WHISPER_CLI_EXECUTABLE_PATH);
        if (!executable.exists() || !executable.isFile()) {
             String errorMsg = "配置错误：外部 Whisper CLI 未找到或不是文件: " + WHISPER_CLI_EXECUTABLE_PATH;
             log.error(errorMsg);
             throw new RuntimeException(errorMsg);
        }
        if (!executable.canExecute()) { // 检查执行权限
            log.warn("警告：文件存在但可能没有执行权限: {}", WHISPER_CLI_EXECUTABLE_PATH);
            // 如果需要，可以尝试设置权限，但这可能失败
            // boolean success = executable.setExecutable(true);
            // if(!success) log.warn("尝试设置执行权限失败。");
        }
        log.info("外部 Whisper CLI 路径验证通过: {}", WHISPER_CLI_EXECUTABLE_PATH);
    }

    /** 计算默认线程数 */
    private static int calculateDefaultThreads() {
        int cores = Runtime.getRuntime().availableProcessors();
        return Math.max(4, Math.min(cores > 1 ? cores / 2 : 1, 16)); // 至少1，最多16，优先一半逻辑核心
    }


    /**
     * 外部转录配置类 (macOS 版本)。
     */
    public static class MacExternalTranscriptionConfig {
        private String language = "auto";
        private Boolean translate = null;
        private Integer threads = null;
        private Boolean printProgress = null;
        private Boolean disableGpu = null;

        public MacExternalTranscriptionConfig setLanguage(String language) {
             this.language = (language != null && language.isBlank()) ? null : language;
             if (this.language == null) log.info("语言设为 null，将使用 whisper-cli 默认语言 (en)。");
             else if ("auto".equalsIgnoreCase(language)) log.info("语言设置为 'auto' 进行自动检测。");
             return this;
        }
        public MacExternalTranscriptionConfig setTranslate(Boolean translate) { this.translate = translate; return this; }
        public MacExternalTranscriptionConfig setThreads(Integer threads) { this.threads = (threads != null && threads < 1) ? 1 : threads; return this; }
        public MacExternalTranscriptionConfig setPrintProgress(Boolean printProgress) { this.printProgress = printProgress; return this; }
        public MacExternalTranscriptionConfig setDisableGpu(Boolean disableGpu) { this.disableGpu = disableGpu; return this; }

        public String getLanguage() { return language; } // 返回原始设置
        public Boolean getTranslate() { return translate; }
        public Integer getThreads() { return threads; }
        public Boolean getPrintProgress() { return printProgress; }
        public Boolean getDisableGpu() { return disableGpu; }

        // 获取有效值的方法
        public int getEffectiveThreads() { return threads != null ? threads : DEFAULT_THREADS; }
        public boolean getEffectiveTranslate() { return translate != null && translate; }
        public boolean getEffectivePrintProgress() { return printProgress != null && printProgress; }
        public boolean getEffectiveDisableGpu() { return disableGpu != null && disableGpu; }
        /** 获取用于命令行参数的语言代码，null 表示使用默认 'en' */
        public String getEffectiveLanguageForCli() {
             if (language == null) return null; // 返回 null，调用方不添加 -l
             return language; // 返回用户设置的，包括 "auto"
        }
    }


    // --- ==================== 公开 API 方法 ==================== ---

    /**
     * 【Mac接口1】最简单的调用方式：转录指定音频文件为 SRT，使用所有默认设置。
     * 使用默认模型，语言自动检测，输出 SRT 到音频文件同目录。
     *
     * @param audioFilePath 要转录的音频文件的【完整路径字符串】。
     * @return 如果成功生成 SRT 文件，返回该文件的 Path；否则返回 null。
     */
    public static Path transcribeToSrt(String audioFilePath)
            throws IOException, InterruptedException, RuntimeException {
        log.info("收到 Mac 简化接口调用请求 (transcribeToSrt(String))，使用默认设置。");
        if (audioFilePath == null || audioFilePath.isBlank()) { log.error("音频文件路径字符串不能为空。"); return null; }
        Path audioPath = Paths.get(audioFilePath);
        return transcribeToSrt(audioPath, new MacExternalTranscriptionConfig());
    }

    /**
     * 【Mac接口2】带配置的调用方式：转录指定音频文件为 SRT，允许自定义语言、翻译、线程数等。
     * 模型使用默认路径，SRT 文件自动生成在音频文件旁边。
     *
     * @param audioPath 要转录的音频文件的 Path 对象。
     * @param config    包含自定义设置的 MacExternalTranscriptionConfig 对象。未设置的选项将使用默认值。
     * @return 如果成功生成 SRT 文件，返回该文件的 Path；否则返回 null。
     */
    public static Path transcribeToSrt(Path audioPath, MacExternalTranscriptionConfig config)
            throws IOException, InterruptedException, RuntimeException {
        log.info("收到 Mac 带配置接口调用请求 (transcribeToSrt(Path, Config))，使用默认模型和输出路径。");
        Objects.requireNonNull(audioPath, "音频文件路径不能为空");
        Objects.requireNonNull(config, "配置对象不能为空");
        if (!Files.exists(audioPath)) throw new IOException("音频文件未找到: " + audioPath);

        Path modelPath = Paths.get(DEFAULT_MODEL_PATH);
        if (!Files.exists(modelPath)) {
            throw new IOException("默认模型文件未找到: " + modelPath);
        }

        Path outputSrtPath = determineSrtOutputPath(audioPath, config);
        log.info("将使用默认模型 '{}'，根据配置生成 SRT 到 '{}'", DEFAULT_MODEL_PATH, outputSrtPath);

        return executeWhisperCliAndWriteSrt(modelPath, audioPath, outputSrtPath, config, DEFAULT_TIMEOUT_SECONDS);
    }


    /**
     * 【Mac全功能接口】允许完全控制所有参数。
     */
    public static Path transcribeToSrtFullyConfigurable(Path modelPath, Path audioPath, Path outputSrtPath,
                                                        MacExternalTranscriptionConfig config, long timeoutSeconds)
             throws IOException, InterruptedException, RuntimeException {
         log.info("收到 Mac 全功能接口调用请求。");
         return executeWhisperCliAndWriteSrt(modelPath, audioPath, outputSrtPath, config, timeoutSeconds);
     }


    // --- ================== 核心私有执行方法 ================== ---

    private static Path executeWhisperCliAndWriteSrt(Path modelPath, Path audioPath, Path outputSrtPath,
                                                     MacExternalTranscriptionConfig config, long timeoutSeconds)
             throws IOException, InterruptedException, RuntimeException {

        Objects.requireNonNull(modelPath); Objects.requireNonNull(audioPath);
        Objects.requireNonNull(outputSrtPath); Objects.requireNonNull(config);

        // --- 构建命令行 (macOS 版本) ---
        List<String> command = new ArrayList<>();
        command.add(WHISPER_CLI_EXECUTABLE_PATH);
        command.add(ARG_MODEL); command.add(modelPath.toAbsolutePath().toString());
        command.add(ARG_THREADS); command.add(String.valueOf(config.getEffectiveThreads()));

        String lang = config.getEffectiveLanguageForCli(); // 获取用于 CLI 的语言参数
        if (lang != null) { // 只有非 null 时才添加 -l
            command.add(ARG_LANGUAGE); command.add(lang);
        } else {
            log.info("语言配置为 null，将使用 CLI 默认语言 (en)。");
        }

        if (config.getEffectiveTranslate()) command.add(ARG_TRANSLATE);
        if (config.getEffectivePrintProgress()) command.add(ARG_PRINT_PROGRESS);
        if (config.getEffectiveDisableGpu()) command.add(ARG_NO_GPU);

        command.add(ARG_INPUT_FILE); command.add(audioPath.toAbsolutePath().toString());

        log.info("准备执行外部 Whisper CLI 命令 (macOS):");
        command.forEach(arg -> log.info("  {}", arg));

        // --- 执行进程 ---
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = null;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        StringBuilder processOutput = new StringBuilder();

        try {
            process = processBuilder.start();
            final Process pFinal = process;
            executor.submit(() -> readProcessOutput(pFinal, processOutput));

            log.info("外部 Whisper CLI 已启动 (PID: {}), 等待完成...", process.pid());
            boolean finished = waitForProcess(process, timeoutSeconds);
            executor.shutdown();
            try { if (!executor.awaitTermination(5, TimeUnit.SECONDS)) executor.shutdownNow(); }
            catch (InterruptedException ex) { executor.shutdownNow(); Thread.currentThread().interrupt(); }

            if (!finished) {
                log.error("外部 Whisper CLI 执行超时！PID: {}", process.pid());
                if (process != null) process.destroyForcibly();
                log.error("强制终止。进程部分输出:\n{}", getOutputExcerpt(processOutput));
                return null;
            }

            int exitCode = process.exitValue();
            log.info("外部 Whisper CLI 已结束 (PID: {}), 退出码: {}", process.pid(), exitCode);
            if (log.isDebugEnabled()) { log.debug("外部 Whisper CLI 完整输出 (PID: {}):\n{}", process.pid(), processOutput); }

            // --- 处理结果 ---
            if (exitCode == 0) {
                if (processOutput.toString().toLowerCase().contains("error:")) {
                     log.error("CLI (PID: {}) 退出码 0 但输出含错误:\n{}", process.pid(), getOutputExcerpt(processOutput));
                     return null;
                }
                boolean parseSuccess = parseAndWriteSrtFromOutput(processOutput.toString(), outputSrtPath);
                if (parseSuccess) {
                    log.info("成功为 PID {} 生成 SRT 文件: {}", process.pid(), outputSrtPath);
                    return outputSrtPath;
                } else {
                    log.error("CLI (PID: {}) 退出码 0 但无法解析/写入 SRT {}", process.pid(), outputSrtPath);
                    return null;
                }
            } else {
                log.error("CLI (PID: {}) 执行失败，退出码: {}。检查日志了解详情。", process.pid(), exitCode);
                return null;
            }
        } finally {
            if (process != null && process.isAlive()) { process.destroyForcibly(); log.warn("强制销毁进程 (PID: {})", process.pid());}
            if (!executor.isTerminated()) { executor.shutdownNow(); }
        }
    }

    /** 辅助方法：读取进程输出流 */
    private static void readProcessOutput(Process process, StringBuilder output) {
        // 在 macOS 上，系统的默认编码通常是 UTF-8，但显式指定更安全
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                log.info("Whisper CLI (PID: {}): {}", process.pid(), line);
            }
        } catch (IOException e) {
            if (process.isAlive()) log.error("读取 CLI (PID: {}) 输出时出错: {}", process.pid(), e.getMessage());
            else log.trace("读取进程 (PID: {}) 输出流时IO异常，进程或已结束。", process.pid());
        }
    }

    /** 辅助方法：等待进程结束 */
    private static boolean waitForProcess(Process process, long timeoutSeconds) throws InterruptedException {
        if (timeoutSeconds > 0) return process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        else { process.waitFor(); return true; }
    }

     /** 辅助方法：获取输出摘要 */
    private static String getOutputExcerpt(StringBuilder output) {
         int maxLength = 2000;
         return output.length() <= maxLength ? output.toString() : output.substring(0, maxLength) + "\n... [输出过长，已截断]";
     }

    // --- SRT 解析和写入方法 (保持不变) ---
    private static boolean parseAndWriteSrtFromOutput(String cliOutput, Path srtPath) throws IOException {
        List<String> srtContent = new ArrayList<>();
        Pattern timePattern = Pattern.compile("^\\s*\\[(\\d{2}:\\d{2}:\\d{2}\\.\\d{3}) --> (\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\]\\s*(.*)");
        Pattern textPattern = Pattern.compile("^\\s*\\S+.*");
        String[] lines = cliOutput.split("\\r?\\n");
        int segmentIndex = 1;
        String currentTimeLine = null;
        for (String line : lines) {
            Matcher timeMatcher = timePattern.matcher(line);
            if (timeMatcher.matches()) {
                currentTimeLine = timeMatcher.group(1) + " --> " + timeMatcher.group(2);
                String textAfter = timeMatcher.group(3).trim();
                if (!textAfter.isEmpty() && !isLikelyIgnoredLine(textAfter)) { // 检查后面文本是否有效
                    srtContent.add(String.valueOf(segmentIndex++)); srtContent.add(currentTimeLine); srtContent.add(textAfter); srtContent.add(""); currentTimeLine = null;
                }
                 // 如果时间戳后无有效文本，则等待下一行
            } else if (currentTimeLine != null && textPattern.matcher(line).matches()) {
                String text = line.trim();
                if (!isLikelyIgnoredLine(text)) {
                    srtContent.add(String.valueOf(segmentIndex++)); srtContent.add(currentTimeLine); srtContent.add(text); srtContent.add(""); currentTimeLine = null;
                } else { log.trace("忽略可能的非字幕行: {}", line); }
            } else { currentTimeLine = null; }
        }
        if (srtContent.isEmpty()) { log.warn("未能从 CLI 输出中解析出有效的 SRT 片段。"); return false; }
        log.info("从 CLI 输出中解析出 {} 个 SRT 片段，写入文件: {}", segmentIndex - 1, srtPath);
        try (BufferedWriter writer = Files.newBufferedWriter(srtPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String srtLine : srtContent) { writer.write(srtLine); writer.newLine(); }
        }
        return Files.exists(srtPath) && Files.size(srtPath) > 0;
    }

    /** 辅助方法：判断是否可能是应忽略的行（统计/进度等） */
    private static boolean isLikelyIgnoredLine(String line) {
        String lowerLine = line.toLowerCase();
        // 增加对 whisper-cli 进度条格式的过滤 (例如 whisper_print_progress_bar)
        // 以及其他可能的非字幕输出
        return lowerLine.startsWith("whisper_print_progress") || lowerLine.startsWith("whisper_") // 过滤内部函数名
            || lowerLine.startsWith("system_info:") || lowerLine.startsWith("main:") // 过滤程序信息
            || lowerLine.startsWith("log_mel") || lowerLine.contains("log mel filter") // 过滤调试信息
            || lowerLine.startsWith("cpu tasks") || lowerLine.startsWith("gpu tasks")
            || lowerLine.startsWith("compute shaders") || lowerLine.startsWith("memory usage")
            || lowerLine.contains(" calls, ") || lowerLine.contains(" average")
            || lowerLine.contains(" vram") || lowerLine.contains(" ram")
            || lowerLine.matches("^\\s*(loadmodel|runcomplete|run|callbacks|spectrogram|sample|encode|decode|decodestep)\\s+.*");
    }


    /**
     * 【辅助方法】根据输入音频路径和配置确定输出 SRT 文件路径 (macOS 版本)。
     */
    private static Path determineSrtOutputPath(Path audioPath, MacExternalTranscriptionConfig config) {
        Path parentDir = audioPath.getParent();
        if (parentDir == null) parentDir = Paths.get("").toAbsolutePath();
        String baseName = audioPath.getFileName().toString().replaceAll("\\.[^.]*$", "");
        String langSuffix;
        if (config.getEffectiveTranslate()) langSuffix = "en";
        else { String lang = config.getLanguage(); langSuffix = (lang == null || "auto".equalsIgnoreCase(lang)) ? "auto" : lang.toLowerCase(Locale.ROOT); }
        String srtFileName = baseName + "." + langSuffix + ".srt";
        return parentDir.resolve(srtFileName);
    }


    // --- 示例用法 Main 方法 ---
    public static void main(String[] args) {
        log.info("--- 测试调用外部 Whisper CLI Util (macOS 版本) ---");

        // --- 【必须修改】设置您的 Mac 上的音频文件路径 ---
        String audioFilePath1 = "/Volumes/System/0000/bgmusic/eng.wav"; // <--- 修改这里
        String audioFilePath2 = "//Volumes/System/0000/bgmusic/chn.wav"; // <--- 修改这里
        // --- 路径设置结束 ---

        log.info("默认模型路径: {}", DEFAULT_MODEL_PATH);
        log.info("测试音频1: {}", audioFilePath1);
        log.info("测试音频2: {}", audioFilePath2);
        log.info("CLI 程序路径: {}", WHISPER_CLI_EXECUTABLE_PATH);


        // --- 示例 1：使用最简单的接口 (String 路径，全默认 auto) ---
        log.info("\n--- 示例 1: 调用 transcribeToSrt(String audioFilePath) ---");
        try {
             Path audio1 = Paths.get(audioFilePath1);
             if (!Files.exists(audio1)) { log.error("示例 1 音频文件未找到: {}", audio1); throw new IOException("文件不存在");}
            long startTime = System.currentTimeMillis();
            Path resultPath1 = transcribeToSrt(audioFilePath1);
            long endTime = System.currentTimeMillis();
            if (resultPath1 != null) { log.info("示例 1 成功！SRT: {}", resultPath1); log.info("耗时: {} ms", endTime - startTime); }
            else { log.error("示例 1 失败。"); }
        } catch (Exception e) { log.error("示例 1 执行异常: {}", e.getMessage(), e); }


        // --- 示例 2：带配置接口 (指定中文，启用进度) ---
        log.info("\n--- 示例 2: 调用 transcribeToSrt(Path audioPath, Config) ---");
        try {
            Path audioPath2 = Paths.get(audioFilePath2);
            if (!Files.exists(audioPath2)) { log.error("示例 2 音频文件未找到: {}", audioPath2); throw new IOException("文件不存在");}
            MacExternalTranscriptionConfig config2 = new MacExternalTranscriptionConfig()
                    .setLanguage("zh")
                    .setPrintProgress(true); // 让 CLI 打印进度
            log.info("示例 2 配置: 语言={}, 线程={}, 打印进度={}", config2.getEffectiveLanguageForCli(), config2.getEffectiveThreads(), config2.getEffectivePrintProgress());
            long startTime = System.currentTimeMillis();
            Path resultPath2 = transcribeToSrt(audioPath2, config2);
            long endTime = System.currentTimeMillis();
            if (resultPath2 != null) { log.info("示例 2 成功！SRT: {}", resultPath2); log.info("耗时: {} ms", endTime - startTime); }
            else { log.error("示例 2 失败。"); }
        } catch (Exception e) { log.error("示例 2 执行异常: {}", e.getMessage(), e); }


        // --- 示例 3：全功能接口 (翻译，禁用GPU，自定义输出) ---
         log.info("\n--- 示例 3: 调用 transcribeToSrtFullyConfigurable(...) ---");
         try {
             Path modelPath3 = Paths.get(DEFAULT_MODEL_PATH);
             Path audioPath3 = Paths.get(audioFilePath2); // 用中文音频翻译
             if (!Files.exists(audioPath3)) { log.error("示例 3 音频文件未找到: {}", audioPath3); throw new IOException("文件不存在");}
             // 自定义输出路径
             Path outputSrtPath3 = audioPath3.resolveSibling(audioPath3.getFileName().toString().replaceAll("\\.[^.]*$", "") + ".translated_cpu.en.srt");
             MacExternalTranscriptionConfig config3 = new MacExternalTranscriptionConfig()
                     .setLanguage("zh") // 源语言
                     .setTranslate(true) // 翻译
                     .setDisableGpu(true) // 禁用 GPU
                     .setThreads(4);
             long timeout3 = 2400;
             log.info("示例 3 配置: 输出={}, 语言={}, 翻译={}, 线程={}, 禁用GPU={}, 超时={}", outputSrtPath3.getFileName(), config3.getLanguage(), config3.getEffectiveTranslate(), config3.getEffectiveThreads(), config3.getEffectiveDisableGpu(), timeout3);
             long startTime = System.currentTimeMillis();
             Path resultPath3 = transcribeToSrtFullyConfigurable(modelPath3, audioPath3, outputSrtPath3, config3, timeout3);
             long endTime = System.currentTimeMillis();
             if (resultPath3 != null) { log.info("示例 3 成功！SRT: {}", resultPath3); log.info("耗时: {} ms", endTime - startTime); }
             else { log.error("示例 3 失败。"); }
         } catch (Exception e) { log.error("示例 3 执行异常: {}", e.getMessage(), e); }


        log.info("\n--- 所有测试示例执行完毕 ---");
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
