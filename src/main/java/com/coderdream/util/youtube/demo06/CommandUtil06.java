package com.coderdream.util.youtube.demo06;

import com.coderdream.util.cd.CdTimeUtil;
// 假设 YouTubeApiUtil 包含代理设置逻辑或提供获取代理参数的方法
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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * 命令执行工具类，优化了 YouTube 视频/音频下载和合并流程。
 */
@Slf4j
public class CommandUtil06 {

  // --- 配置常量 ---
  private static final int MAX_RETRIES = 100; // 获取格式ID的最大重试次数
  private static final long RETRY_DELAY_MS = 3000; // 重试间隔 (毫秒)
  private static final long DOWNLOAD_DELAY_MS = 2000; // 下载视频和音频之间的间隔 (毫秒)
  private static final String TEMP_VIDEO_SUFFIX = "_video_temp"; // 临时视频文件后缀部分
  private static final String TEMP_AUDIO_SUFFIX = "_audio_temp"; // 临时音频文件后缀部分

  // --- 核心命令执行 ---

  /**
   * 执行指定的控制台命令。
   *
   * @param command 要执行的命令列表 (每个参数作为列表的一个元素)。
   * @param timeoutSeconds 命令执行的超时时间（秒）。0 或负数表示不设置超时。
   * @return 命令执行是否成功 (退出码为 0)。
   */
  public static boolean executeCommand(List<String> command, int timeoutSeconds) {
    LocalDateTime startTime = LocalDateTime.now();
    String finalCommand = String.join(" ", command);
    boolean success = false;
    Process process = null;

    log.info("准备执行命令: {}", finalCommand);

    try {
      ProcessBuilder builder = createProcessBuilder(command);
      process = builder.start();

      // 使用单独的线程来读取输出，避免阻塞
      StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");
      StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
      new Thread(outputGobbler).start();
      new Thread(errorGobbler).start();

      // 等待进程完成，带超时
      boolean finished;
      if (timeoutSeconds > 0) {
        finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        if (!finished) {
          log.error("命令执行超时 ({} 秒): {}", timeoutSeconds, finalCommand);
          process.destroyForcibly(); // 强制结束超时的进程
          return false; // 超时视为失败
        }
      } else {
        process.waitFor(); // 无超时，一直等待
      }

      int exitCode = process.exitValue();
      success = (exitCode == 0);

      if (!success) {
        log.error("命令执行失败，退出码: {}", exitCode);
        // 可以在这里记录更详细的错误输出 (从 StreamGobbler 获取)
        // log.error("错误流输出:\n{}", errorGobbler.getOutput());
      }

    } catch (IOException e) {
      log.error("执行命令时发生IO异常: {} - {}", finalCommand, e.getMessage(), e);
    } catch (InterruptedException e) {
      log.error("命令执行被中断: {} - {}", finalCommand, e.getMessage(), e);
      Thread.currentThread().interrupt(); // 重新设置中断状态
      if (process != null) {
        process.destroyForcibly();
      }
    } catch (Exception e) { // 捕获其他潜在异常
        log.error("执行命令时发生未知异常: {} - {}", finalCommand, e.getMessage(), e);
        if (process != null) {
            process.destroyForcibly();
        }
    } finally {
      LocalDateTime endTime = LocalDateTime.now();
      Duration duration = Duration.between(startTime, endTime);
      String formattedTime = CdTimeUtil.formatDuration(duration.toMillis());
      log.info("命令执行完成，耗时: {}, 结果: {}", formattedTime, success ? "成功" : "失败");
    }
    return success;
  }

  /**
   * 创建 ProcessBuilder 实例，根据操作系统设置命令前缀。
   * @param command 原始命令列表。
   * @return ProcessBuilder 实例。
   */
  private static ProcessBuilder createProcessBuilder(List<String> command) {
      List<String> fullCommandList = new ArrayList<>();
      String osName = System.getProperty("os.name", "").toLowerCase();

      if (osName.contains("win")) {
          fullCommandList.add("cmd.exe");
          fullCommandList.add("/c");
          // 在 Windows 上，如果命令本身包含空格或特殊字符，最好将整个命令作为一个参数传递给 /c
          // 但 yt-dlp 和 ffmpeg 通常参数是分开的，所以这里保持addAll
          fullCommandList.addAll(command);
      } else { // macOS 或 Linux
          // 对于 Unix-like 系统，直接执行命令通常更好，除非需要 shell 特性
          // fullCommandList.add("bash");
          // fullCommandList.add("-c");
          // fullCommandList.add(String.join(" ", command)); // 如果需要 shell 解析
          fullCommandList.addAll(command); // 直接传递参数列表
      }
      log.debug("构建的完整命令列表: {}", fullCommandList);
      ProcessBuilder builder = new ProcessBuilder(fullCommandList);
      // builder.redirectErrorStream(true); // 重定向错误流到主输出流，方便 StreamGobbler 处理
      return builder;
  }

