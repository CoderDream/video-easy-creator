package com.coderdream.util.youtube.demo02;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * 命令执行工具类，用于执行 Windows 控制台命令
 */
@Slf4j
public class CommandUtil {

  private static final String YT_DLP_COMMAND = "yt-dlp";
  private static final String FFPROBE_COMMAND = "ffprobe";
  private static final String FFMPEG_COMMAND = "ffmpeg";

  private static final int MAX_RETRIES = 3; // 最大重试次数
  private static final int INITIAL_WAIT_SECONDS = 10; // 初始等待时间
  private static final int MAX_WAIT_SECONDS = 60; // 最大等待时间
  private static final List<String> USER_AGENTS = Arrays.asList(
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Safari/605.1.15",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0"
  );

  /**
   * 执行指定的 Windows 控制台命令，并将执行过程中的输出显示在控制台中，并记录执行时间
   *
   * @param command 要执行的命令字符串
   */
  public static void executeCommand(List<String> command) {
    LocalDateTime startTime = LocalDateTime.now(); // 记录开始时间
    String finalCommand = String.join(" ", command);

    try (BufferedReader reader = getCommandReader(command)) {
      log.info("开始执行命令: {}", finalCommand); // 打印由 List 组装后的完整命令
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line); // 输出到控制台
        log.debug(line); // 记录到日志，方便调试
      }
      Duration duration = Duration.between(startTime, LocalDateTime.now());
      String formattedTime = CdTimeUtil.formatDuration(duration.toMillis());
      log.info("命令执行完成，执行耗时：{}", formattedTime);
    } catch (IOException e) {
      log.error("执行命令时发生IO异常: {}", e.getMessage(), e);
    }
  }

  /**
   * 执行命令并获取 BufferedReader
   *
   * @param commandList 要执行的命令字符串列表
   * @return BufferedReader
   * @throws IOException
   */
  private static BufferedReader getCommandReader(List<String> commandList)
    throws IOException {
    ProcessBuilder builder;
    List<String> fullCommandList = new ArrayList<>();

    // 根据操作系统选择不同的命令前缀
    if (System.getProperty("os.name").toLowerCase().contains("win")) {
      // Windows 系统
      fullCommandList.add("cmd.exe");
      fullCommandList.add("/c");
      fullCommandList.addAll(commandList);
    } else {
      // macOS 或 Linux 系统
      // 使用 bash -c 可以执行更复杂的命令，包括管道、重定向等
      fullCommandList.add("bash");
      fullCommandList.add("-c");
      fullCommandList.add(String.join(" ", commandList)); // 合并为单个命令字符串
    }

    builder = new ProcessBuilder(fullCommandList);
    builder.redirectErrorStream(true); // 将错误输出重定向到标准输出
    Process process = builder.start(); // 启动进程

    // 获取控制台的字符编码
    Charset consoleCharset = getConsoleCharset();

    return new BufferedReader(
      new InputStreamReader(process.getInputStream(), consoleCharset));
  }

  /**
   * 获取控制台字符编码 (Java 9+)
   *
   * @return Charset
   */
  private static Charset getConsoleCharset() {
    Console console = System.console();
    if (console != null) {
      return console.charset();
    } else {
      return Charset.defaultCharset();
    }
  }

  /**
   * 使用 yt-dlp 下载最佳 1080p 视频。
   *
   * @param videoLink      视频链接
   * @param outputFileName 目标 MP4 文件名 (包含路径)
   */
  public static void downloadBest1080p(String videoLink,
    String outputFileName) {
    downloadBestResolution(videoLink, outputFileName, "1920x1080");
  }

  /**
   * 使用 yt-dlp 下载最佳 720p 视频。
   *
   * @param videoLink      视频链接
   * @param outputFileName 目标 MP4 文件名 (包含路径)
   */
  public static void downloadBest720p(String videoLink, String outputFileName) {
    downloadBestResolution(videoLink, outputFileName, "1280x720");
  }

  /**
   * 下载最佳指定分辨率的视频。
   *
   * @param videoLink      视频链接。
   * @param outputFileName 输出文件名。
   * @param resolution     目标分辨率，例如 "1280x720" 或 "1920x1080"。
   */
  public static void downloadBestResolution(String videoLink,
    String outputFileName, String resolution) {
    String proxyProtocol = "http"; // 或 "https", "socks5"
    String proxyAddress = "127.0.0.1";
    int proxyPort = OperatingSystem.getProxyPort();
    downloadBestResolutionAndMerge(videoLink, outputFileName, proxyProtocol, resolution,
      proxyAddress, proxyPort);
  }

  /**
   * 使用 yt-dlp 分别下载音频和视频，然后使用 FFmpeg 合并。
   *
   * @param videoLink      视频链接。
   * @param outputFileName 输出 MP4 文件的完整路径。
   * @param resolution     目标分辨率，例如 "1280x720" 或 "1920x1080"。
   * @param proxyProtocol  代理协议 (例如: http, https, socks5)。 可以为null或空字符串，表示不使用代理
   * @param proxyAddress   代理地址。 可以为null或空字符串，表示不使用代理
   * @param proxyPort      代理端口。 如果没有代理，该值会被忽略
   */
  public static void downloadBestResolutionAndMerge(String videoLink, String outputFileName,
    String resolution, String proxyProtocol, String proxyAddress, int proxyPort) {

    log.info("开始下载视频并合并: videoLink={}, outputFileName={}, resolution={}, proxy={}:{} ({})",
      videoLink, outputFileName, resolution, proxyAddress, proxyPort, proxyProtocol);

    String baseFileName = outputFileName.substring(0, outputFileName.lastIndexOf(".")); // 去除扩展名
    String videoOutputFileName = baseFileName + "_video.mp4";
    String audioOutputFileName = baseFileName + "_audio.m4a"; // m4a 格式更常见于音频

    String bestVideoFormat = null;
    List<String> availableAudioFormats = new ArrayList<>();

    // 1. 获取可用格式
    List<String> formats = listFormats(videoLink, proxyProtocol, proxyAddress, proxyPort);
    if (formats.isEmpty()) {
      log.error("未找到任何可用格式.");
      return;
    }

    // 2. 选择最佳视频和音频格式
    for (String format : formats) {
      log.debug("可用格式: {}", format);
      if (format.contains(resolution) && format.contains("video only")) {
        if (bestVideoFormat == null || getBitrate(format) > getBitrate(
          bestVideoFormat)) {
          bestVideoFormat = format;
        }
      }

      if (format.contains("audio only")) {
        availableAudioFormats.add(format);
      }
    }

    if (bestVideoFormat == null) {
      log.error("未找到最佳视频格式.");
      return;
    }

    String videoId = extractFormatId(bestVideoFormat);
    log.info("选择的视频格式ID: {}", videoId);

    // 3. 下载视频
    boolean videoDownloadSuccess = downloadStream(videoLink, videoOutputFileName, videoId,
      proxyProtocol, proxyAddress, proxyPort);

    // 4. 下载音频 (尝试所有可用的音频格式)
    boolean audioDownloadSuccess = false;
    String downloadedAudioFormat = null;
    for (String audioFormat : availableAudioFormats) {
      String audioId = extractFormatId(audioFormat);
      log.info("尝试下载音频格式ID: {}", audioId);
      audioDownloadSuccess = retryDownloadStream(videoLink, audioOutputFileName, audioId,
        proxyProtocol, proxyAddress, proxyPort);

      if (audioDownloadSuccess) {
        downloadedAudioFormat = audioFormat;
        break; // 下载成功，退出循环
      } else {
        log.warn("音频格式 {} 下载失败, 尝试下一个.", audioId);
      }
    }

    if (!audioDownloadSuccess) {
      log.error("所有音频格式下载失败.");
    }

    // 5. 合并音视频
    if (videoDownloadSuccess && audioDownloadSuccess) {
      boolean mergeSuccess = mergeVideoAndAudio(videoOutputFileName, audioOutputFileName, outputFileName);
      if (mergeSuccess) {
        log.info("成功合并音视频到: {}", outputFileName);
      } else {
        log.error("合并音视频失败.");
      }
    } else {
      log.error("视频或音频下载失败，无法合并.");
    }

    // 6. 清理临时文件 (可选)
    // cleanUpTempFiles(videoOutputFileName, audioOutputFileName);
  }

  /**
   * 重试下载视频流
   *  @param videoLink      视频链接
   * @param outputFileName 输出文件名
   * @param formatId       格式ID
   * @param proxyProtocol  代理协议
   * @param proxyAddress   代理地址
   * @param proxyPort      代理端口
   * @return
   */
  private static boolean retryDownloadStream(String videoLink, String outputFileName, String formatId,
    String proxyProtocol, String proxyAddress, int proxyPort) {
    boolean audioDownloadSuccess = false;
    for (int i = 0; i < MAX_RETRIES; i++) {
      int waitSeconds = INITIAL_WAIT_SECONDS + new Random().nextInt(MAX_WAIT_SECONDS - INITIAL_WAIT_SECONDS);
      log.info("尝试下载音频 (尝试次数: {}/{})", i + 1, MAX_RETRIES);
      audioDownloadSuccess = downloadStream(videoLink, outputFileName, formatId,
        proxyProtocol, proxyAddress, proxyPort);
      if (audioDownloadSuccess) {
        break; // 下载成功，退出循环
      } else {
        log.warn("音频下载失败，将在 {} 秒后重试.", waitSeconds);
        try {
          Thread.sleep(waitSeconds * 1000); // 等待一段时间后重试
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt(); // 恢复中断状态
          log.error("线程中断: {}", e.getMessage(), e);
          break;
        }
      }
    }
    return audioDownloadSuccess;
  }

  /**
   * 下载指定格式的视频流
   *
   * @param videoLink      视频链接
   * @param outputFileName 输出文件名
   * @param formatId       格式ID
   * @param proxyProtocol  代理协议
   * @param proxyAddress   代理地址
   * @param proxyPort      代理端口
   * @return true 下载成功, false 下载失败
   */
  private static boolean downloadStream(String videoLink, String outputFileName, String formatId,
    String proxyProtocol, String proxyAddress, int proxyPort) {
    List<String> downloadCommand = new ArrayList<>();
    downloadCommand.add(YT_DLP_COMMAND);

    // 添加 User-Agent
    downloadCommand.add("--user-agent");
    downloadCommand.add(USER_AGENTS.get(new Random().nextInt(USER_AGENTS.size())));

    if (proxyAddress != null && !proxyAddress.isEmpty() && proxyProtocol != null
      && !proxyProtocol.isEmpty()) {
      downloadCommand.add("--proxy");
      downloadCommand.add(
        proxyProtocol + "://" + proxyAddress + ":" + proxyPort);
    }

    downloadCommand.add("-f");
    downloadCommand.add(formatId);
    downloadCommand.add("-o");
    downloadCommand.add(outputFileName);
    downloadCommand.add(videoLink);

    String finalDownloadCommand = String.join(" ", downloadCommand);
    log.info("执行下载命令: {}", finalDownloadCommand);

    int exitCode = executeCommandAndGetExitCode(downloadCommand);

    File outputFile = new File(outputFileName);
    boolean success = outputFile.exists() && outputFile.length() > 0 && exitCode == 0;
    if (success) {
      log.info("成功下载: {} to {}", formatId, outputFileName);
    } else {
      log.error("下载失败: {} to {}, exitCode={}", formatId, outputFileName, exitCode);
    }
    return success;
  }

  /**
   * 执行命令并获取退出码
   *
   * @param command 命令列表
   * @return 退出码
   */
  private static int executeCommandAndGetExitCode(List<String> command) {
    ProcessBuilder builder = new ProcessBuilder(command);
    builder.redirectErrorStream(true);
    Process process = null;
    try {
      process = builder.start();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          System.out.println(line);
          log.debug(line);
        }
      }

      int exitCode = process.waitFor();
      log.info("命令执行完成，退出码：{}", exitCode);
      return exitCode;

    } catch (IOException e) {
      log.error("执行命令时发生IO异常: {}", e.getMessage(), e);
      return -1; // 返回一个错误码
    } catch (InterruptedException e) {
      log.error("命令执行被中断: {}", e.getMessage(), e);
      Thread.currentThread().interrupt();
      return -1;
    } finally {
      if (process != null) {
        process.destroy(); // 确保进程被销毁
      }
    }
  }

  /**
   * 使用 FFmpeg 合并视频和音频文件
   *
   * @param videoFile   视频文件路径
   * @param audioFile   音频文件路径
   * @param outputFile  输出文件路径
   * @return true 合并成功, false 合并失败
   */
  private static boolean mergeVideoAndAudio(String videoFile, String audioFile, String outputFile) {
    List<String> command = new ArrayList<>();
    command.add(FFMPEG_COMMAND);
    command.add("-i");
    command.add(videoFile);
    command.add("-i");
    command.add(audioFile);
    command.add("-c");
    command.add("copy");
    command.add(outputFile);

    String mergeCommand = String.join(" ", command);
    log.info("执行合并命令: {}", mergeCommand);

    executeCommand(command);

    File mergedFile = new File(outputFile);
    boolean success = mergedFile.exists() && mergedFile.length() > 0;
    if (success) {
      log.info("成功合并文件: {} 和 {} 到 {}", videoFile, audioFile, outputFile);
    } else {
      log.error("合并文件失败: {} 和 {} 到 {}", videoFile, audioFile, outputFile);
    }
    return success;
  }

  /**
   * 列出可用格式，返回格式列表。
   *  @param videoLink     视频链接
   * @param proxyProtocol 代理协议
   * @param proxyAddress  代理地址
   * @param proxyPort     代理端口
   * @return
   */
  private static List<String> listFormats(String videoLink, String proxyProtocol, String proxyAddress, int proxyPort) {
    // 构建列出格式的命令
    List<String> listFormatsCommandList = new ArrayList<>();
    listFormatsCommandList.add(YT_DLP_COMMAND);

    // 添加 User-Agent
    listFormatsCommandList.add("--user-agent");
    listFormatsCommandList.add(USER_AGENTS.get(new Random().nextInt(USER_AGENTS.size())));

    if (proxyAddress != null && !proxyAddress.isEmpty() && proxyProtocol != null
      && !proxyProtocol.isEmpty()) {
      listFormatsCommandList.add("--proxy");
      listFormatsCommandList.add(
        proxyProtocol + "://" + proxyAddress + ":" + proxyPort);
    }
    listFormatsCommandList.add("-F");
    listFormatsCommandList.add(videoLink);

    String listFormatsCommand = String.join(" ",
      listFormatsCommandList); // 将 List<String> 转换为 String

    log.debug("列出格式的命令: {}", listFormatsCommand);

    List<String> formats = listFormatsInternal(listFormatsCommand);
    return formats;
  }

  /**
   * 列出可用格式，返回格式列表。（内部方法）
   *
   * @param listFormatsCommand 列表命令
   * @return
   */
  private static List<String> listFormatsInternal(String listFormatsCommand) {
    List<String> formats = new ArrayList<>();
    try (BufferedReader reader = getCommandReader(
      Arrays.asList(listFormatsCommand))) {
      String line;
      boolean startCapture = false;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("ID")) {
          startCapture = true;
          continue;
        }
        if (startCapture && !line.startsWith("---")) { // Skip separator lines
          formats.add(line);
        }
      }
    } catch (IOException e) {
      log.error("列出格式时发生错误: {}", e.getMessage(), e);
    }
    return formats;
  }

  /**
   * 清理临时文件
   *
   * @param videoFile 视频文件路径
   * @param audioFile 音频文件路径
   */
  private static void cleanUpTempFiles(String videoFile, String audioFile) {
    try {
      FilesUtil.deleteIfExists(videoFile);
      FilesUtil.deleteIfExists(audioFile);
      log.info("临时文件清理完成: {}, {}", videoFile, audioFile);
    } catch (IOException e) {
      log.warn("清理临时文件时发生错误: {}", e.getMessage());
    }
  }

  /**
   * 从格式信息中提取格式 ID
   *
   * @param format 格式信息字符串
   * @return 格式 ID
   */
  private static String extractFormatId(String format) {
    String id = format.substring(0, format.indexOf(" "));
    return id.trim();
  }

  /**
   * 获取比特率
   *
   * @param format 格式信息
   * @return 比特率
   */
  private static int getBitrate(String format) {
    Pattern pattern = Pattern.compile("(\\d+)k");
    Matcher matcher = pattern.matcher(format);
    if (matcher.find()) {
      try {
        return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        log.warn("解析比特率失败: format={}, error={}", format, e.getMessage());
        return 0;
      }
    }
    return 0;
  }

  /**
   * 从格式信息中提取音频比特率
   *
   * @param format 格式信息字符串
   * @return 音频比特率
   */
  private static int getAudioBitrate(String format) {
    Pattern pattern = Pattern.compile("(\\d+)k");
    Matcher matcher = pattern.matcher(format);
    if (matcher.find()) {
      try {
        return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        log.warn("解析音频比特率失败: format={}, error={}", format, e.getMessage());
        return 0;
      }
    }
    return 0;
  }

  /**
   * 使用 FFmpeg 提取 MP4 文件的左声道到指定的 MP3 文件。
   *
   * @param inputMp4  输入 MP4 文件的完整路径
   * @param outputMp3 输出 MP3 文件的完整路径
   */
  public static void extractLeftChannel(String inputMp4, String outputMp3) {
    List<String> command = new ArrayList<>();
    command.add(FFMPEG_COMMAND);
    command.add("-i");
    command.add(inputMp4);
    command.add("-filter_complex");
    command.add("channelsplit=channel_layout=stereo:channels=FL[left]");
    command.add("-map");
    command.add("[left]");
    command.add("-ac");
    command.add("1");
    command.add("-acodec");
    command.add("libmp3lame");
    command.add(outputMp3);

    log.info("开始提取左声道: input={}, output={}", inputMp4, outputMp3);
    executeCommand(command);

    log.info("成功提取左声道到: {}", outputMp3);
  }

  /**
   * 使用 FFmpeg 探测 MP4 文件是否有音频流
   *
   * @param inputMp4 输入 MP4 文件的完整路径
   * @return true 有音频流，false 没有音频流
   */
  public static boolean hasAudioStream(String inputMp4) {
    log.info("开始检测音频流: input={}", inputMp4);
    List<String> command = new ArrayList<>();
    command.add(FFPROBE_COMMAND);
    command.add("-i");
    command.add(inputMp4);
    command.add("-show_streams");
    command.add("-select_streams");
    command.add("a");
    command.add("-loglevel");
    command.add("error"); // 只显示错误信息，避免大量无用输出

    try {
      ProcessBuilder builder = new ProcessBuilder(command);
      builder.redirectErrorStream(true);
      Process process = null;
      process = builder.start();

      try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          log.debug("ffprobe 输出: {}", line);
          // 如果ffprobe 有输出，说明有音频流，如果没有任何流信息输出，说明没有
          log.info("检测到音频流.");
          return true;
        }
      }

      int exitCode = process.waitFor();
      log.info("ffprobe 命令执行完毕，退出码: {}", exitCode);
      return false; // 如果命令执行成功且没有输出，则认为没有音频流
    } catch (IOException | InterruptedException e) {
      log.error("探测音频流时发生错误: {}", e.getMessage(), e);
      return false; // 发生异常也返回 false
    } finally {
      log.info("音频流检测完成: input={}", inputMp4);
    }
  }

  /**
   * 从视频文件中提取纯视频文件. 纯视频
   *
   * @param inputVideo  输入视频文件的完整路径
   * @param outputVideo 输出纯视频文件的完整路径
   */
  public static void extractPureVideo(String inputVideo, String outputVideo) {
    List<String> command = new ArrayList<>();
    command.add(FFMPEG_COMMAND);
    command.add("-i");
    command.add(inputVideo);
    command.add("-an"); // 移除音频
    command.add("-c:v");
    command.add("copy"); // 视频流直接复制，不重新编码
    command.add(outputVideo);

    log.info("开始提取纯视频: input={}, output={}", inputVideo, outputVideo);
    executeCommand(command);

    log.info("成功提取纯视频到: {}", outputVideo);
  }

  /**
   * 修改了executeCommand的入参类型为List<String> 为了兼容
   *
   * @param command 要执行的命令字符串
   */
  @Deprecated
  public static void executeCommand(String command) {
    executeCommand(Arrays.asList(command));
  }

  @Deprecated
  private static BufferedReader getCommandReader(String command)
    throws IOException {
    return getCommandReader(Arrays.asList(command));
  }
}
