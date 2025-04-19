package com.coderdream.util.whisper; // 请确保包名正确

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets; // 明确指定 UTF-8 尝试
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
import java.util.function.Consumer; // 虽然没直接用，但保留以备将来扩展

/**
 * 使用外部 Whisper 进程 (例如 Const-me/Whisper 的 WhisperDesktop.exe)
 * 来执行高性能的语音转文本和 SRT 生成。
 * <p>
 * 需要用户根据实际情况验证并修改外部程序的路径和命令行参数。
 */
@Slf4j
public class ExternalWhisperUtil {

    // --- ==================== 配置区域 ==================== ---

    /**
     * 【已根据用户提供信息修改】外部 Whisper 可执行文件的完整路径。
     */
    private static final String WHISPER_EXECUTABLE_PATH = "D:\\00_Green\\WhisperDesktop\\WhisperDesktop.exe";

    /**
     * 【保持 null】如果直接调用 exe，此项为 null。如果是通过 PowerShell 脚本调用，则需修改。
     */
    private static final String WHISPER_SCRIPT_PATH = null;

    /**
     * 【！！！极其重要！！！】
     * 下面的参数名是基于常见模式的【猜测】，您【必须】通过运行
     * "D:\00_Green\WhisperDesktop\WhisperDesktop.exe --help" (或其他帮助命令)
     * 或查看其文档来【确认实际的命令行参数】！
     * 如果 WhisperDesktop.exe 不支持命令行，您可能需要使用其附带的 PowerShell 脚本
     * (需要修改 WHISPER_SCRIPT_PATH 和下面的参数格式)。
     */
    private static final String ARG_MODEL = "-Model";         // 猜测的模型参数名
    private static final String ARG_INPUT_FILE = "-InputFile";   // 猜测的输入文件参数名
    private static final String ARG_OUTPUT_FILE = "-OutputFile"; // 猜测的输出 SRT 文件参数名
    private static final String ARG_LANGUAGE = "-Language";    // 猜测的语言参数名
    private static final String ARG_TRANSLATE = "-Translate";   // 猜测的翻译参数名 (可能用法是 -Translate 或 -Translate true)
    // 可能还有其他参数，例如: -Threads, -GpuDeviceIndex 等，需自行确认并添加

    // --- ================= 配置区域结束 ================== ---


    /**
     * 外部转录配置类。
     */
    public static class ExternalTranscriptionConfig {
        private String language = "en";
        private boolean translate = false;
        // 可以根据需要添加其他配置项，例如线程数、GPU设备索引等
        // private int threads = 4;

        public ExternalTranscriptionConfig setLanguage(String language) { this.language = language; return this; }
        public ExternalTranscriptionConfig setTranslate(boolean translate) { this.translate = translate; return this; }
        // public ExternalTranscriptionConfig setThreads(int threads) { this.threads = threads; return this; }

        public String getLanguage() { return language; }
        public boolean isTranslate() { return translate; }
        // public int getThreads() { return threads; }
    }

