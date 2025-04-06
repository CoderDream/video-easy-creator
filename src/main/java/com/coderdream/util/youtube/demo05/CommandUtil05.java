package com.coderdream.util.youtube.demo05;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.youtube.YouTubeApiUtil; // 假设这个类包含代理设置
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * 命令执行工具类，用于执行 Windows 控制台命令
 */
@Slf4j
public class CommandUtil05 {

  private static final int MAX_RETRIES = 3; // 最大重试次数
  private static final long RETRY_DELAY = 2000; // 重试延迟 (毫秒)

  /**
   * 执行指定的 Windows 控制台命令，并将执行过程中的输出显示在控制台中，并记录执行时间
   *
   * @param command 要执行的命令字符串
   * @return 命令执行成功是否成功
   */
  public static boolean executeCommand(List<String> command) {
    LocalDateTime startTime = LocalDateTime.now(); // 记录开始时间
    String finalCommand = String.join(" ", command);
    boolean success = false;  // 添加一个标志来跟踪命令是否成功执行
    try (BufferedReader reader = getCommandReader(command)) {
      log.info("开始执行命令: {}", finalCommand); // 打印由 List 组装后的完整命令
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
      // 等待进程执行完成
      int exitCode = getProcessExitCode(command);
      if(exitCode != 0){
        log.error("命令执行失败，退出码：{}", exitCode);
      }
      success = exitCode == 0;  // 将退出码设为成功
      Duration duration = Duration.between(startTime, LocalDateTime.now());
      String formattedTime = CdTimeUtil.formatDuration(duration.toMillis());
      log.info("命令执行完成，执行耗时：{}, 执行结果: {}", formattedTime, success ? "成功" : "失败");
    } catch (IOException e) {
      log.error("执行命令时发生IO异常: {}", e.getMessage(), e);
    } catch (InterruptedException e) {
      log.error("命令执行被中断：{}", e.getMessage(), e);
    }
    return success; //返回成功与否，供其他方法调用判断
  }

  /**
   * 封装获取进程退出码的方法
   *
   * @param command
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  private static int getProcessExitCode(List<String> command)
    throws IOException, InterruptedException {
    ProcessBuilder builder;
    List<String> fullCommandList = new ArrayList<>();

    // 根据操作系统选择不同的命令前缀
    if (System.getProperty("os.name").toLowerCase().contains("win")) {
      // Windows 系统
      fullCommandList.add("cmd.exe");
      fullCommandList.add("/c");
      fullCommandList.addAll(command);
    } else {
      // macOS 或 Linux 系统
      // 使用 bash -c 可以执行更复杂的命令，包括管道、重定向等
      fullCommandList.add("bash");
      fullCommandList.add("-c");
      fullCommandList.add(String.join(" ", command)); // 合并为单个命令字符串
    }
    builder = new ProcessBuilder(fullCommandList);
    builder.redirectErrorStream(true); // 将错误输出重定向到标准输出
    Process process = builder.start(); // 启动进程
    return process.waitFor();
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
   * 封装检查文件是否存在的方法
   *
   * @param filePath 文件路径
   * @return 文件是否存在
   */
  private static boolean fileExists(String filePath) {
    File file = new File(filePath);
    return file.exists() && file.isFile();
  }

  /**
   * 修改了downloadBest720p 和downloadBest1080p
   *
   * @param videoLink      视频链接。
   * @param outputFileName 输出文件名。
   */
  public static void downloadBest720p(String videoLink, String outputFileName) {
    downloadBest(videoLink, outputFileName, "1280x720");
  }

  public static void downloadBest1080p(String videoLink, String outputFileName) {
    downloadBest(videoLink, outputFileName, "1920x1080");
  }