  // --- YouTube 下载核心逻辑 ---

  /**
   * 下载指定分辨率的最佳视频和最佳音频，然后合并。
   *
   * @param videoLink      视频链接。
   * @param outputFileName 最终输出文件名 (例如 "c:\\mydir\\video.mp4")。
   * @param resolution     目标视频分辨率 (例如 "1920x1080", "1280x720")。
   * @param useProxy       是否为下载操作启用代理。
   * @param downloadTimeoutSeconds 每个下载步骤的超时时间（秒）。
   * @param mergeTimeoutSeconds 合并步骤的超时时间（秒）。
   * @return 下载并合并是否成功。
   */
  public static boolean downloadBestVideoAndAudio(String videoLink, String outputFileName, String resolution,
                                                 boolean useProxy, int downloadTimeoutSeconds, int mergeTimeoutSeconds) {
    if (videoLink == null || videoLink.isEmpty() || outputFileName == null || outputFileName.isEmpty() || resolution == null || resolution.isEmpty()) {
        log.error("无效的输入参数：videoLink, outputFileName, resolution 不能为空。");
        return false;
    }

    File outputFile = new File(outputFileName);
    File outputDir = outputFile.getParentFile();
    if (outputDir != null && !outputDir.exists()) {
        if (!outputDir.mkdirs()) {
            log.error("无法创建输出目录: {}", outputDir.getAbsolutePath());
            return false;
        }
    }
    // C:\abc\video.mp4 -> video
    String baseName = outputFile.getName().substring(0, outputFile.getName().lastIndexOf('.'));
    // D:\path\video_video_temp.mp4 (假设)
    String tempVideoFile = new File(outputDir, baseName + TEMP_VIDEO_SUFFIX + ".mp4").getAbsolutePath(); // 初始假设扩展名
    // D:\path\video_audio_temp.m4a (假设)
    String tempAudioFile = new File(outputDir, baseName + TEMP_AUDIO_SUFFIX + ".m4a").getAbsolutePath(); // 初始假设扩展名

    String videoFormatId = null;
    String audioFormatId = null;
    String videoExt = "mp4"; // 默认或根据需要调整
    String audioExt = "m4a"; // 默认或根据需要调整

    // 1. 获取格式信息并解析 ID 和扩展名
    try {
      List<String> formats = listFormatsWithRetry(videoLink); // listFormats 通常不需要代理，如果需要则添加
      if (formats.isEmpty()) {
        log.error("无法获取视频格式列表。");
        return false;
      }
      log.info("获取到 {} 个格式，开始解析...", formats.size());

      String bestVideoFormatInfo = findBestFormat(formats, resolution, true);
      String bestAudioFormatInfo = findBestFormat(formats, null, false);

      if (bestVideoFormatInfo != null) {
        videoFormatId = extractFormatId(bestVideoFormatInfo);
        videoExt = extractExtension(bestVideoFormatInfo, "mp4"); // 尝试获取真实扩展名
        tempVideoFile = new File(outputDir, baseName + TEMP_VIDEO_SUFFIX + "." + videoExt).getAbsolutePath(); // 更新临时文件名
        log.info("找到最佳视频格式 ID: {}, 扩展名: {}", videoFormatId, videoExt);
      } else {
        log.error("未找到分辨率为 {} 的视频格式。", resolution);
        return false; // 找不到所需格式则失败
      }

      if (bestAudioFormatInfo != null) {
        audioFormatId = extractFormatId(bestAudioFormatInfo);
        audioExt = extractExtension(bestAudioFormatInfo, "m4a"); // 尝试获取真实扩展名
        tempAudioFile = new File(outputDir, baseName + TEMP_AUDIO_SUFFIX + "." + audioExt).getAbsolutePath(); // 更新临时文件名
        log.info("找到最佳音频格式 ID: {}, 扩展名: {}", audioFormatId, audioExt);
      } else {
        log.error("未找到音频格式。");
        return false; // 找不到所需格式则失败
      }

    } catch (IOException e) {
      log.error("获取或解析视频格式时失败: {}", e.getMessage(), e);
      return false;
    } catch (InterruptedException e) {
        log.error("获取格式信息时线程被中断: {}", e.getMessage(), e);
        Thread.currentThread().interrupt();
        return false;
    }

    // 2. 下载视频
    log.info("准备下载视频 (ID: {}) 到 {}", videoFormatId, tempVideoFile);
    boolean videoDownloaded = downloadMedia(videoLink, videoFormatId, tempVideoFile, useProxy, downloadTimeoutSeconds);
    if (!videoDownloaded) {
      log.error("视频下载失败，中止操作。");
      deleteTemporaryFiles(tempVideoFile, null);
      return false;
    }

    // 3. 添加下载间隔
    try {
      log.info("视频下载完成，等待 {}ms 后下载音频...", DOWNLOAD_DELAY_MS);
      Thread.sleep(DOWNLOAD_DELAY_MS);
    } catch (InterruptedException e) {
      log.error("下载间隔等待被中断: {}", e.getMessage(), e);
      Thread.currentThread().interrupt();
      deleteTemporaryFiles(tempVideoFile, null); // 删除已下载的视频
      return false;
    }

    // 4. 下载音频
    log.info("准备下载音频 (ID: {}) 到 {}", audioFormatId, tempAudioFile);
    boolean audioDownloaded = downloadMedia(videoLink, audioFormatId, tempAudioFile, useProxy, downloadTimeoutSeconds);
    if (!audioDownloaded) {
      log.error("音频下载失败，中止操作。");
      deleteTemporaryFiles(tempVideoFile, tempAudioFile); // 删除可能已下载的文件
      return false;
    }

    // 5. 合并视频和音频
    log.info("视频和音频下载完成，准备合并到 {}", outputFileName);
    boolean merged = mergeVideoAndAudio(tempVideoFile, tempAudioFile, outputFileName, mergeTimeoutSeconds);
    if (!merged) {
      log.error("视频和音频合并失败。临时文件保留在: {}, {}", tempVideoFile, tempAudioFile);
      // 合并失败，可以选择保留临时文件方便排查，或者也删除掉
      // deleteTemporaryFiles(tempVideoFile, tempAudioFile);
      return false;
    } else {
       log.info("视频成功合并到: {}", outputFileName);
    }

    // 6. 删除临时文件 (仅在合并成功后)
    deleteTemporaryFiles(tempVideoFile, tempAudioFile);

    return true; // 所有步骤成功
  }

