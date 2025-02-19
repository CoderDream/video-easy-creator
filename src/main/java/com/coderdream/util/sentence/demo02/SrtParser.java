package com.coderdream.util.sentence.demo02;

import com.coderdream.util.bbc.StringSplitter4;
import com.coderdream.util.cd.CdFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于解析 SRT 文件并提取句子列表的工具类。
 */
public class SrtParser {

  private static final Logger log = LoggerFactory.getLogger(SrtParser.class);
  private static final int MAX_SENTENCE_LENGTH = 80;

  /**
   * 从给定的 SRT 文件路径中读取内容，并将其解析为句子列表。
   *
   * @param filePath SRT 文件的路径
   * @return 解析后的句子列表
   * @throws IOException 如果读取文件时发生错误
   */
  public static List<String> parseSrtFile(String filePath) throws IOException {
    Instant start = Instant.now();
    List<String> sentences = new ArrayList<>();

    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
      StringBuilder currentSentence = new StringBuilder();
      String line;
      boolean speakerLine = false;

      while ((line = reader.readLine()) != null) {
        line = line.trim();

        if (line.isEmpty() || line.matches("\\d+") || line.matches(
          "\\d{2}:\\d{2}:\\d{2},\\d{3} --> \\d{2}:\\d{2}:\\d{2},\\d{3}")) {
          continue;
        }

        Matcher speakerMatcher = Pattern.compile("^[A-Za-z]+\\. [A-Za-z]+:$")
          .matcher(line);
        if (speakerMatcher.matches()) {
          speakerLine = true;
          continue;
        }

        if (speakerLine) {
          speakerLine = false;
        }
        currentSentence.append(line).append(" ");
        List<String> splitSentences = splitSentence(currentSentence.toString());
        if (splitSentences.size() > 1) {
          for (int i = 0; i < splitSentences.size() - 1; i++) {
            sentences.addAll(
              splitLongSentence(removeSpeakerPrefix(splitSentences.get(i))));
          }
          currentSentence.setLength(0);
          currentSentence.append(splitSentences.get(splitSentences.size() - 1));
        }
      }

      if (!currentSentence.isEmpty()) {
        sentences.addAll(splitLongSentence(
          removeSpeakerPrefix(currentSentence.toString().trim())));
      }
    } catch (IOException e) {
      log.error("Error reading file: {}", filePath, e);
      throw e;
    }

    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    log.info("SRT file parsed in: {}h {}m {}s {}ms",
      duration.toHoursPart(), duration.toMinutesPart(),
      duration.toSecondsPart(), duration.toMillisPart());

    return sentences;
  }

  private static List<String> splitSentence(String text) {
    List<String> sentences = new ArrayList<>();
    Pattern pattern = Pattern.compile(
      "(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?|!|:')\\s+");
    Matcher matcher = pattern.matcher(text);
    int start = 0;
    while (matcher.find()) {
      String sentence = text.substring(start, matcher.end()).trim();
      if (!sentence.isEmpty()) {
        sentences.add(sentence);
      }
      start = matcher.end();
    }
    if (start < text.length()) {
      String lastSentence = text.substring(start).trim();
      if (!lastSentence.isEmpty()) {
        sentences.add(lastSentence);
      }
    }
    return sentences;
  }

  private static String removeSpeakerPrefix(String sentence) {
    Pattern prefixPattern = Pattern.compile(
      "^[A-Za-z]+\\. [A-Za-z]+: |^The Press: ");
    Matcher prefixMatcher = prefixPattern.matcher(sentence);
    if (prefixMatcher.find()) {
      return sentence.substring(prefixMatcher.end()).trim();
    }
    return sentence;
  }

  /**
   * 将长句子分割成多个短句子，每个短句子的长度不超过 MAX_SENTENCE_LENGTH。
   * 优先按句号分割，如果没有句号了还超长，就从最中间的逗号（,）、分号（;）分割
   *
   * @param sentence 原始句子
   * @return 分割后的短句子列表
   */
  private static List<String> splitLongSentence(String sentence) {
    List<String> result = new ArrayList<>();
    if (sentence.length() <= MAX_SENTENCE_LENGTH) {
      result.add(sentence);
      return result;
    }

    int middle = sentence.length() / 2;
    int commaIndex = -1;
    int semicolonIndex = -1;

    // 正确的循环条件：同时检查左右边界
    for (int i = 0; middle - i >= 0 && middle + i < sentence.length(); i++) {
      if (middle - i >= 0 && sentence.charAt(middle - i) == ',') {
        commaIndex = middle - i;
      }
      if (commaIndex == -1 && middle + i < sentence.length()
        && sentence.charAt(middle + i) == ',') {
        commaIndex = middle + i;
      }
      if (commaIndex != -1) {
        break; // 找到逗号就停止
      }
    }

    if (commaIndex == -1) {
      for (int i = 0; middle - i >= 0 && middle + i < sentence.length(); i++) {
        if (middle - i >= 0 && sentence.charAt(middle - i) == ';') {
          semicolonIndex = middle - i;
        }
        if (semicolonIndex == -1 && middle + i < sentence.length()
          && sentence.charAt(middle + i) == ';') {
          semicolonIndex = middle + i;
        }
        if (semicolonIndex != -1) {
          break;//找到分号就停止
        }
      }
    }

    int splitIndex = (commaIndex != -1) ? commaIndex : semicolonIndex;

    if (splitIndex != -1) {
      String part1 = sentence.substring(0, splitIndex + 1).trim();
      String part2 = sentence.substring(splitIndex + 1).trim();

      if (!part1.isEmpty() && !part1.replaceAll("\\s+", "")
        .equals(sentence.replaceAll("\\s+", ""))) {
        result.addAll(splitLongSentence(part1));
      }
      if (!part2.isEmpty() && !part2.replaceAll("\\s+", "")
        .equals(sentence.replaceAll("\\s+", ""))) {
        result.addAll(splitLongSentence(part2));
      }
      if (result.isEmpty()) {
        result.add(sentence);
      }

    } else {
      log.warn("Sentence exceeds max length and cannot be split: {}",
        sentence);
      result.add(sentence);
    }

    return result;
  }


  public static void main(String[] args) throws IOException {
    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250128.txt";
    String outputFilePath = CdFileUtil.addPostfixToFileName(filePath, "_raw"); // "D:\\0000\\0003_PressBriefings\\250128\\250128_srt_raw.txt";
    List<String> sentences = SrtParser.parseSrtFile(filePath);
    List<String> shortSentences = new ArrayList<>();
    for (int i = 0; i < sentences.size(); i++) {
//      System.out.println("Sentence " + (i + 1) + ": " + sentences.get(i));
      List<String> shortSentence = StringSplitter4.splitString((sentences.get(i)));
      shortSentences.addAll(shortSentence);
    }

    CdFileUtil.writeToFile(outputFilePath, shortSentences);
  }
}
