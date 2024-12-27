package com.coderdream.util.bbc;

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TextParser1 类 用于解析文本内容，提取对话和词汇，并将结果保存到文件。
 */
@Slf4j
public class TextParser1 {

  /**
   * 解析指定文本文件，提取对话和词汇内容，并保存到文件。
   *
   * @param filePath 要解析的文本文件路径
   */
  public static void parse(String filePath) {
    Path path = Paths.get(filePath);
    if (!Files.exists(path) || Files.isDirectory(path)) {
      log.error("The file path {} is not correct, please check.", filePath);
      return;
    }
    try {
      // 1. 提取对话内容并保存
      String dialogContent = extractContent(path,
        "This is not a word-for-word transcript.", "VOCABULARY");
      if (dialogContent != null) {
        Path dialogOutputPath = path.getParent().resolve("script_dialog.txt");
        saveContentToFile(dialogContent, dialogOutputPath);
      }

      // 2. 提取词汇内容并保存
      String vocabularyContent = extractVocabulary(path);
      if (vocabularyContent != null) {
        Path vocOutputPath = path.getParent().resolve("voc.txt");
        saveContentToFile(vocabularyContent, vocOutputPath);
      }

    } catch (IOException e) {
      log.error("Error during parsing process for file: {}", filePath, e);
      System.err.println("Error during parsing process: " + e.getMessage());
    }
  }


  /**
   * 从文本文件中提取指定开始和结束标签之间的内容。
   *
   * @param filePath  要解析的文本文件路径
   * @param startText 开始文本
   * @param endText   结束文本
   * @return 提取到的文本内容，如果没找到或发生错误则返回 null
   * @throws IOException 如果读取文件时发生错误
   */
  private static String extractContent(Path filePath, String startText,
    String endText) throws IOException {
    StringBuilder content = new StringBuilder();
    boolean inSection = false;

    try (BufferedReader reader = Files.newBufferedReader(filePath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().startsWith(startText)) {
          inSection = true;
          content.append(line).append(System.lineSeparator());
          continue;
        }
        if (line.trim().startsWith(endText)) {
          inSection = false;
          break; // 结束行之后的内容不需要了
        }
        if (inSection) {
          content.append(line).append(System.lineSeparator()); // 将内容行添加到提取列表
        }
      }
    }
    if (content.length() == 0) {
      log.warn("Content is empty from {} to {}.", startText, endText);
      return null;
    }
    log.info(
      "Successfully extracted content between '{}' and '{}' from file: {}",
      startText, endText, filePath);
    return content.toString();
  }


  /**
   * 从文本文件中提取词汇内容。
   *
   * @param filePath 要解析的文本文件路径
   * @return 提取到的词汇内容，如果没找到或发生错误则返回 null
   * @throws IOException 如果读取文件时发生错误
   */
  private static String extractVocabulary(Path filePath) throws IOException {
    StringBuilder vocabulary = new StringBuilder();
    boolean inVocabulary = false;
    int vocabularyLineCount = 0; // 记录词汇行数

    try (BufferedReader reader = Files.newBufferedReader(filePath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().toLowerCase().startsWith("vocabulary")) {
          inVocabulary = true;
          continue;
        }

        if (inVocabulary) {
          vocabularyLineCount++;
          vocabulary.append(line).append(System.lineSeparator());
          if (vocabularyLineCount >= 18) {
            break;
          }

        }
      }
    }
    if (vocabulary.length() == 0) {
      log.warn("Vocabulary content is empty.");
      return null;
    }
    log.info("Successfully extracted vocabulary content from file: {}",
      filePath);
    return vocabulary.toString();
  }


  /**
   * 将字符串内容保存到文件。
   *
   * @param content    要保存的字符串内容
   * @param outputPath 输出文件路径
   */
  private static void saveContentToFile(String content, Path outputPath) {
    try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
      writer.write(content);
      log.info("Saved content to: {}", outputPath);
    } catch (IOException e) {
      log.error("Failed to save content to file: {}", outputPath, e);
      System.err.println("Failed to save content to file: " + e.getMessage());
    }
  }


  /**
   * 主方法，用于测试文本解析功能。
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    String filePath = "D:\\14_LearnEnglish\\6MinuteEnglish\\2024\\241226\\241226_script.txt";
    parse(filePath);
  }
}
