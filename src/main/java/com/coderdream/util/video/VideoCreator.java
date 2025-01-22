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
import java.util.*;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

@Slf4j
public class VideoCreator {

  private static final String IMAGE_PATH = "D:/04_GitHub/video-easy-creator/src/main/resources/pic";
  private static final String AUDIO_CN_PATH = "D:/04_GitHub/video-easy-creator/src/main/resources/wav/cn";
  private static final String AUDIO_EN_PATH = "D:/04_GitHub/video-easy-creator/src/main/resources/wav/en";
  private static final String OUTPUT_PATH = "D:/04_GitHub/video-easy-creator/src/main/resources/output_video/";

  // 创建线程池，最多支持4个并发任务
  private static final int THREAD_POOL_SIZE = 4;
  private static final ExecutorService executor = new ThreadPoolExecutor(
      THREAD_POOL_SIZE, THREAD_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

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
      String lang = "cn"; // 默认为中文

      // 2. 根据图片文件名提取对应的AUDIO文件
      List<File> audioCnFiles = getAudioFiles(imageFiles, audioType, lang);
      if (audioCnFiles.isEmpty()) {
        log.error("没有找到对应的 {} 文件！{} {}", audioType, imageFiles, lang);
        return;
      }

      // 3.1 使用线程池并行生成视频
      List<Future<?>> videoCnFutures = new ArrayList<>();
      for (int i = 0; i < imageFiles.size(); i++) {
        final int index = i;
        String finalLang = lang;
        videoCnFutures.add(executor.submit(() -> {
          File imageFile = imageFiles.get(index);
          File audioFile = audioCnFiles.get(index);
          File videoFile = new File(OUTPUT_PATH + "video_" + (index + 1) + "_" + finalLang
            + ".mp4");

          // 计算AUDIO时长
          double duration = getAudioDuration(audioFile);

          // 生成视频
          createVideo(imageFile, audioFile, videoFile, duration);
        }));
      }

      // 等待所有中文视频任务完成
      for (Future<?> future : videoCnFutures) {
        future.get();
      }

      // 英文部分
      lang = "en"; // 英文
      List<File> audioEnFiles = getAudioFiles(imageFiles, audioType, lang);
      if (audioEnFiles.isEmpty()) {
        log.error("没有找到对应的 {} 文件！{} {}", audioType, imageFiles, lang);
        return;
      }

      // 3.2 使用线程池并行生成英文视频
      List<Future<?>> videoEnFutures = new ArrayList<>();
      for (int i = 0; i < imageFiles.size(); i++) {
        final int index = i;
        String finalLang1 = lang;
        videoEnFutures.add(executor.submit(() -> {
          File imageFile = imageFiles.get(index);
          File audioFile = audioEnFiles.get(index);
          File videoFile = new File(OUTPUT_PATH + "video_" + (index + 1) + "_" + finalLang1
            + ".mp4");

          // 计算AUDIO时长
          double duration = getAudioDuration(audioFile);

          // 生成视频
          createVideo(imageFile, audioFile, videoFile, duration);
        }));
      }

      // 等待所有英文视频任务完成
      for (Future<?> future : videoEnFutures) {
        future.get();
      }

      // 4. 合并所有视频文件
      mergeVideos(audioCnFiles, audioEnFiles);

    } catch (Exception e) {
      log.error("视频创建过程中出现异常", e);
    } finally {
      executor.shutdown();
    }
  }

  private static List<File> getImageFiles() {
    File dir = new File(IMAGE_PATH);
    File[] files = dir.listFiles((dir1, name) -> name.endsWith(".png"));
    if (files != null) {
      return Arrays.asList(files);
    }
    return Collections.emptyList();
  }

  private static List<File> getAudioFiles(List<File> imageFiles, String audioType, String lang) {
    List<File> audioFiles = new ArrayList<>();
    for (File imageFile : imageFiles) {
      String imageName = imageFile.getName();
      int indexBegin = imageName.lastIndexOf("_");
      int indexEnd = imageName.lastIndexOf(".");
      String indexStr = imageName.substring(indexBegin + 1, indexEnd);
      int number = Integer.parseInt(indexStr);
      String audioName = imageName.substring(0, indexBegin + 1) +
          MessageFormat.format("{0,number,000}", number) + "_" + lang + "." + audioType;

      String audioPath = switch (lang) {
        case "cn" -> AUDIO_CN_PATH;
        case "en" -> AUDIO_EN_PATH;
        default -> throw new IllegalArgumentException("Unsupported language: " + lang);
      };
      File audioFile = new File(audioPath, audioName);
      if (audioFile.exists()) {
        audioFiles.add(audioFile);
      } else {
        log.warn("getAudioFiles 未找到对应的 {} 文件: {} \\ {}", audioType, audioPath, audioName);
      }
    }
    return audioFiles;
  }

  public static double getAudioDuration(File audioFile) {
    try {
      String command = "ffmpeg -i " + audioFile.getAbsolutePath() + " 2>&1";
      log.info("正在执行命令： " + command);
      Process process = Runtime.getRuntime().exec(command);
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains("Duration")) {
          String durationStr = DurationParser.extractDuration(line);
          if (Strings.isBlank(durationStr)) {
            log.error("durationStr为空: {}", durationStr);
            return 0;
          }

          String[] timeParts = durationStr.split(":");
          if (timeParts.length != 3) {
            log.error("AUDIO文件时长格式不正确: {}", durationStr);
            return 0;
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

  public static void createVideo(File imageFile, File audioFile, File videoFile, double duration) {
    long startTime = System.currentTimeMillis();

    try {
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

      String commandString = String.join(" ", command);
//      log.info("正在执行命令：{}", commandString);
      // 打印构建的 ffmpeg 命令
//      log.info("构建并执行 ffmpeg 命令：{}", String.join(" ", command));

      ProcessBuilder processBuilder = new ProcessBuilder(command);
      processBuilder.redirectErrorStream(true);

      Process process = processBuilder.start();
      int exitCode = process.waitFor();

      if (exitCode == 0) {
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        log.info("视频创建成功: {}，耗时: {}", videoFile.getAbsolutePath(), CdTimeUtil.formatDuration(durationMillis));
      } else {
        log.error("视频创建失败: {}", videoFile.getAbsolutePath());
      }
    } catch (Exception e) {
      log.error("合成视频时出错", e);
    }
  }

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
