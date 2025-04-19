package com.coderdream.util.whisper; // 请确保包名正确

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter; // 导入 BufferedWriter
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
import java.util.regex.Matcher; // 导入 Matcher
import java.util.regex.Pattern; // 导入 Pattern

/**
 * 使用外部 Whisper CLI 进程 (main.exe from Const-me/Whisper)
 * 来执行高性能的语音转文本。
 * <p>
 * 此版本通过捕获 main.exe 的标准输出并解析，来生成 SRT 文件，
 * 因为发现 -osrt 参数仅在控制台打印，不保证生成文件。
 */
@Slf4j
public class ExternalWhisperCliUtil04 {

    // --- 配置区域 ---
    private static final String WHISPER_CLI_EXECUTABLE_PATH = "D:\\00_Green\\WhisperDesktop\\cli\\main.exe";
    private static final String ARG_MODEL = "-m";
    private static final String ARG_INPUT_FILE = "-f"; // 保留 -f，明确指定输入文件
    // private static final String ARG_OUTPUT_SRT = "-osrt"; // 不再需要 osrt 开关
    private static final String ARG_LANGUAGE = "-l";
    private static final String ARG_TRANSLATE = "-tr";
    private static final String ARG_THREADS = "-t";
    // 添加一个参数来禁止 main.exe 打印颜色代码，以免干扰解析
    private static final String ARG_NO_COLORS = "-nc";
    // --- 配置结束 ---

    public static class ExternalTranscriptionConfig {
        private String language = null;
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

