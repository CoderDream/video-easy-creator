package com.coderdream.util.txt.demo01;

import com.coderdream.entity.SentencePair;
import java.io.File;
import java.nio.charset.Charset;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析 LRC 文件并转换为 Book003Entity 对象的工具类
 */
@Slf4j
public class LrcParser {

//  private static final Pattern LRC_PATTERN = Pattern.compile(
//    "\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})] (.*)");


  private static final Pattern LRC_PATTERN = Pattern.compile(
    "\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\](.*)");

  /**
   * 解析 LRC 文件为 Book003Entity 对象
   *
   * @param filePath LRC 文件路径
   * @return 解析后的 Book003Entity 对象
   */
  public static Book003Entity parseLrcFile(String filePath) {
    long startTime = System.nanoTime();
//    Path path = Paths.get(filePath);
    Book003Entity book = new Book003Entity();

    String encoding = EncodingDetector.detectEncoding(filePath);

    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath),
      Charset.forName(encoding != null ? encoding : "UTF-8"))) {
      String line;
      while ((line = reader.readLine()) != null) {
        parseLine(book, line);
      }
    } catch (IOException e) {
      log.error("读取 LRC 文件失败: {}", filePath, e);
    }

    long endTime = System.nanoTime();
    log.info("LRC 文件解析完成，耗时: {} ms", (endTime - startTime) / 1_000_000);
    return book;
  }

  /**
   * 解析 LRC 文件中的一行数据
   *
   * @param book 目标 Book003Entity 对象
   * @param line 当前行内容
   */
  private static void parseLine(Book003Entity book, String line) {
    Matcher matcher = LRC_PATTERN.matcher(line);
    if (matcher.matches()) {
      int minutes = Integer.parseInt(matcher.group(1));
      int seconds = Integer.parseInt(matcher.group(2));
      int milliseconds = Integer.parseInt(
        matcher.group(3).length() == 2 ? matcher.group(3) + "0"
          : matcher.group(3));
      String text = matcher.group(4);

      book.getEntries().add(new LrcEntry(minutes, seconds, milliseconds, text));
    } else if (!line.trim().isEmpty()) {
      book.getMetadata().add(line.trim());
    }
  }

  public static List<Book003Info> genBook003InfoList(String filePath) {
    List<Book003Info> book003InfoList = new ArrayList<>();
    File file = new File(filePath);
    if (!file.exists()) {
      System.err.println("File not found: " + filePath);
      return book003InfoList;
    }

    Book003Entity bookEntity = parseLrcFile(filePath);
    System.out.println("Metadata: " + bookEntity.getMetadata());
    boolean beginFlag = false;
    SentencePair scnceSentencePair;
    SentencePair sentencePair;
    Book003Info book003Info = null;
    List<SentencePair> sentencePairs = null;
    for (LrcEntry entry : bookEntity.getEntries()) {
      System.out.printf("[%d ms] %s%n", entry.getMilliseconds(),
        entry.getText());
      if (entry.getText().startsWith("●") || (entry.getText().contains("(")
        && entry.getText().contains(")"))) {
        if (entry.getText().startsWith("●")) {
          beginFlag = true;
          book003Info = new Book003Info();
          sentencePairs = new ArrayList<>();
          book003Info.setSentencePairs(sentencePairs);
          scnceSentencePair = new SentencePair();
          scnceSentencePair.setChineseSentence(entry.getText().substring(1));
          book003Info.setScnceSentencePair(scnceSentencePair);

          book003InfoList.add(book003Info);
        } else {
          beginFlag = false;
          sentencePair  = new SentencePair();
          sentencePair.setEnglishSentence(entry.getText().substring(0,
            entry.getText().indexOf("(")));
          sentencePair.setChineseSentence(entry.getText().substring(entry.getText()
            .indexOf("(") + 1, entry.getText().lastIndexOf(")")));

          sentencePairs.add(sentencePair);
        }
      }
    }

    return book003InfoList;
  }

  public static void main(String[] args) {
    String filePath = "D:\\0000\\EnBook003\\英语口语8000句\\01.在家中.lrc";

    genBook003InfoList(filePath).forEach(System.out::println);

//    File file = new File(filePath);
//    if (!file.exists()) {
//      System.err.println("File not found: " + filePath);
//      return;
//    }
//
//    Book003Entity bookEntity = parseLrcFile(filePath);
//    System.out.println("Metadata: " + bookEntity.getMetadata());
//    for (LrcEntry entry : bookEntity.getEntries()) {
//      System.out.printf("[%d ms] %s%n", entry.getMilliseconds(),
//        entry.getText());
//    }
  }
}

/**
 * Book003Entity 对象，包含 LRC 解析的所有信息
 */
@Data
class Book003Entity {

  private List<String> metadata = new ArrayList<>(); // 存储章节信息或无时间戳的内容
  private List<LrcEntry> entries = new ArrayList<>(); // 存储 LRC 内容
}

/**
 * LRC 解析后的单个条目
 */
@Data
class LrcEntry {

  private int minutes;
  private int seconds;
  private int milliseconds;
  private String text;

  public LrcEntry(int minutes, int seconds, int milliseconds, String text) {
    this.minutes = minutes;
    this.seconds = seconds;
    this.milliseconds = milliseconds;
    this.text = text;
  }
}
