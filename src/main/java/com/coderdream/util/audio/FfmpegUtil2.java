package com.coderdream.util.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FfmpegUtil2 {


  // 执行 ffmpeg 命令进行合并
  public static void executeFfmpegMerge(String listFilePath,
    String outputFilePath) throws IOException {
    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    command.add("-y");
    command.add("-f");
    command.add("concat");
    command.add("-safe");
    command.add("0");
    command.add("-i");
    command.add(listFilePath);
    command.add("-c");
    command.add("copy");
    command.add(outputFilePath);
    ProcessBuilder processBuilder = new ProcessBuilder(command);
//    log.info("执行 FFmpeg 命令: {}", command);
    // 打印命令，便于调试和查看
    log.info("执行 FFmpeg 命令：{}", String.join(" ", processBuilder.command()));
    processBuilder.redirectErrorStream(true); //将错误输出也定向到标准输出

    Process process = processBuilder.start();
    try {
      int exitCode = process.waitFor();

      if (exitCode == 0) {
        log.info("FFmpeg command executed successfully.");
      } else {
        String errorMessage = new java.io.BufferedReader(
          new java.io.InputStreamReader(process.getInputStream()))
          .lines()
          .collect(Collectors.joining("\n"));
        log.error("FFmpeg command failed with exit code: {}, error: {}",
          exitCode, errorMessage);
        throw new IOException(
          "FFmpeg command failed with exit code: " + exitCode + ", error: "
            + errorMessage);
      }

    } catch (InterruptedException e) {
      log.error("执行 FFmpeg 命令时发生错误: {}", e.getMessage());
      Thread.currentThread().interrupt();
      throw new IOException("执行 FFmpeg 命令时发生错误", e);
    }
  }

}
