package com.coderdream.util.video;

import com.coderdream.util.CdTimeUtil;
import com.coderdream.util.ffmpeg.DurationParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

@Slf4j
public class VideoCreator4 {

  private static final String IMAGE_PATH = "D:/04_GitHub/video-easy-creator/src/main/resources/pic";
  private static final String MP3_CN_PATH = "D:/04_GitHub/video-easy-creator/src/main/resources/mp3/cn";
  private static final String MP3_EN_PATH = "D:/04_GitHub/video-easy-creator/src/main/resources/mp3/en";
  private static final String OUTPUT_PATH = "D:/04_GitHub/video-easy-creator/src/main/resources/output_video/";

  public static void main(String[] args) {
    try {

      // 创建输出目录
      File dir = new File(OUTPUT_PATH);
      if (!dir.exists() && dir.mkdirs()) {
        log.info("目录创建成功: {}", dir.getAbsolutePath());
      }

      // 1. 获取图片文件夹中的所有图片文件
      List<File> imageFiles = getImageFiles();
      if (imageFiles.isEmpty()) {
        log.error("图片文件夹为空！");
        return;
      }

      // 2. 根据图片文件名提取对应的MP3文件
      String lang = "cn"; // 默认为中文
      List<File> mp3CnFiles = getMp3Files(imageFiles, lang);
      if (mp3CnFiles.isEmpty()) {
        log.error("没有找到对应的MP3文件！{} {}", imageFiles, lang);
        return;
      }

      // 3.1 使用FFmpeg命令合成视频
      List<File> videoCnFiles = new ArrayList<>();
      for (int i = 0; i < imageFiles.size(); i++) {
        File imageFile = imageFiles.get(i);
        File mp3File = mp3CnFiles.get(i);
        File videoFile = new File(
          OUTPUT_PATH + "video_" + (i + 1) + "_" + lang + ".mp4");

        // 计算MP3时长
        double duration = getMp3Duration(mp3File);

        // 生成视频
        createVideo(imageFile, mp3File, videoFile, duration);
        videoCnFiles.add(videoFile);
      }

      lang = "en"; // 英文
      List<File> mp3EnFiles = getMp3Files(imageFiles, lang);
      if (mp3EnFiles.isEmpty()) {
        log.error("没有找到对应的MP3文件！{} {}", imageFiles, lang);
        return;
      }

      // 3.2 使用FFmpeg命令合成视频
      List<File> videoEnFiles = new ArrayList<>();
      for (int i = 0; i < imageFiles.size(); i++) {
        File imageFile = imageFiles.get(i);
        File mp3File = mp3EnFiles.get(i);
        File videoFile = new File(
          OUTPUT_PATH + "video_" + (i + 1) + "_" + lang + ".mp4");

        // 计算MP3时长
        double duration = getMp3Duration(mp3File);

        // 生成视频
        createVideo(imageFile, mp3File, videoFile, duration);
        videoEnFiles.add(videoFile);
      }

      // 4. 合并所有视频文件
      mergeVideos(videoCnFiles, videoEnFiles);

    } catch (Exception e) {
      log.error("视频创建过程中出现异常", e);
    }
  }

  /**
   * 获取图片文件夹下所有图片文件
   *
   * @return 图片文件列表
   */
  private static List<File> getImageFiles() {
    File dir = new File(IMAGE_PATH);
    File[] files = dir.listFiles((dir1, name) -> name.endsWith(".png"));
    if (files != null) {
      return Arrays.asList(files);
    }
    return Collections.emptyList();
  }

