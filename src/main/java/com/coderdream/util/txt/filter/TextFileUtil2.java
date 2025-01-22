package com.coderdream.util.txt.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * 文本文件处理工具类
 */
@Slf4j
public class TextFileUtil2 {

  /**
   * 移除文本文件中的空行，并将结果写入目标文件。
   *
   * @param sourceFilePath 源文件路径
   * @param targetFilePath 目标文件路径
   * @return 耗时，格式为：时:分:秒.毫秒
   */
  public static String removeEmptyLinesAndWrite(String sourceFilePath,
    String targetFilePath) {
    Instant start = Instant.now(); // 记录开始时间
    log.info("开始处理文件，源文件路径：{}，目标文件路径：{}", sourceFilePath,
      targetFilePath);

    try {
      removeEmptyLines(sourceFilePath, targetFilePath); // 调用私有方法移除空行并写入
      log.info("文件处理完成，源文件路径：{}，目标文件路径：{}", sourceFilePath,
        targetFilePath);
    } catch (IOException e) {
      log.error("文件处理失败，源文件路径：{}，目标文件路径：{}，错误信息：{}",
        sourceFilePath, targetFilePath, e.getMessage(), e);
      throw new RuntimeException("文件处理失败", e); // 抛出运行时异常，方便上层处理
    }

    Instant end = Instant.now();  // 记录结束时间
    Duration duration = Duration.between(start, end); // 计算耗时
    String elapsedTime = formatDuration(duration);  // 格式化耗时
    log.info("文件处理耗时：{}", elapsedTime);
    return elapsedTime;
  }

  /**
   * 移除文本文件中的空行，并将结果写入目标文件。
   *
   * @param sourceFilePath 源文件路径
   * @param targetFilePath 目标文件路径
   * @throws IOException IO异常
   */
  private static void removeEmptyLines(String sourceFilePath,
    String targetFilePath) throws IOException {

    // 使用 try-with-resources 确保资源被正确关闭
    try (BufferedReader reader = Files.newBufferedReader(
      Paths.get(sourceFilePath));
      BufferedWriter writer = Files.newBufferedWriter(Paths.get(targetFilePath),
        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

      List<String> lines = reader.lines()
//                   .filter(line -> !line.trim().isEmpty())
        .toList();

      List<String> newList = extracted(lines);

      for (String line : newList) {
        writer.write(line);
        writer.newLine();
      }
    }
  }

  private static List<String> extracted(List<String> lines) {
    List<String> lines2 = new ArrayList<>();
    // 存在冒号
    boolean hasColon = false;
    for (String line : lines) {
      if (line.trim().isEmpty()) {
        continue;
      }
      if (hasColon) {
        lines2.add(line);
        hasColon = false;
        continue;
      }

      // 如果包含中英文冒号
      if (line.contains(":") || line.contains("：")) {
        lines2.add(line);
        hasColon = true;
      }
    }

    return lines2;
  }

  /**
   * 格式化时间
   *
   * @param duration Duration时间
   * @return 返回时分秒毫秒
   */
  private static String formatDuration(Duration duration) {
    long hours = duration.toHours();
    long minutes = duration.toMinutesPart();
    long seconds = duration.toSecondsPart();
    long millis = duration.toMillisPart();
    return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds,
      millis);
  }

  public static void main(String[] args) {
    String sourcePath = "D:\\0000\\EnBook001\\900\\ch003\\ch003_temp.txt";
    String targetPath = "D:\\0000\\EnBook001\\900\\ch003\\ch003.txt";
    String elapsedTime = TextFileUtil2.removeEmptyLinesAndWrite(sourcePath,
      targetPath);
    log.info("耗时：{}", elapsedTime);
  }
}
