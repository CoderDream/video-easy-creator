package com.coderdream.util.sentence;

import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.util.cd.CdStringUtil;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本解析工具类
 */
@Slf4j
public class TextParser {

  /**
   * 解析文本文件到对象列表
   *
   * @param filePath 文件路径
   * @return List<List < DialogSingleEntity>> 返回对话列表，每一段对话一个列表
   */
  public static List<List<DialogSingleEntity>> parseTextFile(String filePath) {
    Instant start = Instant.now();
    List<List<DialogSingleEntity>> dialogList = new ArrayList<>();
    List<String> lines = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) { // 空行表示段落结束
          if (!lines.isEmpty()) {
            if (lines.size() % 2 != 0) {
              String paragraphContent = String.join("\n", lines);
              log.error("1 段落不是偶数行，无法解析，段落内容:\n{}",
                paragraphContent);
              System.exit(1); // 退出程序
            }
            List<DialogSingleEntity> dialogSingleEntities = parseParagraph(
              lines);
            if (!dialogSingleEntities.isEmpty()) {
              dialogList.add(dialogSingleEntities);
            }

            lines.clear(); // 清空当前段落的行
          }
          continue; // 跳过空行
        }
        lines.add(line); // 添加到当前段落的行
      }

      // 处理最后一段
      if (!lines.isEmpty()) {
        if (lines.size() % 2 != 0) {
          String paragraphContent = String.join("\n", lines);
          log.error("2 段落不是偶数行，无法解析，段落内容:\n{}",
            paragraphContent);
          System.exit(1); // 退出程序
        }
        List<DialogSingleEntity> dialogSingleEntities = TextParser.parseParagraph(
          lines);
        if (!dialogSingleEntities.isEmpty()) {
          dialogList.add(dialogSingleEntities);
        }
      }
    } catch (IOException e) {
      log.error("解析文件时发生异常: ", e);
    }

    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    long millis = duration.toMillis();
    long seconds = millis / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;

    millis %= 1000;
    seconds %= 60;
    minutes %= 60;

    log.info("解析文件耗时: {}小时 {}分钟 {}秒 {}毫秒", hours, minutes, seconds,
      millis);
    return dialogList;
  }

  /**
   * 解析文本文件到对象列表
   *
   * @param contentList 文件路径
   * @return List<List < DialogSingleEntity>> 返回对话列表，每一段对话一个列表
   */
  public static List<List<DialogSingleEntity>> parseTextFile(
    List<String> contentList) {
    Instant start = Instant.now();
    List<List<DialogSingleEntity>> dialogList = new ArrayList<>();

    List<List<String>> paragraphs = CdStringUtil.splitByEmptyLine(contentList);

    for (List<String> lines : paragraphs) {
      // 处理最后一段
      if (!lines.isEmpty()) {
        if (lines.size() % 2 != 0) {
          String paragraphContent = String.join("\n", lines);
          log.error("2 段落不是偶数行，无法解析，段落内容:\n{}",
            paragraphContent);
          System.exit(1); // 退出程序
        }
        List<DialogSingleEntity> dialogSingleEntities = TextParser.parseParagraph(
          lines);
        if (!dialogSingleEntities.isEmpty()) {
          dialogList.add(dialogSingleEntities);
        }
      }
    }

    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    long millis = duration.toMillis();
    long seconds = millis / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;

    millis %= 1000;
    seconds %= 60;
    minutes %= 60;

    log.info("解析文件耗时: {}小时 {}分钟 {}秒 {}毫秒", hours, minutes, seconds,
      millis);
    return dialogList;
  }

  /**
   * 解析段落，分割英文和中文部分，生成 DialogSingleEntity 列表
   *
   * @param lines 段落行列表
   * @return DialogSingleEntity 列表
   */
  public static List<DialogSingleEntity> parseParagraph(List<String> lines) {
    List<DialogSingleEntity> dialogList = new ArrayList<>();
    int halfSize = lines.size() / 2;
    // 拆分英文和中文部分
    List<String> englishLines = lines.subList(0, halfSize);
    List<String> chineseLines = lines.subList(halfSize, lines.size());

    if (englishLines.size() != chineseLines.size()) {
      log.warn(
        "当前段落英文行数({})和中文行数({})不匹配,跳过当前段落，英文为:{},中文为:{}",
        englishLines.size(), chineseLines.size(),
        String.join("\n", englishLines), String.join("\n", chineseLines));
      return dialogList;
    }
    for (int i = 0; i < englishLines.size(); i++) {
      DialogSingleEntity dialogSingleEntity = parseLine(englishLines.get(i),
        chineseLines.get(i));
      if (dialogSingleEntity != null) {
        dialogList.add(dialogSingleEntity);
      }
    }
    return dialogList;
  }

  /**
   * 解析单行文本，提取对话信息
   *
   * @param englishLine 英文行
   * @param chineseLine 中文行
   * @return DialogSingleEntity  对话实体
   */
  private static DialogSingleEntity parseLine(String englishLine,
    String chineseLine) {
    DialogSingleEntity dialogSingleEntity = new DialogSingleEntity();
    String enRegex = "^([^:：]+)[:：](.*)$";
    Pattern enPattern = Pattern.compile(enRegex);
    Matcher enMatcher = enPattern.matcher(englishLine);

    String cnRegex = "^([^:：]+)[:：](.*)$";
    Pattern cnPattern = Pattern.compile(cnRegex);
    Matcher cnMatcher = cnPattern.matcher(chineseLine);

    if (enMatcher.find() && cnMatcher.find()) {
      dialogSingleEntity.setHostEn(enMatcher.group(1).trim());
      dialogSingleEntity.setHostCn(cnMatcher.group(1).trim());
      dialogSingleEntity.setContentEn(
        PunctuationUtil.replaceChinesePunctuationToEnglish(
          enMatcher.group(2).trim()));
      dialogSingleEntity.setContentCn(
        PunctuationUtil.replaceEnglishPunctuationToChinese(
          cnMatcher.group(2).trim()));

    } else {
      log.warn("无法解析的行，英文行:{},中文行:{}", englishLine, chineseLine);
      return null;
    }
    return dialogSingleEntity;
    //         String result1 = PunctuationUtil.replaceEnglishPunctuationToChinese(text2);
    //        String result2 = PunctuationUtil.replaceChinesePunctuationToEnglish(text1);
  }

  public static void main(String[] args) {
    String filePath = "D:\\0000\\EnBook001\\900\\900V1_ch0202.txt";
    List<List<DialogSingleEntity>> dialogList = TextParser.parseTextFile(
      filePath);
    if (dialogList != null) {
      dialogList.forEach(dialogSingleEntities -> {
        dialogSingleEntities.forEach(System.out::println);
      });
    }
  }
}