  /**
   * 根据图片文件提取对应的MP3文件
   *
   * @param imageFiles 图片文件列表
   * @return MP3文件列表
   */
  private static List<File> getMp3Files(List<File> imageFiles, String lang) {
    List<File> mp3Files = new ArrayList<>();
    for (File imageFile : imageFiles) {
      // 从图片文件名中提取数字部分
      String imageName = imageFile.getName();
      String indexStr = imageName.substring(18,
        21); // 提取图片名中的数字部分（比如 CampingInvitation_001.png => 001）
      int number = Integer.parseInt(indexStr);
      String mp3Name = "CampingInvitation_" + MessageFormat.format(
        "{0,number,000}",
        number) + "_" + lang + ".mp3";  // 对应的MP3文件名

      String MP3_PATH = switch (lang) {
        case "cn" -> MP3_CN_PATH;
        case "en" -> MP3_EN_PATH;
        default ->
          throw new IllegalArgumentException("Unsupported language: " + lang);
      };
      File mp3File = new File(MP3_PATH, mp3Name);
      if (mp3File.exists()) {
        mp3Files.add(mp3File);
      } else {
        log.warn("未找到对应的MP3文件: {} \\ {}", MP3_PATH, mp3Name);
      }
    }
    return mp3Files;
  }

  /**
   * 获取MP3文件的时长
   *
   * @param mp3File MP3文件
   * @return MP3的时长（秒）
   */
  public static double getMp3Duration(File mp3File) {
    try {
      String command = "ffmpeg -i " + mp3File.getAbsolutePath() + " 2>&1";
      log.info("正在执行命令： " + command);
      Process process = Runtime.getRuntime().exec(command);
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getErrorStream()));
      String line;
      while ((line = reader.readLine()) != null) {
//                log.info("while line: {}", line);
        if (line.contains("Duration")) {
//                    log.info("line: {}", line);
          String durationStr = DurationParser.extractDuration(
            line);// line.split(",")[0].split(" ")[1];
          if (Strings.isBlank(durationStr)) {
            log.error("durationStr为空: {}", durationStr);
            return 0; // 如果时长格式不正确，返回0秒
          }

          String[] timeParts = durationStr.split(":");
          // 判断是否为空
          if (timeParts.length != 3) {
            log.error("MP3文件时长格式不正确: {}", durationStr);
            return 0; // 如果时长格式不正确，返回0秒
          }
          int hours = Integer.parseInt(timeParts[0]);
          int minutes = Integer.parseInt(timeParts[1]);
          double seconds = Double.parseDouble(timeParts[2]);
          return hours * 3600 + minutes * 60 + seconds;
        }
      }
    } catch (IOException e) {
      log.error("获取MP3文件时长时出错", e);
    }
    return 0;
  }

  /**
   * 使用FFmpeg将图片和音频合成一个视频
   *
   * @param imageFile 图片文件
   * @param mp3File   MP3文件
   * @param videoFile 输出的视频文件
   * @param duration  视频时长
   */