  /**
   * 下载指定格式的媒体文件 (视频或音频)。
   *
   * @param videoLink   视频链接。
   * @param formatId    要下载的格式 ID。
   * @param outputFile  输出文件路径。
   * @param useProxy    是否使用代理。
   * @param timeoutSeconds 命令超时时间（秒）。
   * @return 下载是否成功。
   */
  public static boolean downloadMedia(String videoLink, String formatId, String outputFile, boolean useProxy, int timeoutSeconds) {
    List<String> downloadCommand = new ArrayList<>();
    downloadCommand.add("yt-dlp");

    // 添加代理参数
    if (useProxy) {
      String proxyString = "--proxy http://127.0.0.1:1080";// YouTubeApiUtil.getProxyArgumentString(); // 假设此方法返回 "--proxy http://127.0.0.1:1080" 或类似格式
      if (proxyString != null && !proxyString.isEmpty()) {
         // 需要将代理参数拆分添加到 list 中
         String[] proxyParts = proxyString.split("\\s+", 2); // 最多拆分成两部分
         if(proxyParts.length == 2) {
             downloadCommand.add(proxyParts[0]); // --proxy
             downloadCommand.add(proxyParts[1]); // http://127.0.0.1:1080
             log.info("下载命令使用代理: {}", proxyParts[1]);
         } else {
             log.warn("无法解析代理参数字符串: {}", proxyString);
         }
      } else {
        log.warn("useProxy为true，但未获取到有效的代理参数字符串");
      }
    } else {
      log.info("下载命令未使用代理");
    }

     // 添加 cookies 参数 (示例，需要你有获取 cookie 文件路径的逻辑)
//     String cookiesPath = YouTubeApiUtil.getCookiesPath(); // 假设有这个方法
//     if (cookiesPath != null && !cookiesPath.isEmpty() && new File(cookiesPath).exists()) {
//        downloadCommand.add("--cookies");
//        downloadCommand.add(cookiesPath);
//        log.info("下载命令使用 Cookies: {}", cookiesPath);
//     }

    downloadCommand.add("-f");
    downloadCommand.add(formatId);
    downloadCommand.add("--force-overwrites"); // 强制覆盖已存在的临时文件
    downloadCommand.add("-o");
    downloadCommand.add(outputFile); // 直接下载到指定的临时文件路径
    downloadCommand.add(videoLink);

    boolean success = executeCommand(downloadCommand, timeoutSeconds);

    // 检查文件是否实际下载成功且不为空
    if (success && !fileExistsAndNotEmpty(outputFile)) {
      log.error("命令看似成功，但输出文件 {} 不存在或为空。", outputFile);
      return false;
    } else if (!success) {
        // 如果命令失败，尝试删除可能产生的空文件或部分文件
        deleteFile(outputFile);
    }
    return success;
  }

