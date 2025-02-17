package com.coderdream.util.video;

import static com.coderdream.util.cd.CdConstants.OS_MAC;
import static com.coderdream.util.cd.CdConstants.OS_WINDOWS;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PureCreateVideo02 {
  public static void createVideoCore(File imageFile, File audioFile, File videoFile,
    double duration) throws Exception {
    long startTime = System.currentTimeMillis();
    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    command.add("-y"); // 覆盖输出文件（如果存在）
    command.add("-loop");
    command.add("1"); // 循环输入图片
    command.add("-framerate");
    command.add("60"); // 设置帧率为 60
    command.add("-t");
    command.add(String.format("%.2f", duration)); // 设置视频时长
    command.add("-i");
    command.add(imageFile.getAbsolutePath()); // 输入图片文件
    command.add("-i");
    command.add(audioFile.getAbsolutePath()); // 输入音频文件
    command.add("-s");
    command.add("3840x2160"); // 设置分辨率为 4K (3840x2160)

    // 根据操作系统选择不同的编码器和参数
    String os = OperatingSystem.getOS();
    if (OS_WINDOWS.equals(os)) {
      // Windows 使用 NVIDIA 硬件加速 (h264_nvenc)
      command.add("-c:v");
      command.add("h264_nvenc");
      command.add("-preset");
      command.add("p4"); // 使用 p4 预设.  可以根据需要调整 (p1 - p7, slow, medium, fast, etc.)
      command.add("-b:v"); //设置比特率
      command.add("10000k");
      //command.add("-rc");
      //command.add("vbr");  // 使用 vbr 可变比特率
      //command.add("-cq");  //和 -rc vbr 一起使用
      //command.add("19");   //和 -rc vbr 一起使用
    } else if (OS_MAC.equals(os)) {
      // macOS 使用 VideoToolbox 硬件加速 (h264_videotoolbox)
      command.add("-c:v");
      command.add("h264_videotoolbox");
      command.add("-q:v");  // 使用质量模式, 1-100, 值越小质量越高
      command.add("60"); // 设置质量 (根据需要调整)
    } else {
      // 其他操作系统使用 libx264 软件编码
      command.add("-c:v");
      command.add("libx264");
      command.add("-preset");
      command.add("medium"); // 可以是 ultrafast, superfast, veryfast, faster, fast, medium, slow, slower, veryslow
      command.add("-crf");
      command.add("19");     // 0-51, 0 是无损, 23 是默认值, 18-28 是常用范围. 值越小质量越高.
    }

    command.add("-c:a");
    command.add("aac"); // 使用 AAC 音频编码
    command.add("-ac");
    command.add("2");    // 设置音频通道数为 2 (立体声)
    command.add("-shortest"); // 以最短的输入流（音频或视频）为准结束
    command.add(videoFile.getAbsolutePath()); // 输出视频文件

    String commandString = String.join(" ", command);
    log.info("执行 FFmpeg 命令: {}", commandString);

    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true); // 将错误输出合并到标准输出

    Process process = processBuilder.start();

    // 读取 FFmpeg 的输出（包括标准输出和错误输出）
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        // 在这里可以处理 FFmpeg 的输出，例如记录日志或显示进度
        // 但要注意，大量的 FFmpeg 输出可能会导致程序阻塞，
        // 所以如果不需要详细的 FFmpeg 输出，最好不要在这里打印每一行
//        log.info("{}", line); // 将FFmpeg的输出也记录到日志中
      }
    }

    int exitCode = process.waitFor(); // 等待 FFmpeg 进程结束

    if (exitCode == 0) {
      long endTime = System.currentTimeMillis();
      long durationMillis = endTime - startTime;
      log.info("视频创建成功 (文件: {}), 耗时: {}", videoFile.getAbsolutePath(), CdTimeUtil.formatDuration(durationMillis));
    } else {
      throw new Exception("FFmpeg 进程执行失败，退出代码: " + exitCode);
    }
  }
}
