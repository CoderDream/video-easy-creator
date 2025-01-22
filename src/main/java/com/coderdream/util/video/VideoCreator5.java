package com.coderdream.util.video;

import com.coderdream.util.cd.CdTimeUtil;
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
public class VideoCreator5 {

  private static final String IMAGE_PATH = "D:/04_GitHub/video-easy-creator/src/main/resources/pic";
  private static final String AUDIO_CN_PATH = "D:/04_GitHub/video-easy-creator/src/main/resources/wav/cn";
  private static final String AUDIO_EN_PATH = "D:/04_GitHub/video-easy-creator/src/main/resources/wav/en";
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

      String audioType = "wav"; // 默认为AUDIO音频文件

      // 2. 根据图片文件名提取对应的AUDIO文件
      String lang = "cn"; // 默认为中文

      List<File> audioCnFiles = getAudioFiles(imageFiles, audioType, lang);
      if (audioCnFiles.isEmpty()) {
        log.error("没有找到对应的 {} 文件！{} {}", audioType, imageFiles, lang);
        return;
      }

      // 3.1 使用FFmpeg命令合成视频
      List<File> videoCnFiles = new ArrayList<>();
      for (int i = 0; i < imageFiles.size(); i++) {
        File imageFile = imageFiles.get(i);
        File audioFile = audioCnFiles.get(i);
        File videoFile = new File(
          OUTPUT_PATH + "video_" + (i + 1) + "_" + lang + ".mp4");

        // 计算AUDIO时长
        double duration = getAudioDuration(audioFile);

        // 生成视频
        createVideo(imageFile, audioFile, videoFile, duration);
        videoCnFiles.add(videoFile);
      }

      lang = "en"; // 英文
      List<File> audioEnFiles = getAudioFiles(imageFiles, audioType, lang);
      if (audioEnFiles.isEmpty()) {
        log.error("没有找到对应的 {} 文件！{} {}", audioType, imageFiles, lang);
        return;
      }

