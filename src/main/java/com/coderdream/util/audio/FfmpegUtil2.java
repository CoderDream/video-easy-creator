package com.coderdream.util.audio;

import com.coderdream.util.cd.CdDateTimeUtils;
import com.coderdream.util.cd.CdTimeUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  public static void extractAudioFromMp4(String inputFilePath, String outputFilePath) {
    long startTime = System.currentTimeMillis(); // 记录开始时间

    List<String> command = List.of(
      "ffmpeg", "-y", "-i", inputFilePath, "-q:a", "2", "-vn", outputFilePath
    );

    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true);  // 合并标准输出和错误输出

    try {
      Process process = processBuilder.start();

      // 异步读取 FFmpeg 输出，防止阻塞
      new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            log.info("FFmpeg: {}", line);
          }
        } catch (IOException e) {
          log.error("读取 FFmpeg 输出失败: {}", e.getMessage());
        }
      }).start();

      int exitCode = process.waitFor();
      long elapsedTime = System.currentTimeMillis() - startTime; // 计算耗时

      if (exitCode == 0) {
        log.info("音频提取成功: {}", outputFilePath);
        log.info("转换耗时: {}", CdTimeUtil.formatDuration(elapsedTime));
      } else {
        log.error("extractAudioFromMp4 FFmpeg 失败，错误码: {}", exitCode);
      }

    } catch (IOException | InterruptedException e) {
      log.error("执行 FFmpeg 过程中发生异常: {}", e.getMessage());
      Thread.currentThread().interrupt(); // 重新设置线程中断状态
    }
  }

  // 提取 MP3 音频
  public static void extractAudioFromMp4(String filePath) throws IOException {
    Path inputPath = Paths.get(filePath);
    String outputFilePath = inputPath.toString().replaceAll("\\.mp4$", ".mp3");

    List<String> command = List.of("ffmpeg", "-y", "-i", filePath, "-q:a", "2", "-vn", outputFilePath);
//    List<String> command = List.of("ffmpeg", "-y", "-i", filePath, "-q:a", "2", "-vn", "-progress", "pipe:1", outputFilePath);

    ProcessBuilder processBuilder = new ProcessBuilder(command);

    processBuilder.redirectErrorStream(true);  // 合并标准输出和错误输出
    Process process = processBuilder.start();

    // 处理输出流，防止阻塞
    new Thread(() -> {
      try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          log.info("FFmpeg: {}", line);
        }
      } catch (IOException e) {
        log.error("读取 FFmpeg 输出失败: {}", e.getMessage());
      }
    }).start();

    try {
      int exitCode = process.waitFor();
      if (exitCode == 0) {
        log.info("音频提取成功: {}", outputFilePath);
      } else {
        log.error("FFmpeg 失败，错误码: {}", exitCode);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("执行 FFmpeg 命令时被中断", e);
    }
  }

}