  /**
   * 使用 FFmpeg 合并视频和音频文件。
   *
   * @param videoFile      视频文件路径。
   * @param audioFile      音频文件路径。
   * @param outputFileName 最终合并后的文件路径。
   * @param timeoutSeconds 命令超时时间（秒）。
   * @return 合并是否成功。
   */
  public static boolean mergeVideoAndAudio(String videoFile, String audioFile, String outputFileName, int timeoutSeconds) {
    if (!fileExistsAndNotEmpty(videoFile)) {
      log.error("视频文件 {} 不存在或为空，无法合并。", videoFile);
      return false;
    }
    if (!fileExistsAndNotEmpty(audioFile)) {
      log.error("音频文件 {} 不存在或为空，无法合并。", audioFile);
      return false;
    }

    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    command.add("-y"); // 覆盖已存在的输出文件
    command.add("-i");
    command.add(videoFile); // 输入视频
    command.add("-i");
    command.add(audioFile); // 输入音频
    command.add("-c:v");   // 视频编解码器
    command.add("copy");   // 直接复制视频流，不重新编码
    command.add("-c:a");   // 音频编解码器
    command.add("aac");    // 尝试使用 AAC 编码音频 (兼容性较好)，如果copy失败可以试试这个
    // command.add("copy"); // 或者尝试直接复制音频流，如果格式兼容的话更快
    command.add("-map");   // 映射流
    command.add("0:v:0");  // 映射第一个输入的第一个视频流
    command.add("-map");
    command.add("1:a:0");  // 映射第二个输入的第一个音频流
    command.add("-shortest"); // 以最短的输入流结束输出
    command.add(outputFileName); // 输出文件

    boolean success = executeCommand(command, timeoutSeconds);
    // 检查合并后的文件
     if (success && !fileExistsAndNotEmpty(outputFileName)) {
         log.error("FFmpeg 命令看似成功，但合并后的文件 {} 不存在或为空。", outputFileName);
         return false;
     } else if (!success) {
         deleteFile(outputFileName); // 删除合并失败产生的文件
     }
    return success;
  }

  // --- 辅助方法 ---

  /**
   * 列出指定视频链接的可用格式，带重试机制。
   *
   * @param videoLink 视频链接。
   * @return 格式列表。
   * @throws IOException          如果命令执行失败或读取输出错误。
   * @throws InterruptedException 如果线程在等待时被中断。
   */
  private static List<String> listFormatsWithRetry(String videoLink) throws IOException, InterruptedException {
    List<String> formats = null;
    for (int i = 0; i < MAX_RETRIES; i++) {
      log.info("尝试第 {}/{} 次获取格式列表: {}", i + 1, MAX_RETRIES, videoLink);
      // 构造命令列表
      List<String> listCommandList = new ArrayList<>();
      listCommandList.add("yt-dlp");
      // listFormats 一般不需要代理，除非访问 YouTube 本身受限
      // String proxyArg = YouTubeApiUtil.getProxyArgumentString();
      // if(proxyArg != null && !proxyArg.isEmpty()) { listCommandList.addAll(Arrays.asList(proxyArg.split("\\s+", 2))); }
      listCommandList.add("-F");
      listCommandList.add(videoLink);

      // 执行命令并读取输出
      StringBuilder outputBuilder = new StringBuilder();
      boolean commandSuccess = false;
      Process process = null;
      try {
           ProcessBuilder builder = createProcessBuilder(listCommandList);
           process = builder.start();
           BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), getConsoleCharset()));
           String line;
           while ((line = reader.readLine()) != null) {
               outputBuilder.append(line).append(System.lineSeparator());
           }
           reader.close(); // 关闭 reader