  /**
   * 优化后的视频下载方法
   *
   * @param videoLink      视频链接。
   * @param outputFileName 输出文件名。
   * @param resolution     清晰度
   */
  public static void downloadBest(String videoLink, String outputFileName,
    String resolution) {
    YouTubeApiUtil.enableProxy();  // 启用代理

    String baseName =
      outputFileName.substring(0, outputFileName.lastIndexOf(".")); // 不带后缀的文件名
    String videoFile = baseName + "_video.mp4";  // 纯视频文件名
    String audioFile = baseName + "_audio.mp4";  // 纯音频文件名

    String videoId = null;
    String audioId = null;
    // 1. 获取视频和音频格式 ID
    try {
      videoId = getVideoFormatId(videoLink, resolution);
      audioId = getAudioFormatId(videoLink);

    } catch (IOException e) {
      log.error("获取视频或音频格式ID失败: {}", e.getMessage(), e);
      return;
    }

    if (videoId == null || audioId == null) {
      log.error("无法找到最佳视频或音频格式。");
      return;
    }

    // 2. 下载视频和音频

    boolean videoDownloaded = downloadMedia(videoLink, videoId, videoFile);
    boolean audioDownloaded = downloadMedia(videoLink, audioId, audioFile);
    if (!videoDownloaded || !audioDownloaded) {
      log.error("视频或音频下载失败，无法合并。");
      return;
    }

    //3.合并音频和视频
    boolean merged = mergeVideoAndAudio(videoFile, audioFile, outputFileName);
    if(!merged){
      log.error("视频和音频合并失败");
      return;
    }

    //4. 删除临时文件
    deleteTemporaryFiles(videoFile, audioFile);
  }


  /**
   * 提取最佳视频流的格式id, 可重试。
   * @param videoLink
   * @param resolution
   * @return
   * @throws IOException
   */
  private static String getVideoFormatId(String videoLink, String resolution)
    throws IOException {
    String videoId = null;
    for (int i = 0; i < MAX_RETRIES; i++) {
      String listFormatsCommand =
        "yt-dlp -F \"" + videoLink + "\""; // 不要代理，因为只是获取format
      List<String> formats = listFormats(listFormatsCommand);
      for (String format : formats) {
        if (format.contains(resolution) && format.contains("video only")) {
          if (videoId == null || getBitrate(format) > getBitrate(
            videoId)) {
            videoId = extractFormatId(format);
          }
        }
      }
      if(videoId != null){
        break;
      } else {
        log.warn("第 {} 次尝试获取视频id，没有找到最佳清晰度，{}后重试", i + 1, RETRY_DELAY);
        try {
          Thread.sleep(RETRY_DELAY);
        } catch (InterruptedException e) {
          log.error("线程中断: {}", e.getMessage(), e);
          Thread.currentThread().interrupt(); // 重新设置中断状态
          break;
        }
      }
    }
    return videoId;
  }

  /**
   *  提取最佳音轨的格式id，可重试
   * @param videoLink
   * @return
   * @throws IOException
   */
  private static String getAudioFormatId(String videoLink) throws IOException {
    String audioId = null;
    for (int i = 0; i < MAX_RETRIES; i++) {
      String listFormatsCommand =
        "yt-dlp -F \"" + videoLink + "\""; //  代理,
      List<String> formats = listFormats(listFormatsCommand);
      for (String format : formats) {
        if (format.contains("audio only")) {
          if (audioId == null || getAudioBitrate(format) > getAudioBitrate(
            audioId)) {
            audioId = extractFormatId(format);
          }
        }
      }
      if(audioId != null){
        break;
      } else {
        log.warn("第 {} 次尝试获取音频id，{}后重试", i + 1, RETRY_DELAY);
        try {
          Thread.sleep(RETRY_DELAY);
        } catch (InterruptedException e) {
          log.error("线程中断: {}", e.getMessage(), e);
          Thread.currentThread().interrupt(); // 重新设置中断状态
          break;
        }
      }
    }
    return audioId;
  }

  /**
   * 封装了下载音视频的逻辑
   *
   * @param videoLink
   * @param formatId
   * @param outputFile
   * @return
   */
  public static boolean downloadMedia(String videoLink, String formatId,
    String outputFile) {
    List<String> downloadCommand = new ArrayList<>();
    downloadCommand.add("yt-dlp");
    downloadCommand.add("-f");
    downloadCommand.add(formatId);
    downloadCommand.add("-o");
    downloadCommand.add(outputFile);
    downloadCommand.add(videoLink);
    // 1,命令执行
    return executeCommand(downloadCommand);
  }

  /**
   * 从视频文件中提取纯视频文件. 纯视频
   *
   * @param inputVideo  输入视频文件的完整路径
   * @param outputVideo 输出纯视频文件的完整路径
   */
  public static void extractPureVideo(String inputVideo, String outputVideo) {
    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    command.add("-i");
    command.add(inputVideo);
    command.add("-an"); // 移除音频
    command.add("-c:v");
    command.add("copy"); // 视频流直接复制，不重新编码
    command.add(outputVideo);

    executeCommand(command);

    log.info("成功提取纯视频到: {}", outputVideo);
  }

