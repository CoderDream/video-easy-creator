package com.coderdream.util.whisper; // 请确保包名正确

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 使用外部 Whisper CLI 进程 (main.exe from Const-me/Whisper) 来执行高性能的语音转文本和 SRT 生成。
 * <p>
 * 提供多种调用接口，包括仅需音频文件路径字符串的简化接口。 所有公共方法内部处理异常并记录日志，返回 SRT 文件的绝对路径字符串或 null。
 */
@Slf4j
public class WhisperUtil03 {

    // --- ==================== 配置与默认值区域 ==================== ---

    /**
     * 【确认】外部 Whisper CLI 可执行文件的完整路径。
     * !!! 请务必修改为你的实际路径 !!!
     */
    private static final String WHISPER_CLI_EXECUTABLE_PATH = "D:\\00_Green\\WhisperDesktop\\cli\\main.exe";

    /**
     * 【确认】默认使用的 Whisper 模型文件的完整路径。
     * !!! 请务必修改为你的实际路径 !!!
     */
    private static final String DEFAULT_MODEL_PATH = "D:\\00_Green\\WhisperDesktop\\models\\ggml-model-whisper-medium.bin";

    /**
     * 【确认】命令行参数常量 (根据 main.exe --help 结果)。
     */
    private static final String ARG_MODEL = "-m";
    private static final String ARG_INPUT_FILE = "-f"; // 使用 -f 明确指定输入
    private static final String ARG_LANGUAGE = "-l";
    private static final String ARG_TRANSLATE = "-tr";
    private static final String ARG_THREADS = "-t";
    private static final String ARG_NO_COLORS = "-nc"; // 禁止颜色代码

    /**
     * 默认使用的 CPU 线程数。
     */
    private static final int DEFAULT_THREADS = 8; // 可根据普遍测试调整

    /**
     * 默认的进程执行超时时间（秒）。0 或负数表示不超时。
     */
    private static final long DEFAULT_TIMEOUT_SECONDS = 1800; // 默认 30 分钟

    // --- ================= 配置区域结束 ================== ---