    /**
     * 调用外部 Whisper 进程生成 SRT 字幕文件。
     *
     * @param modelPath      Whisper 模型文件的路径。
     * @param audioPath      要转录的音频文件的路径。
     * @param outputSrtPath  期望生成的 SRT 文件的路径。外部进程将写入此文件。
     * @param config         转录配置。
     * @param timeoutSeconds 进程执行的超时时间（秒）。设置为 0 或负数表示不超时。
     * @return 如果成功且生成了非空 SRT 文件，返回 true；否则返回 false。
     * @throws IOException          如果启动进程或文件操作出错。
     * @throws InterruptedException 如果等待进程时线程被中断。
     * @throws RuntimeException     如果配置错误或外部进程返回错误。
     */
    public static boolean transcribeToSrtExternal(Path modelPath, Path audioPath, Path outputSrtPath,
                                                 ExternalTranscriptionConfig config, long timeoutSeconds)
            throws IOException, InterruptedException, RuntimeException {

        Objects.requireNonNull(modelPath, "模型路径不能为空");
        Objects.requireNonNull(audioPath, "音频路径不能为空");
        Objects.requireNonNull(outputSrtPath, "输出 SRT 路径不能为空");
        Objects.requireNonNull(config, "配置不能为空");
        if (!Files.exists(modelPath)) throw new IOException("模型文件未找到: " + modelPath);
        if (!Files.exists(audioPath)) throw new IOException("音频文件未找到: " + audioPath);

        // 验证可执行文件路径
        File executable = new File(WHISPER_EXECUTABLE_PATH);
        if (!executable.exists() || !executable.isFile()) {
             log.error("配置错误：外部 Whisper 可执行文件未找到或不是文件: {}", WHISPER_EXECUTABLE_PATH);
             throw new IOException("指定的外部 Whisper 可执行文件无效: " + WHISPER_EXECUTABLE_PATH);
        }
        log.info("外部 Whisper 可执行文件路径验证通过: {}", WHISPER_EXECUTABLE_PATH);

        // --- 构建命令行参数列表 ---
        List<String> command = new ArrayList<>();
        command.add(WHISPER_EXECUTABLE_PATH); // 可执行文件

        // 添加参数 (再次强调，参数名需要核实！)
        command.add(ARG_MODEL); command.add(modelPath.toAbsolutePath().toString());
        command.add(ARG_INPUT_FILE); command.add(audioPath.toAbsolutePath().toString());
        command.add(ARG_OUTPUT_FILE); command.add(outputSrtPath.toAbsolutePath().toString());
        command.add(ARG_LANGUAGE); command.add(config.getLanguage());
        if (config.isTranslate()) {
            // 假设需要值 "true"
            command.add(ARG_TRANSLATE); command.add("true");
            // 如果是标志参数，则只需 command.add(ARG_TRANSLATE);
        }
        // 添加其他参数，例如:
        // command.add("-Threads"); command.add(String.valueOf(config.getThreads()));

        log.info("准备执行外部 Whisper 命令 (请务必确认以下参数是否正确！):");
        // 使用换行符打印每个参数，更清晰
        command.forEach(log::info);

        // --- 执行外部进程 ---
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 合并 stdout 和 stderr

        Process process = null; // 初始化为 null
        ExecutorService executor = Executors.newSingleThreadExecutor();
        StringBuilder processOutput = new StringBuilder();

        try {
            process = processBuilder.start();
            final Process pFinal = process; // effectively final for lambda

            // 异步读取进程输出
            executor.submit(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(pFinal.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        processOutput.append(line).append(System.lineSeparator());
                        log.info("Whisper Proc: {}", line); // 实时打印
                    }
                } catch (IOException e) { log.error("读取外部 Whisper 进程输出时出错: {}", e.getMessage()); }
            });

            log.info("外部 Whisper 进程已启动，等待其完成 (超时: {} 秒)...", timeoutSeconds > 0 ? timeoutSeconds : "无");