           boolean finished = process.waitFor(60, TimeUnit.SECONDS); // 给 listFormats 设置一个超时
           if(finished && process.exitValue() == 0) {
               commandSuccess = true;
           } else if (!finished) {
               log.warn("获取格式列表超时");
               process.destroyForcibly();
           } else {
               log.warn("获取格式列表命令失败，退出码: {}", process.exitValue());
               // 读取错误流
               BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), getConsoleCharset()));
               StringBuilder errorBuilder = new StringBuilder();
               while ((line = errorReader.readLine()) != null) {
                   errorBuilder.append(line).append(System.lineSeparator());
               }
                errorReader.close();
               log.warn("错误流输出:\n{}", errorBuilder.toString());
           }
      } catch (IOException e) {
           log.warn("第 {} 次获取格式列表IO异常: {}", i + 1, e.getMessage());
      } catch (InterruptedException e) {
          log.error("获取格式列表时线程被中断");
          Thread.currentThread().interrupt();
          throw e; // 重新抛出中断异常
      } finally {
          if (process != null) {
             process.destroy(); // 确保进程被关闭
          }
      }


      if (commandSuccess) {
        formats = parseFormatsOutput(outputBuilder.toString());
        if (formats != null && !formats.isEmpty()) {
          log.info("成功获取到 {} 个格式", formats.size());
          return formats; // 成功获取则返回
        } else {
           log.warn("命令成功，但未能从输出中解析出格式。");
        }
      }

      // 如果失败，则等待后重试
      if (i < MAX_RETRIES - 1) {
        log.warn("获取格式列表失败，将在 {}ms 后重试...", RETRY_DELAY_MS);
        Thread.sleep(RETRY_DELAY_MS);
      }
    }
    log.error("重试 {} 次后仍无法获取格式列表。", MAX_RETRIES);
    return new ArrayList<>(); // 返回空列表表示失败
  }

   /**
    * 从 yt-dlp -F 的完整输出中解析出格式行。
    * @param fullOutput yt-dlp -F 命令的完整输出。
    * @return 包含格式行的列表，如果无法解析则返回 null 或空列表。
    */
   private static List<String> parseFormatsOutput(String fullOutput) {
       List<String> formats = new ArrayList<>();
       if (fullOutput == null || fullOutput.isEmpty()) {
           return formats;
       }
       String[] lines = fullOutput.split("\\r?\\n"); // 按行分割
       boolean startCapture = false;
       Pattern headerPattern = Pattern.compile("^ID\\s+EXT\\s+RESOLUTION"); // 匹配表头行的模式
       Pattern separatorPattern = Pattern.compile("^-{5,}"); // 匹配分隔符行的模式

       for (String line : lines) {
            if (headerPattern.matcher(line).find()) {
               startCapture = true;
               continue;
            }
            if (separatorPattern.matcher(line).find()) {
                continue; // 跳过分隔符
            }
            if (startCapture && line.trim().length() > 0) {
                formats.add(line.trim());
            }
       }
        if (!startCapture) {
            log.warn("未能在yt-dlp -F的输出中找到格式表头。完整输出:\n{}", fullOutput.substring(0, Math.min(fullOutput.length(), 1000))); // 只记录部分输出避免日志过长
        }
       return formats;
   }

  /**
   * 在格式列表中查找符合条件的最佳格式信息字符串。
   *
   * @param formats          格式列表 (从 listFormats 获取)。
   * @param targetResolution 目标分辨率 (例如 "1920x1080")，如果查找音频则为 null。
   * @param isVideo          true 表示查找视频，false 表示查找音频。
   * @return 最佳格式的信息字符串，未找到则返回 null。
   */
  private static String findBestFormat(List<String> formats, String targetResolution, boolean isVideo) {
    String bestFormatInfo = null;
    int bestMetric = -1; // 用于比较的指标（视频用比特率，音频也用比特率，可调整）

    for (String format : formats) {
      if (isVideo) {
        // 确保 targetResolution 不为 null
        if (targetResolution == null) continue;
        // 检查是否包含分辨率和 "video only"
        if (format.contains(targetResolution) && format.contains("video only")) {
          int currentBitrate = getBitrate(format); // 解析比特率
          if (currentBitrate > bestMetric) { // 选择比特率最高的
            bestMetric = currentBitrate;
            bestFormatInfo = format;
          }
        }
      } else { // 查找音频
        if (format.contains("audio only")) {
          int currentBitrate = getAudioBitrate(format); // 解析音频比特率
          if (currentBitrate > bestMetric) { // 选择比特率最高的
            bestMetric = currentBitrate;
            bestFormatInfo = format;
          }
        }
      }
    }
    if (bestFormatInfo == null) {
        log.warn("在 {} 个格式中未能找到 {} 格式 (分辨率: {})", formats.size(), isVideo ? "视频" : "音频", targetResolution);
    }
    return bestFormatInfo;
  }

  /**
   * 从格式信息字符串中提取格式 ID (通常是第一个单词)。
   *
   * @param formatInfo 格式信息字符串。
   * @return 格式 ID，失败返回 null。
   */
  private static String extractFormatId(String formatInfo) {
    if (formatInfo == null || formatInfo.isEmpty()) {
      return null;
    }
    String[] parts = formatInfo.trim().split("\\s+");
    if (parts.length > 0) {
      return parts[0];
    }
    return null;
  }

   /**
    * 从格式信息字符串中提取文件扩展名 (通常是第二个单词)。
    * @param formatInfo 格式信息字符串。
    * @param defaultExt 如果提取失败，返回的默认扩展名。
    * @return 文件扩展名。
    */
   private static String extractExtension(String formatInfo, String defaultExt) {
       if (formatInfo == null || formatInfo.isEmpty()) {
           return defaultExt;
       }
       String[] parts = formatInfo.trim().split("\\s+");
       if (parts.length > 1) {
           // 检查第二部分是否像一个扩展名 (例如 mp4, webm, m4a)
           if (parts[1].matches("^[a-zA-Z0-9]+$") && parts[1].length() <= 5) {
               return parts[1];
           }
       }
       return defaultExt; // 提取失败返回默认值
   }


  /**
   * 从格式信息字符串中获取视频比特率。
   *
   * @param formatInfo 格式信息字符串。
   * @return 比特率 (kbps)，失败返回 0。
   */
  private static int getBitrate(String formatInfo) {
    if (formatInfo == null) return 0;
    // 改进正则，匹配比特率数字，可能前面有~，后面可能有k
    Pattern pattern = Pattern.compile("\\s(?:~\\s*)?(\\d+)(?:k)?\\s");
    Matcher matcher = pattern.matcher(formatInfo);
    // 尝试多次查找，因为比特率可能不在第一个匹配项
    while (matcher.find()) {
         // 附加条件：确保该行包含分辨率信息，增加是视频比特率的可能性
         if (formatInfo.matches(".*\\d+x\\d+.*") && formatInfo.contains("video")) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                log.trace("解析视频比特率数字失败: {}", matcher.group(1), e);
            }
         }
    }
     log.trace("未能在视频格式行找到比特率: {}", formatInfo);
    return 0;
  }

  /**
   * 从格式信息字符串中获取音频比特率。
   *
   * @param formatInfo 格式信息字符串。
   * @return 比特率 (kbps)，失败返回 0。
   */
  private static int getAudioBitrate(String formatInfo) {
    if (formatInfo == null) return 0;
    // 匹配 "audio only" 行的比特率
    Pattern pattern = Pattern.compile("\\s(?:~\\s*)?(\\d+)(?:k)?\\s.*audio only");
    Matcher matcher = pattern.matcher(formatInfo);
    if (matcher.find()) {
      try {
        return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        log.trace("解析音频比特率数字失败: {}", matcher.group(1), e);
      }
    }
    log.trace("未能在音频格式行找到比特率: {}", formatInfo);
    return 0;
  }

  /**
   * 获取当前控制台的字符编码。
   * @return 字符集。
   */
  /**
   * 获取当前控制台的字符编码。
   * @return 字符集。
   */
  private static Charset getConsoleCharset() {
    // Console 对象在 IDE 或非交互式环境可能为 null
    Console console = System.console();
    if (console != null && console.charset() != null) {
      log.debug("获取到 Console Charset: {}", console.charset());
      return console.charset();
    }

    // 提供一个更可靠的备选方案 (Windows)
    String osName = System.getProperty("os.name", "").toLowerCase();
    if (osName.contains("win")) {
      Process process = null;
      try {
        // 执行 chcp 命令获取活动代码页
        process = Runtime.getRuntime().exec("cmd.exe /c chcp");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        reader.close(); // 关闭流

        boolean finished = process.waitFor(5, TimeUnit.SECONDS); // 设置超时
        if (!finished) {
          log.warn("执行 chcp 命令超时，无法获取控制台编码。");
          process.destroyForcibly();
        } else if (process.exitValue() != 0) {
          log.warn("执行 chcp 命令失败，退出码: {}", process.exitValue());
        } else if (line != null) {
          log.debug("chcp 命令输出: {}", line);
          // 正则表达式匹配数字代码页 (例如 "Active code page: 936")
          Pattern pattern = Pattern.compile("\\d+");
          Matcher matcher = pattern.matcher(line);
          if (matcher.find()) {
            String cpNumber = matcher.group();
            String charsetName = "CP" + cpNumber;
            try {
              // 尝试获取 Charset 实例
              Charset charset = Charset.forName(charsetName);
              log.info("通过 chcp 获取到控制台编码: {}", charset.name());
              return charset;
            } catch (java.nio.charset.UnsupportedCharsetException e) {
              log.warn("不支持的编码名称 '{}' (来自 chcp)，将使用默认编码。", charsetName);
            } catch (java.nio.charset.IllegalCharsetNameException e) {
              log.warn("非法的编码名称 '{}' (来自 chcp)，将使用默认编码。", charsetName);
            }
          } else {
            log.warn("无法从 chcp 输出 '{}' 中解析代码页数字。", line);
          }
        } else {
          log.warn("chcp 命令没有输出。");
        }
      } catch (IOException e) {
        log.warn("执行 chcp 命令时发生IO异常，无法获取控制台编码。", e);
      } catch (InterruptedException e) {
        log.warn("执行 chcp 命令时被中断。", e);
        Thread.currentThread().interrupt(); // 重新设置中断状态
        if (process != null) process.destroyForcibly();
      } catch (Exception e) { // 捕获其他潜在异常
        log.warn("获取 Windows 控制台编码时发生未知异常。", e);
        if (process != null) process.destroyForcibly();
      }
    }

    // 如果所有方法都失败，返回系统默认编码
    Charset defaultCharset = Charset.defaultCharset();
    log.warn("无法准确获取控制台编码，将使用系统默认编码: {}", defaultCharset.name());
    return defaultCharset;
  }

  /**
   * 检查文件是否存在且不为空。
   * @param filePath 文件路径。
   * @return true 如果文件存在且大小大于 0。
   */
  private static boolean fileExistsAndNotEmpty(String filePath) {
    if (filePath == null || filePath.isEmpty()) return false;
    File file = new File(filePath);
    return file.exists() && file.isFile() && file.length() > 0;
  }

  /**
   * 删除指定的临时文件。
   * @param videoFile 临时视频文件路径 (可以为 null)。
   * @param audioFile 临时音频文件路径 (可以为 null)。
   */
  private static void deleteTemporaryFiles(String videoFile, String audioFile) {
    deleteFile(videoFile);
    deleteFile(audioFile);
  }

  /**
   * 安全地删除文件。
   * @param filePath 文件路径 (可以为 null)。
   */
  private static void deleteFile(String filePath) {
    if (filePath == null || filePath.isEmpty()) {
      return;
    }
    try {
        File fileToDelete = new File(filePath);
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                log.info("成功删除临时文件: {}", filePath);
            } else {
                log.warn("删除临时文件失败: {}", filePath);
            }
        }
    } catch (SecurityException e) {
         log.error("删除文件时权限不足: {}", filePath, e);
    } catch (Exception e) {
        log.error("删除文件时发生未知错误: {}", filePath, e);
    }
  }

    // --- 简化的公共下载方法 ---

    /**
     * 下载最佳 720p 视频（使用默认超时和代理设置）。
     * @param videoLink 视频链接。
     * @param outputFileName 输出文件名。
     * @return 是否成功。
     */
    public static boolean downloadBest720p(String videoLink, String outputFileName) {
        // 假设默认需要代理，超时时间设为 5 分钟 (300秒)
        return downloadBestVideoAndAudio(videoLink, outputFileName, "1280x720", true, 300, 120);
    }

    /**
     * 下载最佳 1080p 视频（使用默认超时和代理设置）。
     * @param videoLink 视频链接。
     * @param outputFileName 输出文件名。
     * @return 是否成功。
     */
    public static boolean downloadBest1080p(String videoLink, String outputFileName) {
        // 假设默认需要代理，超时时间设为 10 分钟 (600秒)，合并超时 3 分钟 (180秒)
        return downloadBestVideoAndAudio(videoLink, outputFileName, "1920x1080", true, 600, 180);
    }

  // --- Main 方法 (示例) ---
  public static void main(String[] args) {
    // 配置日志输出 (如果还没有配置 Logback)
    // BasicConfigurator.configure(); // 简单的控制台日志配置

    // 示例用法：
    String videoLink = "https://www.youtube.com/watch?v=3R0swDZgjH8"; // 替换为实际的视频链接
    // String videoLink = "https://www.youtube.com/watch?v=aayJ6wlyfII";
    String outputFileName = "C:\\temp\\youtube_test\\tech_news_test.mp4"; // 替换为期望的输出路径和文件名

     // 确保目录存在
     File outputDir = new File(outputFileName).getParentFile();
     if (!outputDir.exists()) {
         outputDir.mkdirs();
     }

    log.info("开始下载 720p 视频...");
    boolean success720 = downloadBest720p(videoLink, outputFileName);
    if (success720) {
      log.info("720p 视频下载并合并成功: {}", outputFileName);
    } else {
      log.error("720p 视频下载或合并失败。");
    }

    // 可以添加 1080p 的测试
    // String output1080 = "C:\\temp\\youtube_test\\tech_news_1080p.mp4";
    // log.info("开始下载 1080p 视频...");
    // boolean success1080 = downloadBest1080p(videoLink, output1080);
    // if (success1080) {
    //     log.info("1080p 视频下载并合并成功: {}", output1080);
    // } else {
    //     log.error("1080p 视频下载或合并失败。");
    // }
  }

  // --- 内部类：用于异步读取进程输出流 ---
  private static class StreamGobbler implements Runnable {
    private final BufferedReader reader;
    private final String type;
    // 可选：如果需要在主线程获取输出内容
    // private final StringBuilder outputBuffer = new StringBuilder();

    StreamGobbler(java.io.InputStream inputStream, String type) {
      this.reader = new BufferedReader(new InputStreamReader(inputStream, getConsoleCharset()));
      this.type = type;
    }

    @Override
    public void run() {
      String line;
      try {
        while ((line = reader.readLine()) != null) {
          // 直接打印到控制台或日志
          // 根据 type 可以区分是标准输出还是错误输出
           if ("ERROR".equals(type)) {
               log.warn("[{}] {}", type, line); // 错误流用 warn 级别
           } else {
               log.trace("[{}] {}", type, line); // 标准输出用 trace 或 debug 级别，避免过多日志
               System.out.println(line); // 保持原样输出到控制台
           }
          // 可选：存储输出
          // outputBuffer.append(line).append(System.lineSeparator());
        }
      } catch (IOException e) {
        // 流关闭时 readLine() 可能抛出异常，通常是正常的进程结束
          log.trace("StreamGobbler 读取异常 (可能是进程结束): {}", e.getMessage());
      } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("关闭 StreamGobbler reader 失败", e);
            }
        }
    }

    // 可选：获取输出内容的方法
    // public String getOutput() {
    //     return outputBuffer.toString();
    // }
  }

    // --- 弃用的方法 ---
    @Deprecated
    public static boolean executeCommand(List<String> command) {
       log.warn("调用了弃用的 executeCommand(List<String>)，请使用带超时的版本。");
       return executeCommand(command, 0); // 默认无超时
    }

    @Deprecated
    public static void executeCommand(String command) {
        log.warn("调用了弃用的 executeCommand(String)，请使用 executeCommand(List<String>, int)。");
        // 简单的空格拆分，可能不健壮
        executeCommand(Arrays.asList(command.split("\\s+")), 0);
    }

    @Deprecated
    private static BufferedReader getCommandReader(List<String> commandList) throws IOException {
         log.warn("调用了弃用的 getCommandReader(List<String>)，内部已不再使用。");
         ProcessBuilder builder = createProcessBuilder(commandList);
         Process process = builder.start();
         return new BufferedReader(new InputStreamReader(process.getInputStream(), getConsoleCharset()));
     }

    @Deprecated
    private static BufferedReader getCommandReader(String command) throws IOException {
       log.warn("调用了弃用的 getCommandReader(String)，请使用内部的 StreamGobbler。");
       return getCommandReader(Arrays.asList(command.split("\\s+")));
    }

     // 标记弃用或移除，因为新的流程是先下载再合并，不需要预先检查
     @Deprecated
     public static boolean hasAudioStream(String inputMp4) {
         log.warn("hasAudioStream 方法已弃用，新的流程不依赖此检查。");
         return false;
     }

     @Deprecated
     public static void extractLeftChannel(String inputMp4, String outputMp3) {
          log.warn("extractLeftChannel 方法与核心下载流程无关，已弃用。");
          // 可以保留实现或移除
     }

     @Deprecated
      public static void extractPureVideo(String inputVideo, String outputVideo) {
           log.warn("extractPureVideo 方法与核心下载流程无关，已弃用。");
           // 可以保留实现或移除
      }

      // 旧的 downloadBest720p/1080p (带代理参数的) 也可以标记为弃用或移除
      @Deprecated
      public static void downloadBest720p(String videoLink, String outputFileName, String proxyProtocol, String proxyAddress, int proxyPort) {
            log.warn("带代理参数的 downloadBest720p 方法已弃用，请使用 downloadBestVideoAndAudio。");
            // 可以调用新的方法，或者移除
            downloadBestVideoAndAudio(videoLink, outputFileName, "1280x720",
                (proxyAddress != null && !proxyAddress.isEmpty()), 300, 120); // 假设代理存在即使用
      }

}