  /**
   * 删除临时文件. 纯视频，纯音频
   *
   * @param videoFile
   * @param audioFile
   */
  private static void deleteTemporaryFiles(String videoFile, String audioFile) {
    deleteFile(videoFile);
    deleteFile(audioFile);
  }

  private static void deleteFile(String filePath) {
    File fileToDelete = new File(filePath);
    if (fileToDelete.exists()) {
      if (fileToDelete.delete()) {
        log.info("成功删除临时文件：{}", filePath);
      } else {
        log.error("删除临时文件失败：{}", filePath);
      }
    }
  }


  /**
   * 修改
   *
   * @param videoFile
   * @param audioFile
   * @param outputFileName
   */
  private static boolean mergeVideoAndAudio(String videoFile, String audioFile,
    String outputFileName) {

    if (!fileExists(videoFile) || !fileExists(audioFile)) {
      log.error("视频文件或音频文件不存在，无法合并.");
      return false;
    }
    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    command.add("-i");
    command.add(videoFile);
    command.add("-i");
    command.add(audioFile);
    command.add("-c");
    command.add("copy");
    command.add("-map");
    command.add("0:v");
    command.add("-map");
    command.add("1:a");
    command.add(outputFileName);

    return executeCommand(command);
  }

  /**
   * 使用 FFmpeg 探测 MP4 文件是否有音频流
   *
   * @param inputMp4 输入 MP4 文件的完整路径
   * @return true 有音频流，false 没有音频流
   */
  @Deprecated
  public static boolean hasAudioStream(String inputMp4) {
    List<String> command = new ArrayList<>();
    command.add("ffprobe");
    command.add("-i");
    command.add(inputMp4);
    command.add("-show_streams");
    command.add("-select_streams");
    command.add("a");

    try {
      ProcessBuilder builder = new ProcessBuilder(command);
      builder.redirectErrorStream(true);
      Process process = builder.start();

      try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          // 如果ffprobe 有输出，说明有音频流，如果没有任何流信息输出，说明没有
          return true;
        }
      }