      // 3.2 使用FFmpeg命令合成视频
      List<File> videoEnFiles = new ArrayList<>();
      for (int i = 0; i < imageFiles.size(); i++) {
        File imageFile = imageFiles.get(i);
        File audioFile = audioEnFiles.get(i);
        File videoFile = new File(
          OUTPUT_PATH + "video_" + (i + 1) + "_" + lang + ".mp4");

        // 计算AUDIO时长
        double duration = getAudioDuration(audioFile);

        // 生成视频
        createVideo(imageFile, audioFile, videoFile, duration);
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
   * 根据图片文件提取对应的AUDIO文件
   *
   * @param imageFiles 图片文件列表
   * @return 音频文件列表
   */
  private static List<File> getAudioFiles(List<File> imageFiles,
    String audioType,
    String lang) {
    List<File> audioFiles = new ArrayList<>();
    for (File imageFile : imageFiles) {
      // 从图片文件名中提取数字部分
      String imageName = imageFile.getName();
      int indexBegin = imageName.lastIndexOf("_"); // 找到最后一个下划线
      int indexEnd = imageName.lastIndexOf("."); // 找到最后一个点
      String indexStr = imageName.substring(indexBegin + 1, indexEnd);
      // 提取图片名中的数字部分（比如 CampingInvitation_001.png => 001）
//      log.info("indexStr: {}", indexStr);
      int number = Integer.parseInt(indexStr); // "CampingInvitation_"
      String audioName =
        imageName.substring(0, indexBegin + 1) + MessageFormat.format(
          "{0,number,000}",
          number) + "_" + lang + "." + audioType;  // 对应的AUDIO文件名

      String audioPath = switch (lang) {
        case "cn" -> AUDIO_CN_PATH;
        case "en" -> AUDIO_EN_PATH;
        default ->
          throw new IllegalArgumentException("Unsupported language: " + lang);
      };
      File audioFile = new File(audioPath, audioName);
      if (audioFile.exists()) {
        audioFiles.add(audioFile);
      } else {
        log.warn("getAudioFiles 未找到对应的 {} 文件: {} \\ {}", audioType,
          audioPath, audioName);
      }
    }
    return audioFiles;
  }

  /**
   * 获取AUDIO文件的时长
   *
   * @param audioFile AUDIO文件
   * @return AUDIO的时长（秒）
   */
  public static double getAudioDuration(File audioFile) {
    try {
      String command = "ffmpeg -i " + audioFile.getAbsolutePath() + " 2>&1";
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
            log.error("AUDIO文件时长格式不正确: {}", durationStr);
            return 0; // 如果时长格式不正确，返回0秒
          }
          int hours = Integer.parseInt(timeParts[0]);
          int minutes = Integer.parseInt(timeParts[1]);
          double seconds = Double.parseDouble(timeParts[2]);
          return hours * 3600 + minutes * 60 + seconds;
        }
      }
    } catch (IOException e) {
      log.error("获取AUDIO文件时长时出错", e);
    }
    return 0;
  }

  /**
   * 使用FFmpeg将图片和音频合成一个视频
   *
   * @param imageFile 图片文件
   * @param audioFile   AUDIO文件
   * @param videoFile 输出的视频文件
   * @param duration  视频时长
   */
  public static void createVideo(File imageFile, File audioFile, File videoFile,
    double duration) {
    long startTime = System.currentTimeMillis(); // 记录视频生成开始时间

    ProcessBuilder processBuilder;

    try {
      // 构建FFmpeg命令
      List<String> command = new ArrayList<>();
      command.add("ffmpeg"); // 1. 使用 ffmpeg 工具

      command.add("-y"); // 2. 自动回答 "yes" 给所有询问（如覆盖输出文件等）

      command.add("-loop"); // 3. 循环输入图片，直到音频播放完
      command.add("1"); // 图片循环显示

      command.add("-framerate"); // 4. 设置帧率为 60 帧每秒
      command.add("60"); // 60 帧每秒

      command.add("-t"); // 5. 设置视频时长
      command.add(String.format("%.2f", duration));  // 保证时长格式为2位小数

      command.add("-i"); // 6. 输入图片文件
      command.add(imageFile.getAbsolutePath()); // 输入的图片文件路径

      command.add("-i"); // 7. 输入音频文件
      command.add(audioFile.getAbsolutePath()); // 输入的音频文件路径

      command.add("-s"); // 8. 设置输出分辨率为 4K (3840x2160)
      command.add("3840x2160"); // 4K 分辨率

      command.add("-c:v"); // 9. 视频编码器
      command.add("libx264"); // 使用 libx264 编码器

      command.add("-preset"); // 10. 设置编码预设（越快越低质量，越慢越高质量）
      command.add("slow"); // 使用 "slow" 预设以提高质量

      command.add("-crf"); // 11. 设置视频质量
      command.add("18"); // "18" 是很高的质量，范围是 0-51，数字越低，质量越高（但文件更大）

      command.add("-b:v"); // 12. 设置视频码率
      command.add("10000k"); // 设置视频码率为 10Mbps，适合高质量的视频

      command.add("-c:a"); // 13. 音频编码器
      command.add("aac"); // 使用 AAC 音频编码

      command.add("-b:a"); // 14. 设置音频码率
      command.add("192k"); // 设置音频码率为 192kbps（高音质）

      command.add("-ac"); // 15. 设置音频通道数为 2（立体声）
      command.add("2"); // 立体声：2 通道

      command.add("-shortest"); // 16. 确保视频的时长与音频匹配

      command.add(videoFile.getAbsolutePath()); // 17. 输出文件路径

      // 打印命令，便于调试和查看
      String commandString = String.join(" ", command);
      log.info("正在执行命令：{}", commandString);

      // 创建 ProcessBuilder，传入命令和参数
      processBuilder = new ProcessBuilder(command);
      processBuilder.redirectErrorStream(true); // 合并标准输出和标准错误输出

      // 启动进程
      Process process = processBuilder.start();

//      // 读取并打印ffmpeg的输出流
//      try (BufferedReader reader = new BufferedReader(
//        new InputStreamReader(process.getInputStream()))) {
////        String line;
////        while ((line = reader.readLine()) != null) {
////          log.info("ffmpeg输出: {}", line);  // 打印ffmpeg的日志输出
////        }
//      }

      // 等待进程执行完毕
      int exitCode = process.waitFor();

      // 检查执行结果
      if (exitCode == 0) {
        long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
        long durationMillis = endTime - startTime; // 计算耗时（毫秒）
        log.info("视频创建成功: {}，耗时: {}", videoFile.getAbsolutePath(),
          CdTimeUtil.formatDuration(durationMillis));
      } else {
        log.error("视频创建失败: {}", videoFile.getAbsolutePath());
      }

    } catch (Exception e) {
      log.error("合成视频时出错", e);
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
    ProcessBuilder processBuilder;

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