//  public static void createVideo(File imageFile, File mp3File, File videoFile,
//    double duration) {
//    long startTime = System.currentTimeMillis(); // 记录合并开始时间
//    ProcessBuilder processBuilder = null;
//    try {
//      // 构建命令
//      List<String> command = new ArrayList<>();
//      command.add("ffmpeg");
//      command.add("-y");
//      command.add("-loop");
//      command.add("1");
//      command.add("-framerate");
//      command.add("1");
//      command.add("-t");
//      command.add(String.format("%.2f", duration));
//      command.add("-i");
//      command.add(imageFile.getAbsolutePath());
//      command.add("-i");
//      command.add(mp3File.getAbsolutePath());
//      command.add("-c:v");
//      command.add("libx264");
//      command.add("-c:a");
//      command.add("aac");
//      command.add("-strict");
//      command.add("experimental");
//      command.add("-shortest");
//      command.add(videoFile.getAbsolutePath());
//
//      // 打印日志
//      String commandString = String.join(" ", command);
//      log.info("正在执行命令： " + commandString);
//
//      // 创建ProcessBuilder
//      processBuilder = new ProcessBuilder(command);
//      processBuilder.redirectErrorStream(true); // 合并标准输出和标准错误输出
//
//      // 启动进程
//      Process process = processBuilder.start();
//
//      // 读取进程的输出流
//      try (BufferedReader reader = new BufferedReader(
//        new InputStreamReader(process.getInputStream()))) {
//        String line;
//        while ((line = reader.readLine()) != null) {
//          log.info(line);  // 打印ffmpeg的日志输出
//        }
//      }
//
//      // 等待进程执行完毕
//      int exitCode = process.waitFor();
//
//      // 检查执行结果
//      if (exitCode == 0) {
//        long endTime = System.currentTimeMillis(); // 记录合并结束时间
//        long durationMillis = endTime - startTime; // 计算总耗时（毫秒）
//        log.info("视频创建成功: {}，耗时: {}", videoFile.getAbsolutePath(),
//          formatDuration(durationMillis));
//      } else {
//        log.error("视频创建失败: {}", videoFile.getAbsolutePath());
//      }
//
//    } catch (Exception e) {
//      log.error("合成视频时出错", e);
//    }
//  }
//  public static void createVideo(File imageFile, File mp3File, File videoFile,
//    double duration) {
//    long startTime = System.currentTimeMillis(); // 记录视频生成开始时间
//
//    ProcessBuilder processBuilder = null;
//
//    try {
//      // 构建FFmpeg命令
//      List<String> command = new ArrayList<>();
//      command.add("ffmpeg"); // 1. 使用 ffmpeg 工具
//
//      command.add("-y"); // 2. 自动回答 "yes" 给所有询问（如覆盖输出文件等）
//
//      command.add("-loop"); // 3. 循环输入图片，直到音频播放完
//      command.add("1"); // 图片循环显示
//
//      command.add("-framerate"); // 4. 设置帧率为 60 帧每秒
//      command.add("60"); // 60 帧每秒
//
//      command.add("-t"); // 5. 设置视频时长
//      command.add(String.format("%.2f", duration));  // 保证时长格式为2位小数
//
//      command.add("-i"); // 6. 输入图片文件
//      command.add(imageFile.getAbsolutePath()); // 输入的图片文件路径
//
//      command.add("-i"); // 7. 输入音频文件
//      command.add(mp3File.getAbsolutePath()); // 输入的音频文件路径
//
//      command.add("-s"); // 8. 设置输出分辨率为 4K (3840x2160)
//      command.add("3840x2160"); // 4K 分辨率
//
//      command.add("-c:v"); // 9. 视频编码器
//      command.add("libx264"); // 使用 libx264 编码器
//
//      command.add("-preset"); // 10. 设置编码预设（越快越低质量，越慢越高质量）
//      command.add("slow"); // 使用 "slow" 预设以提高质量
//
//      command.add("-crf"); // 11. 设置视频质量
//      command.add("18"); // "18" 是很高的质量，范围是 0-51，数字越低，质量越高（但文件更大）
//
//      command.add("-b:v"); // 12. 设置视频码率
//      command.add("10000k"); // 设置视频码率为 10Mbps，适合高质量视频
//
//      command.add("-c:a"); // 13. 音频编码器
//      command.add("aac"); // 使用 AAC 音频编码
//
//      command.add("-b:a"); // 14. 设置音频码率
//      command.add("192k"); // 设置音频码率为 192kbps
//
//      command.add("-shortest"); // 15. 确保视频的时长与音频匹配
//
//      command.add(videoFile.getAbsolutePath()); // 16. 输出文件路径
//
//      // 打印命令，便于调试和查看
//      String commandString = String.join(" ", command);
//      log.info("正在执行命令：{}", commandString);
//
//      // 创建 ProcessBuilder，传入命令和参数
//      processBuilder = new ProcessBuilder(command);
//      processBuilder.redirectErrorStream(true); // 合并标准输出和标准错误输出
//
//      // 启动进程
//      Process process = processBuilder.start();
//
//      // 读取并打印ffmpeg的输出流
//      try (BufferedReader reader = new BufferedReader(
//        new InputStreamReader(process.getInputStream()))) {
//        String line;
//        while ((line = reader.readLine()) != null) {
//          log.info("ffmpeg输出: {}", line);  // 打印ffmpeg的日志输出
//        }
//      }
//
//      // 等待进程执行完毕
//      int exitCode = process.waitFor();
//
//      // 检查执行结果
//      if (exitCode == 0) {
//        long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
//        long durationMillis = endTime - startTime; // 计算耗时（毫秒）
//        log.info("视频创建成功: {}，耗时: {}", videoFile.getAbsolutePath(),
//          formatDuration(durationMillis));
//      } else {
//        log.error("视频创建失败: {}", videoFile.getAbsolutePath());
//      }
//
//    } catch (Exception e) {
//      log.error("合成视频时出错", e);
//    }
//  }
//  public static void createVideo(File imageFile, File mp3File, File videoFile,
//    double duration) {
//    long startTime = System.currentTimeMillis(); // 记录视频生成开始时间
//
//    ProcessBuilder processBuilder;
//
//    try {
//      // 构建FFmpeg命令
//      List<String> command = new ArrayList<>();
//      command.add("ffmpeg"); // 1. 使用 ffmpeg 工具
//
//      command.add("-y"); // 2. 自动回答 "yes" 给所有询问（如覆盖输出文件等）
//
//      command.add("-loop"); // 3. 循环输入图片，直到音频播放完
//      command.add("1"); // 图片循环显示
//
//      command.add("-framerate"); // 4. 设置帧率为 60 帧每秒
//      command.add("60"); // 60 帧每秒
//
//      command.add("-t"); // 5. 设置视频时长
//      command.add(String.format("%.2f", duration));  // 保证时长格式为2位小数
//
//      command.add("-i"); // 6. 输入图片文件
//      command.add(imageFile.getAbsolutePath()); // 输入的图片文件路径
//
//      command.add("-i"); // 7. 输入音频文件
//      command.add(mp3File.getAbsolutePath()); // 输入的音频文件路径
//
//      command.add("-s"); // 8. 设置输出分辨率为 4K (3840x2160)
//      command.add("3840x2160"); // 4K 分辨率
//
//      command.add("-c:v"); // 9. 视频编码器
//      command.add("libx264"); // 使用 libx264 编码器
//
//      command.add("-preset"); // 10. 设置编码预设（越快越低质量，越慢越高质量）
//      command.add("slow"); // 使用 "slow" 预设以提高质量
//
//      command.add("-crf"); // 11. 设置视频质量
//      command.add("18"); // "18" 是很高的质量，范围是 0-51，数字越低，质量越高（但文件更大）
//
//      command.add("-b:v"); // 12. 设置视频码率
//      command.add("10000k"); // 设置视频码率为 10Mbps，适合高质量的视频
//
//      command.add("-c:a"); // 13. 音频编码器
//      command.add("aac"); // 使用 AAC 音频编码
//
//      command.add("-b:a"); // 14. 设置音频码率
//      command.add("192k"); // 设置音频码率为 192kbps（高音质）
//
//      command.add("-ac"); // 15. 设置音频通道数为 2（立体声）
//      command.add("2"); // 立体声：2 通道
//
//      command.add("-shortest"); // 16. 确保视频的时长与音频匹配
//
//      command.add(videoFile.getAbsolutePath()); // 17. 输出文件路径
//
//      // 打印命令，便于调试和查看
//      String commandString = String.join(" ", command);
//      log.info("正在执行命令：{}", commandString);
//
//      // 创建 ProcessBuilder，传入命令和参数
//      processBuilder = new ProcessBuilder(command);
//      processBuilder.redirectErrorStream(true); // 合并标准输出和标准错误输出
//
//      // 启动进程
//      Process process = processBuilder.start();
//
//      // 读取并打印ffmpeg的输出流
//      try (BufferedReader reader = new BufferedReader(
//        new InputStreamReader(process.getInputStream()))) {
////        String line;
////        while ((line = reader.readLine()) != null) {
////          log.info("ffmpeg输出: {}", line);  // 打印ffmpeg的日志输出
////        }
//      }
//
//      // 等待进程执行完毕
//      int exitCode = process.waitFor();
//
//      // 检查执行结果
//      if (exitCode == 0) {
//        long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
//        long durationMillis = endTime - startTime; // 计算耗时（毫秒）
//        log.info("视频创建成功: {}，耗时: {}", videoFile.getAbsolutePath(),
//          formatDuration(durationMillis));
//      } else {
//        log.error("视频创建失败: {}", videoFile.getAbsolutePath());
//      }
//
//    } catch (Exception e) {
//      log.error("合成视频时出错", e);
//    }
//  }
  public static void createVideo(File imageFile, File mp3File, File videoFile,
    double duration) {
    long startTime = System.currentTimeMillis(); // 记录视频生成开始时间

    try {
      // 生成包含前后静音的音频
      File adjustedAudioFile = createAudioWithSilence(mp3File);

      // 构建 FFmpeg 命令
      List<String> command = buildFFmpegCommand(imageFile, adjustedAudioFile,
        videoFile, duration);

      // 打印调试信息
      log.info("正在执行命令：{}", String.join(" ", command));

      // 执行 FFmpeg 命令
      ProcessBuilder processBuilder = new ProcessBuilder(command);
      processBuilder.redirectErrorStream(true);

      Process process = processBuilder.start();
      try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {
        reader.lines()
          .forEach(line -> log.debug("ffmpeg输出: {}", line)); // 打印 ffmpeg 输出
      }

      int exitCode = process.waitFor(); // 等待进程执行完毕
      if (exitCode == 0) {
        log.info("视频创建成功: {}，耗时: {}", videoFile.getAbsolutePath(),
          CdTimeUtil.formatDuration(System.currentTimeMillis() - startTime));
      } else {
        log.error("视频创建失败，退出码: {}", exitCode);
      }
    } catch (Exception e) {
      log.error("合成视频时出错", e);
    }
  }

  // 构建添加前后静音的音频文件
  // 构建添加前后静音的音频文件
  private static File createAudioWithSilence(File mp3File)
    throws IOException, InterruptedException {
    File adjustedAudioFile = new File(mp3File.getParent(),
      "adjusted_" + mp3File.getName());
    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    command.add("-y");

    // 输入静音片段
    command.add("-f");
    command.add("lavfi");
    command.add("-i");
    command.add("anullsrc=channel_layout=stereo:sample_rate=44100");
    command.add("-t");
    command.add("0.4"); // 前 400 毫秒静音

    // 输入原始音频
    command.add("-i");
    command.add(mp3File.getAbsolutePath());

    // 输入后 300 毫秒静音
    command.add("-f");
    command.add("lavfi");
    command.add("-i");
    command.add("anullsrc=channel_layout=stereo:sample_rate=44100");
    command.add("-t");
    command.add("0.3");

    // 合并音频，确保格式一致
    command.add("-filter_complex");
    command.add("[0:a]aformat=sample_rates=44100:channel_layouts=stereo[s0];" +
      "[1:a]aresample=44100[a1];" +
      "[2:a]aformat=sample_rates=44100:channel_layouts=stereo[s2];" +
      "[s0][a1][s2]concat=n=3:v=0:a=1[out]");
    command.add("-map");
    command.add("[out]");

    // 输出合成后的音频
    command.add(adjustedAudioFile.getAbsolutePath());

    // 执行命令
    executeFFmpegCommand(command, "生成带静音的音频文件");

    // 校验文件大小
    if (!adjustedAudioFile.exists() || adjustedAudioFile.length() == 0) {
      throw new IOException(
        "生成的音频文件为空: " + adjustedAudioFile.getAbsolutePath());
    }
    return adjustedAudioFile;
  }


  // 构建视频生成的 FFmpeg 命令
  private static List<String> buildFFmpegCommand(File imageFile, File audioFile,
    File videoFile, double duration) {
    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    command.add("-y");
    command.add("-loop");
    command.add("1");
    command.add("-framerate");
    command.add("60");
    command.add("-t");
    command.add(String.format("%.2f", duration));
    command.add("-i");
    command.add(imageFile.getAbsolutePath());
    command.add("-i");
    command.add(audioFile.getAbsolutePath());
    command.add("-s");
    command.add("3840x2160");
    command.add("-c:v");
    command.add("libx264");
    command.add("-preset");
    command.add("slow");
    command.add("-crf");
    command.add("18");
    command.add("-b:v");
    command.add("10000k");
    command.add("-c:a");
    command.add("aac");
    command.add("-b:a");
    command.add("192k");
    command.add("-ac");
    command.add("2");
    command.add("-shortest");
    command.add(videoFile.getAbsolutePath());
    return command;
  }

  // 执行 FFmpeg 命令
  private static void executeFFmpegCommand(List<String> command,
    String description) throws IOException, InterruptedException {
    log.info("正在执行 {}: {}", description, String.join(" ", command));
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true);

    Process process = processBuilder.start();
    try (BufferedReader reader = new BufferedReader(
      new InputStreamReader(process.getInputStream()))) {
      reader.lines().forEach(line -> log.debug("ffmpeg输出: {}", line));
    }

    int exitCode = process.waitFor();
    if (exitCode != 0) {
      throw new RuntimeException(
        String.format("%s 失败，退出码: %d", description, exitCode));
    }
  }

  /**
   * 合并所有生成的视频文件
   *
   * @param videoCnFiles 中文视频文件列表
   * @param videoEnFiles 英文视频文件列表
   */
  public static void mergeVideos(List<File> videoCnFiles,
    List<File> videoEnFiles) {
    long startTime = System.currentTimeMillis(); // 记录合并开始时间
    // 如果两个列表为空或者大小不相等，则直接报错退出
    if (videoCnFiles.isEmpty() || videoEnFiles.isEmpty()
      || videoCnFiles.size() != videoEnFiles.size()) {
      log.error("视频文件列表为空或大小不相等，无法合并");
      return;
    }

    int size = videoCnFiles.size();
    ProcessBuilder processBuilder = null;

    try {
      // 创建合并文件列表
      File listFile = new File(OUTPUT_PATH + "file_list.txt");
      try (BufferedWriter writer = new BufferedWriter(
        new FileWriter(listFile))) {
        for (int i = 0; i < size; i++) {
          // 写入每个视频文件的路径
          File videoCnFile = videoCnFiles.get(i);
          File videoEnFile = videoEnFiles.get(i);
          // 先播放3次英文，再播放1次中文，再播放1次英文，以此类推
          writer.write("file '" + videoEnFile.getAbsolutePath() + "'\n");
          writer.write("file '" + videoEnFile.getAbsolutePath() + "'\n");
          writer.write("file '" + videoEnFile.getAbsolutePath() + "'\n");
          writer.write("file '" + videoCnFile.getAbsolutePath() + "'\n");
          writer.write("file '" + videoEnFile.getAbsolutePath() + "'\n");
        }
      }

      // 构建FFmpeg命令
      List<String> command = new ArrayList<>();
      command.add("ffmpeg"); // 1. 使用 ffmpeg 工具
      command.add("-y"); // 2. 自动回答 "yes" 给所有询问（如覆盖输出文件等）
      command.add("-f"); // 3. 设置输入文件格式
      command.add("concat"); // 4. 使用 concat 模式进行文件合并
      command.add("-safe"); // 5. 允许路径包含特殊字符
      command.add("0"); // 6. 开启 "safe" 模式
      command.add("-i"); // 7. 输入参数
      command.add(listFile.getAbsolutePath()); // 输入文件列表
      command.add("-c"); // 8. 指定编码格式
      command.add("copy"); // 9. 使用 copy 模式，不重新编码
      command.add(OUTPUT_PATH + "final_output.mp4"); // 10. 输出路径

      // 打印命令，便于调试和查看
      String commandString = String.join(" ", command);
      log.info("正在执行命令：{}", commandString);

      // 创建 ProcessBuilder，传入命令和参数
      processBuilder = new ProcessBuilder(command);
      processBuilder.redirectErrorStream(true); // 合并标准输出和标准错误输出

      // 启动进程
      Process process = processBuilder.start();

      // 读取并打印ffmpeg的输出流
      try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          log.info("ffmpeg输出: {}", line);  // 打印ffmpeg的日志输出
        }
      }

      // 等待进程执行完毕
      int exitCode = process.waitFor();

      // 检查执行结果
      if (exitCode == 0) {
        long endTime = System.currentTimeMillis(); // 记录合并结束时间
        long durationMillis = endTime - startTime; // 计算总耗时（毫秒）
        log.info("视频合并成功，最终视频路径: {}final_output.mp4，耗时: {}",
          OUTPUT_PATH, CdTimeUtil.formatDuration(durationMillis));
      } else {
        log.error("视频合并失败");
      }

      // 删除临时的文件列表
      // listFile.delete();

    } catch (Exception e) {
      log.error("合并视频时出错", e);
    }
  }

}
