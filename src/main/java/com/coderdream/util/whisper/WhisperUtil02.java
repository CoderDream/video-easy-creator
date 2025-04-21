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
public class WhisperUtil02 { // 类名修改以区分

  // --- ==================== 配置与默认值区域 ==================== ---

  /**
   * 【确认】外部 Whisper CLI 可执行文件的完整路径。
   */
  private static final String WHISPER_CLI_EXECUTABLE_PATH = "D:\\00_Green\\WhisperDesktop\\cli\\main.exe";

  /**
   * 【确认】默认使用的 Whisper 模型文件的完整路径。
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

  // 静态初始化块 (保持不变)
  static {
    // 可以在这里添加对 DEFAULT_MODEL_PATH 的检查
    try {
      Path defaultModel = Paths.get(DEFAULT_MODEL_PATH);
      if (!Files.exists(defaultModel)) {
        log.warn("警告：默认模型文件路径 '{}' 不存在！使用简化接口时可能会失败。",
          DEFAULT_MODEL_PATH);
      }
      checkCliExecutable(); // 检查 CLI 程序
    } catch (InvalidPathException e) {
      log.error("配置错误：默认模型路径 '{}' 无效。", DEFAULT_MODEL_PATH, e);
      throw new RuntimeException("默认模型路径配置无效", e); // 抛出以阻止类加载
    } catch (RuntimeException e) {
      // checkCliExecutable 可能会抛出 RuntimeException
      log.error("初始化检查失败: {}", e.getMessage(), e);
      throw e; // 重新抛出以阻止类加载
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
      // 在静态块中抛出异常会导致类加载失败，后续所有调用都会出错
      throw new RuntimeException(errorMsg);
    }
    log.info("外部 Whisper CLI 路径验证通过: {}", WHISPER_CLI_EXECUTABLE_PATH);
    // 可以考虑在这里尝试运行 main.exe --version 或 --help 进一步验证
  }


  /**
   * 外部转录配置类。
   */
  @Getter
  public static class ExternalTranscriptionConfig {

    private String language = null; // null 表示使用 CLI 默认语言
    private Boolean translate = null; // null 表示使用 CLI 默认值 (false)
    private Integer threads = null; // null 表示使用 DEFAULT_THREADS

    /**
     * 设置目标语言。传入 null 使用 CLI 默认值。 "auto" 无效。
     */
    public ExternalTranscriptionConfig setLanguage(String language) {
      if ("auto".equalsIgnoreCase(language)) {
        log.warn(
          "CLI 不支持 'auto' 语言检测，将使用 CLI 默认语言或不传递 -l 参数。");
        this.language = null;
      } else {
        this.language =
          (language != null && language.isBlank()) ? null : language;
      }
      return this;
    }

    /**
     * 设置是否翻译。传入 null 使用 CLI 默认值 (false)。
     */
    public ExternalTranscriptionConfig setTranslate(Boolean translate) {
      this.translate = translate;
      return this;
    }

    /**
     * 设置 CPU 线程数。传入 null 使用默认值 DEFAULT_THREADS。
     */
    public ExternalTranscriptionConfig setThreads(Integer threads) {
      this.threads = (threads != null && threads < 1) ? Integer.valueOf(1)
        : threads;
      return this;
    }

    // 获取实际使用的线程数（如果为 null 则返回默认值）
    public int getEffectiveThreads() {
      return threads != null ? threads : DEFAULT_THREADS;
    }

    // 获取实际使用的翻译设置（如果为 null 则返回 false）
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
      // 校验输入字符串
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

      // 使用默认模型路径
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

      // 自动生成输出路径
      String audioFileName = audioPath.getFileName().toString();
      // 防止文件名以点开头导致问题
      String baseName = audioFileName;
      int dotIndex = audioFileName.lastIndexOf('.');
      if (dotIndex > 0) {
        baseName = audioFileName.substring(0, dotIndex);
      } else if (dotIndex == 0) {
        log.warn("音频文件名 '{}' 以点开头，生成的 SRT 文件名可能不符合预期。",
          audioFileName);
        // 可以选择移除开头的点或保留原样
      }

