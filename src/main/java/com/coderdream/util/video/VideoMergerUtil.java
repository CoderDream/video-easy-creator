package com.coderdream.util.video;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class VideoMergerUtil {

  /**
   * 使用 FFmpeg 合并多个视频文件。
   *
   * @param videoFiles 要合并的视频文件列表（按顺序）
   * @param outputFile 输出的合并后的视频文件
   */
  public static void mergeVideos(List<String> videoFileNames, String outputFile)
    throws IOException {
    // 1. 创建一个包含所有视频文件路径的文本文件 (FFmpeg concat demuxer 格式)
    Path listFile = Files.createTempFile("ffmpeg_concat_list", ".txt");
    try (BufferedWriter writer = Files.newBufferedWriter(listFile,
      StandardOpenOption.CREATE)) {
      for (String videoFileName : videoFileNames) {
        writer.write("file '" + videoFileName + "'");
        writer.newLine();
      }
    }

    // 2. 构建 FFmpeg 命令
    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    command.add("-y"); // 覆盖输出文件
    command.add("-f");
    command.add("concat");
    command.add("-safe");
    command.add("0");
    command.add("-i");
    command.add(listFile.toAbsolutePath().toString());
    command.add("-c");
    command.add("copy"); // 使用流复制，避免重新编码，速度快
    command.add(outputFile);

    // 3. 执行 FFmpeg 命令
    String commandString = String.join(" ", command);
    log.info("执行 FFmpeg 合并命令: {}", commandString);

    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true); // 将错误输出合并到标准输出
    Process process = processBuilder.start();

    // 读取 FFmpeg 输出
    try (BufferedReader reader = new BufferedReader(
      new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.debug("{}", line); // FFmpeg 输出 (debug 级别)
      }
    }

    int exitCode;
    try {
      exitCode = process.waitFor();
    } catch (InterruptedException e) {
      log.error("FFmpeg 合并进程被中断", e);
      Thread.currentThread().interrupt(); // 重新设置中断状态
      throw new IOException("FFmpeg 合并进程被中断", e);
    }

    // 4. 检查执行结果
    if (exitCode == 0) {
      log.info("视频合并成功: {}", outputFile);
    } else {
      throw new IOException("FFmpeg 合并失败，退出代码: " + exitCode);
    }

    // 5. 删除列表文件
    try {
      Files.delete(listFile);
    } catch (IOException e) {
      log.warn("无法删除临时列表文件: {}", listFile, e);
    }
  }


  public static void mergerVideos(String videoPath, String outputFile) {

    List<String> videoPathNameList = FileUtil.listFileNames(videoPath);

    // 如果图片列表数量和音频列表数量不一致，则抛出异常
    videoPathNameList.sort(String::compareTo);
    try {
      mergeVideos(videoPathNameList, outputFile);
    } catch (IOException e) {
      log.error("视频合并失败，{}", e.getMessage(), e);
    }
  }
}
