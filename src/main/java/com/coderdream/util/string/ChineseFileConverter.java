package com.coderdream.util.string;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

/**
 * 中文转换工具类
 */
@Slf4j
public class ChineseFileConverter {


  /**
   * 将指定文件中的简体中文行，每逢三行转换为繁体中文，并写入到目标文件中
   *
   * @param inputFilePath  输入文件路径
   * @param outputFilePath 输出文件路径
   * @return 返回处理结果信息
   */
  public static String convertFile(String inputFilePath,
    String outputFilePath) {
    Instant start = Instant.now();
    log.info("开始处理文件转换: {} -> {}", inputFilePath, outputFilePath);
    int lineNumber = 0;

    try (
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(inputFilePath),
          StandardCharsets.UTF_8));
      BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(outputFilePath),
          StandardCharsets.UTF_8))
    ) {
      String line;
      while ((line = reader.readLine()) != null) {
        lineNumber++; // 记录当前行数
        String outputLine = line;

        if (lineNumber % 3 == 0 && !line.trim().isEmpty()) { // 每逢三的行，进行转换，并排除空行
          outputLine = ZhConverterUtil.toTraditional(line); // 调用转换方法
          log.debug("将第 {} 行简体中文转换为繁体中文：{}", lineNumber, line);
        }
        writer.write(outputLine);
        writer.newLine();
      }
      log.info("文件转换完成，总行数:{}", lineNumber);
      return getElapsedTime(start);
    } catch (IOException e) {
      log.error("文件处理过程中出现错误: {}", e.getMessage(), e);
      return "文件处理出现错误: " + e.getMessage();
    }
  }


  /**
   * 记录方法调用耗时，返回时分秒毫秒
   *
   * @param start 起始时间
   * @return 耗时时长
   */
  private static String getElapsedTime(Instant start) {
    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    long hours = duration.toHours();
    long minutes = duration.toMinutes() % 60;
    long seconds = duration.getSeconds() % 60;
    long millis = duration.toMillis() % 1000;

    return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds,
      millis);
  }


  public static void main(String[] args) {
    String inputFilePath = "D:\\0000\\EnBook001\\900\\ch01\\ch01.txt";
    String outputFilePath = "D:\\0000\\EnBook001\\900\\ch01\\ch01_cht.txt";

    String result = ChineseFileConverter.convertFile(inputFilePath,
      outputFilePath);

    log.info("文件转换耗时： {}", result);
  }
}
