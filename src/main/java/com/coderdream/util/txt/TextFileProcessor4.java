package com.coderdream.util.txt;

import com.coderdream.util.txt.doubao.DoubaoTextFileProcessor3;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本文件处理工具类，用于替换指定格式的数字标记、合并行，并去除空行
 */
@Slf4j
public class TextFileProcessor4 {

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

    Path tempPath = Paths.get(tempFilePath);
    Path outputPath = Paths.get(outputFilePath);

    // 用于匹配 "【^数字^】" 的正则表达式
    Pattern numberPattern = Pattern.compile("【\\^\\d+\\^】");
    // 匹配以中英文句号、问号、感叹号结尾的行
    Pattern endPunctuationPattern = Pattern.compile(".*[。？！.?!]$");
    List<String> processedLines = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(
      new FileReader(tempPath.toFile()))) {
      String line;
      List<String> preProcessedLines = new ArrayList<>();
      while ((line = reader.readLine()) != null) {
        preProcessedLines.add(line);

      }
      // 预处理阶段：合并以回车换行符结尾且前面没有标点的行
      List<String> preMergedLines = preMergeLines(preProcessedLines,
        endPunctuationPattern);
      for (String preMergedLine : preMergedLines) {
        Matcher matcher = numberPattern.matcher(preMergedLine);
        // 1. 替换数字标记，并去除两端的空格
        String replacedLine = matcher.replaceAll("").trim();
        if (!replacedLine.isEmpty()) {
          processedLines.add(replacedLine);
        }
      }

      //2. 合并行
      List<String> mergedLines = mergeLines(processedLines);

      //3. 输出到目标文件
      try (BufferedWriter writer = new BufferedWriter(
        new FileWriter(outputPath.toFile()))) {
        for (String mergedLine : mergedLines) {
          writer.write(mergedLine);
          writer.newLine();
        }
        log.info("文件处理完成，输出到：{}", outputPath);
      } catch (IOException e) {
        log.error("写入文件失败：{}", outputFilePath, e);
        return "写入文件失败";
      }

    } catch (IOException e) {
      log.error("读取文件失败：{}", tempFilePath, e);
      return "读取文件失败";
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
      boolean flag1 = currentLine.endsWith("\r");
      boolean flag2 = currentLine.endsWith("\r\n");
      boolean flag3 = endPunctuationPattern.matcher(currentLine).matches();

      //  如果当前行以回车换行符结尾，并且不是以标点结尾，则需要合并
      if (currentLine.endsWith("\r") && !endPunctuationPattern.matcher(
        currentLine).matches()) {
        StringBuilder mergedLine = new StringBuilder(
          currentLine.trim().replace("\r", ""));
        int nextIndex = i + 1;
        if (nextIndex < lines.size()) {
          mergedLine.append(" ").append(lines.get(nextIndex).trim());
          mergedLines.add(mergedLine.toString());
          i++;
        } else {
          mergedLines.add(currentLine.trim().replace("\r", ""));
        }

      } else {
        mergedLines.add(currentLine.trim().replace("\r", ""));
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

    for (int i = 0; i < lines.size(); i++) {
      String currentLine = lines.get(i);
      Matcher colonMatcher = colonPattern.matcher(currentLine);
      // 如果当前行以冒号开头，并且不是以标点结尾，则需要合并
      if (colonMatcher.matches()) {
        Matcher endMatcher = endPunctuationPattern.matcher(currentLine);

        if (!endMatcher.matches()) {
          StringBuilder mergedLine = new StringBuilder(currentLine);
          int nextIndex = i + 1;
          // 循环合并后续的行，直到遇到以标点符号结尾的行，或者到达最后一行
          while (nextIndex < lines.size()) {
            String nextLine = lines.get(nextIndex).trim();
            mergedLine.append(" ").append(nextLine);
            Matcher nextEndMatcher = endPunctuationPattern.matcher(nextLine);
            //判断当前行是否是已标点结尾
            if (nextEndMatcher.matches()) {
              break;
            }
            nextIndex++;
          }
          mergedLines.add(mergedLine.toString());
          i = nextIndex;

        } else {
          mergedLines.add(currentLine);
        }
      } else {
        mergedLines.add(currentLine);
      }
    }
    return mergedLines;
  }

  public static void main(String[] args) {
    String tempFilePath = "D:\\0000\\EnBook001\\900\\ch003\\ch003_temp.txt";
    String midOutFilePath = "D:\\0000\\EnBook001\\900\\ch003\\ch003_mid.txt";
    String outputFilePath = "D:\\0000\\EnBook001\\900\\ch003\\ch003.txt";
    String time = TextFileProcessor4.processTextFile(tempFilePath,
      midOutFilePath);
    DoubaoTextFileProcessor3.processTextFile(midOutFilePath,
      outputFilePath);
    System.out.println("耗时:" + time);
  }
}