            // 等待进程结束
            boolean finished;
            if (timeoutSeconds > 0) {
                finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            } else {
                process.waitFor(); // 无超时等待
                finished = true;
            }

            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("读取输出的线程在关闭时超时。");
                    executor.shutdownNow();
                }
            } catch (InterruptedException ex) {
                 executor.shutdownNow();
                 Thread.currentThread().interrupt(); // 保留中断状态
            }


            if (!finished) {
                log.error("外部 Whisper 进程执行超时 (超过 {} 秒)！", timeoutSeconds);
                process.destroyForcibly();
                log.error("强制终止了超时的外部 Whisper 进程。进程输出:\n{}", processOutput);
                return false;
            }

            int exitCode = process.exitValue();
            log.info("外部 Whisper 进程已结束，退出码: {}", exitCode);
            log.debug("外部 Whisper 进程完整输出:\n{}", processOutput);

            // --- 检查结果 ---
            if (exitCode == 0) {
                // 检查输出文件是否存在且非空
                if (Files.exists(outputSrtPath) && Files.size(outputSrtPath) > 0) {
                    log.info("外部 Whisper 进程成功执行，并生成了有效的 SRT 文件: {}", outputSrtPath);
                    return true;
                } else {
                    log.error("外部 Whisper 进程退出码为 0，但未找到预期的输出 SRT 文件或文件为空: {}", outputSrtPath);
                    // 打印进程输出帮助诊断
                    log.error("请检查进程输出以了解可能的原因:\n{}", processOutput);
                    return false;
                }
            } else {
                log.error("外部 Whisper 进程执行失败，退出码: {}。请检查上面的进程输出以获取错误详情。", exitCode);
                return false;
            }

        } catch (IOException ioEx) {
            log.error("启动或与外部 Whisper 进程通信时发生 IO 错误: {}", ioEx.getMessage(), ioEx);
            if (process != null) process.destroyForcibly();
            throw ioEx; // 重新抛出
        } catch (InterruptedException interruptedEx) {
             Thread.currentThread().interrupt(); // 保留中断状态
             log.error("等待外部 Whisper 进程时线程被中断。", interruptedEx);
             if (process != null) process.destroyForcibly();
             throw interruptedEx; // 重新抛出
        } finally {
            // 最终确保进程关闭
            if (process != null && process.isAlive()) {
                log.warn("外部 Whisper 进程在方法退出时仍存活，强制销毁。");
                process.destroyForcibly();
            }
            // 确保线程池关闭
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        }
    }


    // --- 示例用法 Main 方法 ---
    public static void main(String[] args) {
        log.info("--- 测试调用外部 Whisper 进程 ---");

        // --- 使用您提供的路径 ---
        String modelFilePath = "D:\\00_Green\\WhisperDesktop\\models\\ggml-model-whisper-medium.bin";
        String audioFilePath = "D:\\0000\\0003_PressBriefings\\20250415\\20250415.mp3";
        audioFilePath = "D:\\14_LearnEnglish\\6MinuteEnglish\\2017\\171005\\171005_adult_exercise.mp3";

        // --- 根据音频文件自动生成输出 SRT 文件路径 ---
        Path audioPathObj = Paths.get(audioFilePath);
        String outputFileNameBase = audioPathObj.getFileName().toString();
        int lastDot = outputFileNameBase.lastIndexOf('.');
        if (lastDot > 0) {
            outputFileNameBase = outputFileNameBase.substring(0, lastDot);
        }
        // 假设默认生成英文 SRT，或根据 config 调整语言后缀
        String outputSrtFileName = outputFileNameBase + ".en.srt";
        Path outputSrtPathObj = audioPathObj.resolveSibling(outputSrtFileName); // 与音频文件同目录
        String outputSrtFilePath = outputSrtPathObj.toString();

        // --- 配置区域结束 ---

        log.info("模型文件路径: {}", modelFilePath);
        log.info("音频文件路径: {}", audioFilePath);
        log.info("输出 SRT 路径: {}", outputSrtFilePath);
        log.info("外部程序路径: {}", WHISPER_EXECUTABLE_PATH);

        Path modelPath = Paths.get(modelFilePath);
        Path audioPath = Paths.get(audioFilePath);
        Path outputSrtPath = Paths.get(outputSrtFilePath);

        // 确保文件存在
        if (!Files.exists(modelPath)) { log.error("错误：模型文件未找到: {}", modelPath); return; }
        if (!Files.exists(audioPath)) { log.error("错误：音频文件未找到: {}", audioPath); return; }

        // 确保输出目录存在
        try {
            Files.createDirectories(outputSrtPath.getParent());
            log.info("输出目录检查/创建完毕: {}", outputSrtPath.getParent());
        } catch (IOException e) {
            log.error("创建输出目录失败: {}", outputSrtPath.getParent(), e);
            return;
        }

        // 配置转录参数 (例如，自动检测语言，不翻译)
        ExternalTranscriptionConfig config = new ExternalTranscriptionConfig()
                .setLanguage("auto") // 让外部程序自动检测
                .setTranslate(false);

        // 设置超时时间 (秒)，例如 20 分钟 = 1200 秒。设为 0 或负数不超时。
        long timeoutInSeconds = 1200;

        log.info("准备调用外部 Whisper (语言: {}, 翻译: {}, 超时: {} 秒)...",
                 config.getLanguage(), config.isTranslate(), timeoutInSeconds > 0 ? timeoutInSeconds : "无");
        log.warn("【请务必确认】代码中配置的命令行参数（如 {}, {}, {} 等）与 {} 的实际用法一致！",
                 ARG_MODEL, ARG_INPUT_FILE, ARG_OUTPUT_FILE, WHISPER_EXECUTABLE_PATH);


        try {
            long startTime = System.currentTimeMillis();
            boolean success = transcribeToSrtExternal(modelPath, audioPath, outputSrtPath, config, timeoutInSeconds);
            long endTime = System.currentTimeMillis();
            double durationMinutes = (endTime - startTime) / 60000.0;

            if (success) {
                log.info("外部 Whisper 调用成功！SRT 文件已生成。总耗时: {} ms (~= {} 分钟)", (endTime - startTime), String.format("%.2f", durationMinutes));
                // 可以添加代码读取 SRT 文件的前几行进行验证
                try {
                    List<String> lines = Files.readAllLines(outputSrtPath, StandardCharsets.UTF_8);
                    log.info("生成的 SRT 文件 '{}' 前 {} 行预览:", outputSrtPath.getFileName(), Math.min(lines.size(), 5));
                    for(int i = 0; i < Math.min(lines.size(), 5); i++) {
                        log.info("  {}", lines.get(i));
                    }
                } catch (IOException readEx) {
                     log.warn("读取生成的 SRT 文件预览时出错: {}", readEx.getMessage());
                }
            } else {
                log.error("外部 Whisper 调用失败或超时。请检查上面的日志和外部程序的输出。总耗时: {} ms (~= {} 分钟)", (endTime - startTime), String.format("%.2f", durationMinutes));
            }
        } catch (IOException e) {
            log.error("调用外部 Whisper 时发生 IO 错误: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("调用外部 Whisper 时线程被中断。", e);
        } catch (RuntimeException e) {
            log.error("调用外部 Whisper 时发生运行时错误 (可能是配置或参数问题): {}", e.getMessage(), e);
        }

        log.info("--- 外部 Whisper 调用测试结束 ---");
    }
}
