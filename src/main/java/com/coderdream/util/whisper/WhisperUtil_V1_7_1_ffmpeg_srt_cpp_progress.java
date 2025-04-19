package com.coderdream.util.whisper; // 请确保包名与你的项目结构一致

import io.github.givimad.whisperjni.WhisperContext;
import io.github.givimad.whisperjni.WhisperFullParams;
import io.github.givimad.whisperjni.WhisperJNI;
import lombok.extern.slf4j.Slf4j; // 使用 Lombok 和 SLF4J 进行日志记录

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

/**
 * 使用 whisper.jni (io.github.givimad:whisper-jni) v1.7.1 进行语音转文本的工具类。
 * <p>
 * 功能：
 * <ul>
 *     <li>依赖外部 FFmpeg 支持多种音频格式 (MP3, WAV, Ogg, FLAC 等)。</li>
 *     <li>生成 SRT 格式的字幕文件。</li>
 *     <li>尝试启用 C++ 层的进度打印 (通过设置 {@code WhisperFullParams.printProgress = true})，
 *         进度信息将直接输出到控制台（如果底层库支持并输出）。</li>
 * </ul>
 * <p>
 * 注意：
 * <ul>
 *     <li>此版本的 whisper-jni 库不支持 Java 层的进度回调函数来获取精确百分比。</li>
 *     <li>C++ 层的进度输出格式和频率由底层 C++ 库决定，并会与 Java 日志混合显示。</li>
 * </ul>
 * <p>
 * 使用前提：
 * <ul>
 *     <li>FFmpeg 已安装并在系统的 PATH 环境变量中，或者通过 FFMPEG_PATH 环境变量 / ffmpeg.path Java 系统属性指定其可执行文件路径。</li>
 *     <li>Whisper 模型文件 (.bin) 已下载并存在于指定路径。</li>
 * </ul>
 */
@Slf4j
public class WhisperUtil_V1_7_1_ffmpeg_srt_cpp_progress {

  // FFmpeg 可执行文件路径: 优先环境变量 FFMPEG_PATH, 其次 Java 系统属性 ffmpeg.path, 最后默认为 "ffmpeg"
  private static final String FFMPEG_EXECUTABLE =
    System.getenv("FFMPEG_PATH") != null ?
      System.getenv("FFMPEG_PATH") :
      System.getProperty("ffmpeg.path", "ffmpeg");

  // 静态初始化块: 加载 JNI 库并检查 FFmpeg 可用性
  static {
    try {
      log.info("正在加载 Whisper JNI 本地库 (v1.7.1)...");
      WhisperJNI.loadLibrary();
      log.info("Whisper JNI 本地库加载成功。");
    } catch (Exception e) {
      log.error(
        "加载 Whisper JNI 本地库失败! 请确保 JNI 依赖和本地库文件正确配置。", e);
      throw new RuntimeException("无法加载 Whisper JNI 本地库", e);
    }
    checkFFmpegAvailability();
  }

