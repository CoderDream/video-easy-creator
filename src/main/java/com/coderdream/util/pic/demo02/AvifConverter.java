package com.coderdream.util.pic.demo02;

import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * AVIF 文件批量转换工具类。 支持将 AVIF 文件批量转换为 PNG 或 JPG 格式。
 */
@Slf4j
public class AvifConverter {

  /**
   * 主方法，程序入口。
   *
   * @param args 参数列表：[输入目录] [输出目录] [输出格式(png/jpg)]
   */
  public static void main(String[] args) {
//    if (args.length < 3) {
//      log.error(
//        "Usage: java AvifConverter <inputDir> <outputDir> <outputFormat (png/jpg)>");
//      return;
//    }
    String outputFormat = "png";//args[2].toLowerCase();
    String inputDir = "D:\\Download\\avif\\";// args[0];
    String outputDir = "D:\\Download\\" + outputFormat;//args[1];

    if (!outputFormat.equals("png") && !outputFormat.equals("jpg")) {
      log.error("Output format must be 'png' or 'jpg'.");
      return;
    }

    convertAvifToImage(inputDir, outputDir, outputFormat);
  }

  /**
   * 批量转换 AVIF 文件为指定格式。
   *
   * @param inputDir     输入文件夹路径。
   * @param outputDir    输出文件夹路径。
   * @param outputFormat 输出图片格式（png 或 jpg）。
   */
  public static void convertAvifToImage(String inputDir, String outputDir,
    String outputFormat) {
    Instant start = Instant.now();

    try {
      Files.createDirectories(Paths.get(outputDir));

      try (var paths = Files.walk(Paths.get(inputDir))) {
        paths.filter(path -> path.toString().toLowerCase().endsWith(".avif"))
          .forEach(
            avifFile -> convertToImage(avifFile, outputDir, outputFormat));
      }

      log.info("Conversion completed.");
    } catch (IOException e) {
      log.error("Error during conversion: ", e);
    }

    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    log.info("Total time: {} hours {} minutes {} seconds {} milliseconds",
      duration.toHoursPart(),
      duration.toMinutesPart(),
      duration.toSecondsPart(),
      duration.toMillisPart());
  }

  /**
   * 将单个 AVIF 文件转换为指定格式。
   *
   * @param avifFile     AVIF 文件路径。
   * @param outputDir    输出文件夹路径。
   * @param outputFormat 输出图片格式（png 或 jpg）。
   */
  private static void convertToImage(Path avifFile, String outputDir,
    String outputFormat) {
    Instant start = Instant.now();

    String outputFileName =
      outputDir + File.separator + avifFile.getFileName().toString()
        .replaceAll("(?i)\\.avif$", "." + outputFormat);
    ProcessBuilder builder = new ProcessBuilder("ffmpeg", "-i",
      avifFile.toString(), outputFileName);

    try {
      Process process = builder.start();
      try (InputStream is = process.getInputStream();
        InputStream es = process.getErrorStream()) {
        is.transferTo(System.out);
        es.transferTo(System.err);
      }

      int exitCode = process.waitFor();
      if (exitCode == 0) {
        log.info("Converted: {} -> {}", avifFile, outputFileName);
      } else {
        log.error("Failed to convert: {}", avifFile);
      }
    } catch (IOException | InterruptedException e) {
      log.error("Error during file conversion: ", e);
      Thread.currentThread().interrupt();
    }

    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    log.info(
      "File {} conversion time: {} hours {} minutes {} seconds {} milliseconds",
      avifFile.getFileName(),
      duration.toHoursPart(),
      duration.toMinutesPart(),
      duration.toSecondsPart(),
      duration.toMillisPart());
  }
}
