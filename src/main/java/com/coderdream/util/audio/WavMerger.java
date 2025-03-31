package com.coderdream.util.audio;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WAV 文件合并工具类
 */
@Slf4j
public class WavMerger {

  /**
   * 合并多个 WAV 文件为一个 WAV 文件，并将文件列表保存到指定文件夹下。
   *
   * @param wavFilePaths   WAV 文件路径列表
   * @param outputFilePath 合并后的 WAV 文件路径
   */
  public static String mergeWavFiles(List<String> wavFilePaths,
    String outputFilePath) {
    long startTime = System.currentTimeMillis();
    log.info("开始合并 WAV 文件，输出文件：{}", outputFilePath);

    Path outputFile = Paths.get(outputFilePath);
    Path outputDir = outputFile.getParent();

    // 创建输出目录，如果不存在
    if (outputDir != null && !Files.exists(outputDir)) {
      try {
        Files.createDirectories(outputDir);
      } catch (IOException e) {
        log.error("创建输出目录失败：{}", e.getMessage(), e);
        return "创建输出目录失败"; // 或者返回一个默认值，表示合并失败
      }
    }

    // 1. 创建 filelist 文件
    assert outputDir != null;
    Path fileListPath = Paths.get(outputDir.toString(), "filelist.txt");
    if (!createFileList(wavFilePaths, fileListPath)) {
      return "创建 filelist 文件失败"; // 创建 filelist 文件失败
    }

    // 2. 使用 FFmpeg 合并 WAV 文件
    if (!executeFfmpegMerge(fileListPath.toString(), outputFilePath)) {
      return "FFmpeg 合并失败"; //FFmpeg 合并失败
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    String duration = CdTimeUtil.formatDuration(elapsedTime);
    log.info("WAV 文件合并完成，耗时：{}", duration);

    return duration;
  }

  /**
   * 创建包含 WAV 文件列表的文本文件，供 FFmpeg 使用。
   *
   * @param wavFilePaths WAV 文件路径列表
   * @param fileListPath filelist.txt 文件路径
   * @return boolean  true:创建成功  false:创建失败
   */
  private static boolean createFileList(List<String> wavFilePaths,
    Path fileListPath) {
    log.info("创建 filelist 文件：{}", fileListPath.toString());
    try (BufferedWriter writer = Files.newBufferedWriter(fileListPath)) {
      for (String filePath : wavFilePaths) {
        writer.write("file '" + filePath + "'");
        writer.newLine();
      }
      log.info("filelist 文件创建完成");
      return true;
    } catch (IOException e) {
      log.error("创建 filelist 文件失败：{}", e.getMessage(), e);
      return false;
    }
  }

  /**
   * 执行 FFmpeg 命令进行合并 WAV 文件。
   *
   * @param listFilePath   包含 WAV 文件列表的文本文件路径
   * @param outputFilePath 合并后的 WAV 文件路径
   * @return boolean  true:执行成功  false:执行失败
   */
  private static boolean executeFfmpegMerge(String listFilePath,
    String outputFilePath) {
    List<String> command = List.of(
      "ffmpeg",
      "-y", // 覆盖输出文件，如果存在
      "-f", "concat",
      "-safe", "0", // 允许访问任何文件，即使它们不是在同一个目录下
      "-i", listFilePath,
      "-c", "copy", // 直接复制流，避免重新编码
      outputFilePath
    );

    ProcessBuilder processBuilder = new ProcessBuilder(command);
    // 打印命令，便于调试和查看
    log.info("执行 FFmpeg 命令：{}", String.join(" ", processBuilder.command()));
    processBuilder.redirectErrorStream(true); // 将错误输出也定向到标准输出

    try {
      Process process = processBuilder.start();

      // 异步读取 FFmpeg 输出，防止阻塞
      new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(process.getInputStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            log.info("FFmpeg: {}", line);
          }
        } catch (IOException e) {
          log.error("读取 FFmpeg 输出失败: {}", e.getMessage(), e);
        }
      }).start();

      int exitCode = process.waitFor();

      if (exitCode == 0) {
        log.info("FFmpeg 命令执行成功.");
        return true;
      } else {
        try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(process.getErrorStream()))) {
          String errorMessage = reader.lines()
            .collect(Collectors.joining("\n"));
          log.error("FFmpeg 命令失败，错误码: {}, 错误信息: {}", exitCode,
            errorMessage);
        }
        return false;
      }
    } catch (IOException e) {
      log.error("执行 FFmpeg 命令时发生异常: {}", e.getMessage(), e);
      return false;
    } catch (InterruptedException e) {
      log.error("执行 FFmpeg 命令时发生中断: {}", e.getMessage(), e);
      Thread.currentThread().interrupt(); // 重新设置中断状态
      return false;
    }
  }

  public static void main(String[] args) {
    // 示例用法
    List<String> wavFilePaths = List.of(
      "D:\\0000\\EnBook005\\head\\EnBook005_en.wav",
      "D:\\0000\\EnBook005\\head\\EnBook005_cn.wav"
    );
    String outputFilePath = "D:\\0000\\EnBook005\\head\\EnBook005_head.wav";

    if (CdFileUtil.isFileEmpty(outputFilePath)) {
      String duration = mergeWavFiles(wavFilePaths, outputFilePath);
      System.out.println("合并完成，耗时: " + duration);
    }
  }
}
