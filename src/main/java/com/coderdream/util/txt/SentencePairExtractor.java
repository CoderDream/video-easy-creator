package com.coderdream.util.txt;

import com.coderdream.entity.SentencePair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 句子对提取工具类 用于从文本文件中提取句子对，并根据规则解析成 SentencePair 对象
 */
@Slf4j
public class SentencePairExtractor {

  /**
   * 从字符串中提取句子对
   *
   * @param line         待处理的字符串
   * @param previousPair 前一个句子对，用于处理 "同类表达" 的情况
   * @return 解析后的句子对对象，如果解析失败则返回 null
   */
  private static SentencePair parseSentencePair(String line,
    SentencePair previousPair) {
    // 定义匹配序号的正则表达式 ❶、❷、...、❿
    Pattern sequencePattern = Pattern.compile("^[❶-❿]\\s*");

    // 移除序号
    String lineWithoutSequence = sequencePattern.matcher(line).replaceAll("");
    log.debug("处理前的字符串：{}， 处理后的字符串：{}", line,
      lineWithoutSequence);

    //处理 同类表达
    if (lineWithoutSequence.startsWith("同类表达")) {

      if (previousPair == null) {
        log.warn("遇到\"同类表达\"，但前一个句子对为空，无法解析。行内容：{}",
          line);
        return null;
      }
      String remain = lineWithoutSequence.substring("同类表达".length()).trim();
      return new SentencePair(remain, previousPair.getChineseSentence());
    }

    // 处理  这样回答
    if (lineWithoutSequence.startsWith("这样回答")) {
      String remain = lineWithoutSequence.substring("这样回答".length()).trim();
      int index = findFirstChineseCharIndex(remain);
      if (index > 0) {
        return new SentencePair(remain.substring(0, index).trim(),
          remain.substring(index).trim());
      } else {
        log.warn("遇到\"这样回答\"，但没有找到中文，无法解析。行内容：{}", line);
        return null;
      }
    }
    // 处理对话
    Pattern dialogPattern = Pattern.compile("^(对话\\s*[A-Z]:|\\s*[A-Z]:)");
    Matcher dialogMatcher = dialogPattern.matcher(lineWithoutSequence);
    if (dialogMatcher.find()) {
      String remain = dialogMatcher.replaceFirst("").trim();
      int index = findFirstChineseCharIndex(remain);
      if (index > 0) {
        return new SentencePair(remain.substring(0, index).trim(),
          remain.substring(index).trim());
      } else {
        log.warn("遇到\"对话\"，但没有找到中文，无法解析。行内容：{}", line);
        return null;
      }

    }

    // 处理一般情况
    int index = findFirstChineseCharIndex(lineWithoutSequence);
    if (index > 0) {
      return new SentencePair(lineWithoutSequence.substring(index).trim(),
        lineWithoutSequence.substring(0, index).trim());
    } else {
      log.warn("没有找到中文，无法解析。行内容：{}", line);
      return null;
    }
  }

  /**
   * 查找字符串中第一个中文字符的索引
   *
   * @param text 待查找的字符串
   * @return 如果找到，返回索引，否则返回 -1
   */
  public static int findFirstChineseCharIndex(String text) {
    Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return matcher.start();
    }
    return -1;
  }

  /**
   * 查找字符串中最后一个中文字符的索引
   *
   * @param text 待查找的字符串
   * @return 如果找到，返回索引，否则返回 -1
   */
  public static int findLastChineseCharIndex(String text) {
    Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return matcher.end();
    }
    return -1;
  }


  /**
   * 从指定文件中提取句子对
   *
   * @param filePath 文件路径
   * @return 句子对列表
   */
  public static List<SentencePair> extractSentencePairs(String filePath) {
    Instant start = Instant.now(); // 记录方法开始时间
    log.info("开始提取文件中的句子对，文件路径: {}", filePath);

    List<SentencePair> sentencePairs = new ArrayList<>();
    Path path = Paths.get(filePath);
    SentencePair previousPair = null;

    try (BufferedReader reader = new BufferedReader(
      new FileReader(path.toFile()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        SentencePair sentencePair = parseSentencePair(line, previousPair);

        if (sentencePair != null) {
          sentencePairs.add(sentencePair);
          log.debug("解析到句子对：{}", sentencePair);
          previousPair = sentencePair; //记录前一个句子对
        } else {
          log.warn("解析句子对失败， 行内容：{}", line);
        }
      }
    } catch (IOException e) {
      log.error("读取文件时发生异常: {}", e.getMessage(), e);
      return new ArrayList<>();// 如果发生异常，返回一个空的列表，避免NullPointerException
    }

    Instant end = Instant.now();  // 记录方法结束时间
    Duration duration = Duration.between(start, end); // 计算方法执行耗时
    long milliseconds = duration.toMillis();
    long seconds = milliseconds / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;

    log.info("句子对提取完成，耗时：{}时{}分{}秒{}毫秒，提取到的句子对数量: {}",
      hours,
      minutes % 60,
      seconds % 60,
      milliseconds % 1000,
      sentencePairs.size());
    return sentencePairs;
  }

  public static List<String> extractEnglishSentenceString(String filePath) {
    // 测试用例
//    String filePath = "D:\\0000\\EnBook002\\Chapter001\\Chapter001.txt";  // 替换为您的实际文件路径
    //String filePath="D:\\0000\\EnBook002\\Chapter001\\test.txt"; //测试用例，请自行修改

    List<SentencePair> sentencePairs = SentencePairExtractor.extractSentencePairs(
      filePath);
    List<String> result = new ArrayList<>();
    sentencePairs.forEach(pair -> result.add(pair.getEnglishSentence()));
    return result;
  }


  public static void main(String[] args) {
    // 测试用例
    String filePath = "D:\\0000\\EnBook002\\Chapter001\\Chapter001.txt";  // 替换为您的实际文件路径
    //String filePath="D:\\0000\\EnBook002\\Chapter001\\test.txt"; //测试用例，请自行修改

//    List<SentencePair> sentencePairs = SentencePairExtractor.extractSentencePairs(
//      filePath);
//
//    if (sentencePairs != null) {
//      System.out.println("提取到的句子对列表：");
//      sentencePairs.forEach(pair -> System.out.println(
//        pair.getChineseSentence() + " : " + pair.getEnglishSentence()));
//    }

    String filePath2 = "D:\\0000\\EnBook002\\Chapter001\\Chapter001_total.txt";  // 替换为您的实际文件路径
    List<String> strings = SentencePairExtractor.extractEnglishSentenceString(
      filePath2);
    strings.forEach(System.out::println);
  }
}
