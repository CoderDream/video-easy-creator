package com.coderdream.util.video;

import com.coderdream.util.CdTimeUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VideoCreatorUtil3 {

  /**
   * 使用FFmpeg将图片和音频合成一个视频
   *
   * @param imageFile 图片文件
   * @param audioFile AUDIO文件
   * @param videoFile 输出的视频文件
   * @param duration  视频时长
   */
  public static void createVideo(File imageFile, File audioFile, File videoFile,
    double duration) {
    long startTime = System.currentTimeMillis(); // 记录开始时间
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

      // 9. 使用 NVIDIA GPU 进行编码
      command.add("-c:v"); // 设置视频编码器
      command.add("h264_nvenc"); // 使用 NVIDIA GPU 的 h264 编码器

      command.add("-preset"); // 10. 设置编码预设
      command.add("fast"); // 使用 "fast" 预设，提高编码速度

      command.add("-rc:v"); // 11. 设置码率控制方法
      command.add("vbr_hq"); // 使用高质量 VBR（可变比特率）编码

      command.add("-cq:v"); // 12. 设置质量控制
      command.add("19"); // 19 是比较好的质量，范围是 0-51，数字越低质量越高

      command.add("-b:v"); // 13. 设置视频码率
      command.add("10000k"); // 设置视频码率为 10Mbps，适合高质量的视频

      command.add("-c:a"); // 14. 音频编码器
      command.add("aac"); // 使用 AAC 音频编码

      command.add("-b:a"); // 15. 设置音频码率
      command.add("192k"); // 设置音频码率为 192kbps（高音质）

      command.add("-ac"); // 16. 设置音频通道数为 2（立体声）
      command.add("2"); // 立体声：2 通道

      command.add("-shortest"); // 17. 确保视频的时长与音频匹配

      command.add(videoFile.getAbsolutePath()); // 18. 输出文件路径

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
          // log.info("ffmpeg输出: {}", line);  // 打印ffmpeg的日志输出
        }
      }

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

}