    /**
     * 调用外部 Whisper CLI (main.exe)，捕获其标准输出，解析并生成 SRT 字幕文件。
     *
     * @param modelPath      Whisper 模型文件的路径。
     * @param audioPath      要转录的音频文件的路径。
     * @param outputSrtPath  期望生成的 SRT 文件的路径 (由本方法创建和写入)。
     * @param config         转录配置。
     * @param timeoutSeconds 进程执行的超时时间（秒）。设置为 0 或负数表示不超时。
     * @return 如果成功解析并写入了非空 SRT 文件，返回该 SRT 文件的 Path；否则返回 null。
     * @throws IOException          如果启动进程或文件操作出错。
     * @throws InterruptedException 如果等待进程时线程被中断。
     * @throws RuntimeException     如果配置错误或外部进程返回错误。
     */
    public static Path transcribeToSrtExternalCli(Path modelPath, Path audioPath, Path outputSrtPath, // 需要指定输出路径
                                                 ExternalTranscriptionConfig config, long timeoutSeconds)
            throws IOException, InterruptedException, RuntimeException {

        Objects.requireNonNull(modelPath, "模型路径不能为空");
        Objects.requireNonNull(audioPath, "音频路径不能为空");
        Objects.requireNonNull(outputSrtPath, "输出 SRT 路径不能为空"); // 现在需要这个参数
        Objects.requireNonNull(config, "配置不能为空");
        if (!Files.exists(modelPath)) throw new IOException("模型文件未找到: " + modelPath);
        if (!Files.exists(audioPath)) throw new IOException("音频文件未找到: " + audioPath);

        File executable = new File(WHISPER_CLI_EXECUTABLE_PATH);
        if (!executable.exists() || !executable.isFile()) {
             log.error("配置错误：外部 Whisper CLI 未找到或不是文件: {}", WHISPER_CLI_EXECUTABLE_PATH);
             throw new IOException("指定的外部 Whisper CLI 无效: " + WHISPER_CLI_EXECUTABLE_PATH);
        }
        log.info("外部 Whisper CLI 路径验证通过: {}", WHISPER_CLI_EXECUTABLE_PATH);

        // --- 构建命令行参数列表 ---
        List<String> command = new ArrayList<>();
        command.add(WHISPER_CLI_EXECUTABLE_PATH);

        command.add(ARG_MODEL); command.add(modelPath.toAbsolutePath().toString());
        command.add(ARG_THREADS); command.add(String.valueOf(config.getThreads()));
        if (config.getLanguage() != null && !config.getLanguage().trim().isEmpty()) {
             if (!"auto".equalsIgnoreCase(config.getLanguage())) { // 再次确认非 auto
                 command.add(ARG_LANGUAGE); command.add(config.getLanguage());
             } else {
                 log.warn("跳过无效的 'auto' 语言参数。");
             }
        } else {
             log.info("未指定语言，将使用 CLI 默认语言。");
        }
        if (config.isTranslate()) {
            command.add(ARG_TRANSLATE);
        }
        // **修改点：不再添加 -osrt 开关**
        // command.add(ARG_OUTPUT_SRT);
        // **修改点：添加 -nc (no-colors) 开关，防止颜色代码干扰解析**
        command.add(ARG_NO_COLORS);
        // **修改点：明确使用 -f 指定输入文件**
        command.add(ARG_INPUT_FILE); command.add(audioPath.toAbsolutePath().toString());


        log.info("准备执行外部 Whisper CLI 命令 (将捕获并解析输出):");
        command.forEach(log::info);

        // --- 执行外部进程 ---
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = null;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        StringBuilder processOutput = new StringBuilder();

        try {
            process = processBuilder.start();
            final Process pFinal = process;

            executor.submit(() -> {
                 // 使用 UTF-8 读取，如果 CLI 输出是其他编码导致乱码，可能需要调整
                 try (BufferedReader reader = new BufferedReader(new InputStreamReader(pFinal.getInputStream(), StandardCharsets.UTF_8))) {
                     String line;
                     while ((line = reader.readLine()) != null) {
                         processOutput.append(line).append(System.lineSeparator());
                         // 不再实时打印每一行，避免日志过多，只在 DEBUG 级别打印完整输出
                         log.trace("Whisper CLI Raw: {}", line);
                     }
                 } catch (IOException e) { log.error("读取外部 Whisper CLI 输出时出错: {}", e.getMessage()); }
            });

            log.info("外部 Whisper CLI 已启动，等待其完成 (超时: {} 秒)...", timeoutSeconds > 0 ? timeoutSeconds : "无");

            boolean finished;
            if (timeoutSeconds > 0) { finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS); }
            else { process.waitFor(); finished = true; }

            // 等待异步读取完成
            executor.shutdown();
            try { if (!executor.awaitTermination(5, TimeUnit.SECONDS)) executor.shutdownNow(); }
            catch (InterruptedException ex) { executor.shutdownNow(); Thread.currentThread().interrupt(); }

            if (!finished) {
                log.error("外部 Whisper CLI 执行超时 (超过 {} 秒)！", timeoutSeconds);
                if (process != null) process.destroyForcibly(); // 确认 process 非 null
                log.error("强制终止了超时的外部 Whisper CLI。进程部分输出:\n{}", processOutput.substring(0, Math.min(processOutput.length(), 2000))); // 只打印部分输出
                return null;
            }

            int exitCode = process.exitValue();
            log.info("外部 Whisper CLI 已结束，退出码: {}", exitCode);
            // 在 DEBUG 级别记录完整输出，方便排查
            if (log.isDebugEnabled()) {
                log.debug("外部 Whisper CLI 完整输出:\n{}", processOutput);
            }

            // --- 检查结果 ---
            if (exitCode == 0) {
                // **修改点：调用解析方法写入文件**
                boolean parseSuccess = parseAndWriteSrtFromOutput(processOutput.toString(), outputSrtPath);
                if (parseSuccess) {
                    return outputSrtPath; // 返回指定的输出路径表示成功
                } else {
                    log.error("CLI 退出码为 0，但无法从输出中解析有效内容或写入 SRT 文件。");
                    return null; // 返回 null 表示失败
                }
            } else {
                log.error("CLI 执行失败，退出码: {}。请查看上面的日志或 DEBUG 级别的完整看来了解详情。", exitCode);
                return null; // 返回 null 表示失败
            }

        } finally {
             if (process != null && process.isAlive()) process.destroyForcibly();
             if (!executor.isTerminated()) executor.shutdownNow();
        }
    }

