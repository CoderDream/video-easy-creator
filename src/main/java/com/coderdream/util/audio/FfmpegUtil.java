package com.coderdream.util.audio;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FfmpegUtil {

  // 执行 ffmpeg 命令进行合并
  public static void executeFfmpegMerge(String listFilePath,
    String outputFilePath) throws IOException {

    if (listFilePath == null || listFilePath.trim().isEmpty()) {
      log.error("listFilePath 不能为空");
      throw new IllegalArgumentException("listFilePath 不能为空");
    }

    if (outputFilePath == null || outputFilePath.trim().isEmpty()) {
      log.error("outputFilePath 不能为空");
      throw new IllegalArgumentException("outputFilePath 不能为空");
    }

    if (!Files.exists(Paths.get(listFilePath))) {
      log.error("listFilePath 文件不存在：{}", listFilePath);
      throw new IOException("listFilePath 文件不存在：" + listFilePath);
    }

    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    command.add("-y");
    command.add("-f");
    command.add("concat");
    command.add("-safe");
    command.add("0");
    command.add("-i");
    command.add(listFilePath);
    command.add("-map");
    command.add("0:a");
    command.add("-c");
    command.add("copy");
    command.add(outputFilePath);
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    // 打印命令，便于调试和查看
    log.info("执行 FFmpeg 命令：{}", String.join(" ", processBuilder.command()));
    processBuilder.redirectErrorStream(true); //将错误输出也定向到标准输出

    Process process = processBuilder.start();
    try (BufferedReader reader = new BufferedReader(
      new InputStreamReader(process.getInputStream()))) {
      int exitCode = process.waitFor();

      if (exitCode == 0) {
        log.info("FFmpeg command executed successfully.");
      } else {
        String errorMessage = reader.lines().collect(Collectors.joining("\n"));
        log.error("FFmpeg command failed with exit code: {}, error: {}",
          exitCode, errorMessage);
        throw new IOException(
          "FFmpeg command failed with exit code: " + exitCode + ", error: "
            + errorMessage);
      }

    } catch (InterruptedException e) {
      log.error("执行 FFmpeg 命令时发生错误: {}", e.getMessage(), e);
      Thread.currentThread().interrupt();
      throw new IOException("执行 FFmpeg 命令时发生错误", e);
    }
  }
}
