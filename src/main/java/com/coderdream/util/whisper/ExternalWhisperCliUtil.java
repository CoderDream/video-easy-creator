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
 * 使用外部 Whisper CLI 进程 (main.exe from Const-me/Whisper) 来执行高性能的语音转文本和 SRT 生成。
 * <p>
 * 提供多种调用接口，包括仅需音频文件路径字符串的简化接口。
 */
@Slf4j
public class ExternalWhisperCliUtil {

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
  // private static final String ARG_OUTPUT_SRT = "-osrt"; // 不再需要，从输出解析
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
    Path defaultModel = Paths.get(DEFAULT_MODEL_PATH);
    if (!Files.exists(defaultModel)) {
      log.warn("警告：默认模型文件路径 '{}' 不存在！使用简化接口时可能会失败。",
        DEFAULT_MODEL_PATH);
    }
    checkCliExecutable(); // 检查 CLI 程序
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
      this.threads = (threads != null && threads < 1) ? 1 : threads;
      return this;
    }

    public String getLanguage() {
      return language;
    }

    public Boolean getTranslate() {
      return translate;
    }

    public Integer getThreads() {
      return threads;
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
   * "音频文件名.srt"。
   *
   * @param audioFilePath 要转录的音频文件的【完整路径字符串】。
   * @return 如果成功生成 SRT 文件，返回该文件的 Path；否则返回 null。
   * @throws IOException          如果文件操作或进程启动出错。
   * @throws InterruptedException 如果线程被中断。
   * @throws RuntimeException     如果配置错误或依赖缺失。
   */
  public static Path transcribeToSrt(String audioFilePath)
    throws IOException, InterruptedException, RuntimeException {
    log.info("收到简化接口调用请求 (transcribeToSrt(String))，使用默认设置。");

    // 校验输入字符串
    if (audioFilePath == null || audioFilePath.isBlank()) {
      log.error("音频文件路径字符串不能为空。");
      return null;
    }
    Path audioPath = Paths.get(audioFilePath);
    if (!Files.exists(audioPath)) {
      throw new IOException("音频文件未找到: " + audioPath);
    }

    // 使用默认模型路径
    Path modelPath = Paths.get(DEFAULT_MODEL_PATH);
    if (!Files.exists(modelPath)) {
      throw new IOException("默认模型文件未找到: " + modelPath
        + "，请检查 DEFAULT_MODEL_PATH 配置或使用全参数接口指定模型。");
    }

    // 自动生成输出路径
    String audioFileName = audioPath.getFileName().toString();
    String srtFileName = audioFileName + ".srt";
    Path outputSrtPath = audioPath.resolveSibling(srtFileName);
    log.info("将使用默认模型 '{}'，自动生成 SRT 到 '{}'", DEFAULT_MODEL_PATH,
      outputSrtPath);

    // 使用默认配置和默认超时
    ExternalTranscriptionConfig defaultConfig = new ExternalTranscriptionConfig(); // 所有字段为 null，将使用默认值

    // 调用核心执行方法
    return executeWhisperCliAndWriteSrt(modelPath, audioPath, outputSrtPath,
      defaultConfig, DEFAULT_TIMEOUT_SECONDS);
  }

  /**
   * 【新增接口2】带配置的调用方式：转录指定音频文件为 SRT，允许自定义语言、翻译、线程数等。 模型使用默认路径，SRT 文件自动生成在音频文件旁边。
   *
   * @param audioPath 要转录的音频文件的 Path 对象。
   * @param config    包含自定义设置的 ExternalTranscriptionConfig 对象。未设置的选项将使用默认值。
   * @return 如果成功生成 SRT 文件，返回该文件的 Path；否则返回 null。
   * @throws IOException          如果文件操作或进程启动出错。
   * @throws InterruptedException 如果线程被中断。
   * @throws RuntimeException     如果配置错误或依赖缺失。
   */
  public static Path transcribeToSrt(Path audioPath,
    ExternalTranscriptionConfig config)
    throws IOException, InterruptedException, RuntimeException {
    log.info(
      "收到带配置接口调用请求 (transcribeToSrt(Path, Config))，使用默认模型和输出路径。");

    Objects.requireNonNull(audioPath, "音频文件路径不能为空");
    Objects.requireNonNull(config, "配置对象不能为空");
    if (!Files.exists(audioPath)) {
      throw new IOException("音频文件未找到: " + audioPath);
    }

    // 使用默认模型路径
    Path modelPath = Paths.get(DEFAULT_MODEL_PATH);
    if (!Files.exists(modelPath)) {
      throw new IOException("默认模型文件未找到: " + modelPath
        + "，请检查 DEFAULT_MODEL_PATH 配置或使用全参数接口指定模型。");
    }

    // 自动生成输出路径
    String audioFileName = audioPath.getFileName().toString();
    String srtFileName = audioFileName + ".srt";
    Path outputSrtPath = audioPath.resolveSibling(srtFileName);
    log.info("将使用默认模型 '{}'，根据配置生成 SRT 到 '{}'", DEFAULT_MODEL_PATH,
      outputSrtPath);

    // 使用提供的配置和默认超时
    return executeWhisperCliAndWriteSrt(modelPath, audioPath, outputSrtPath,
      config, DEFAULT_TIMEOUT_SECONDS);
  }


  /**
   * 【全功能接口】允许完全控制所有参数。 调用外部 Whisper CLI (main.exe)，捕获其标准输出，解析并生成 SRT 字幕文件。
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
  public static Path transcribeToSrtFullyConfigurable(Path modelPath,
    Path audioPath, Path outputSrtPath,
    ExternalTranscriptionConfig config, long timeoutSeconds)
    throws IOException, InterruptedException, RuntimeException {
    log.info("收到全功能接口调用请求。");
    // 直接调用核心方法
    return executeWhisperCliAndWriteSrt(modelPath, audioPath, outputSrtPath,
      config, timeoutSeconds);
  }

  // --- ================== 核心私有执行方法 ================== ---

  /**
   * 【核心实现】执行外部 Whisper CLI 进程，捕获输出，解析并写入 SRT 文件。
   *
   * @param modelPath      模型路径。
   * @param audioPath      音频路径。
   * @param outputSrtPath  要写入的 SRT 文件路径。
   * @param config         转录配置。
   * @param timeoutSeconds 超时时间。
   * @return 成功则返回 outputSrtPath，失败返回 null。
   */
  private static Path executeWhisperCliAndWriteSrt(Path modelPath,
    Path audioPath, Path outputSrtPath,
    ExternalTranscriptionConfig config, long timeoutSeconds)
    throws IOException, InterruptedException, RuntimeException {

    // 参数校验 (简化，因为公共方法已做)
    Objects.requireNonNull(modelPath);
    Objects.requireNonNull(audioPath);
    Objects.requireNonNull(outputSrtPath);
    Objects.requireNonNull(config);

    // --- 构建命令行 ---
    List<String> command = new ArrayList<>();
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
    processBuilder.redirectErrorStream(true);
    Process process = null;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    StringBuilder processOutput = new StringBuilder();

    try {
      process = processBuilder.start();
      final Process pFinal = process;
      executor.submit(
        () -> readProcessOutput(pFinal, processOutput)); // 委托给单独的方法读取

      log.info("外部 Whisper CLI 已启动 (PID: {}), 等待完成...", process.pid());

      boolean finished = waitForProcess(process, timeoutSeconds);

      // 关闭读取器并等待其结束
      executor.shutdown();
      try {
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
          log.warn("读取输出的线程在关闭时超时，强制关闭。");
          executor.shutdownNow();
        }
      } catch (InterruptedException ex) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();
        log.warn("等待输出读取线程结束时被中断。");
      }

      if (!finished) {
        log.error("外部 Whisper CLI 执行超时 (超过 {} 秒)！PID: {}",
          timeoutSeconds, process.pid());
        process.destroyForcibly();
        log.error("强制终止了超时的外部 Whisper CLI。进程部分输出:\n{}",
          getOutputExcerpt(processOutput));
        return null;
      }

      int exitCode = process.exitValue();
      log.info("外部 Whisper CLI 已结束 (PID: {}), 退出码: {}", process.pid(),
        exitCode);
      if (log.isDebugEnabled()) {
        log.debug("外部 Whisper CLI 完整输出 (PID: {}):\n{}", process.pid(),
          processOutput);
      }

      // --- 处理结果 ---
      if (exitCode == 0) {
        boolean parseSuccess = parseAndWriteSrtFromOutput(
          processOutput.toString(), outputSrtPath);
        if (parseSuccess) {
          log.info("成功为 PID {} 的进程生成 SRT 文件: {}", process.pid(),
            outputSrtPath);
          return outputSrtPath;
        } else {
          log.error(
            "CLI (PID: {}) 退出码为 0，但无法从输出中解析有效内容或写入 SRT 文件 {}",
            process.pid(), outputSrtPath);
          return null;
        }
      } else {
        log.error(
          "CLI (PID: {}) 执行失败，退出码: {}。请检查上面的日志或 DEBUG 级别的完整输出了解详情。",
          process.pid(), exitCode);
        return null;
      }
    } finally {
      if (process != null && process.isAlive()) {
        process.destroyForcibly();
        log.warn("强制销毁了仍在运行的进程 (PID: {})", process.pid());
      }
      if (!executor.isTerminated()) {
        executor.shutdownNow();
      }
    }
  }

  /**
   * 辅助方法：读取进程输出流
   */
  private static void readProcessOutput(Process process, StringBuilder output) {
    try (BufferedReader reader = new BufferedReader(
      new InputStreamReader(process.getInputStream(),
        StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append(System.lineSeparator());
        log.info("Whisper CLI (PID: {}): {}", process.pid(),
          line); // 实时打印，包含 PID
      }
    } catch (IOException e) {
      // 进程可能已结束，读取流可能失败，属于正常情况或错误情况
      if (process.isAlive()) { // 只有进程还活着时才算错误
        log.error("读取外部 Whisper CLI (PID: {}) 输出时出错: {}",
          process.pid(), e.getMessage());
      } else {
        log.trace("读取进程 (PID: {}) 输出流时遇到 IO 异常，进程可能已结束。",
          process.pid());
      }
    }
  }

  /**
   * 辅助方法：等待进程结束
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
   * 辅助方法：获取输出摘要
   */
  private static String getOutputExcerpt(StringBuilder output) {
    int maxLength = 2000; // 只显示一部分
    if (output.length() <= maxLength) {
      return output.toString();
    } else {
      return output.substring(0, maxLength) + "\n... [输出过长，已截断]";
    }
  }

  // --- SRT 解析和写入方法 (保持不变) ---
  private static boolean parseAndWriteSrtFromOutput(String cliOutput,
    Path srtPath) throws IOException {
    List<String> srtContent = new ArrayList<>();
    Pattern timePattern = Pattern.compile(
      "^\\s*\\[(\\d{2}:\\d{2}:\\d{2}\\.\\d{3}) --> (\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\]\\s*(.*)");
    Pattern textPattern = Pattern.compile("^\\s*\\S+.*");
    String[] lines = cliOutput.split("\\r?\\n");
    int segmentIndex = 1;
    String currentTimeLine = null;
    for (String line : lines) {
      Matcher timeMatcher = timePattern.matcher(line);
      if (timeMatcher.matches()) {
        currentTimeLine = timeMatcher.group(1) + " --> " + timeMatcher.group(2);
        String textAfter = timeMatcher.group(3).trim();
        if (!textAfter.isEmpty()) {
          srtContent.add(String.valueOf(segmentIndex++));
          srtContent.add(currentTimeLine);
          srtContent.add(textAfter);
          srtContent.add("");
          currentTimeLine = null;
        }
      } else if (currentTimeLine != null && textPattern.matcher(line)
        .matches()) {
        String text = line.trim();
        if (!isLikelyStatsLine(text)) { // 过滤统计行
          srtContent.add(String.valueOf(segmentIndex++));
          srtContent.add(currentTimeLine);
          srtContent.add(text);
          srtContent.add("");
          currentTimeLine = null;
        } else {
          log.trace("忽略可能的统计行: {}", line);
        }
      } else {
        currentTimeLine = null;
      }
    }
    if (srtContent.isEmpty()) {
      log.warn("未能从 CLI 输出中解析出有效的 SRT 片段。");
      return false;
    }
    log.info("从 CLI 输出中解析出 {} 个 SRT 片段，写入文件: {}",
      segmentIndex - 1, srtPath);
    try (BufferedWriter writer = Files.newBufferedWriter(srtPath,
      StandardCharsets.UTF_8, StandardOpenOption.CREATE,
      StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
      for (String srtLine : srtContent) {
        writer.write(srtLine);
        writer.newLine();
      }
    }
    return Files.exists(srtPath) && Files.size(srtPath) > 0;
  }

  /**
   * 辅助方法：判断是否可能是统计信息行
   */
  private static boolean isLikelyStatsLine(String line) {
    String lowerLine = line.toLowerCase();
    return lowerLine.startsWith("cpu tasks") || lowerLine.startsWith(
      "gpu tasks")
      || lowerLine.startsWith("compute shaders") || lowerLine.startsWith(
      "memory usage")
      || lowerLine.contains(" calls, ") || lowerLine.contains(" average")
      || lowerLine.contains(" vram") || lowerLine.contains(" ram")
      || lowerLine.matches(
      "^\\s*(loadmodel|runcomplete|run|callbacks|spectrogram|sample|encode|decode|decodestep)\\s+.*"); // 匹配统计关键字开头
  }


  // --- 示例用法 Main 方法 (展示新接口) ---
  public static void main(String[] args) {
    log.info("--- 测试调用外部 Whisper CLI Util ---");

    // --- 路径配置 ---
    String modelFilePath = DEFAULT_MODEL_PATH; // 使用默认模型
    String audioFilePath1 = "D:\\14_LearnEnglish\\6MinuteEnglish\\2017\\171005\\171005_adult_exercise.mp3";
    String audioFilePath2 = "D:\\0000\\0003_PressBriefings\\20250415\\20250415.mp3"; // 另一个测试文件

    log.info("默认模型路径: {}", modelFilePath);
    log.info("测试音频1: {}", audioFilePath1);
    log.info("测试音频2: {}", audioFilePath2);
    log.info("CLI 程序路径: {}", WHISPER_CLI_EXECUTABLE_PATH);

    // --- 示例 1：使用最简单的接口 (String 路径，全默认) ---
    log.info("\n--- 示例 1: 调用 transcribeToSrt(String audioFilePath) ---");
    try {
      long startTime = System.currentTimeMillis();
      Path resultPath1 = transcribeToSrt(audioFilePath1); // 传入 String
      long endTime = System.currentTimeMillis();
      if (resultPath1 != null) {
        log.info("示例 1 成功！SRT 文件: {}", resultPath1);
        log.info("耗时1: {} ms", formatTimestamp(endTime - startTime));
      } else {
        log.error("示例 1 失败。请检查日志。");
      }
    } catch (Exception e) {
      log.error("示例 1 执行时发生异常: {}", e.getMessage(), e);
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

      long startTime = System.currentTimeMillis();
      Path resultPath2 = transcribeToSrt(audioPath2,
        config2); // 传入 Path 和 Config
      long endTime = System.currentTimeMillis();
      if (resultPath2 != null) {
        log.info("示例 2 成功！SRT 文件: {}", resultPath2);
        log.info("耗时2: {} ms", formatTimestamp(endTime - startTime));
        // 可以在这里添加预览代码
      } else {
        log.error("示例 2 失败。请检查日志。");
      }
    } catch (Exception e) {
      log.error("示例 2 执行时发生异常: {}", e.getMessage(), e);
    }

    // --- 示例 3：使用全功能接口 (指定所有路径和配置) ---
    log.info("\n--- 示例 3: 调用 transcribeToSrtFullyConfigurable(...) ---");
    try {
      Path modelPath3 = Paths.get(modelFilePath); // 使用默认模型
      Path audioPath3 = Paths.get(audioFilePath1);
      Path outputSrtPath3 = audioPath3.resolveSibling(
        audioPath3.getFileName().toString() + ".zh_translated.srt"); // 自定义输出路径
      ExternalTranscriptionConfig config3 = new ExternalTranscriptionConfig()
        .setLanguage("zh") // 假设源是中文
        .setTranslate(true) // 翻译成英文
        .setThreads(6);    // 用 6 线程
      long timeout3 = 2400; // 40 分钟超时

      log.info(
        "示例 3 配置: 模型={}, 音频={}, 输出={}, 语言={}, 翻译={}, 线程={}, 超时={}",
        modelPath3, audioPath3, outputSrtPath3, config3.getLanguage(),
        config3.getEffectiveTranslate(), config3.getEffectiveThreads(),
        timeout3);

      long startTime = System.currentTimeMillis();
      Path resultPath3 = transcribeToSrtFullyConfigurable(modelPath3,
        audioPath3, outputSrtPath3, config3, timeout3);
      long endTime = System.currentTimeMillis();
      if (resultPath3 != null) {
        log.info("示例 3 成功！SRT 文件: {}", resultPath3);
        log.info("耗时3: {} ms", formatTimestamp(endTime - startTime));
      } else {
        log.error("示例 3 失败。请检查日志。");
      }
    } catch (Exception e) {
      log.error("示例 3 执行时发生异常: {}", e.getMessage(), e);
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