    /**
     * 从 main.exe 的标准输出中解析 SRT 格式的行并写入文件。
     *
     * @param cliOutput     main.exe 的完整标准输出字符串。
     * @param srtPath       要写入的 SRT 文件的 Path。
     * @return 如果成功解析并写入了非空文件，返回 true；否则返回 false。
     * @throws IOException 如果写入文件时发生错误。
     */
    private static boolean parseAndWriteSrtFromOutput(String cliOutput, Path srtPath) throws IOException {
        List<String> srtContent = new ArrayList<>();
        // 正则表达式匹配 SRT 时间戳行: [HH:MM:SS.ms --> HH:MM:SS.ms]
        // 改进：允许时间戳前后有空格，并捕获时间戳后面的文本（如果存在）
        Pattern timePattern = Pattern.compile("^\\s*\\[(\\d{2}:\\d{2}:\\d{2}\\.\\d{3}) --> (\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\]\\s*(.*)");
        // 用于匹配纯文本行的简单检查（避免匹配空行或纯粹的统计行）
        Pattern textPattern = Pattern.compile("^\\s*\\S+.*"); // 至少包含一个非空白字符

        String[] lines = cliOutput.split("\\r?\\n"); // 按行分割
        int segmentIndex = 1;
        String currentTimeLine = null; // 存储最近找到的时间戳行内容

        for (String line : lines) {
            Matcher timeMatcher = timePattern.matcher(line);
            if (timeMatcher.matches()) {
                // 找到时间戳行
                currentTimeLine = timeMatcher.group(1) + " --> " + timeMatcher.group(2);
                String textAfterTimestamp = timeMatcher.group(3).trim();
                if (!textAfterTimestamp.isEmpty()) {
                     // 时间戳行后面直接有文本内容
                     srtContent.add(String.valueOf(segmentIndex++));
                     srtContent.add(currentTimeLine);
                     srtContent.add(textAfterTimestamp);
                     srtContent.add("");
                     currentTimeLine = null; // 处理完毕
                }
                // 如果时间戳后无文本，则等待下一行
            } else if (currentTimeLine != null && textPattern.matcher(line).matches()) {
                // 如果上一行是时间戳行，并且当前行是有效的文本行
                String text = line.trim();
                // 进一步过滤掉可能的统计信息行（根据日志示例添加）
                if (!text.startsWith("CPU Tasks") && !text.startsWith("GPU Tasks") && !text.startsWith("Compute Shaders") && !text.startsWith("Memory Usage") && !text.contains(" calls, ") && !text.contains(" average") && !text.contains(" VRAM") && !text.contains(" RAM")) {
                     srtContent.add(String.valueOf(segmentIndex++));
                     srtContent.add(currentTimeLine);
                     srtContent.add(text);
                     srtContent.add("");
                     currentTimeLine = null; // 处理完毕
                } else {
                     // 虽然是文本行，但看起来像统计信息，忽略
                     log.trace("忽略可能的统计行: {}", line);
                     // 保持 currentTimeLine 不变，可能后面还有文本行
                     // 或者，如果确定统计信息总是在时间戳之后，可以取消注释下一行来重置
                     // currentTimeLine = null;
                }

            } else {
                 // 不是时间戳行，也不是紧跟着时间戳行的文本行，重置期待状态
                 // （如果上一行是时间戳但这一行是空行或统计信息，也重置）
                 currentTimeLine = null;
            }
        }

        if (srtContent.isEmpty()) {
            log.warn("未能从 CLI 输出中解析出任何有效的 SRT 片段。");
            return false;
        }

        log.info("从 CLI 输出中解析出 {} 个 SRT 片段，准备写入文件: {}", segmentIndex - 1, srtPath);
        // 使用 try-with-resources 确保 writer 关闭
        try (BufferedWriter writer = Files.newBufferedWriter(srtPath, StandardCharsets.UTF_8,
                                                             StandardOpenOption.CREATE,
                                                             StandardOpenOption.WRITE,
                                                             StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String srtLine : srtContent) {
                writer.write(srtLine);
                writer.newLine();
            }
        }

        // 最后检查文件是否成功写入且非空
        if (Files.exists(srtPath) && Files.size(srtPath) > 0) {
             log.info("成功将解析出的内容写入 SRT 文件。");
             return true;
        } else {
             log.error("尝试写入 SRT 文件后，文件不存在或为空: {}", srtPath);
             return false;
        }
    }