      String srtFileName = baseName + ".srt";
      outputSrtPath = audioPath.resolveSibling(srtFileName);
      log.info("将使用默认模型 '{}'，自动生成 SRT 到 '{}'", DEFAULT_MODEL_PATH,
        outputSrtPath);

      // 使用默认配置和默认超时
      ExternalTranscriptionConfig defaultConfig = new ExternalTranscriptionConfig(); // 所有字段为 null，将使用默认值

      // 调用核心执行方法
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

      // 使用默认模型路径
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

      // 自动生成输出路径
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

      // 使用提供的配置和默认超时
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
      // 基本的 Null 检查，更详细的检查在核心方法内部的 try-catch 中
      Objects.requireNonNull(modelPath, "模型文件路径 (modelPath) 不能为空");
      Objects.requireNonNull(audioPath, "音频文件路径 (audioPath) 不能为空");
      Objects.requireNonNull(outputSrtPath,
        "输出 SRT 文件路径 (outputSrtPath) 不能为空");
      Objects.requireNonNull(config, "配置对象 (config) 不能为空");

      // 直接调用核心方法，核心方法已包含异常处理和日志记录
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
    List<String> command = new ArrayList<>(); // 在 try 外部声明以便 finally 中可能引用

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
      // 检查输出路径的父目录是否存在且可写
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
        // 如果需要，可以在这里检查当前工作目录的写权限
      }

      // --- 构建命令行 ---
      command.add(WHISPER_CLI_EXECUTABLE_PATH);
      command.add(ARG_MODEL);
      command.add(modelPath.toAbsolutePath().toString());
      command.add(ARG_THREADS);
      command.add(String.valueOf(config.getEffectiveThreads())); // 使用有效线程数

      String lang = config.getLanguage();
      if (lang != null && !lang.trim().isEmpty()) {
        command.add(ARG_LANGUAGE);
        command.add(lang);
      }
      if (config.getEffectiveTranslate()) { // 使用有效翻译设置
        command.add(ARG_TRANSLATE);
      }
      command.add(ARG_NO_COLORS);
      command.add(ARG_INPUT_FILE);
      command.add(audioPath.toAbsolutePath().toString());

      log.info("准备执行核心 Whisper CLI 命令:");
      command.forEach(arg -> log.info("  {}", arg)); // 打印每个参数，更清晰

      // --- 执行进程 ---
      ProcessBuilder processBuilder = new ProcessBuilder(command);
      processBuilder.redirectErrorStream(true); // 合并 stdout 和 stderr

      process = processBuilder.start();
      final Process pFinal = process; // final variable for lambda
      final long processPid = process.pid(); // 获取 PID 用于日志

      // 异步读取输出
      executor.submit(
        () -> readProcessOutput(pFinal, processOutput, processPid));

      log.info("外部 Whisper CLI 已启动 (PID: {}), 等待完成...", processPid);

      boolean finished = waitForProcess(process, timeoutSeconds);