    // 静态初始化块
    static {
        try {
            Path defaultModel = Paths.get(DEFAULT_MODEL_PATH);
            if (!Files.exists(defaultModel)) {
                log.warn("警告：默认模型文件路径 '{}' 不存在！使用简化接口时可能会失败。",
                        DEFAULT_MODEL_PATH);
            }
            checkCliExecutable();
        } catch (InvalidPathException e) {
            log.error("配置错误：默认模型路径 '{}' 无效。", DEFAULT_MODEL_PATH, e);
            throw new RuntimeException("默认模型路径配置无效", e);
        } catch (RuntimeException e) {
            log.error("初始化检查失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 检查 CLI 可执行文件是否存在且有效。
     */
    private static void checkCliExecutable() {
        File executable = new File(WHISPER_CLI_EXECUTABLE_PATH);
        if (!executable.exists() || !executable.isFile()) {
            String errorMsg = "配置错误：外部 Whisper CLI 未找到或不是文件: "
                    + WHISPER_CLI_EXECUTABLE_PATH;
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        log.info("外部 Whisper CLI 路径验证通过: {}", WHISPER_CLI_EXECUTABLE_PATH);
    }


    /**
     * 外部转录配置类。
     */
    @Getter
    public static class ExternalTranscriptionConfig {

        private String language = null; // null 表示使用 CLI 默认语言
        private Boolean translate = null; // null 表示使用 CLI 默认值 (false)
        private Integer threads = null; // null 表示使用 DEFAULT_THREADS

        public ExternalTranscriptionConfig setLanguage(String language) {
            if ("auto".equalsIgnoreCase(language)) {
                log.warn("CLI 不支持 'auto' 语言检测，将使用 CLI 默认语言或不传递 -l 参数。");
                this.language = null;
            } else {
                this.language =
                        (language != null && language.isBlank()) ? null : language;
            }
            return this;
        }

        public ExternalTranscriptionConfig setTranslate(Boolean translate) {
            this.translate = translate;
            return this;
        }

        public ExternalTranscriptionConfig setThreads(Integer threads) {
            this.threads = (threads != null && threads < 1) ? Integer.valueOf(1)
                    : threads;
            return this;
        }

        public int getEffectiveThreads() {
            return threads != null ? threads : DEFAULT_THREADS;
        }

        public boolean getEffectiveTranslate() {
            return translate != null && translate;
        }
    }

    // --- ==================== 公开 API 方法 ==================== ---

    /**
     * 【新增接口1】最简单的调用方式：转录指定音频文件为 SRT，使用所有默认设置。 SRT 文件将自动生成在音频文件旁边，文件名为
     * "音频文件名.srt"。 内部处理所有异常并记录日志。
     *
     * @param audioFilePath 要转录的音频文件的【完整路径字符串】。
     * @return 如果成功生成 SRT 文件，返回该文件的【绝对路径字符串】；否则返回 null。
     */
    public static String transcribeToSrt(String audioFilePath) {
        log.info(
                "收到简化接口调用请求 (transcribeToSrt(String))，使用默认设置。音频文件: {}",
                audioFilePath);
        Path audioPath = null;
        Path modelPath = null;
        Path outputSrtPath = null;

        try {
            if (audioFilePath == null || audioFilePath.isBlank()) {
                log.error("音频文件路径字符串不能为空。");
                return null;
            }
            audioPath = Paths.get(audioFilePath);
            if (!Files.exists(audioPath)) {
                log.error("音频文件未找到: {}", audioPath);
                return null;
            }
            if (!Files.isReadable(audioPath)) {
                log.error("音频文件不可读: {}", audioPath);
                return null;
            }

            modelPath = Paths.get(DEFAULT_MODEL_PATH);
            if (!Files.exists(modelPath)) {
                log.error(
                        "默认模型文件未找到: {}，请检查 DEFAULT_MODEL_PATH 配置或使用全参数接口指定模型。",
                        modelPath);
                return null;
            }
            if (!Files.isReadable(modelPath)) {
                log.error("默认模型文件不可读: {}", modelPath);
                return null;
            }

            String audioFileName = audioPath.getFileName().toString();
            String baseName = audioFileName;
            int dotIndex = audioFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                baseName = audioFileName.substring(0, dotIndex);
            } else if (dotIndex == 0) {
                log.warn("音频文件名 '{}' 以点开头，生成的 SRT 文件名可能不符合预期。",
                        audioFileName);
            }

            String srtFileName = baseName + ".srt";
            outputSrtPath = audioPath.resolveSibling(srtFileName);
            log.info("将使用默认模型 '{}'，自动生成 SRT 到 '{}'", DEFAULT_MODEL_PATH,
                    outputSrtPath);

            ExternalTranscriptionConfig defaultConfig = new ExternalTranscriptionConfig();

            return executeWhisperCliAndWriteSrt(modelPath, audioPath, outputSrtPath,
                    defaultConfig, DEFAULT_TIMEOUT_SECONDS);

        } catch (InvalidPathException e) {
            log.error("提供的音频文件路径 '{}' 无效: {}", audioFilePath,
                    e.getMessage(), e);
            return null;
        } catch (SecurityException e) {
            log.error(
                    "无权限访问文件或目录: 音频路径 '{}', 模型路径 '{}', 输出路径 '{}': {}",
                    audioPath, modelPath, outputSrtPath, e.getMessage(), e);
            return null;
        } catch (Exception e) { // 捕获其他意外运行时异常
            log.error("处理音频文件 '{}' 时发生意外错误: {}", audioFilePath,
                    e.getMessage(), e);
            return null;
        }
    }

    /**
     * 【新增接口2】带配置的调用方式：转录指定音频文件为 SRT，允许自定义语言、翻译、线程数等。 模型使用默认路径，SRT 文件自动生成在音频文件旁边。
     * 内部处理所有异常并记录日志。
     *
     * @param audioPath 要转录的音频文件的 Path 对象。
     * @param config    包含自定义设置的 ExternalTranscriptionConfig 对象。未设置的选项将使用默认值。
     * @return 如果成功生成 SRT 文件，返回该文件的【绝对路径字符串】；否则返回 null。
     */
    public static String transcribeToSrt(Path audioPath,
                                         ExternalTranscriptionConfig config) {
        log.info(
                "收到带配置接口调用请求 (transcribeToSrt(Path, Config))，使用默认模型和输出路径。音频文件: {}",
                audioPath);
        Path modelPath = null;
        Path outputSrtPath = null;

        try {
            Objects.requireNonNull(audioPath, "音频文件路径 (Path) 不能为空");
            Objects.requireNonNull(config,
                    "配置对象 (ExternalTranscriptionConfig) 不能为空");

            if (!Files.exists(audioPath)) {
                log.error("音频文件未找到: {}", audioPath);
                return null;
            }
            if (!Files.isReadable(audioPath)) {
                log.error("音频文件不可读: {}", audioPath);
                return null;
            }

            modelPath = Paths.get(DEFAULT_MODEL_PATH);
            if (!Files.exists(modelPath)) {
                log.error(
                        "默认模型文件未找到: {}，请检查 DEFAULT_MODEL_PATH 配置或使用全参数接口指定模型。",
                        modelPath);
                return null;
            }
            if (!Files.isReadable(modelPath)) {
                log.error("默认模型文件不可读: {}", modelPath);
                return null;
            }

            String audioFileName = audioPath.getFileName().toString();
            String baseName = audioFileName;
            int dotIndex = audioFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                baseName = audioFileName.substring(0, dotIndex);
            } else if (dotIndex == 0) {
                log.warn("音频文件名 '{}' 以点开头，生成的 SRT 文件名可能不符合预期。",
                        audioFileName);
            }
            String srtFileName = baseName + ".srt";
            outputSrtPath = audioPath.resolveSibling(srtFileName);
            log.info("将使用默认模型 '{}'，根据配置生成 SRT 到 '{}'",
                    DEFAULT_MODEL_PATH, outputSrtPath);

            return executeWhisperCliAndWriteSrt(modelPath, audioPath, outputSrtPath,
                    config, DEFAULT_TIMEOUT_SECONDS);

        } catch (NullPointerException e) { // 由 Objects.requireNonNull 抛出
            log.error("输入参数不能为空: {}", e.getMessage(), e);
            return null;
        } catch (InvalidPathException e) {
            log.error("默认模型路径 '{}' 无效: {}", DEFAULT_MODEL_PATH,
                    e.getMessage(), e);
            return null;
        } catch (SecurityException e) {
            log.error(
                    "无权限访问文件或目录: 音频路径 '{}', 模型路径 '{}', 输出路径 '{}': {}",
                    audioPath, modelPath, outputSrtPath, e.getMessage(), e);
            return null;
        } catch (Exception e) { // 捕获其他意外运行时异常
            log.error("处理音频文件 '{}' (带配置) 时发生意外错误: {}", audioPath,
                    e.getMessage(), e);
            return null;
        }
    }


    /**
     * 【全功能接口】允许完全控制所有参数。 调用外部 Whisper CLI (main.exe)，捕获其标准输出，解析并生成 SRT 字幕文件。
     * 内部处理所有异常并记录日志。
     *
     * @param modelPath      Whisper 模型文件的路径。
     * @param audioPath      要转录的音频文件的路径。
     * @param outputSrtPath  期望生成的 SRT 文件的路径 (由本方法创建和写入)。
     * @param config         转录配置。
     * @param timeoutSeconds 进程执行的超时时间（秒）。设置为 0 或负数表示不超时。
     * @return 如果成功解析并写入了非空 SRT 文件，返回该 SRT 文件的【绝对路径字符串】；否则返回 null。
     */
    public static String transcribeToSrtFullyConfigurable(Path modelPath,
                                                          Path audioPath, Path outputSrtPath,
                                                          ExternalTranscriptionConfig config, long timeoutSeconds) {
        log.info("收到全功能接口调用请求。模型: {}, 音频: {}, 输出: {}, 超时: {}s",
                modelPath, audioPath, outputSrtPath, timeoutSeconds);
        try {
            Objects.requireNonNull(modelPath, "模型文件路径 (modelPath) 不能为空");
            Objects.requireNonNull(audioPath, "音频文件路径 (audioPath) 不能为空");
            Objects.requireNonNull(outputSrtPath,
                    "输出 SRT 文件路径 (outputSrtPath) 不能为空");
            Objects.requireNonNull(config, "配置对象 (config) 不能为空");

            return executeWhisperCliAndWriteSrt(modelPath, audioPath, outputSrtPath,
                    config, timeoutSeconds);

        } catch (NullPointerException e) { // 由 Objects.requireNonNull 抛出
            log.error("全功能接口调用时，输入参数不能为空: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            // 捕获 executeWhisperCliAndWriteSrt 之外可能发生的任何意外错误
            log.error(
                    "执行全功能转录时发生顶层意外错误 (模型: {}, 音频: {}, 输出: {}): {}",
                    modelPath, audioPath, outputSrtPath, e.getMessage(), e);
            return null;
        }
    }

    // --- ================== 核心私有执行方法 ================== ---

    /**
     * 【核心实现】执行外部 Whisper CLI 进程，捕获输出，解析并写入 SRT 文件。 内部处理所有与进程执行、文件操作相关的异常。
     *
     * @param modelPath      模型路径。
     * @param audioPath      音频路径。
     * @param outputSrtPath  要写入的 SRT 文件路径。
     * @param config         转录配置。
     * @param timeoutSeconds 超时时间。
     * @return 成功则返回 outputSrtPath 的绝对路径字符串，失败或异常返回 null。
     */
    private static String executeWhisperCliAndWriteSrt(Path modelPath,
                                                       Path audioPath, Path outputSrtPath,
                                                       ExternalTranscriptionConfig config, long timeoutSeconds) {

        Process process = null;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        StringBuilder processOutput = new StringBuilder();
        List<String> command = new ArrayList<>();

        try {
            // --- 参数校验 ---
            Objects.requireNonNull(modelPath, "内部调用：模型路径不能为空");
            Objects.requireNonNull(audioPath, "内部调用：音频路径不能为空");
            Objects.requireNonNull(outputSrtPath, "内部调用：输出路径不能为空");
            Objects.requireNonNull(config, "内部调用：配置不能为空");

            if (!Files.exists(modelPath) || !Files.isReadable(modelPath)) {
                log.error("模型文件不存在或不可读: {}", modelPath);
                return null;
            }
            if (!Files.exists(audioPath) || !Files.isReadable(audioPath)) {
                log.error("音频文件不存在或不可读: {}", audioPath);
                return null;
            }
            Path outputDir = outputSrtPath.getParent();
            if (outputDir != null) {
                if (!Files.exists(outputDir)) {
                    try {
                        Files.createDirectories(outputDir);
                        log.info("输出目录不存在，已创建: {}", outputDir);
                    } catch (IOException | SecurityException e) {
                        log.error("无法创建输出目录 '{}': {}", outputDir, e.getMessage(),
                                e);
                        return null;
                    }
                }
                if (!Files.isWritable(outputDir)) {
                    log.error("输出目录不可写: {}", outputDir);
                    return null;
                }
            } else {
                log.warn("输出路径 '{}' 没有父目录，将尝试在当前工作目录写入。",
                        outputSrtPath);
            }

            // --- 构建命令行 ---
            command.add(WHISPER_CLI_EXECUTABLE_PATH);
            command.add(ARG_MODEL);
            command.add(modelPath.toAbsolutePath().toString());
            command.add(ARG_THREADS);
            command.add(String.valueOf(config.getEffectiveThreads()));

            String lang = config.getLanguage();
            if (lang != null && !lang.trim().isEmpty()) {
                command.add(ARG_LANGUAGE);
                command.add(lang);
            }
            if (config.getEffectiveTranslate()) {
                command.add(ARG_TRANSLATE);
            }
            command.add(ARG_NO_COLORS); // 避免颜色代码干扰解析
            command.add(ARG_INPUT_FILE);
            command.add(audioPath.toAbsolutePath().toString());

            log.info("准备执行核心 Whisper CLI 命令:");
            command.forEach(arg -> log.info("  {}", arg));

            // --- 执行进程 ---
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // 合并 stdout 和 stderr

            long startTime = System.currentTimeMillis();
            process = processBuilder.start();
            final Process pFinal = process;
            final long processPid = process.pid();

            // 异步读取输出
            executor.submit(
                    () -> readProcessOutput(pFinal, processOutput, processPid));

            log.info("外部 Whisper CLI 已启动 (PID: {}), 等待完成...", processPid);

            boolean finished = waitForProcess(process, timeoutSeconds);

            if (!finished) {
                log.error("外部 Whisper CLI (PID: {}) 执行超时 (超过 {} 秒)！",
                        processPid, timeoutSeconds);
                if (process.isAlive()) {
                    process.destroyForcibly();
                    log.info("强制终止了超时的外部 Whisper CLI (PID: {})。", processPid);
                }
                log.error("进程部分输出 (PID: {}):\n{}", processPid,
                        getOutputExcerpt(processOutput));
                return null;
            }

            int exitCode = process.exitValue();
            long endTime = System.currentTimeMillis();
            log.info("外部 Whisper CLI (PID: {}) 已结束, 退出码: {}, 耗时: {} ms", processPid,
                    exitCode, (endTime - startTime));

            // 在检查退出码之前，确保输出读取线程已完成
            executor.shutdown(); // 发出关闭信号
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) { // 增加等待时间
                    log.warn("读取输出的线程 (PID: {}) 在关闭时超时 (10s)，尝试强制关闭。",
                            processPid);
                    executor.shutdownNow();
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        log.error("强制关闭输出读取线程 (PID: {}) 失败。", processPid);
                    }
                } else {
                    log.debug("输出读取线程 (PID: {}) 正常结束。", processPid);
                }
            } catch (InterruptedException ex) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                log.warn("等待输出读取线程 (PID: {}) 结束时被中断。", processPid);
            }

            if (log.isDebugEnabled()) {
                // 打印完整输出以供调试，只在 debug 级别
                log.debug("外部 Whisper CLI 完整输出 (PID: {}):\n{}", processPid,
                        processOutput.toString());
            }

            // --- 处理结果 ---
            if (exitCode == 0) {
                boolean parseSuccess = parseAndWriteSrtFromOutput(
                        processOutput.toString(), outputSrtPath, processPid);
                if (parseSuccess) {
                    log.info("成功为进程 (PID: {}) 生成 SRT 文件: {}", processPid,
                            outputSrtPath.toAbsolutePath());
                    return outputSrtPath.toAbsolutePath().toString();
                } else {
                    log.error(
                            "CLI (PID: {}) 退出码为 0，但无法从输出中解析有效内容或写入 SRT 文件 {}",
                            processPid, outputSrtPath);
                    // 同时记录部分输出，帮助排查解析问题
                    log.error("进程输出摘要 (PID: {}):\n{}", processPid,
                            getOutputExcerpt(processOutput));
                    return null;
                }
            } else {
                log.error("CLI (PID: {}) 执行失败，退出码: {}。输出摘要:\n{}", processPid,
                        exitCode, getOutputExcerpt(processOutput));
                return null;
            }

        } catch (IOException e) {
            log.error(
                    "执行 Whisper CLI 或处理文件时发生 IO 错误 (相关路径: {}, {}, {}): {}",
                    modelPath, audioPath, outputSrtPath, e.getMessage(), e);
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("等待 Whisper CLI 进程时线程被中断: {}", e.getMessage(), e);
            return null;
        } catch (SecurityException e) {
            log.error("执行 Whisper CLI 时权限不足 (命令: {}): {}",
                    String.join(" ", command), e.getMessage(), e);
            return null;
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("执行 Whisper CLI 时遇到无效参数或状态错误: {}", e.getMessage(),
                    e);
            return null;
        } catch (Exception e) {
            log.error("执行 Whisper CLI 时发生意外错误 (相关路径: {}, {}, {}): {}",
                    modelPath, audioPath, outputSrtPath, e.getMessage(), e);
            return null;
        } finally {
            // 确保进程被销毁（如果还在运行）
            if (process != null && process.isAlive()) {
                log.warn("进程 (PID: {}) 在方法结束时仍在运行，强制销毁。",
                        process.pid());
                process.destroyForcibly();
            }
            // 确保 ExecutorService 被关闭 (即使之前尝试过关闭)
            if (executor != null && !executor.isTerminated()) {
                executor.shutdownNow();
                log.warn("在 finally 块中强制关闭了未终止的 ExecutorService。");
            }
        }
    }

    /**
     * 辅助方法：读取进程输出流
     */
    private static void readProcessOutput(Process process, StringBuilder output,
                                          long pid) {
        // 确保使用正确的字符集，UTF-8 通常是安全的
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(),
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                // 仅在 TRACE 级别实时打印 CLI 输出，避免日志过于冗长
                log.trace("Whisper CLI Raw Output (PID: {}): {}", pid, line);
            }
        } catch (IOException e) {
            // 进程结束时读取流可能会失败，记录为 DEBUG 级别
            log.debug("读取外部 Whisper CLI (PID: {}) 输出时遇到 IO 异常 (可能是正常结束): {}",
                    pid, e.getMessage());
        } catch (Exception e) {
            log.error("读取进程 (PID: {}) 输出时发生意外错误: {}", pid,
                    e.getMessage(), e);
        }
        log.debug("输出读取线程 (PID: {}) 结束。", pid);
    }

    /**
     * 辅助方法：等待进程结束
     */
    private static boolean waitForProcess(Process process, long timeoutSeconds)
            throws InterruptedException {
        if (timeoutSeconds > 0) {
            log.debug("等待进程 PID: {} 结束，超时时间: {} 秒", process.pid(), timeoutSeconds);
            return process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        } else {
            log.debug("无限期等待进程 PID: {} 结束", process.pid());
            process.waitFor();
            return true;
        }
    }

    /**
     * 辅助方法：获取输出摘要
     */
    private static String getOutputExcerpt(StringBuilder output) {
        int maxLength = 3000; // 增加摘要长度以包含更多上下文
        if (output == null || output.length() == 0) {
            return "[无输出]";
        }
        String fullOutput = output.toString();
        if (fullOutput.length() <= maxLength) {
            return fullOutput;
        } else {
            // 显示开头和结尾可能更有用
            int halfLength = maxLength / 2;
            return fullOutput.substring(0, halfLength) +
                    "\n...\n[输出过长，已截断]\n...\n" +
                    fullOutput.substring(fullOutput.length() - halfLength);
        }
    }

    // --- SRT 解析和写入方法 ---

    /**
     * 解析 CLI 输出并写入 SRT 文件。
     * 【再次修改】处理时间戳同行文本中混杂统计标记的情况。
     *
     * @param cliOutput CLI 的标准输出内容。
     * @param srtPath   要写入的 SRT 文件路径。
     * @param pid       关联的进程 PID，用于日志记录。
     * @return 如果成功写入了非空 SRT 文件，返回 true；否则返回 false。
     */
    private static boolean parseAndWriteSrtFromOutput(String cliOutput,
                                                      Path srtPath, long pid) {
        List<String> srtContent = new ArrayList<>();
        Pattern timePattern;

        try {
            // 【保持】编译正则表达式，匹配时间戳行
            timePattern = Pattern.compile(
                    "^\\s*\\[(\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s*-->\\s*(\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\]\\s*(.*)");

            String[] lines = cliOutput.split("\\r?\\n"); // 按行分割
            int segmentIndex = 1;
            String currentTimeLine = null; // 当前SRT片段的时间行 "HH:MM:SS.ms --> HH:MM:SS.ms"
            StringBuilder currentTextAccumulator = new StringBuilder(); // 用于累积当前片段的文本

            log.debug("开始解析 PID: {} 的输出，共 {} 行", pid, lines.length);

            for (String line : lines) {
                Matcher timeMatcher = timePattern.matcher(line);

                if (timeMatcher.matches()) {
                    // --- 发现新的时间戳行 ---
                    log.trace("PID: {} 匹配到时间戳行: {}", pid, line);

                    // 1. 处理并写入 *上一个* 已经累积完成的片段 (如果存在)
                    if (currentTimeLine != null && currentTextAccumulator.length() > 0) {
                        String finalizedText = currentTextAccumulator.toString().trim();
                        // 再次检查最终累积的文本是否意外地变成了统计行
                        if (!finalizedText.isEmpty() && !isLikelyStatsLine(finalizedText)) {
                            srtContent.add(String.valueOf(segmentIndex++));
                            srtContent.add(currentTimeLine);
                            srtContent.add(finalizedText);
                            srtContent.add(""); // SRT段之间的空行
                            log.trace("PID: {} 添加上一个片段: Index={}, Time={}, Text='{}'", pid, segmentIndex - 1, currentTimeLine, finalizedText);
                        } else {
                            log.trace("PID: {} 忽略累积的统计行或空行: '{}'", pid, finalizedText);
                        }
                    } else if (currentTimeLine != null) {
                        log.trace("PID: {} 忽略上一个空片段 Time={}", pid, currentTimeLine);
                    }

                    // 2. 开始处理 *当前* 新的时间戳行
                    currentTimeLine = timeMatcher.group(1) + " --> " + timeMatcher.group(2);
                    currentTextAccumulator = new StringBuilder(); // 重置文本累积器

                    // 3. 【关键修改点】处理时间戳行 *本身* 可能附带的文本 (捕获组3)
                    String textOnTimeLineRaw = timeMatcher.group(3); // 获取原始文本，不立即 trim
                    String actualTextPart = textOnTimeLineRaw; // 假设整行都是有效文本

                    // --- 检测是否包含已知的统计标记 ---
                    // 定义一些明确的统计块开始标记 (大小写不敏感)
                    String[] statsMarkers = {"CPU TASKS", "GPU TASKS", "COMPUTE SHADERS", "MEMORY USAGE"};
                    int firstMarkerIndex = -1;
                    String foundMarker = null;

                    for (String marker : statsMarkers) {
                        // 使用 toUpperCase 避免大小写问题
                        int markerIndex = textOnTimeLineRaw.toUpperCase().indexOf(marker);
                        if (markerIndex != -1) {
                            // 如果找到标记，记录第一次出现的位置
                            if (firstMarkerIndex == -1 || markerIndex < firstMarkerIndex) {
                                firstMarkerIndex = markerIndex;
                                foundMarker = marker; // 记录找到的标记类型
                            }
                        }
                    }

                    if (firstMarkerIndex != -1) {
                        // 如果在时间戳行的文本中找到了统计标记
                        // 只取标记之前的部分作为潜在的有效文本
                        actualTextPart = textOnTimeLineRaw.substring(0, firstMarkerIndex).trim();
                        String statsPart = textOnTimeLineRaw.substring(firstMarkerIndex).trim();
                        log.trace("PID: {} 在时间戳行文本中发现统计标记 '{}'，分割: 有效文本='{}', 统计部分='{}'",
                                pid, foundMarker, actualTextPart, statsPart);
                        // 标记后的统计部分将被忽略 (因为 isLikelyStatsLine 会处理后续完整的统计行)
                    } else {
                        // 如果没有找到明确的统计标记，则认为整行（去除前后空格后）都是潜在有效文本
                        actualTextPart = textOnTimeLineRaw.trim();
                    }
                    // --- 检测结束 ---


                    // 现在，只处理分割出来的（或完整的） actualTextPart
                    if (!actualTextPart.isEmpty()) {
                        // 再次检查 actualTextPart 本身是否是干扰信息 (例如，它可能只包含 "[INAUDIBLE]")
                        if (!isLikelyStatsLine(actualTextPart)) {
                            currentTextAccumulator.append(actualTextPart);
                            log.trace("PID: {} 从时间戳行提取有效文本: '{}'", pid, actualTextPart);
                        } else {
                            // 如果分割出的部分本身也是干扰信息，则忽略
                            log.trace("PID: {} 忽略时间戳行上的统计/日志文本 (分割后检查): '{}'", pid, actualTextPart);
                        }
                    }
                    // 如果 actualTextPart 为空 (例如原文本只有 "CPU TASKS")，则什么也不做

                } else if (currentTimeLine != null) {
                    // --- 当前行 *不是* 时间戳行，但我们正处于一个片段内 (即 currentTimeLine 已设置) ---
                    // 这意味着此行可能是多行字幕的一部分，或者是干扰信息

                    String trimmedLine = line.trim(); // 去除前后空格

                    // 【保持过滤】只有当它不是空行，并且不是干扰信息时，才追加
                    if (!trimmedLine.isEmpty() && !isLikelyStatsLine(trimmedLine)) {
                        if (currentTextAccumulator.length() > 0) {
                            // 如果累积器中已有内容，则添加换行符以支持多行字幕
                            currentTextAccumulator.append(System.lineSeparator());
                        }
                        currentTextAccumulator.append(trimmedLine);
                        log.trace("PID: {} 追加多行文本: '{}'", pid, trimmedLine);
                    } else if (!trimmedLine.isEmpty()) {
                        // 如果是非空但被识别为干扰信息，记录日志
                        log.trace("PID: {} 忽略片段内的统计/日志行: '{}'", pid, trimmedLine);
                    }
                    // 空行直接被忽略

                } else {
                    // --- 当前行 *不是* 时间戳行，且 *不在* 任何片段内 (currentTimeLine == null) ---
                    // 这通常是程序启动时的日志信息，直接忽略
                    if (!line.trim().isEmpty()) { // 避免记录空行
                        log.trace("PID: {} 忽略非 SRT 内容行 (片段外): '{}'", pid, line);
                    }
                }
            }

            // --- 循环结束后，处理最后一个累积的片段 ---
            if (currentTimeLine != null && currentTextAccumulator.length() > 0) {
                String finalizedText = currentTextAccumulator.toString().trim();
                if (!finalizedText.isEmpty() && !isLikelyStatsLine(finalizedText)) {
                    srtContent.add(String.valueOf(segmentIndex++));
                    srtContent.add(currentTimeLine);
                    srtContent.add(finalizedText);
                    srtContent.add("");
                    log.trace("PID: {} 添加最后一个片段: Index={}, Time={}, Text='{}'", pid, segmentIndex - 1, currentTimeLine, finalizedText);
                } else {
                    log.trace("PID: {} 忽略最后一个累积的统计行或空行: '{}'", pid, finalizedText);
                }
            } else if (currentTimeLine != null) {
                log.trace("PID: {} 忽略最后一个空片段 Time={}", pid, currentTimeLine);
            }

            log.debug("PID: {} 解析完成，准备写入 {} 个 SRT 片段。", pid, (srtContent.size() / 4));

            // --- 写入文件 ---
            if (srtContent.isEmpty()) {
                // 如果解析后没有任何有效的 SRT 内容
                log.warn(
                        "未能从 CLI (PID: {}) 输出中解析出有效的 SRT 片段。请检查 DEBUG 级别的完整输出。输出摘要:\n{}", pid,
                        getOutputExcerpt(new StringBuilder(cliOutput)));
                return false;
            }

            log.info("从 CLI (PID: {}) 输出中解析出 {} 个 SRT 片段，准备写入文件: {}",
                    pid, (srtContent.size() / 4), srtPath); // 使用 srtContent.size() / 4 计算段落数

            // 使用 try-with-resources 确保 BufferedWriter 被关闭
            try (BufferedWriter writer = Files.newBufferedWriter(srtPath,
                    StandardCharsets.UTF_8, // 确保使用 UTF-8 编码写入 SRT
                    StandardOpenOption.CREATE, // 如果文件不存在则创建
                    StandardOpenOption.WRITE, // 以写入模式打开
                    StandardOpenOption.TRUNCATE_EXISTING)) { // 如果文件已存在则清空内容
                for (String srtLine : srtContent) {
                    writer.write(srtLine);
                    writer.newLine(); // 写入换行符
                }
            }

            // 验证文件是否成功写入并且包含内容
            long fileSize = Files.size(srtPath);
            if (Files.exists(srtPath) && fileSize > 0) {
                log.debug("PID: {} SRT 文件写入成功，大小: {} 字节", pid, fileSize);
                return true;
            } else {
                log.error("PID: {} SRT 文件写入失败或为空。路径: {}, 大小: {}", pid, srtPath, fileSize);
                return false;
            }

        } catch (PatternSyntaxException e) {
            log.error("PID: {} SRT 解析正则表达式语法错误: {}", pid, e.getMessage(), e);
            return false;
        } catch (IOException e) {
            log.error("PID: {} 写入 SRT 文件 '{}' 时出错: {}", pid, srtPath, e.getMessage(), e);
            return false;
        } catch (SecurityException e) {
            log.error("PID: {} 无权限写入 SRT 文件 '{}': {}", pid, srtPath, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            // 捕获其他所有在解析或写入过程中可能发生的异常
            log.error("PID: {} 解析或写入 SRT 文件 '{}' 时发生意外错误: {}",
                    pid, srtPath, e.getMessage(), e);
            return false;
        }
    }


    /**
     * 辅助方法：判断一行是否可能是统计信息、日志或其他非 SRT 文本。
     * 【已增强】基于提供的日志增加了更多过滤规则。
     *
     * @param line 要检查的单行文本
     * @return 如果看起来像统计/日志行，返回 true；否则返回 false。
     */
    private static boolean isLikelyStatsLine(String line) {
        if (line == null || line.isBlank()) {
            return false; // 空白行不是统计行（但也不是有效字幕文本）
        }
        String lowerLine = line.toLowerCase(Locale.ROOT).trim();

        // 规则列表：检查是否以特定关键字开头或包含特定模式
        boolean isStats =
                lowerLine.startsWith("using gpu") ||
                        lowerLine.startsWith("loaded mel filters") ||
                        lowerLine.startsWith("loaded vocabulary") ||
                        (lowerLine.startsWith("loaded") && lowerLine.contains("gpu tensors")) ||
                        lowerLine.startsWith("computed cpu base frequency") ||
                        lowerLine.startsWith("created source reader") ||
                        lowerLine.startsWith("whisper_print_timings:") ||
                        lowerLine.startsWith("log_mel_spectrogram") ||
                        lowerLine.startsWith("encode              :") || // 注意可能的空格填充
                        lowerLine.startsWith("decode              :") ||
                        lowerLine.startsWith("total time          :") ||
                        lowerLine.equals("cpu tasks") ||  // 完全匹配标题
                        lowerLine.equals("gpu tasks") ||
                        lowerLine.equals("compute shaders") ||
                        lowerLine.equals("memory usage") ||
                        // 匹配常见的计时行格式 (例如: LoadModel 1.13149 seconds) - 使用 \b 确保是单词边界
                        lowerLine.matches("^\\s*(loadmodel|runcomplete|run|callbacks|spectrogram|sample|encode|decode|decodestep|total)\\b.*\\b(seconds|milliseconds|microseconds)\\b.*") ||
                        // 匹配 GPU/计算着色器任务计时
                        lowerLine.matches("^\\s*(encodelayer|decodelayer)\\b.*\\b(seconds|milliseconds|microseconds)\\b.*") ||
                        lowerLine.matches("^\\s*(mulmatbyrowtiled|mulmattiled|softmaxfixed|addrepeatex|fmarepeat1|normfixed|copytranspose|copyconvert|softmaxlong|addrepeatscale|addrepeatgelu|scaleinplace|addrepeat|softmax|convolutionmain2fixed|diagmaskinf|convolutionmain|convolutionprep1|addrows|convolutionprep2|add)\\b.*\\b(seconds|milliseconds|microseconds)\\b.*") ||
                        // 匹配内存使用行 (例如: Model 877.966 KB RAM)
                        lowerLine.matches("^\\s*(model|context|total)\\b.*\\b(kb|mb|gb)\\s+(ram|vram).*") ||
                        // 包含特定短语，但要小心误判普通对话
                        lowerLine.contains("ms/token") ||
                        lowerLine.contains("tokens/s") ||
                        // 包含特定硬件/技术词汇，通常是日志
                        lowerLine.contains(" vram,") || // 注意逗号和空格，增加特异性
                        lowerLine.contains(" system ram") || // 注意空格
                        lowerLine.contains("cuda") ||
                        lowerLine.contains("opencl") ||
                        lowerLine.contains("directcompute") ||
                        // 过滤掉特定非语音内容（如果需要）
                        lowerLine.equals("(camera shutters clicking)"); // 从示例日志中添加

        if (isStats) {
            // 只在 TRACE 级别记录被过滤掉的行，避免日志泛滥
            log.trace("识别为统计/日志行，将被过滤: '{}'", line);
        }
        return isStats;
    }


    // --- 示例用法 Main 方法 (保持不变，用于测试) ---
    public static void main(String[] args) {
        log.info("--- 测试调用优化后的外部 Whisper CLI Util ---");

        // --- 路径配置 (请根据你的环境修改) ---
        // String audioFilePath1 = "D:\\14_LearnEnglish\\6MinuteEnglish\\2017\\171005\\171005_adult_exercise.mp3";
        String audioFilePath2 = "D:\\0000\\0003_PressBriefings\\20250416\\20250416.mp3"; // 示例中使用的文件
        // String nonExistentAudio = "D:\\path\\to\\non_existent_audio.mp3";
        // String invalidPathAudio = "D:\\invalid?*\\path.mp3";

        log.info("默认模型路径: {}", DEFAULT_MODEL_PATH);
        // log.info("测试音频1: {}", audioFilePath1);
        log.info("测试音频2: {}", audioFilePath2);
        // log.info("测试不存在音频: {}", nonExistentAudio);
        // log.info("测试无效路径: {}", invalidPathAudio);
        log.info("CLI 程序路径: {}", WHISPER_CLI_EXECUTABLE_PATH);


        // --- 示例 1 (使用日志中的文件): 调用 transcribeToSrt(String audioFilePath) ---
        log.info("\n--- 示例 1 (日志文件): 调用 transcribeToSrt(String audioFilePath) for {} ---", audioFilePath2);
        long startTime1_log = System.currentTimeMillis();
        String resultPath1_log = transcribeToSrt(audioFilePath2); // 测试日志中使用的文件
        long endTime1_log = System.currentTimeMillis();
        if (resultPath1_log != null) {
            log.info("示例 1 (日志文件) 成功！SRT 文件: {}", resultPath1_log);
            log.info("耗时1 (日志文件): {}", formatTimestamp(endTime1_log - startTime1_log));
        } else {
            log.error("示例 1 (日志文件) 失败。请检查上面的日志。");
        }

        // --- 可以取消注释其他测试用例进行更全面的测试 ---
        /*
        // --- 示例 1：使用最简单的接口 (String 路径，全默认) ---
        log.info("\n--- 示例 1: 调用 transcribeToSrt(String audioFilePath) for {} ---", audioFilePath1);
        long startTime1 = System.currentTimeMillis();
        String resultPath1 = transcribeToSrt(audioFilePath1);
        long endTime1 = System.currentTimeMillis();
        if (resultPath1 != null) {
            log.info("示例 1 成功！SRT 文件: {}", resultPath1);
            log.info("耗时1: {}", formatTimestamp(endTime1 - startTime1));
        } else {
            log.error("示例 1 失败。请检查上面的日志。");
        }


        // --- 示例 1.1：测试不存在的文件 ---
        log.info("\n--- 示例 1.1: 调用 transcribeToSrt(String) - 文件不存在 ---");
        String resultPath1_1 = transcribeToSrt(nonExistentAudio);
        if (resultPath1_1 == null) {
            log.info("示例 1.1 失败符合预期 (文件不存在)。");
        } else {
            log.error("示例 1.1 异常！预期失败但返回了: {}", resultPath1_1);
        }

        // --- 示例 1.2：测试无效路径 ---
        log.info("\n--- 示例 1.2: 调用 transcribeToSrt(String) - 无效路径 ---");
        String resultPath1_2 = transcribeToSrt(invalidPathAudio);
        if (resultPath1_2 == null) {
            log.info("示例 1.2 失败符合预期 (无效路径)。");
        } else {
            log.error("示例 1.2 异常！预期失败但返回了: {}", resultPath1_2);
        }

        // --- 示例 2：使用带配置的接口 (Path 路径，自定义语言和线程) ---
        log.info("\n--- 示例 2: 调用 transcribeToSrt(Path audioPath, Config) for {} ---", audioFilePath1); // 使用第一个文件测试
        try {
            Path audioPath2 = Paths.get(audioFilePath1); // 使用第一个文件
            ExternalTranscriptionConfig config2 = new ExternalTranscriptionConfig()
                    .setLanguage("en")
                    .setTranslate(false)
                    .setThreads(12);

            log.info("示例 2 配置: 语言={}, 翻译={}, 线程={}",
                    config2.getLanguage(), config2.getEffectiveTranslate(),
                    config2.getEffectiveThreads());

            long startTime2 = System.currentTimeMillis();
            String resultPath2 = transcribeToSrt(audioPath2, config2);
            long endTime2 = System.currentTimeMillis();

            if (resultPath2 != null) {
                log.info("示例 2 成功！SRT 文件: {}", resultPath2);
                log.info("耗时2: {}", formatTimestamp(endTime2 - startTime2));
            } else {
                log.error("示例 2 失败。请检查上面的日志。");
            }
        } catch (InvalidPathException e) {
            log.error("示例 2 的音频文件路径 '{}' 无效: {}", audioFilePath1,
                    e.getMessage());
        } catch (Exception e) {
            log.error("示例 2 执行时 main 方法捕获到意外异常: {}", e.getMessage(), e);
        }

        // --- 示例 3：使用全功能接口 (指定所有路径和配置) ---
        log.info("\n--- 示例 3: 调用 transcribeToSrtFullyConfigurable(...) for {} ---", audioFilePath2); // 使用第二个文件测试
        try {
            Path modelPath3 = Paths.get(DEFAULT_MODEL_PATH);
            Path audioPath3 = Paths.get(audioFilePath2); // 使用第二个文件
            // 自定义输出路径，例如添加语言和翻译标记
            String baseName3 = audioPath3.getFileName().toString().replaceFirst("[.][^.]+$", "");
            Path outputSrtPath3 = audioPath3.resolveSibling(baseName3 + ".en_notranslate_custom.srt");

            ExternalTranscriptionConfig config3 = new ExternalTranscriptionConfig()
                    .setLanguage("en") // 明确指定英文
                    .setTranslate(false) // 不翻译
                    .setThreads(6);      // 用 6 线程
            long timeout3 = 2400; // 40 分钟超时

            log.info(
                    "示例 3 配置: 模型={}, 音频={}, 输出={}, 语言={}, 翻译={}, 线程={}, 超时={}",
                    modelPath3, audioPath3, outputSrtPath3, config3.getLanguage(),
                    config3.getEffectiveTranslate(), config3.getEffectiveThreads(),
                    timeout3);

            long startTime3 = System.currentTimeMillis();
            String resultPath3 = transcribeToSrtFullyConfigurable(modelPath3,
                    audioPath3, outputSrtPath3, config3, timeout3);
            long endTime3 = System.currentTimeMillis();

            if (resultPath3 != null) {
                log.info("示例 3 成功！SRT 文件: {}", resultPath3);
                log.info("耗时3: {}", formatTimestamp(endTime3 - startTime3));
            } else {
                log.error("示例 3 失败。请检查上面的日志。");
            }
        } catch (InvalidPathException e) {
            log.error("示例 3 的路径配置无效: {}", e.getMessage());
        } catch (Exception e) {
            log.error("示例 3 执行时 main 方法捕获到意外异常: {}", e.getMessage(), e);
        }
        */

        log.info("\n--- 所有测试示例执行完毕 ---");
    }

    // formatTimestamp 方法保持不变
    private static String formatTimestamp(long totalMillis) {
        if (totalMillis < 0) {
            totalMillis = 0;
        }
        long milliseconds = totalMillis % 1000;
        long totalSeconds = totalMillis / 1000;
        long seconds = totalSeconds % 60;
        long totalMinutes = totalSeconds / 60;
        long minutes = totalMinutes % 60;
        long hours = totalMinutes / 60;
        // 使用 Locale.ROOT 确保小数点是 '.'
        return String.format(Locale.ROOT, "%02d:%02d:%02d,%03d (%d ms)", hours, minutes,
                seconds, milliseconds, totalMillis); // 添加总毫秒数显示
    }
}