  /**
   * 检查 FFmpeg 是否可执行。
   */
  private static void checkFFmpegAvailability() {
    log.info("检查 FFmpeg 可用性 (路径: {})...", FFMPEG_EXECUTABLE);
    ProcessBuilder pb = new ProcessBuilder(FFMPEG_EXECUTABLE, "-version");
    pb.redirectErrorStream(true);
    try {
      Process process = pb.start();
      StringBuilder output = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {
        String line;
        for (int i = 0; i < 5 && (line = reader.readLine()) != null; i++) {
          output.append(line).append(System.lineSeparator());
        }
      }
      boolean ffmpegSeemsAvailable = output.toString().toLowerCase()
        .contains("ffmpeg version");

      if (!process.waitFor(5, TimeUnit.SECONDS)) {
        process.destroyForcibly();
        log.warn("FFmpeg 版本检查进程超时。");
          if (!ffmpegSeemsAvailable) {
              throw new RuntimeException(
                "FFmpeg version check timed out and initial output was inconclusive.");
          }
      }
      int exitCode = process.exitValue();

      if (ffmpegSeemsAvailable && exitCode == 0) {
        log.info("FFmpeg 看起来可用。版本信息摘要: {}",
          output.toString().lines().findFirst().orElse(""));
      } else {
        log.warn(
          "FFmpeg 可能未正确配置或未找到。执行 '{} -version' 返回退出码: {}，输出:\n{}",
          FFMPEG_EXECUTABLE, exitCode, output);
        throw new RuntimeException(
          "FFmpeg 不可用或配置错误。退出码: " + exitCode);
      }

    } catch (IOException e) {
      log.error("无法执行 FFmpeg 命令 '{}'。", FFMPEG_EXECUTABLE, e);
      throw new RuntimeException("FFmpeg 不可用或配置错误: " + e.getMessage(),
        e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("等待 FFmpeg 版本检查进程时被中断。", e);
      throw new RuntimeException("FFmpeg 检查中断", e);
    }
  }

  /**
   * 转录过程的配置类。
   */
  public static class TranscriptionConfig {

    private String language = "en";
    private boolean translate = false;
    private int threads = Math.max(1,
      Runtime.getRuntime().availableProcessors() / 2);

    public TranscriptionConfig setLanguage(String language) {
      this.language = language;
      return this;
    }

    public TranscriptionConfig setTranslate(boolean translate) {
      this.translate = translate;
      return this;
    }

    public TranscriptionConfig setThreads(int threads) {
      this.threads = Math.max(1, threads);
      return this;
    }

    public String getLanguage() {
      return language;
    }

    public boolean isTranslate() {
      return translate;
    }

    public int getThreads() {
      return threads;
    }
  }

  // --- 公开 API 方法 ---

  public static String transcribe(Path modelPath, Path audioPath)
    throws IOException, RuntimeException {
    return transcribe(modelPath, audioPath, new TranscriptionConfig());
  }

  public static String transcribe(Path modelPath, Path audioPath,
    TranscriptionConfig config) throws IOException, RuntimeException {
    try (TranscriptionResult result = runWhisperTranscription(modelPath,
      audioPath, config)) {
      return result.getAsPlainText();
    }
  }

  public static Path transcribeToSrt(Path modelPath, Path audioPath)
    throws IOException, RuntimeException {
    return transcribeToSrt(modelPath, audioPath, new TranscriptionConfig());
  }

  public static Path transcribeToSrt(Path modelPath, Path audioPath,
    TranscriptionConfig config) throws IOException, RuntimeException {
    try (TranscriptionResult result = runWhisperTranscription(modelPath,
      audioPath, config)) {
      if (result.getNumSegments() <= 0) {
        log.warn("未检测到任何语音片段，无法生成 SRT 文件。音频: {}", audioPath);
        throw new RuntimeException(
          "No speech segments detected, cannot generate SRT file for: "
            + audioPath.getFileName());
      }
      Path srtPath = determineSrtPath(audioPath, config);
      log.info("准备将字幕写入文件: {}", srtPath);
      try (BufferedWriter writer = Files.newBufferedWriter(srtPath,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING)) {
        for (int i = 0; i < result.getNumSegments(); i++) {
          long t0 = result.getSegmentStartTimestamp(i);
          long t1 = result.getSegmentEndTimestamp(i);
          String text = result.getSegmentText(i);
          if (text == null || text.isBlank()) {
            log.warn("警告: 第 {} 段(索引 {}) 的文本为空或 null，跳过此段。",
              i + 1, i);
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
        log.error("写入 SRT 文件 {} 时出错: {}", srtPath, e.getMessage(), e);
        throw e;
      }
    }
  }

  // --- 内部核心转录逻辑 ---
  private static TranscriptionResult runWhisperTranscription(Path modelPath,
    Path audioPath, TranscriptionConfig config)
    throws IOException, RuntimeException {
    Objects.requireNonNull(modelPath, "模型路径不能为空");
    Objects.requireNonNull(audioPath, "音频路径不能为空");
    Objects.requireNonNull(config, "转录配置不能为空");

    File modelFile = modelPath.toFile();
    File audioFile = audioPath.toFile();
    if (!modelFile.exists() || !modelFile.isFile()) {
      throw new IOException("模型文件未找到: " + modelPath);
    }
    if (!audioFile.exists() || !audioFile.isFile()) {
      throw new IOException("音频文件未找到: " + audioPath);
    }

    WhisperJNI whisper = null;
    WhisperContext ctx = null;
    boolean transcriptionSuccess = false;

    try {
      whisper = new WhisperJNI();
      log.info("正在初始化 Whisper 上下文 (模型: {})...",
        modelPath.getFileName());
      ctx = whisper.init(modelPath);
      if (ctx == null) {
        throw new RuntimeException("初始化 Whisper 上下文失败 (返回 null)");
      }
      log.info("Whisper 上下文初始化成功.");

      log.info("正在使用 FFmpeg 加载并转换音频: {}", audioPath.getFileName());
      float[] audioData = loadAudioDataWithFFmpeg(audioFile);
      log.info("音频加载和转换完成 ({} 个样本).", audioData.length);
      if (audioData.length == 0) {
        log.warn("警告: 从 FFmpeg 获取的音频数据为空。音频文件: {}", audioPath);
        return new TranscriptionResult(ctx, whisper, 0);
      }

      WhisperFullParams whisperParams = new WhisperFullParams();
      whisperParams.language = config.getLanguage();
      whisperParams.translate = config.isTranslate();
      whisperParams.nThreads = config.getThreads();
      whisperParams.printProgress = true; // 启用 C++ 进度打印
      log.info(
        "已设置 whisperParams.printProgress = true，将依赖 C++ 库在控制台打印进度信息。");
      log.info("转录参数: language={}, translate={}, threads={}",
        whisperParams.language, whisperParams.translate,
        whisperParams.nThreads);

      log.info("开始转录 (请观察控制台输出，可能包含来自 C++ 层的进度信息)...");
      long startTime = System.currentTimeMillis();
      int result = whisper.full(ctx, whisperParams, audioData,
        audioData.length);
      long endTime = System.currentTimeMillis();
      System.out.println(); // C++ 进度输出后换行

      log.info("Whisper full() 方法执行完成，返回码: {} (耗时: {} ms).", result,
        (endTime - startTime));

      if (result != 0) {
        throw new RuntimeException("Whisper 转录失败，错误码: " + result);
      }

      int numSegments = whisper.fullNSegments(ctx);
      if (numSegments < 0) {
        throw new RuntimeException(
          "获取分段数量失败，Whisper 返回错误码: " + numSegments);
      }
      log.info("检测到 {} 个语音片段。", numSegments);
      transcriptionSuccess = true;

      return new TranscriptionResult(ctx, whisper, numSegments);

    } catch (IOException | RuntimeException e) {
      log.error("转录过程中发生错误: {}", e.getMessage(), e);
      System.out.println();
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
  private static class TranscriptionResult implements AutoCloseable {

    private final WhisperContext context;
    private final WhisperJNI whisperInterface;
    private final int numSegments;
    private boolean closed = false;

    public TranscriptionResult(WhisperContext context,
      WhisperJNI whisperInterface, int numSegments) {
      this.context = context;
      this.whisperInterface = Objects.requireNonNull(whisperInterface,
        "WhisperJNI interface cannot be null");
      this.numSegments = Math.max(0, numSegments);
    }

    public int getNumSegments() {
      checkClosed();
      return numSegments;
    }

    public long getSegmentStartTimestamp(int i) {
      checkReadyForAccess("开始时间戳");
        if (i < 0 || i >= numSegments) {
            throw new IndexOutOfBoundsException();
        }
      return whisperInterface.fullGetSegmentTimestamp0(context, i);
    }

    public long getSegmentEndTimestamp(int i) {
      checkReadyForAccess("结束时间戳");
        if (i < 0 || i >= numSegments) {
            throw new IndexOutOfBoundsException();
        }
      return whisperInterface.fullGetSegmentTimestamp1(context, i);
    }

    public String getSegmentText(int i) {
      checkReadyForAccess("段落文本");
        if (i < 0 || i >= numSegments) {
            throw new IndexOutOfBoundsException();
        }
      return whisperInterface.fullGetSegmentText(context, i);
    }

    public String getAsPlainText() {
      checkReadyForAccess("纯文本");
        if (numSegments == 0) {
            return "";
        }
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < numSegments; i++) {
        String t = getSegmentText(i);
          if (t != null) {
              sb.append(t);
          }
      }
      return sb.toString();
    }

    @Override
    public void close() {
      if (!closed && context != null) {
        try {
          log.debug(
            "正在关闭 TranscriptionResult 中的 WhisperContext..."); // 移除 getRef
          context.close();
          log.debug(
            "TranscriptionResult 中的 WhisperContext 已关闭。"); // 移除 getRef
        } catch (Exception e) {
          log.error("关闭 TranscriptionResult 中的 WhisperContext 时出错: {}",
            e.getMessage(), e); // 移除 getRef
        } finally {
          closed = true;
        }
      } else if (closed) {
        log.trace("资源已关闭");
      } else {
        log.trace("上下文为 null");
      }
    }

    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("资源已关闭");
        }
    }

    private void checkReadyForAccess(String op) {
      checkClosed();
        if (context == null) {
            throw new IllegalStateException("上下文为 null (" + op + ")");
        }
    }
  }

  // --- 内部辅助方法 ---
  private static float[] loadAudioDataWithFFmpeg(File audioFile)
    throws IOException {
    List<String> command = Arrays.asList(
      FFMPEG_EXECUTABLE, "-i", audioFile.getAbsolutePath(),
      "-nostdin", "-threads", "1", "-ar", "16000", "-ac", "1",
      "-f", "s16le", "-acodec", "pcm_s16le", "-loglevel", "error", "-"
    );
    log.debug("执行 FFmpeg 命令: {}", String.join(" ", command));
    ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectError(ProcessBuilder.Redirect.PIPE);

    // **修正点：移除 final 并初始化为 null**
    Process process = null;
    byte[] pcmBytes = null;
    StringBuilder ffmpegErrorOutput = new StringBuilder();

    try {
      // 赋值给非 final 变量
      process = pb.start();

      // 错误流读取线程 (lambda 访问 process 仍然有效，因为它是 effectively final)
      // **修正点：将 process 捕获到 final 变量中供 lambda 使用** (虽然理论上 effectively final 应该够用，但显式捕获更安全)
      final Process processFinal = process;
      Thread errorReaderThread = new Thread(() -> {
        try (BufferedReader errorReader = new BufferedReader(
          new InputStreamReader(processFinal.getErrorStream()))) {
          String line;
          while ((line = errorReader.readLine()) != null) {
            ffmpegErrorOutput.append(line).append(System.lineSeparator());
            log.trace("FFmpeg stderr: {}", line);
          }
        } catch (IOException e) {
          log.warn("读取 FFmpeg 错误流时发生错误: {}", e.getMessage());
        }
      });
      errorReaderThread.start();

      // 读取标准输出流 (PCM 数据)
      try (InputStream ffmpegInputStream = process.getInputStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        byte[] buffer = new byte[8192];
        int bytesRead;
          while ((bytesRead = ffmpegInputStream.read(buffer)) != -1) {
              baos.write(buffer, 0, bytesRead);
          }
        pcmBytes = baos.toByteArray();
      }

      // 等待进程和错误读取线程结束
      boolean finished = process.waitFor(1800, TimeUnit.SECONDS); // 30 分钟超时
      errorReaderThread.join(1000); // 等待错误线程最多1秒

      if (!finished) {
        process.destroyForcibly();
        throw new IOException("FFmpeg 处理超时: " + audioFile.getName());
      }

      // 检查退出码和错误输出
      int exitCode = process.exitValue();
      String errorDetails = ffmpegErrorOutput.toString().trim();
      if (exitCode != 0) {
        log.error("FFmpeg 转换失败，退出码: {}。文件: {}.\nFFmpeg 错误输出:\n{}",
          exitCode, audioFile.getAbsolutePath(),
          errorDetails.isEmpty() ? "(无错误输出)" : errorDetails);
        throw new IOException(
          "FFmpeg failed with exit code " + exitCode + (errorDetails.isEmpty()
            ? "" : ". Details: " + errorDetails));
      }
      if (pcmBytes == null || pcmBytes.length == 0) {
        log.warn("FFmpeg 成功但未输出 PCM 数据: {}", audioFile.getName());
          if (!errorDetails.isEmpty()) {
              log.warn("FFmpeg 错误流输出:\n{}", errorDetails);
          }
        return new float[0];
      }
      log.debug("FFmpeg 转换成功，读取了 {} 字节的 PCM 数据。", pcmBytes.length);

      // 处理 PCM 数据
      if (pcmBytes.length % 2 != 0) {
        pcmBytes = Arrays.copyOf(pcmBytes, pcmBytes.length - 1);
      }
      if (pcmBytes.length == 0) {
        return new float[0];
      }
      int numSamples = pcmBytes.length / 2;
      float[] audioFloats = new float[numSamples];
      ByteBuffer buffer = ByteBuffer.wrap(pcmBytes)
        .order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < numSamples; i++) {
            audioFloats[i] = (float) buffer.getShort() / 32768.0f;
        }
      return audioFloats;

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("等待 FFmpeg 时被中断。", e);
      // **修正点：在 catch 块中检查 process 是否为 null**
        if (process != null) {
            process.destroyForcibly();
        }
      throw new RuntimeException("FFmpeg process or error reader interrupted",
        e);
    } catch (IOException e) {
      log.error("执行 FFmpeg 或读写流时 IO 错误: {}", e.getMessage(), e);
      // **修正点：在 catch 块中检查 process 是否为 null**
        if (process != null) {
            process.destroyForcibly();
        }
      throw e;
    } finally {
      // **修正点：在 finally 块中检查 process 是否为 null**
      if (process != null && process.isAlive()) {
        log.warn("FFmpeg 进程在方法退出时仍存活，强制销毁。");
        process.destroyForcibly();
      }
    }
  }


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

  private static Path determineSrtPath(Path audioPath,
    TranscriptionConfig config) {
    Path parentDir = audioPath.getParent();
      if (parentDir == null) {
          parentDir = Paths.get("").toAbsolutePath();
      }
    String originalFileName = audioPath.getFileName().toString();
    String baseName;
    int lastDotIndex = originalFileName.lastIndexOf('.');
    baseName =
      (lastDotIndex > 0 && lastDotIndex < originalFileName.length() - 1)
        ? originalFileName.substring(0, lastDotIndex) : originalFileName;
    String langSuffix = config.isTranslate() ? "en" : (
      config.getLanguage() == null || config.getLanguage().isEmpty()
        || "auto".equalsIgnoreCase(config.getLanguage()) ? "auto"
        : config.getLanguage().toLowerCase(Locale.ROOT));
    String srtFileName = baseName + "." + langSuffix + ".srt";
    return parentDir.resolve(srtFileName);
  }

  // --- 示例用法 Main 方法 ---
  public static void main(String[] args) {
    // --- ==================== 配置区域 ==================== ---
    String modelFilePath =
      "D:\\00_Green\\WhisperDesktop\\models" + File.separator
        + "ggml-model-whisper-medium.bin";
    String audioFilePath = "D:\\0000\\0003_PressBriefings\\20250415\\20250415.mp3";
    // --- ================= 配置区域结束 ================== ---

    log.info("--- Whisper 转录工具 (whisper-jni v1.7.1 + FFmpeg) ---");
    log.info("模型文件路径: {}", modelFilePath);
    log.info("音频文件路径: {}", audioFilePath);
    log.info("FFmpeg 路径: {}", FFMPEG_EXECUTABLE);

    Path modelPath = Paths.get(modelFilePath);
    Path audioPath = Paths.get(audioFilePath);

    if (!Files.exists(modelPath)) {
      log.error("错误：模型文件未找到: {}", modelPath);
      return;
    }
    if (!Files.exists(audioPath)) {
      log.error("错误：音频文件未找到: {}", audioPath);
      return;
    }
    log.info("文件检查通过。");

    // --- 场景 1: 转录英文音频为纯文本 ---
    log.info("\n--- 场景 1: 转录英文音频为纯文本 ---");
    TranscriptionConfig configEnText = new TranscriptionConfig().setLanguage(
      "en");
    try {
      System.out.println("\nJava: ===> 开始场景 1 转录...");
      long start = System.currentTimeMillis();
      String plainText = transcribe(modelPath, audioPath, configEnText);
      long end = System.currentTimeMillis();
      System.out.println(
        "\nJava: ===> 场景 1 转录完成 (耗时: " + (end - start) + " ms)。");
      log.info("场景 1 纯文本结果:\n{}", plainText);
    } catch (Exception e) {
      System.out.println("\nJava: ===> 场景 1 转录失败。");
      log.error("场景 1 执行失败: {}", e.getMessage(), e);
    }

    // --- 场景 2: 转录音频为 SRT (自动检测语言) ---
    log.info("\n--- 场景 2: 转录音频为 SRT (自动检测语言) ---");
    TranscriptionConfig configAutoSrt = new TranscriptionConfig().setLanguage(
      "auto");
    try {
      System.out.println("\nJava: ===> 开始场景 2 生成 SRT (自动语言)...");
      long start = System.currentTimeMillis();
      Path srtPath = transcribeToSrt(modelPath, audioPath, configAutoSrt);
      long end = System.currentTimeMillis();
      System.out.println(
        "\nJava: ===> 场景 2 生成 SRT 完成 (耗时: " + (end - start) + " ms)。");
      log.info("场景 2 SRT 文件已生成: {}", srtPath);
    } catch (Exception e) {
      System.out.println("\nJava: ===> 场景 2 生成 SRT 失败。");
      log.error("场景 2 执行失败: {}", e.getMessage(), e);
    }

    // --- 场景 3: 将非英文音频翻译为英文 SRT ---
    log.info("\n--- 场景 3: 将非英文音频翻译为英文 SRT ---");
    TranscriptionConfig configTranslateSrt = new TranscriptionConfig().setLanguage(
      "auto").setTranslate(true);
    try {
        if (modelFilePath.toLowerCase().endsWith(".en.bin")) {
            log.warn("警告：当前模型 '{}' 是仅英文模型，可能不支持翻译。",
              modelFilePath);
        }
      System.out.println("\nJava: ===> 开始场景 3 翻译并生成 SRT...");
      long start = System.currentTimeMillis();
      Path srtPathTranslated = transcribeToSrt(modelPath, audioPath,
        configTranslateSrt);
      long end = System.currentTimeMillis();
      System.out.println(
        "\nJava: ===> 场景 3 翻译并生成 SRT 完成 (耗时: " + (end - start)
          + " ms)。");
      log.info("场景 3 翻译后的 SRT (英文) 文件已生成: {}", srtPathTranslated);
    } catch (Exception e) {
      System.out.println("\nJava: ===> 场景 3 翻译并生成 SRT 失败。");
      log.error("场景 3 执行失败: {}", e.getMessage(), e);
    }

    log.info("\n--- 所有测试场景执行完毕 ---");
  }
}