      // 关闭读取器并等待其结束
      executor.shutdown();
      try {
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
          log.warn("读取输出的线程 (PID: {}) 在关闭时超时，强制关闭。",
            processPid);
          executor.shutdownNow();
        }
      } catch (InterruptedException ex) {
        executor.shutdownNow();
        Thread.currentThread().interrupt(); // 重要：恢复中断状态
        log.warn("等待输出读取线程 (PID: {}) 结束时被中断。", processPid);
        // 即使这里中断，也继续尝试处理进程结果
      }

      if (!finished) {
        log.error("外部 Whisper CLI (PID: {}) 执行超时 (超过 {} 秒)！",
          processPid, timeoutSeconds);
        if (process.isAlive()) {
          process.destroyForcibly();
          log.info("强制终止了超时的外部 Whisper CLI (PID: {})。", processPid);
        }
        log.error("进程部分输出 (PID: {}):\n{}", processPid,
          getOutputExcerpt(processOutput));
        return null; // 超时失败
      }

      int exitCode = process.exitValue();
      log.info("外部 Whisper CLI (PID: {}) 已结束, 退出码: {}", processPid,
        exitCode);
      if (log.isDebugEnabled()) {
        log.debug("外部 Whisper CLI 完整输出 (PID: {}):\n{}", processPid,
          processOutput);
      }

      // --- 处理结果 ---
      if (exitCode == 0) {
        boolean parseSuccess = parseAndWriteSrtFromOutput(
          processOutput.toString(), outputSrtPath, processPid);
        if (parseSuccess) {
          log.info("成功为进程 (PID: {}) 生成 SRT 文件: {}", processPid,
            outputSrtPath.toAbsolutePath());
          return outputSrtPath.toAbsolutePath().toString(); // 返回绝对路径字符串
        } else {
          log.error(
            "CLI (PID: {}) 退出码为 0，但无法从输出中解析有效内容或写入 SRT 文件 {}",
            processPid, outputSrtPath);
          return null; // 解析或写入失败
        }
      } else {
        log.error("CLI (PID: {}) 执行失败，退出码: {}。输出摘要:\n{}", processPid,
          exitCode, getOutputExcerpt(processOutput));
        // 可以在这里增加对特定退出码的分析，如果知道它们的含义
        return null; // 非零退出码失败
      }

    } catch (IOException e) {
      // 可能来自 processBuilder.start(), Files 操作, 或 readProcessOutput/parseAndWriteSrtFromOutput
      log.error(
        "执行 Whisper CLI 或处理文件时发生 IO 错误 (相关路径: {}, {}, {}): {}",
        modelPath, audioPath, outputSrtPath, e.getMessage(), e);
      return null;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // 恢复中断状态
      log.error("等待 Whisper CLI 进程时线程被中断: {}", e.getMessage(), e);
      return null;
    } catch (SecurityException e) {
      log.error("执行 Whisper CLI 时权限不足 (命令: {}): {}",
        String.join(" ", command), e.getMessage(), e);
      return null;
    } catch (NullPointerException | IllegalArgumentException e) {
      // 通常是编程错误或无效参数导致
      log.error("执行 Whisper CLI 时遇到无效参数或状态错误: {}", e.getMessage(),
        e);
      return null;
    } catch (Exception e) {
      // 捕获所有其他未预料到的异常
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
      // 确保 ExecutorService 被关闭
      if (executor != null && !executor.isTerminated()) {
        executor.shutdownNow();
        log.warn("强制关闭了未终止的 ExecutorService。");
      }
    }
  }

  /**
   * 辅助方法：读取进程输出流
   */
  private static void readProcessOutput(Process process, StringBuilder output,
    long pid) {
    // 使用 try-with-resources 确保 reader 关闭
    try (BufferedReader reader = new BufferedReader(
      new InputStreamReader(process.getInputStream(),
        StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append(System.lineSeparator());
        // 实时打印，包含 PID，使用 trace 或 info 级别
        log.trace("Whisper CLI (PID: {}): {}", pid, line);
      }
    } catch (IOException e) {
      // 进程结束时，读取流可能会失败，这通常是正常的，但也可能是错误
      if (process.isAlive()) { // 只有进程还活着时读取错误才值得关注
        log.warn("读取外部 Whisper CLI (PID: {}) 输出时出错 (进程仍在运行): {}",
          pid, e.getMessage());
      } else {
        // 进程已结束，这是预期行为，使用 trace 级别记录
        log.trace("读取进程 (PID: {}) 输出流时遇到 IO 异常，进程已结束。", pid);
      }
    } catch (Exception e) {
      log.error("读取进程 (PID: {}) 输出时发生意外错误: {}", pid,
        e.getMessage(), e);
    }
  }

  /**
   * 辅助方法：等待进程结束 (保持不变，但调用处已处理 InterruptedException)
   */
  private static boolean waitForProcess(Process process, long timeoutSeconds)
    throws InterruptedException {
    if (timeoutSeconds > 0) {
      return process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
    } else {
      process.waitFor(); // 无限期等待
      return true;
    }
  }

  /**
   * 辅助方法：获取输出摘要 (保持不变)
   */
  private static String getOutputExcerpt(StringBuilder output) {
    int maxLength = 2000; // 只显示一部分
    if (output == null || output.length() == 0) {
      return "[无输出]";
    }
    if (output.length() <= maxLength) {
      return output.toString();
    } else {
      // 从末尾截取可能更有用，因为错误信息通常在最后
      // return output.substring(output.length() - maxLength) + "\n... [输出过长，仅显示末尾部分]";
      return output.substring(0, maxLength) + "\n... [输出过长，已截断]";
    }
  }

  // --- SRT 解析和写入方法 ---

  /**
   * 解析 CLI 输出并写入 SRT 文件。
   *
   * @param cliOutput CLI 的标准输出内容。
   * @param srtPath   要写入的 SRT 文件路径。
   * @param pid       关联的进程 PID，用于日志记录。
   * @return 如果成功写入了非空 SRT 文件，返回 true；否则返回 false。
   */
  private static boolean parseAndWriteSrtFromOutput(String cliOutput,
    Path srtPath, long pid) {
    List<String> srtContent = new ArrayList<>();
    Pattern timePattern = null;
    Pattern textPattern = null;

    try {
      // 将正则表达式编译放在 try 块内，以防 PatternSyntaxException
      timePattern = Pattern.compile(
        "^\\s*\\[(\\d{2}:\\d{2}:\\d{2}\\.\\d{3}) --> (\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\]\\s*(.*)");
      textPattern = Pattern.compile("^\\s*\\S+.*"); // 匹配非空行

      String[] lines = cliOutput.split("\\r?\\n");
      int segmentIndex = 1;
      String currentTimeLine = null;
      String currentTextAccumulator = ""; // 用于累积多行文本

      for (String line : lines) {
        Matcher timeMatcher = timePattern.matcher(line);
        if (timeMatcher.matches()) {
          // 新的时间戳行开始
          // 如果之前有累积的文本，先写入上一个片段
          if (currentTimeLine != null && !currentTextAccumulator.isEmpty()) {
            if (!isLikelyStatsLine(currentTextAccumulator)) {
              srtContent.add(String.valueOf(segmentIndex++));
              srtContent.add(currentTimeLine);
              srtContent.add(currentTextAccumulator);
              srtContent.add("");
            } else {
              log.trace("忽略累积的统计行 (PID: {}): {}", pid,
                currentTextAccumulator);
            }
          }

          // 开始新的片段
          currentTimeLine =
            timeMatcher.group(1) + " --> " + timeMatcher.group(2);
          currentTextAccumulator = timeMatcher.group(3)
            .trim(); // 获取时间戳同行可能存在的文本

        } else if (currentTimeLine != null) {
          // 如果当前行不是时间戳行，且我们正在一个片段内，则累积文本
          String trimmedLine = line.trim();
          if (!trimmedLine.isEmpty() && textPattern.matcher(trimmedLine)
            .matches()) {
            // 只有当行看起来是实际内容时才累加
            if (!currentTextAccumulator.isEmpty()) {
              currentTextAccumulator += System.lineSeparator(); // 多行文本用换行符分隔
            }
            currentTextAccumulator += trimmedLine;
          } else {
            // 如果遇到空行或不像文本的行，可能表示片段结束，或者只是输出中的空行
            // 当前策略是：只要遇到非时间戳行就累加（除非是统计行，下面判断）
            // 也可以更严格：只累加紧跟在时间戳行后的非空行
          }
        } else {
          // 不在时间戳片段内，忽略此行 (可能是 CLI 的其他输出)
          log.trace("忽略非 SRT 内容行 (PID: {}): {}", pid, line);
        }
      }

      // 处理最后一个片段 (循环结束后)
      if (currentTimeLine != null && !currentTextAccumulator.isEmpty()) {
        if (!isLikelyStatsLine(currentTextAccumulator)) {
          srtContent.add(String.valueOf(segmentIndex++));
          srtContent.add(currentTimeLine);
          srtContent.add(currentTextAccumulator);
          srtContent.add("");
        } else {
          log.trace("忽略最后一个累积的统计行 (PID: {}): {}", pid,
            currentTextAccumulator);
        }
      }

      if (srtContent.isEmpty()) {
        log.warn(
          "未能从 CLI (PID: {}) 输出中解析出有效的 SRT 片段。输出摘要:\n{}", pid,
          getOutputExcerpt(new StringBuilder(cliOutput)));
        return false;
      }

      log.info("从 CLI (PID: {}) 输出中解析出 {} 个 SRT 片段，准备写入文件: {}",
        pid, segmentIndex - 1, srtPath);

      // 使用 try-with-resources 确保 writer 关闭
      try (BufferedWriter writer = Files.newBufferedWriter(srtPath,
        StandardCharsets.UTF_8, StandardOpenOption.CREATE,
        StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
        for (String srtLine : srtContent) {
          writer.write(srtLine);
          writer.newLine();
        }
      }

      // 验证文件是否成功写入且非空
      return Files.exists(srtPath) && Files.size(srtPath) > 0;

    } catch (PatternSyntaxException e) {
      log.error("SRT 解析正则表达式语法错误 (PID: {}): {}", pid, e.getMessage(),
        e);
      return false;
    } catch (IOException e) {
      log.error("写入 SRT 文件 '{}' (PID: {}) 时出错: {}", srtPath, pid,
        e.getMessage(), e);
      return false;
    } catch (SecurityException e) {
      log.error("无权限写入 SRT 文件 '{}' (PID: {}): {}", srtPath, pid,
        e.getMessage(), e);
      return false;
    } catch (Exception e) {
      log.error("解析或写入 SRT 文件 '{}' (PID: {}) 时发生意外错误: {}",
        srtPath, pid, e.getMessage(), e);
      return false;
    }
  }


  /**
   * 辅助方法：判断是否可能是统计信息行 (稍微改进了判断逻辑)
   */
  private static boolean isLikelyStatsLine(String line) {
    if (line == null || line.isBlank()) {
      return false;
    }
    String lowerLine = line.toLowerCase(Locale.ROOT).trim();
    // 检查是否以常见的统计关键字开头
    if (lowerLine.startsWith("whisper_print_timings:") ||
      lowerLine.startsWith("log_mel_spectrogram") ||
      lowerLine.startsWith("encode              :") ||
      lowerLine.startsWith("decode              :") ||
      lowerLine.startsWith("total time          :") ||
      // 添加一些 Const-me/Whisper 的特定输出模式（如果已知）
      lowerLine.matches("^\\s*(load|encode|decode|sample|total)\\s+time\\s*=.*")
      ||
      lowerLine.contains("ms/token") ||
      lowerLine.contains("tokens/s") ||
      lowerLine.contains(" vram, ") ||
      lowerLine.contains(" system ram") ||
      lowerLine.contains("cuda") || // 可能包含 GPU 相关信息
      lowerLine.contains("opencl") ||
      lowerLine.contains("directcompute") // 可能的计算后端信息
    ) {
      return true;
    }
    // 避免误判包含这些词的普通句子
    // 可以根据需要添加更多规则
    return false;
  }


  // --- 示例用法 Main 方法 (展示新接口) ---
  public static void main(String[] args) {
    log.info("--- 测试调用优化后的外部 Whisper CLI Util ---");

    // --- 路径配置 ---
    // String modelFilePath = DEFAULT_MODEL_PATH; // 不再直接需要，除非用全功能接口
    String audioFilePath1 = "D:\\14_LearnEnglish\\6MinuteEnglish\\2017\\171005\\171005_adult_exercise.mp3";
    String audioFilePath2 = "D:\\0000\\0003_PressBriefings\\20250415\\20250415.mp3"; // 另一个测试文件
    String nonExistentAudio = "D:\\path\\to\\non_existent_audio.mp3";
    String invalidPathAudio = "D:\\invalid?*\\path.mp3";

    log.info("默认模型路径: {}", DEFAULT_MODEL_PATH);
    log.info("测试音频1: {}", audioFilePath1);
    log.info("测试音频2: {}", audioFilePath2);
    log.info("测试不存在音频: {}", nonExistentAudio);
    log.info("测试无效路径: {}", invalidPathAudio);
    log.info("CLI 程序路径: {}", WHISPER_CLI_EXECUTABLE_PATH);

    // --- 示例 1：使用最简单的接口 (String 路径，全默认) ---
    log.info("\n--- 示例 1: 调用 transcribeToSrt(String audioFilePath) ---");
    long startTime1 = System.currentTimeMillis();
    String resultPath1 = transcribeToSrt(audioFilePath1); // 传入 String
    long endTime1 = System.currentTimeMillis();
    if (resultPath1 != null) {
      log.info("示例 1 成功！SRT 文件: {}", resultPath1);
      log.info("耗时1: {} ms", formatTimestamp(endTime1 - startTime1));
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
    log.info("\n--- 示例 2: 调用 transcribeToSrt(Path audioPath, Config) ---");
    try {
      Path audioPath2 = Paths.get(audioFilePath2);
      ExternalTranscriptionConfig config2 = new ExternalTranscriptionConfig()
        .setLanguage("en") // 明确指定英语
        .setTranslate(false)
        .setThreads(12); // 尝试 12 线程

      log.info("示例 2 配置: 语言={}, 翻译={}, 线程={}",
        config2.getLanguage(), config2.getEffectiveTranslate(),
        config2.getEffectiveThreads());

      long startTime2 = System.currentTimeMillis();
      String resultPath2 = transcribeToSrt(audioPath2,
        config2); // 传入 Path 和 Config
      long endTime2 = System.currentTimeMillis();

      if (resultPath2 != null) {
        log.info("示例 2 成功！SRT 文件: {}", resultPath2);
        log.info("耗时2: {} ms", formatTimestamp(endTime2 - startTime2));
        // 可以在这里添加预览代码
      } else {
        log.error("示例 2 失败。请检查上面的日志。");
      }
    } catch (InvalidPathException e) {
      log.error("示例 2 的音频文件路径 '{}' 无效: {}", audioFilePath2,
        e.getMessage());
    } catch (Exception e) {
      log.error("示例 2 执行时 main 方法捕获到意外异常: {}", e.getMessage(), e);
    }

    // --- 示例 3：使用全功能接口 (指定所有路径和配置) ---
    log.info("\n--- 示例 3: 调用 transcribeToSrtFullyConfigurable(...) ---");
    try {
      Path modelPath3 = Paths.get(DEFAULT_MODEL_PATH); // 显式使用默认模型
      Path audioPath3 = Paths.get(audioFilePath1);
      Path outputSrtPath3 = audioPath3.resolveSibling(
        audioPath3.getFileName().toString().replaceFirst("[.][^.]+$", "")
          + ".zh_translated.srt"); // 自定义输出路径
      ExternalTranscriptionConfig config3 = new ExternalTranscriptionConfig()
        // .setLanguage("zh") // 假设源是中文, 但这里是英文音频，让它自动检测或指定英文
        .setLanguage("en")
        .setTranslate(
          true) // 翻译成英文（如果模型支持且源不是英文）-> 这里会尝试英译英，可能效果不明显或报错，取决于CLI行为
        .setThreads(6);    // 用 6 线程
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
        log.info("耗时3: {} ms", formatTimestamp(endTime3 - startTime3));
      } else {
        log.error("示例 3 失败。请检查上面的日志。");
      }
    } catch (InvalidPathException e) {
      log.error("示例 3 的路径配置无效: {}", e.getMessage());
    } catch (Exception e) {
      log.error("示例 3 执行时 main 方法捕获到意外异常: {}", e.getMessage(), e);
    }

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
    return String.format(Locale.ROOT, "%02d:%02d:%02d,%03d", hours, minutes,
      seconds, milliseconds);
  }
}
