package com.coderdream.util.txt.filter;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdStringUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.cd.ChineseCharacterUtil;
import java.io.File;
import java.util.regex.Matcher;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 文本文件处理工具类，先合并行，再移除空行、保留冒号行及其下一行，并过滤特定行。
 */
@Slf4j
public class TextFileUtil {

  private static final Pattern END_PUNCTUATION_PATTERN = Pattern.compile(
    "[。？！.?!]$");
  private static final Pattern NUMBER_START_PATTERN = Pattern.compile(
    "^\\d+\\..*");

  /**
   * 处理文本文件，先合并行，再移除空行、保留包含中英文冒号的行及其下一行，并过滤特定行，然后写入目标文件。
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
   * 读取文件内容，先合并行，再根据规则过滤，并将结果写入目标文件。
   *
   * @param sourceFilePath 源文件路径
   * @param targetFilePath 目标文件路径
   * @throws IOException IO异常
   */
  private static void filterLinesAndWrite(String sourceFilePath,
    String targetFilePath) throws IOException {
    try (
      BufferedWriter writer = Files.newBufferedWriter(Paths.get(targetFilePath),
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING)) {
      // 读取所有行，移除空行、合并未以标点结尾的行。
      List<String> list = CdFileUtil.readFileContent(sourceFilePath);
      // 移除行
      List<String> makeLines = makeLines(list);
      // 合并行
      List<String> mergedLines = mergeLines(makeLines);
      // 过滤行
      List<String> filteredLines = filterLines(mergedLines);

      int i = 0;
      for (String line : filteredLines) {
        // 如果不存在中英文冒号，则记录日志
        if (!line.contains("：") || !line.contains(":")) {
          log.info("{} 存在没有中英文冒号的行：{}", i, line);
        }
        i++;
        writer.write(line);
        writer.newLine();
      }
    }
  }


  /**
   * 读取所有行，先合并未以标点结尾的行。
   *
   * @return 合并后的列表
   * @throws IOException IO异常
   */
  private static List<String> mergeLines(List<String> list) throws IOException {
    List<String> mergedLines = new ArrayList<>();
    StringBuilder previousLine = null;

    for (String line : list) {
      if (NUMBER_START_PATTERN.matcher(line).find()) {
        continue; // 跳过以数字开始的行
      }

      if (line.trim().isEmpty()) {
        continue; // 跳过空行
      }

      if (previousLine != null) {
        if (!END_PUNCTUATION_PATTERN.matcher(previousLine.toString()).find()) {
          // 如果上一行不是以标点符号结尾，则合并当前行到上一行
          // 如果不是中文字符串，则在前面加上空格
          if (!ChineseCharacterUtil.isChinese(
            previousLine.substring(previousLine.length() - 1,
              previousLine.length()))) {
            previousLine.append(" ");
          }
          previousLine.append(line.trim());
          continue;
        } else {
          mergedLines.add(previousLine.toString());
          previousLine = new StringBuilder(line.trim());
          continue;
        }
      }
      previousLine = new StringBuilder(line.trim()); // 新的上一行
    }
    if (previousLine != null) {
      mergedLines.add(previousLine.toString());
    }

    return mergedLines;
  }


  /**
   * 读取所有行，并根据规则过滤：移除空行，保留冒号行及其下一行，并过滤特定行。
   *
   * @param lines 合并后的行列表
   * @return 过滤后的列表
   */
  private static List<String> filterLines(List<String> lines) {
    List<String> filteredLines = new ArrayList<>();

    // 存在冒号
    boolean hasColon = false;
    for (String line : lines) {
      if (line.trim().isEmpty()) {
        continue;
      }
      if (hasColon) {
        filteredLines.add(replaceString(line));
        hasColon = false;
        continue;
      }

      // 如果包含中英文冒号
      if (line.contains(":") || line.contains("：")) {
        filteredLines.add(replaceString(line));
        hasColon = true;
      }
    }

    return filteredLines;
  }

  /**
   * 替换字符串
   *
   * @param line 行内容
   * @return 替换后的内容
   */
  private static String replaceString(String line) {
//    line = line.replace(" ：", "：");
//    line = line.replace(" ：", "：");
//    line = line.replace(" ；", "；");
//    line = line.replace("  ", " ");

    // 定义一个正则表达式，匹配所有中英文标点符号前面的空格
    String regex = "\\s+([\\p{Punct}。，、？！；：‘’“”【】《》「」『』…·（）—])";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(line);

    // 使用replaceAll方法替换所有匹配到的空格
    line = matcher.replaceAll("$1");
    // 去除多余的空格（原代码保留）
    line = line.replace("  ", " ");

    line = CdStringUtil.replaceNumberPattern(line, "");
    return line;
  }

  /**
   * 读取所有行，并根据规则过滤：移除空行，保留冒号行及其下一行，并过滤特定行。
   *
   * @param lines 合并后的行列表
   * @return 过滤后的列表
   */
  private static List<String> makeLines(List<String> lines) {
    List<String> filteredLines = new ArrayList<>();
    for (String line : lines) {

      if (line.trim().isEmpty()) {
        continue; // 跳过空行
      }
      // 过滤以 ☺ 开头的行
      if (line.trim().startsWith("☺")
        || line.trim().startsWith("Chapter")
        || line.trim().startsWith("单词特搜")
        || line.trim().startsWith("2.")
        || line.trim().startsWith("3.")
        || line.trim().startsWith("4.")
        || line.trim().startsWith("5.")
        || line.trim().startsWith("6.")
        || line.trim().startsWith("精选话题")
        || line.trim().startsWith("英语通：")
        || line.trim().startsWith("摘自")
        || line.trim().startsWith("习语")
        || line.trim().startsWith("注释")
      ) {
        continue;
      }
      //过滤以数字.开头的行
      if (NUMBER_START_PATTERN.matcher(line.trim()).find()) {
        continue;
      }

      filteredLines.add(line);

      //
      // 轻松说电影，后面的行都过滤掉
      if (line.trim().startsWith("轻松说电影")) {
        break;
      }
    }
    return filteredLines;
  }

  public static void main(String[] args) {
//    String sourcePath = "D:\\0000\\EnBook001\\900\\ch003\\ch003_temp.txt";
//    String targetPath = "D:\\0000\\EnBook001\\900\\ch003\\ch003.txt";
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch004";
    String sourcePath =
      folderPath + File.separator + subFolder + File.separator + subFolder + "_temp.txt";
    String targetPath =
      folderPath + File.separator + subFolder + File.separator + subFolder + ".txt";

    String elapsedTime = TextFileUtil.filterTextFile(sourcePath, targetPath);
    log.info("耗时：{}", elapsedTime);
  }
}
