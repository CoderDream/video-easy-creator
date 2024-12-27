package com.coderdream.util.bbc;

import com.coderdream.entity.DialogEntity;
import com.coderdream.entity.VocInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * TextParserAdvanced 类 用于解析文本内容，提取对话和词汇，并将结果保存到文件。
 */
@Slf4j
public class TextParserAdvanced {

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
      List<DialogEntity> dialogList = extractDialog(path);
      if (dialogList != null) {
        Path dialogOutputPath = path.getParent().resolve("script_dialog.txt");
        saveDialogListToFile(dialogList, dialogOutputPath);
      }

      // 2. 提取词汇内容并保存
      List<VocInfo> vocInfoList = extractVocabulary(path);
      if (vocInfoList != null) {
        Path vocOutputPath = path.getParent().resolve("voc.txt");
        saveVocInfoListToFile(vocInfoList, vocOutputPath);
      }

    } catch (IOException e) {
      log.error("Error during parsing process for file: {}", filePath, e);
      System.err.println("Error during parsing process: " + e.getMessage());
    }
  }

  /**
   * 从文本文件中提取对话内容。
   *
   * @param filePath 要解析的文本文件路径
   * @return 提取到的对话列表，如果没找到或发生错误则返回 null
   * @throws IOException 如果读取文件时发生错误
   */
  private static List<DialogEntity> extractDialog(Path filePath)
    throws IOException {
    List<DialogEntity> dialogList = new ArrayList<>();
    StringBuilder contentBuilder = new StringBuilder();
    String host = null;
    boolean inDialog = false;
    boolean firstLine = true;

    try (BufferedReader reader = Files.newBufferedReader(filePath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().toLowerCase(Locale.ROOT)
          .startsWith("this is not a word-for-word transcript.")) {
          inDialog = true;
          continue;
        }
        if (line.trim().toLowerCase(Locale.ROOT).startsWith("vocabulary")) {
          inDialog = false;
          break;
        }

        if (inDialog) {
          if (line.trim().isEmpty()) { // 空行，表示一段对话结束，需要保存上一段内容
            if (host != null && contentBuilder.length() > 0) {
              DialogEntity dialogEntity = new DialogEntity();
              dialogEntity.setHost(host);
              dialogEntity.setContentEn(contentBuilder.toString().trim());
              dialogList.add(dialogEntity);
              contentBuilder.setLength(0); // 清空 contentBuilder
              host = null;
            }
            firstLine = true;
            continue;
          }
          if (firstLine) {
            host = line.trim();
            firstLine = false;
          } else {
            contentBuilder.append(line.trim()).append(" "); // 将对话内容拼接成一行
          }
        }
      }
    }

    // 处理最后一段对话内容（如果不是以空行结尾）
    if (host != null && contentBuilder.length() > 0) {
      DialogEntity dialogEntity = new DialogEntity();
      dialogEntity.setHost(host);
      dialogEntity.setContentEn(contentBuilder.toString().trim());
      dialogList.add(dialogEntity);
    }
    if (dialogList.isEmpty()) {
      log.warn("Dialog content is empty in file : {}", filePath);
      return null;
    }
    log.info("Successfully extracted dialog content from file: {}", filePath);
    return dialogList;
  }


  /**
   * 从文本文件中提取词汇内容。
   *
   * @param filePath 要解析的文本文件路径
   * @return 提取到的词汇列表，如果没找到或发生错误则返回 null
   * @throws IOException 如果读取文件时发生错误
   */
  private static List<VocInfo> extractVocabulary(Path filePath)
    throws IOException {
    List<VocInfo> vocInfoList = new ArrayList<>();
    StringBuilder explainBuilder = new StringBuilder();
    String word = null;
    boolean inVocabulary = false;
    try (BufferedReader reader = Files.newBufferedReader(filePath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().toLowerCase(Locale.ROOT).startsWith("vocabulary")) {
          inVocabulary = true;
          continue;
        }

        if (inVocabulary) {
          if (line.trim().isEmpty()) { // 空行，表示一个词汇结束
            if (word != null && explainBuilder.length() > 0) {
              VocInfo vocInfo = new VocInfo();
              vocInfo.setWord(word);
              vocInfo.setWordExplainEn(explainBuilder.toString().trim());
              vocInfoList.add(vocInfo);
              explainBuilder.setLength(0);
              word = null;
            }
            continue;
          }

          if (word == null) {
            word = line.trim();
          } else {
            explainBuilder.append(line.trim()).append(" ");
          }
        }
      }
      //处理最后一个词汇
      if (word != null && explainBuilder.length() > 0) {
        VocInfo vocInfo = new VocInfo();
        vocInfo.setWord(word);
        vocInfo.setWordExplainEn(explainBuilder.toString().trim());
        vocInfoList.add(vocInfo);
      }
    }

    if (vocInfoList.isEmpty()) {
      log.warn("Vocabulary content is empty in file : {}", filePath);
      return null;
    }
    log.info("Successfully extracted vocabulary content from file: {}",
      filePath);
    return vocInfoList;
  }

  /**
   * 将对话列表保存到文件。
   *
   * @param dialogList 要保存的对话列表
   * @param outputPath 输出文件路径
   */
  private static void saveDialogListToFile(List<DialogEntity> dialogList,
    Path outputPath) {
    try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
      for (DialogEntity dialog : dialogList) {
        writer.write("Host: " + dialog.getHost());
        writer.newLine();
        writer.write("ContentEn: " + dialog.getContentEn());
        writer.newLine();
        writer.newLine();
      }
      log.info("Saved dialog list to: {}", outputPath);
    } catch (IOException e) {
      log.error("Failed to save dialog list to file: {}", outputPath, e);
      System.err.println(
        "Failed to save dialog list to file: " + e.getMessage());
    }
  }

  /**
   * 将词汇列表保存到文件。
   *
   * @param vocInfoList 要保存的词汇列表
   * @param outputPath  输出文件路径
   */
  private static void saveVocInfoListToFile(List<VocInfo> vocInfoList,
    Path outputPath) {
    try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
      for (VocInfo vocInfo : vocInfoList) {
        writer.write(vocInfo.getWord() + " " + vocInfo.getWordExplainEn());
        writer.newLine();
      }
      log.info("Saved vocabulary list to: {}", outputPath);
    } catch (IOException e) {
      log.error("Failed to save vocabulary list to file: {}", outputPath, e);
      System.err.println(
        "Failed to save vocabulary list to file: " + e.getMessage());
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

