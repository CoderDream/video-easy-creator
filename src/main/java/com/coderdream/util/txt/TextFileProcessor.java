package com.coderdream.util.txt;

import com.coderdream.util.txt.doubao.DTextFileProcessor;
import com.coderdream.util.txt.doubao.DTextFileProcessor3;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文本文件处理工具类，用于替换指定格式的数字标记、合并行，并去除空行
 */
@Slf4j
public class TextFileProcessor {

  /**
   * 读取文件内容，并统一换行符为 '\n'
   *
   * @param filePath 文件路径
   * @return 文件内容列表，每行作为一个元素
   * @throws IOException 读取文件失败时抛出异常
   */
  private static List<String> readLines(String filePath) throws IOException {
    Path path = Paths.get(filePath);
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      return reader.lines()
        .map(line -> line.replace("\r\n", "\n").replace("\r", "\n")) // 统一换行符
        .collect(Collectors.toList());
    }
  }

  /**
   * 将处理后的文本行写入文件
   *
   * @param lines    处理后的文本行
   * @param filePath 输出文件路径
   * @throws IOException 写入文件失败时抛出异常
   */
  private static void writeLines(List<String> lines, String filePath)
    throws IOException {
    Path path = Paths.get(filePath);
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      for (String line : lines) {
        writer.write(line);
        writer.newLine();
      }
    }
  }

  /**
   * 处理文本文件，替换数字标记，合并行，并去除空行
   *
   * @param tempFilePath   临时文件路径
   * @param outputFilePath 输出文件路径
   * @return 返回一个字符串，表示处理耗时
   */
  public static String processTextFile(String tempFilePath,
    String outputFilePath) {
    Instant startTime = Instant.now();
    log.info("开始处理文件：{}，输出到：{}", tempFilePath, outputFilePath);

    // 用于匹配 "^数字^" 的正则表达式
    Pattern numberPattern = Pattern.compile("(\\^\\d+\\^)");
    // 匹配以中英文句号、问号、感叹号结尾的行
    Pattern endPunctuationPattern = Pattern.compile(".*[。？！.?!]$");

    List<String> preProcessedLines = null;
    try {
      preProcessedLines = readLines(tempFilePath);
    } catch (IOException e) {
      log.error("读取文件失败：{}", tempFilePath, e);
      return "读取文件失败";
    }

    // 预处理阶段：合并以回车换行符结尾且前面没有标点的行
    List<String> preMergedLines = preMergeLines(preProcessedLines,
      endPunctuationPattern);
    // 替换数字标记并去除空行
    List<String> replacedLines = preMergedLines.stream()
      .map(line -> numberPattern.matcher(line).replaceAll("").trim())
      .filter(line -> !line.isEmpty())
      .collect(Collectors.toList());

    // 合并行
    List<String> mergedLines = mergeLines(replacedLines);

    try {
        writeLines(mergedLines, outputFilePath);
        log.info("文件处理完成，输出到：{}", outputFilePath);
      } catch (IOException e) {
          log.error("写入文件失败：{}", outputFilePath, e);
          return "写入文件失败";
      }
    Instant endTime = Instant.now();
    Duration duration = Duration.between(startTime, endTime);
    long seconds = duration.getSeconds();
    long minutes = seconds / 60;
    long hours = minutes / 60;
    long millis = duration.toMillis() % 1000;
    String time = String.format("%02d时:%02d分:%02d秒:%03d毫秒", hours,
      minutes % 60, seconds % 60, millis);
    log.info("文件处理耗时：{}", time); // 记录耗时
    return time;
  }

  /**
   * 预处理文本行，合并以回车换行符结尾且前面没有标点符号的行
   *
   * @param lines                 文本行列表
   * @param endPunctuationPattern 标点符号结尾的正则表达式
   * @return 合并后的文本行列表
   */
  private static List<String> preMergeLines(List<String> lines,
    Pattern endPunctuationPattern) {
    List<String> mergedLines = new ArrayList<>();
    for (int i = 0; i < lines.size(); i++) {
      String currentLine = lines.get(i);
      if (currentLine.endsWith("\n") && !endPunctuationPattern.matcher(
        currentLine).matches()) {
        StringBuilder mergedLine = new StringBuilder(currentLine.trim());
        int nextIndex = i + 1;
        if (nextIndex < lines.size()) {
          mergedLine.append(" ").append(lines.get(nextIndex).trim());
          mergedLines.add(mergedLine.toString());
          i++; // Skip the next line, since it's merged with the current one
        } else {
          mergedLines.add(currentLine.trim());
        }

      } else {
        mergedLines.add(currentLine.trim());
      }

    }
    return mergedLines;
  }


  /**
   * 合并文本行，处理以冒号开头且末尾非标点符号的行
   *
   * @param lines 文本行列表
   * @return 合并后的文本行列表
   */
  private static List<String> mergeLines(List<String> lines) {
    List<String> mergedLines = new ArrayList<>();
    Pattern colonPattern = Pattern.compile("^[:：].*"); // 匹配以冒号开头的行
    Pattern endPunctuationPattern = Pattern.compile(
      ".*[。？！.?!]$"); // 匹配以中英文句号、问号、感叹号结尾的行

    for (int i = 0; i < lines.size(); ) { // 注意这里没有 i++
      String currentLine = lines.get(i);
      Matcher colonMatcher = colonPattern.matcher(currentLine);
      if (colonMatcher.matches() && !endPunctuationPattern.matcher(
        currentLine).matches()) {
        StringBuilder mergedLine = new StringBuilder(currentLine);
        int nextIndex = i + 1;
        while (nextIndex < lines.size()) {
          String nextLine = lines.get(nextIndex).trim();
          mergedLine.append(" ").append(nextLine);
          if (endPunctuationPattern.matcher(nextLine).matches()) {
            break;
          }
          nextIndex++;
        }
        mergedLines.add(mergedLine.toString());
        i = nextIndex + 1; // 跳到下一个需要处理的位置
      } else {
        mergedLines.add(currentLine);
        i++;
      }
    }
    return mergedLines;
  }

  public static void main(String[] args) {
    String tempFilePath = "D:\\0000\\EnBook001\\900\\ch003\\ch003_temp.txt";
    String midOutFilePath = "D:\\0000\\EnBook001\\900\\ch003\\ch003_mid.txt";
    String outputFilePath = "D:\\0000\\EnBook001\\900\\ch003\\ch003.txt";
    String time = TextFileProcessor.processTextFile(tempFilePath,
      midOutFilePath);
    DTextFileProcessor.processTextFile(midOutFilePath,
      outputFilePath);
    System.out.println("耗时:" + time);
  }
}
