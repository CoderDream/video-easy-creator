package com.coderdream.util.cd;

import com.coderdream.vo.SentenceVO;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 文本处理工具类，用于读取指定路径的文本文件，并将内容解析为句子对象列表。
 */
@Slf4j
public class CdTextUtil {

  /**
   * 从文本文件中解析句子列表。
   *
   * @param filePath 文本文件路径
   * @return 句子对象列表
   * @throws IllegalArgumentException 如果文件不存在，或者有效行数不是2的倍数
   */
  public static List<SentenceVO> parseSentencesFromFileWithEnglishAndPhonetics(
    String filePath) {
    Instant start = Instant.now(); // 记录开始时间
    log.info("开始解析文件：{}", filePath);

    Path path = Paths.get(filePath);

    if (!Files.exists(path)) {
      log.error("文件不存在：{}", filePath);
      throw new IllegalArgumentException("文件不存在：" + filePath);
    }

    List<String> lines = new ArrayList<>();
    // 使用 try-with-resources 语句自动关闭资源
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    } catch (IOException e) {
      log.error("读取文件失败：{}, 异常：{}", filePath, e.getMessage(), e);
    }
    //1. 末尾为空行，则先去掉
    while (!lines.isEmpty() && lines.get(lines.size() - 1).trim().isEmpty()) {
      lines.remove(lines.size() - 1);
    }

    // 2. 如果有效行数不为 2 的倍数，则报错退出
    if (lines.size() % 2 != 0) {
      log.error("文件有效行数不是2的倍数: {}, 行数：{}", filePath, lines.size());
      throw new IllegalArgumentException("文件有效行数不是2的倍数");
    }

    List<SentenceVO> sentenceList = new ArrayList<>();

    // 3. 两行为一组，放到对象中，生成对象列表，第一行为 english，第二行为 chinese
    for (int i = 0; i < lines.size(); i += 2) {
      String english = lines.get(i).trim();
      String phonetics = lines.get(i + 1).trim();
      SentenceVO sentenceVO = new SentenceVO(english, phonetics, "");
      sentenceList.add(sentenceVO);
    }

    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();

    long hours = timeElapsed / (1000 * 60 * 60);
    long minutes = (timeElapsed % (1000 * 60 * 60)) / (1000 * 60);
    long seconds = (timeElapsed % (1000 * 60)) / 1000;
    long milliseconds = timeElapsed % 1000;
    log.info("文件解析完成，耗时：{} 时 {} 分 {} 秒 {} 毫秒", hours, minutes,
      seconds, milliseconds);
    return sentenceList;
  }

  /**
   * 从文本文件中解析句子列表。
   *
   * @param filePath 文本文件路径
   * @return 句子对象列表
   * @throws IllegalArgumentException 如果文件不存在，或者有效行数不是2的倍数
   */
  public static List<SentenceVO> parseSentencesFromFile(String filePath) {
    Instant start = Instant.now(); // 记录开始时间
    log.info("开始解析文件：{}", filePath);

    Path path = Paths.get(filePath);

    if (!Files.exists(path)) {
      log.error("文件不存在：{}", filePath);
      throw new IllegalArgumentException("文件不存在：" + filePath);
    }

    List<String> lines = new ArrayList<>();
    // 使用 try-with-resources 语句自动关闭资源
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    } catch (IOException e) {
      log.error("读取文件失败：{}, 异常：{}", filePath, e.getMessage(), e);
    }
    //1. 末尾为空行，则先去掉
    while (!lines.isEmpty() && lines.get(lines.size() - 1).trim().isEmpty()) {
      lines.remove(lines.size() - 1);
    }

    // 2. 如果有效行数不为 2 的倍数，则报错退出
    if (lines.size() % 2 != 0) {
      log.error("文件有效行数不是2的倍数: {}, 行数：{}", filePath, lines.size());
      throw new IllegalArgumentException("文件有效行数不是2的倍数");
    }

    List<SentenceVO> sentenceList = new ArrayList<>();

    // 3. 两行为一组，放到对象中，生成对象列表，第一行为 english，第二行为 chinese
    for (int i = 0; i < lines.size(); i += 2) {
      String english = lines.get(i).trim();
      String chinese = lines.get(i + 1).trim();
      SentenceVO sentenceVO = new SentenceVO(english, "", chinese);
      sentenceList.add(sentenceVO);
    }

    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();

    long hours = timeElapsed / (1000 * 60 * 60);
    long minutes = (timeElapsed % (1000 * 60 * 60)) / (1000 * 60);
    long seconds = (timeElapsed % (1000 * 60)) / 1000;
    long milliseconds = timeElapsed % 1000;
    log.info("文件解析完成，耗时：{} 时 {} 分 {} 秒 {} 毫秒", hours, minutes,
      seconds, milliseconds);
    return sentenceList;
  }

  /**
   * 从文本文件中提取所有的英文句子。
   *
   * @param filePath 文本文件路径
   * @return 包含所有英文句子的列表
   * @throws IllegalArgumentException 如果文件不存在，或者有效行数不是2的倍数
   */
  public static List<String> getAllEnglishSentencesFromFile(String filePath) {
    Instant start = Instant.now();
    log.info("开始解析文件获取英文句子：{}", filePath);

    List<String> englishSentences = new ArrayList<>();

    try {
      List<SentenceVO> sentences = CdTextUtil.parseSentencesFromFile(filePath);
      sentences.forEach(sentence ->
        englishSentences.add(sentence.getEnglish()));
      log.info("解析完成，总共 {} 条英文句子", sentences.size());
    } catch (IllegalArgumentException e) {
      log.error("参数异常：{}", e.getMessage());
    }

    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();
    long hours = timeElapsed / (1000 * 60 * 60);
    long minutes = (timeElapsed % (1000 * 60 * 60)) / (1000 * 60);
    long seconds = (timeElapsed % (1000 * 60)) / 1000;
    long milliseconds = timeElapsed % 1000;
    log.info("解析文件获取英文句子完成，耗时：{} 时 {} 分 {} 秒 {} 毫秒", hours,
      minutes,
      seconds, milliseconds);
    return englishSentences;
  }

  public static void main(String[] args) {

    String filePath = "D:\\0000\\EnBook002\\Chapter001\\Chapter001_total.txt"; //  替换成您的文件路径
    try {
      List<SentenceVO> sentences = CdTextUtil.parseSentencesFromFile(filePath);
      sentences.forEach(sentence ->
        System.out.println("English: " + sentence.getEnglish() + ", Chinese: "
          + sentence.getChinese())
      );
      log.info("解析完成，总共 {} 条句子", sentences.size());
    } catch (IllegalArgumentException e) {
      log.error("参数异常：{}", e.getMessage());
    }
  }
}