      int exitCode = process.waitFor();
      return false; // 如果命令执行成功且没有输出，则认为没有音频流
    } catch (IOException | InterruptedException e) {
      log.error("探测音频流时发生错误: {}", e.getMessage(), e);
      return false; // 发生异常也返回 false
    }
  }

  /**
   * 使用 FFmpeg 提取 MP4 文件的左声道到指定的 MP3 文件。
   *
   * @param inputMp4  输入 MP4 文件的完整路径
   * @param outputMp3 输出 MP3 文件的完整路径
   */
  @Deprecated
  public static void extractLeftChannel(String inputMp4, String outputMp3) {
    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
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

    executeCommand(command);

    log.info("成功提取左声道到: {}", outputMp3);
  }

  public static void main(String[] args) {

    // 示例用法：
    String videoLink = "https://www.youtube.com/watch?v=aayJ6wlyfII"; // 替换为实际的视频链接
    String outputFileName = "c:\\abcd.mp4"; // 替换为期望的输出路径和文件名
    downloadBest720p(videoLink, outputFileName);

//        String inputMp4 = "D:\\0000\\0007_Trump\\20250227\\20250227.mp4"; // 替换为实际的 MP4 文件路径
//        String outputMp3 = "D:\\0000\\0007_Trump\\20250227\\left_channel.mp3"; // 替换为期望的 MP3 文件路径
//
//        if (hasAudioStream(inputMp4)) {
//            extractLeftChannel(inputMp4, outputMp3);
//        } else {
//            log.warn("文件 {} 不包含音频流，无法提取左声道。", inputMp4);
//        }

//    String inputVideo = "c:\\abcd.mp4"; // 替换为实际的视频文件路径
//    String outputVideo = "c:\\abcd_no_audio.mp4"; // 替换为期望的纯视频文件路径
//    extractPureVideo(inputVideo, outputVideo);
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

  /**
   * 从格式信息中提取格式 ID
   *
   * @param format 格式信息字符串
   * @return 格式 ID
   */
  private static String extractFormatId(String format) {
    if (format == null || format.isEmpty()) {
      log.warn("尝试提取ID的格式字符串为空");
      return null; // 或者抛出异常
    }
    String[] parts = format.trim().split("\\s+"); // 使用空白字符分割
    if (parts.length > 0) {
      return parts[0]; // 第一个部分通常是ID
    }
    log.warn("无法从格式字符串提取ID: {}", format);
    return null; // 或者抛出异常
  }

  /**
   * 获取视频比特率 (尝试从格式字符串解析)
   *
   * @param format 格式信息
   * @return 比特率 (kbps), 失败则返回 0
   */
  private static int getBitrate(String format) {
    if (format == null) return 0;
    // 匹配 "video only" 行中的比特率，例如 ~ 1504k , 1504k
    Pattern pattern = Pattern.compile("\\s(?:~\\s*)?(\\d+)k.*video only");
    Matcher matcher = pattern.matcher(format);
    if (matcher.find()) {
      try {
        return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        log.warn("解析视频比特率失败: {}", format, e);
      }
    }
    // 尝试匹配没有 "video only" 但看起来像视频比特率的行
    pattern = Pattern.compile("\\s(?:~\\s*)?(\\d+)k");
    matcher = pattern.matcher(format);
    if (matcher.find()) {
      try {
        // 进一步检查是否包含分辨率信息，以增加确定性
        if (format.matches(".*\\d+x\\d+.*")) {
          return Integer.parseInt(matcher.group(1));
        }
      } catch (NumberFormatException e) {
        log.warn("解析视频比特率失败 (备用模式): {}", format, e);
      }
    }
    return 0;
  }

  /**
   * 从格式信息中提取音频比特率
   *
   * @param format 格式信息字符串
   * @return 音频比特率 (kbps), 失败则返回 0
   */
  private static int getAudioBitrate(String format) {
    if (format == null) return 0;
    // 专门匹配 "audio only" 行中的比特率，例如 128k
    Pattern pattern = Pattern.compile("\\s(?:~\\s*)?(\\d+)k.*audio only");
    Matcher matcher = pattern.matcher(format);
    if (matcher.find()) {
      try {
        return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        log.warn("解析音频比特率失败: {}", format, e);
      }
    }
    return 0;
  }


  /**
   * 列出可用格式，返回格式列表。
   *
   * @param listFormatsCommand 列表命令 (完整的字符串形式)
   * @return 格式列表
   * @throws IOException 如果执行命令或读取输出时出错
   */
  private static List<String> listFormats(String listFormatsCommand) throws IOException {
    List<String> formats = new ArrayList<>();
    // 注意：这里需要将 String 命令拆分成 List<String> 以适配 getCommandReader
    // 简单的按空格拆分可能对带引号或复杂路径的命令有问题，需要更健壮的解析
    // 这里假设 listFormatsCommand 结构比较简单，或由调用方保证格式正确
    List<String> commandParts = Arrays.asList(listFormatsCommand.split("\\s+")); // 基础拆分

    try (BufferedReader reader = getCommandReader(commandParts)) {
      String line;
      boolean startCapture = false;
      Pattern headerPattern = Pattern.compile("^ID\\s+EXT\\s+RESOLUTION"); // 匹配表头行的模式
      Pattern separatorPattern = Pattern.compile("^-{5,}"); // 匹配分隔符行的模式

      while ((line = reader.readLine()) != null) {
        log.debug("读取格式行: {}", line); // 调试日志
        // 使用正则表达式更精确地匹配表头
        if (headerPattern.matcher(line).find()) {
          startCapture = true;
          log.debug("找到格式表头，开始捕获");
          continue; // 跳过表头行
        }
        // 跳过分隔符行
        if (separatorPattern.matcher(line).find()) {
          log.debug("跳过分隔符行");
          continue;
        }

        if (startCapture && line.trim().length() > 0) { // 确保是开始捕获后且非空行
          formats.add(line.trim());
          log.debug("添加格式行: {}", line.trim());
        }
      }
      if (!startCapture) {
        log.warn("未能在yt-dlp -F的输出中找到格式表头，请检查命令或输出: {}", listFormatsCommand);
      }
      if (formats.isEmpty() && startCapture) {
        log.warn("找到了表头但未能捕获到任何格式行，请检查输出格式。");
      }
    } catch (IOException e) {
      log.error("列出格式时发生错误: {}", e.getMessage(), e);
      throw e; // 将异常向上抛出，让调用者知道出错了
    }
    log.info("成功获取到 {} 个格式", formats.size());
    return formats;
  }
}
