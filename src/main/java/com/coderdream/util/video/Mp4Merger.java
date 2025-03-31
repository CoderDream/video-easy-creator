package com.coderdream.util.video;

import com.coderdream.util.cd.CdTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用 ffmpeg 合并 MP4 文件，并控制合并数量
 */
@Slf4j
public class Mp4Merger {

  /**
   * 主方法，程序入口
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    String inputDir = "D:\\0000\\EnBook001\\900\\ch01\\video"; // 输入文件夹路径
    int mergeCount = 4; // 合并的视频数量，0 表示全部
    String outputFilePath =
      "D:\\0000\\EnBook001\\900\\ch01\\output_" + mergeCount + ".mp4"; // 输出文件路径

    // 执行合并操作并记录耗时
    String elapsedTime = mergeMp4Files(inputDir, outputFilePath, mergeCount);
    log.info("MP4 合并完成，总耗时: {}", elapsedTime);
  }


  /**
   * 合并指定文件夹下的 MP4 文件
   *
   * @param inputDir       输入文件夹路径
   * @param outputFilePath 输出文件路径
   * @param mergeCount     合并的视频数量，0 表示全部
   * @return 耗时，格式为时分秒毫秒
   */
  public static String mergeMp4Files(String inputDir, String outputFilePath,
    int mergeCount) {
    Instant startTime = Instant.now(); // 记录开始时间
    List<String> fileList = new ArrayList<>();
    File dir = new File(inputDir);

    if (!dir.exists() || !dir.isDirectory()) {
      log.error("输入文件夹不存在或不是一个文件夹: {}", inputDir);
      return "文件夹不存在或不是一个文件夹"; // 返回错误信息
    }

    // 获取文件夹下的所有 mp4 文件
    File[] files = dir.listFiles(
      file -> file.isFile() && file.getName().toLowerCase().endsWith(".mp4"));
    if (files == null || files.length == 0) {
      log.warn("文件夹下没有找到mp4文件, {}", inputDir);
      return "文件夹下没有找到mp4文件";
    }

    // 添加文件到列表
    int count = 0;
    for (File file : files) {
      fileList.add(file.getAbsolutePath());
      count++;
      if (mergeCount > 0 && count >= mergeCount) {
        break;
      }
    }

    // 构建 ffmpeg 命令
    List<String> command = buildFfmpegCommand(fileList, outputFilePath);

    try {
      // 执行 ffmpeg 命令
      executeFfmpegCommand(command);
      Instant endTime = Instant.now(); // 记录结束时间
      Duration duration = Duration.between(startTime, endTime);
      return CdTimeUtil.formatDuration(duration.toMillis());
    } catch (IOException | InterruptedException e) {
      log.error("合并 MP4 文件失败: {}", e.getMessage(), e);
      return "合并 MP4 文件失败";
    }
  }


  /**
   * 构建 ffmpeg 命令
   *
   * @param fileList       输入文件列表
   * @param outputFilePath 输出文件路径
   * @return ffmpeg 命令列表
   */
  private static List<String> buildFfmpegCommand(List<String> fileList,
    String outputFilePath) {
    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    command.add("-y");

    //为每个输入文件添加 -hwaccel cuda 参数，使用cuda解码
    for (String filePath : fileList) {
      command.add("-hwaccel");
      command.add("cuda");
      command.add("-i");
      command.add(filePath);
    }

    // 添加 concat 命令
    command.add("-filter_complex");
    StringBuilder filterComplex = new StringBuilder();
    for (int i = 0; i < fileList.size(); i++) {
      filterComplex.append("[").append(i).append(":v] [").append(i)
        .append(":a]");
    }
    filterComplex.append("concat=n=").append(fileList.size())
      .append(":v=1:a=1[v][a]");
    command.add(filterComplex.toString());

    // 使用 nvenc 进行编码，并指定 GPU ID
    command.add("-map");
    command.add("[v]");
    command.add("-map");
    command.add("[a]");
    command.add("-c:v");
    command.add("h264_nvenc");
    //添加GPU ID
    command.add("-gpu");
    command.add("0");
    command.add(outputFilePath);
    log.info("生成的 ffmpeg 命令: {}", String.join(" ", command));
    return command;
  }


  /**
   * 执行 ffmpeg 命令
   *
   * @param command ffmpeg 命令列表
   * @throws IOException          IO 异常
   * @throws InterruptedException 中断异常
   */
  private static void executeFfmpegCommand(List<String> command)
    throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true); // 将错误输出和标准输出合并
    Process process = processBuilder.start();
    BufferedReader reader = null;

    try {
      reader = new BufferedReader(
        new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        log.info("ffmpeg 输出: {}", line);
      }
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          log.error("关闭 BufferedReader 失败: {}", e.getMessage(), e);
        }
      }
    }

    int exitCode = process.waitFor();
    if (exitCode != 0) {
      throw new IOException("ffmpeg 命令执行失败，退出代码: " + exitCode);
    }
    log.info("ffmpeg 命令执行成功");
  }

}