    // --- 示例用法 Main 方法 ---
    public static void main(String[] args) {
        log.info("--- 测试调用外部 Whisper CLI (main.exe) - 解析输出版本 ---");
        String modelFilePath = "D:\\00_Green\\WhisperDesktop\\models\\ggml-model-whisper-medium.bin";
        String audioFilePath = "D:\\14_LearnEnglish\\6MinuteEnglish\\2017\\171005\\171005_adult_exercise.mp3"; // 使用新的测试文件

        // --- 自动生成输出 SRT 文件路径 ---
        Path audioPathObj = Paths.get(audioFilePath);
        String outputFileNameBase = audioPathObj.getFileName().toString().replaceAll("\\.[^.]*$", "");
        String languageCodeForFile = "en"; // 假设我们知道是英文，或者根据 config 决定
        String outputSrtFileName = outputFileNameBase + "." + languageCodeForFile + ".srt";
        Path outputSrtPathObj = audioPathObj.resolveSibling(outputSrtFileName);
        String outputSrtFilePath = outputSrtPathObj.toString();
        // --- 配置结束 ---

        log.info("模型文件路径: {}", modelFilePath);
        log.info("音频文件路径: {}", audioFilePath);
        log.info("输出 SRT 路径: {}", outputSrtFilePath); // 现在这个路径是 Java 程序写入的目标
        log.info("CLI 程序路径: {}", WHISPER_CLI_EXECUTABLE_PATH);

        Path modelPath = Paths.get(modelFilePath);
        Path audioPath = Paths.get(audioFilePath);
        Path outputSrtPath = Paths.get(outputSrtFilePath); // Java 程序写入的目标路径

        if (!Files.exists(modelPath)) { log.error("模型文件未找到"); return; }
        if (!Files.exists(audioPath)) { log.error("音频文件未找到"); return; }
        try { Files.createDirectories(outputSrtPath.getParent()); } catch (IOException e) { log.error("创建目录失败"); return; }
        log.info("文件和目录检查通过。");

        ExternalTranscriptionConfig config = new ExternalTranscriptionConfig()
                .setLanguage(languageCodeForFile) // 明确设为英文
                .setTranslate(false)
                .setThreads(8);

        long timeoutInSeconds = 1800;

        log.info("准备调用外部 Whisper CLI (语言: {}, 翻译: {}, 线程: {}, 超时: {})...",
                 config.getLanguage() == null ? "[默认]" : config.getLanguage(),
                 config.isTranslate(), config.getThreads(), timeoutInSeconds > 0 ? timeoutInSeconds : "无");

        try {
            long startTime = System.currentTimeMillis();
            // 调用新逻辑，传入 outputSrtPath 作为写入目标
            Path generatedSrtPath = transcribeToSrtExternalCli(modelPath, audioPath, outputSrtPath, config, timeoutInSeconds);
            long endTime = System.currentTimeMillis();
            double durationMinutes = (endTime - startTime) / 60000.0;
            if (generatedSrtPath != null) {
                 log.info("外部 CLI 调用成功，并成功解析输出写入 SRT 文件: {}", generatedSrtPath);
                 log.info("总耗时: {} ms (~= {} 分钟)", (endTime - startTime), String.format("%.2f", durationMinutes));
                 // ... (预览代码不变) ...
                  try {
                     List<String> lines = Files.readAllLines(generatedSrtPath, StandardCharsets.UTF_8);
                     log.info("生成的 SRT 文件 '{}' 前 {} 行预览:", generatedSrtPath.getFileName(), Math.min(lines.size(), 10));
                     for(int i = 0; i < Math.min(lines.size(), 10); i++) log.info("  {}", lines.get(i));
                 } catch (IOException readEx) { log.warn("读取生成的 SRT 文件预览时出错: {}", readEx.getMessage()); }
            } else {
                 log.error("外部 CLI 调用失败、超时或未能成功解析/写入 SRT 文件。总耗时: {} ms (~= {} 分钟)", (endTime - startTime), String.format("%.2f", durationMinutes));
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
