package com.coderdream.util.ffmpeg;

import com.coderdream.util.cd.CdTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.Instant;

/**
 * FFmpeg 视频处理工具类，用于将图片和音频叠加到背景视频上，并生成新的视频。
 */
@Slf4j
public class FFmpegVideoOverlay {

  /**
   * 使用 FFmpeg 将图片和音频叠加到背景视频上生成新的视频。
   *
   * @param backgroundVideoPath 背景视频的路径
   * @param imagePath           要叠加的图片的路径
   * @param audioPath           要叠加的音频的路径
   * @param outputPath          输出视频的路径
   * @param duration            输出视频的持续时间 (秒) ，如果为 -1 ，则使用背景视频的长度
   * @throws IOException          如果 FFmpeg 命令执行失败
   * @throws InterruptedException 如果 FFmpeg 命令被中断
   */
  public static String createVideoWithOverlay(String backgroundVideoPath,
    String imagePath, String audioPath, String outputPath, int duration)
    throws IOException, InterruptedException {
    Instant start = Instant.now(); // 记录方法开始时间

    // 1. 构建 FFmpeg 命令
    List<String> command = new ArrayList<>();
    command.add("ffmpeg");

    // 2. 添加 -y 参数，自动覆盖输出文件
    command.add("-y");

    // 3. 循环播放背景视频的参数
    command.add("-stream_loop");
    command.add("-1"); // -1 表示无限循环

    // 4. 输入背景视频
    command.add("-i");
    command.add(backgroundVideoPath);

    // 5. 输入图片
    command.add("-i");
    command.add(imagePath);

    // 6. 输入音频
    command.add("-i");
    command.add(audioPath);

    // 7. 设置视频滤镜
    // [1:v]format=rgba,colorchannelmixer=aa=0.8[ovrl]: 将图片转换为 RGBA 格式并设置透明度为 80%。
    // [0:v][ovrl]overlay=x=0:y=0[outv]: 将透明图片叠加到背景视频上，并将结果命名为 [outv]。
    String filterComplex = "[1:v]format=rgba,colorchannelmixer=aa=0.5[ovrl];[0:v][ovrl]overlay=x=0:y=0[outv]";
    command.add("-filter_complex");
    command.add(filterComplex);

    // 8. 映射视频和音频流
    command.add("-map");
    command.add("[outv]"); // 映射处理后的视频流
    command.add("-map");
    command.add("2:a");   // 映射音频流 (来自第三个输入)

    // 9. 视频编码器设置
    command.add("-vcodec");
    command.add("libx264");       // 使用 libx264 编码器
    command.add("-preset");
    command.add("ultrafast");    // 编码速度预设，ultrafast 最快，但质量最低
    command.add("-crf");
    command.add(
      "23");            // Constant Rate Factor, 控制视频质量和文件大小，23 是一个不错的默认值
    command.add("-pix_fmt");
    command.add("yuv420p");       // 像素格式，yuv420p 与大多数播放器兼容

    // 10. 音频编码器设置
    command.add("-acodec");
    command.add("aac");           // 使用 AAC 编码器
    command.add("-b:a");
    command.add("128k");          // 音频比特率，128kbps

    // 11. 设置输出视频的持续时间 (可选)
    if (duration > 0) {
      command.add("-t");
      command.add(String.valueOf(duration)); // 设置视频持续时间
    }

    // 12. 输出视频
    command.add(outputPath); // 输出文件路径

    // 打印 FFmpeg 命令
    String fullCommand = String.join(" ", command);
    log.info("FFmpeg command: {}", fullCommand);

    // 13. 执行 FFmpeg 命令
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true); // 合并错误输出到标准输出

    Process process = processBuilder.start();

    // 读取 FFmpeg 的输出
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(process.getInputStream()));
    String line;
    while ((line = reader.readLine()) != null) {
      log.info(line); // 记录 FFmpeg 输出
    }

    // 等待 FFmpeg 命令执行完成
    int exitCode = process.waitFor();
    if (exitCode != 0) {
      log.error("FFmpeg 命令执行失败，退出代码: {}", exitCode);
      throw new IOException("FFmpeg 命令执行失败，退出代码: " + exitCode);
    } else {
      log.info("视频生成成功！");
    }

    Instant end = Instant.now();   // 记录方法结束时间
    Duration timeElapsed = Duration.between(start, end);

    //格式化耗时
    String elapsedTimeStr = CdTimeUtil.formatDuration(timeElapsed.toMillis());

    log.info("方法执行耗时: {}", elapsedTimeStr); //记录耗时

    return elapsedTimeStr;
  }


  /**
   * 主方法，用于测试视频叠加功能。
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    String backgroundVideo = "D:\\0000\\background\\ocean_0001\\ocean_0001_pure_part.mp4"; // 替换为你的背景视频路径
    String image = "D:\\0000\\EnBook002\\Chapter001\\pic_cht\\Chapter001_total_phonetics_013.png";    // 替换为你的图片路径
    String audio = "D:\\0000\\EnBook002\\Chapter001\\audio_mix\\013.wav"; // 替换为你的音频路径
    String outputVideo = "D:\\0000\\background\\ocean_0001\\ocean_0001_pure_part_output.mp4";   // 替换为你的输出视频路径
    int duration = 23; // 输出视频的持续时间 (秒)

    try {
      String elapsedTime = createVideoWithOverlay(backgroundVideo, image, audio,
        outputVideo, duration);
      log.info("Total elapsed time: {}", elapsedTime);

    } catch (IOException | InterruptedException e) {
      log.error("Error creating video: {}", e.getMessage(), e); // 记录异常信息
    }
  }
}
