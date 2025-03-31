package com.coderdream.util.txt.filter;

import com.coderdream.util.cd.CdTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 文本文件处理工具类，实现移除空行、保留冒号行及其下一行的功能。
 */
@Slf4j
public class TextFileUtil3 {

  /**
   * 处理文本文件，移除空行，保留包含中英文冒号的行及其下一行，并写入目标文件。
   *
   * @param sourceFilePath 源文件路径
   * @param targetFilePath 目标文件路径
   * @return 耗时，格式为：时:分:秒.毫秒
   */
  public static String filterTextFile(String sourceFilePath,
    String targetFilePath) {
    Instant start = Instant.now();
    log.info("开始处理文件，源文件路径：{}，目标文件路径：{}", sourceFilePath,
      targetFilePath);

    try {
      filterLinesAndWrite(sourceFilePath, targetFilePath);
      log.info("文件处理完成，源文件路径：{}，目标文件路径：{}", sourceFilePath,
        targetFilePath);
    } catch (IOException e) {
      log.error("文件处理失败，源文件路径：{}，目标文件路径：{}，错误信息：{}",
        sourceFilePath, targetFilePath, e.getMessage(), e);
      throw new RuntimeException("文件处理失败", e);
    }

    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    String elapsedTime = CdTimeUtil.formatDuration(duration.toMillis());;
    log.info("文件处理耗时：{}", elapsedTime);
    return elapsedTime;
  }


  /**
   * 读取文件内容，根据规则过滤行，并将结果写入目标文件。
   *
   * @param sourceFilePath 源文件路径
   * @param targetFilePath 目标文件路径
   * @throws IOException IO异常
   */
  private static void filterLinesAndWrite(String sourceFilePath,
    String targetFilePath) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(
      Paths.get(sourceFilePath));
      BufferedWriter writer = Files.newBufferedWriter(Paths.get(targetFilePath),
        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

      List<String> filteredLines = filterLines(reader);

      for (String line : filteredLines) {
        writer.write(line);
        writer.newLine();
      }
    }
  }

  /**
   * 读取所有行，并根据规则过滤，移除空行，保留冒号行及其下一行。
   *
   * @param reader BufferedReader 读取器
   * @return 过滤后的列表
   * @throws IOException IO异常
   */
  private static List<String> filterLines(BufferedReader reader)
    throws IOException {
    List<String> filteredLines = new ArrayList<>();
    String previousLine = null; // 记录上一行，用于判断冒号行
    String line;

    while ((line = reader.readLine()) != null) {
      if (line.trim().isEmpty()) {
        continue; // 跳过空行
      }

      if (previousLine != null) {
        filteredLines.add(line); // 添加上一行的下一行
        previousLine = null;
        continue;
      }

      if (line.contains(":") || line.contains("：")) {
        filteredLines.add(line);
        previousLine = line; // 记录当前行，用于判断下一行是否需要保留
      }
    }

    return filteredLines;
  }

  public static void main(String[] args) {
    String sourcePath = "D:\\0000\\EnBook001\\900\\ch003\\ch003_temp.txt";
    String targetPath = "D:\\0000\\EnBook001\\900\\ch003\\ch003.txt";
    String elapsedTime = TextFileUtil3.filterTextFile(sourcePath, targetPath);
    log.info("耗时：{}", elapsedTime);
  }
}
