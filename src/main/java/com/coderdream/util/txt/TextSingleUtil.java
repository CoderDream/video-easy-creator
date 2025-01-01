package com.coderdream.util.txt;

import cn.hutool.core.collection.CollectionUtil;
import com.coderdream.entity.DialogSingleEntity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * 文本处理工具类
 */
@Slf4j
public class TextSingleUtil {


  /**
   * 用于匹配多个空格的正则表达式
   */
  private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

  /**
   * 用于匹配句号，逗号前的空格的正则表达式
   */
  private static final Pattern SPACE_BEFORE_PUNCTUATION = Pattern.compile(
    "\\s+([。，,.])");

  /**
   * 匹配简单句的正则表达式，包含中英文句号、问号、感叹号或省略号
   */
  private static final Pattern SIMPLE_SENTENCE_PATTERN = Pattern.compile(
    "([^。，,?!…]+[。，,?!…])");


  /**
   * 私有构造方法，防止实例化
   */
  private TextSingleUtil() {
    throw new UnsupportedOperationException(
      "This is a utility class and cannot be instantiated");
  }


  /**
   * 查询所有段落的第一个中文/英文冒号前的名字，并去重后存储到相同文件夹的host.txt文档中
   */
  public static void extractHosts(String filePath, String... fileName) {
    Path inputFilePath = Paths.get(filePath, fileName);
    Path outputFilePath = Paths.get(filePath, "host.txt");

    Set<String> hosts = new LinkedHashSet<>();
    try (BufferedReader reader = Files.newBufferedReader(inputFilePath,
      StandardCharsets.UTF_8)) {
      String line;
      List<String> paragraph = new ArrayList<>();
      while ((line = reader.readLine()) != null) {
        if (line.trim().isEmpty()) {
          List<String> hostList = extractHostFromParagraph(paragraph);
          if (CollectionUtil.isNotEmpty(hostList)) {
            hosts.addAll(hostList);
          }
          paragraph.clear(); // 清空段落
          continue; // 跳过空行
        }
        paragraph.add(line);
      }
      // 处理最后一段
      List<String> hostLast = extractHostFromParagraph(paragraph);
      if (CollectionUtil.isNotEmpty(hostLast)) {
        hosts.addAll(hostLast);
      }

      log.info("从文件 {} 中成功读取并提取host信息,共{}条", inputFilePath,
        hosts.size());
    } catch (IOException e) {
      log.error("读取文件 {} 发生异常：{}", inputFilePath, e.getMessage(), e);
      return; // 发生异常直接返回，不再继续写入文件
    }

    try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath,
      StandardCharsets.UTF_8)) {
      for (String host : hosts) {
        writer.write(host);
        writer.newLine();
      }
      log.info("host信息已成功写入到文件: {}", outputFilePath);
    } catch (IOException e) {
      log.error("写入文件 {} 发生异常：{}", outputFilePath, e.getMessage(), e);
    }
  }


  /**
   * 从一段文本中提取host，根据第一个冒号分割
   *
   * @param paragraph 段落
   * @return host 列表
   */
  private static List<String> extractHostFromParagraph(List<String> paragraph) {
    List<String> hosts = new ArrayList<>();
    for (String line : paragraph) {
      String host = extractHostFromLine(line);
      if (host != null) {
        hosts.add(host);
      }
    }
    return hosts;
  }


  /**
   * 从一行文本中提取host，根据第一个冒号分割
   *
   * @param line 一行文本
   * @return host
   */
  private static String extractHostFromLine(String line) {
    int colonIndex = findFirstColonIndex(line);
    if (colonIndex != -1) {
      return line.substring(0, colonIndex).trim();
    }
    return null;
  }

//  /**
//   * 解析文本文件中的对话内容，并返回 DialogSingleEntity 列表
//   *
//   * @return 包含对话信息的 DialogSingleEntity 列表 String first, String... more
//   */
//  public static List<DialogSingleEntity> parseDialogs(String filePath, String... fileName) {
//    Path inputFilePath = Paths.get(filePath, fileName);
//    List<DialogSingleEntity> dialogs = new ArrayList<>();
//    try (BufferedReader reader = Files.newBufferedReader(inputFilePath,
//      StandardCharsets.UTF_8)) {
//      String line;
//      List<String> paragraph = new ArrayList<>();
//      while ((line = reader.readLine()) != null) {
//        if (line.trim().isEmpty()) {
//          //处理段落
//          List<DialogSingleEntity> dialogSingleEntityList = parseParagraph(
//            paragraph);
//          if (CollectionUtil.isNotEmpty(dialogSingleEntityList)) {
//            dialogs.addAll(dialogSingleEntityList);
//          }
//          paragraph.clear();// 清空段落
//          continue; // 跳过空行
//        }
//        paragraph.add(line);
//      }
//      //处理最后一段
//      List<DialogSingleEntity> dialogSingleEntityList = parseParagraph(paragraph);
//      if (CollectionUtil.isNotEmpty(dialogSingleEntityList)) {
//        dialogs.addAll(dialogSingleEntityList);
//      }
//      log.info("从文件 {} 中成功读取并解析对话信息,共{}条", inputFilePath,
//        dialogs.size());
//    } catch (IOException e) {
//      log.error("读取文件 {} 发生异常：{}", inputFilePath, e.getMessage(), e);
//    }
//    return dialogs;
//  }

  /**
   * 解析文本文件中的对话内容，并返回 DialogSingleEntity 列表
   *
   * @return 包含对话信息的 DialogSingleEntity 列表
   */
  public static List<DialogSingleEntity> parseDialogs(String filePath,
    String... fileName) {
    Path inputFilePath = Paths.get(filePath, fileName);
    List<DialogSingleEntity> dialogs = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(inputFilePath,
      StandardCharsets.UTF_8)) {
      String line;
      List<String> paragraph = new ArrayList<>();
      while ((line = reader.readLine()) != null) {
        if (line.trim().isEmpty()) {
          //处理段落
          List<DialogSingleEntity> dialogSingleEntityList = parseParagraph(
            paragraph);
          if (CollectionUtil.isNotEmpty(dialogSingleEntityList)) {
            dialogs.addAll(dialogSingleEntityList);
          }
          paragraph.clear();// 清空段落
          continue; // 跳过空行
        }
        paragraph.add(line);
      }
      //处理最后一段
      List<DialogSingleEntity> dialogSingleEntityList = parseParagraph(
        paragraph);
      if (CollectionUtil.isNotEmpty(dialogSingleEntityList)) {
        dialogs.addAll(dialogSingleEntityList);
      }
      log.info("从文件 {} 中成功读取并解析对话信息,共{}条", inputFilePath,
        dialogs.size());
    } catch (IOException e) {
      log.error("读取文件 {} 发生异常：{}", inputFilePath, e.getMessage(), e);
    }
    return dialogs;
  }


  /**
   * 将对话列表写入文件
   *
   * @param dialogs 对话列表
   */
  public static void writeDialogsToFile(List<DialogSingleEntity> dialogs,
    String filePath) {
    Path outputFilePath = Paths.get(filePath, "dialog.txt");

    try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath,
      StandardCharsets.UTF_8)) {
      for (DialogSingleEntity dialog : dialogs) {
        writer.write(dialog.getContentEn() != null ? removeRedundantSpaces(
          dialog.getContentEn()) : "");
        writer.newLine();
        writer.write(dialog.getContentCn() != null ? removeRedundantSpaces(
          dialog.getContentCn()) : "");
        writer.newLine();
        writer.write(dialog.getContentEn() != null ? removeRedundantSpaces(
          dialog.getContentEn()) : "");
        writer.newLine();
        writer.write(dialog.getContentCn() != null ? removeRedundantSpaces(
          dialog.getContentCn()) : "");
        writer.newLine();
      }
      log.info("对话信息已成功写入到文件: {}", outputFilePath);
    } catch (IOException e) {
      log.error("写入文件 {} 发生异常：{}", outputFilePath, e.getMessage(), e);
    }
  }
//
//  /**
//   * 将对话列表写入文件
//   *
//   * @param dialogs 对话列表
//   */
//  public static void writeDialogsToFile(List<DialogSingleEntity> dialogs, String filePath) {
//    Path outputFilePath = Paths.get(filePath, "dialog.txt");
//
//    try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath,
//      StandardCharsets.UTF_8)) {
//      for (DialogSingleEntity dialog : dialogs) {
//        writer.write(dialog.getContentEn() != null ? removeRedundantSpaces(
//          dialog.getContentEn()) : "");
//        writer.newLine();
//        writer.write(dialog.getContentCn() != null ? removeRedundantSpaces(
//          dialog.getContentCn()) : "");
//        writer.newLine();
//        writer.write(dialog.getContentEn() != null ? removeRedundantSpaces(
//          dialog.getContentEn()) : "");
//        writer.newLine();
//        writer.write(dialog.getContentCn() != null ? removeRedundantSpaces(
//          dialog.getContentCn()) : "");
//        writer.newLine();
//      }
//      log.info("对话信息已成功写入到文件: {}", outputFilePath);
//    } catch (IOException e) {
//      log.error("写入文件 {} 发生异常：{}", outputFilePath, e.getMessage(), e);
//    }
//  }

  public static void writeSentenceToFile(String filePath, String fileName) {
    // 解析对话信息
    List<DialogSingleEntity> dialogs = TextSingleUtil.parseDialogs(filePath,
      fileName);
    Path outputFilePath = Paths.get(filePath, "dialog_single.txt");
    try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath,
      StandardCharsets.UTF_8)) {
      String contentEn;
      String contentCn;
      for (DialogSingleEntity dialog : dialogs) {
        contentEn = dialog.getContentEn() != null ? removeRedundantSpaces(
          dialog.getContentEn()) : "";
        contentCn = dialog.getContentCn() != null ? removeRedundantSpaces(
          dialog.getContentCn()) : "";

        List<String> contentEnSentences = ParagraphUtil.splitSentences(
          contentEn);
        List<String> contentCnSentences = ParagraphUtil.splitSentences(
          contentCn);
        if (CollectionUtil.isNotEmpty(contentEnSentences)
          && CollectionUtil.isNotEmpty(contentCnSentences)) {
          if (contentEnSentences.size() == contentCnSentences.size()) {
            for (int i = 0; i < contentEnSentences.size(); i++) {
              writer.write(contentEnSentences.get(i));
              writer.newLine();
              writer.write(contentCnSentences.get(i));
              writer.newLine();
            }
          } else {
            log.warn("对话信息不一致，写入单个对话: {}", outputFilePath);
            writer.write(contentEn);
            writer.newLine();
            writer.write(contentCn);
            writer.newLine();
          }
        } else {
          writer.write(contentEn);
          writer.newLine();
          writer.write(contentCn);
          writer.newLine();
        }
      }
      log.info("对话信息已成功写入到文件: {}", outputFilePath);
    } catch (IOException e) {
      log.error("写入文件 {} 发生异常：{}", outputFilePath, e.getMessage(), e);
    }
  }

  public static void writeSentenceToFile(String filePath) {
    List<DialogSingleEntity> dialogs = TextSingleUtil.parseDialogs(filePath);
    Path outputFilePath = Paths.get(filePath, "dialog_single.txt");
    try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath,
      StandardCharsets.UTF_8)) {
      String contentEn;
      String contentCn;
      for (DialogSingleEntity dialog : dialogs) {
        contentEn = dialog.getContentEn() != null ? removeRedundantSpaces(
          dialog.getContentEn()) : "";
        contentCn = dialog.getContentCn() != null ? removeRedundantSpaces(
          dialog.getContentCn()) : "";

        List<String> contentEnSentences = ParagraphUtil.splitSentences(
          contentEn);
        List<String> contentCnSentences = ParagraphUtil.splitSentences(
          contentCn);
        if (CollectionUtil.isNotEmpty(contentEnSentences)
          && CollectionUtil.isNotEmpty(contentCnSentences)
          && contentEnSentences.size() == contentCnSentences.size()) {
          for (int i = 0; i < contentEnSentences.size(); i++) {
            writer.write(contentEnSentences.get(i));
            writer.newLine();
            writer.write(contentCnSentences.get(i));
            writer.newLine();
          }
        } else {
          writer.write(contentEn);
          writer.newLine();
          writer.write(contentCn);
          writer.newLine();
        }
      }
      log.info("对话信息已成功写入到文件: {}", outputFilePath);
    } catch (IOException e) {
      log.error("写入文件 {} 发生异常：{}", outputFilePath, e.getMessage(), e);
    }
  }

  // List<String> sentences = ParagraphUtil.splitSentences(paragraph);

  /**
   * 查找字符串中第一个中文或英文冒号的索引位置
   *
   * @param line 一行文本
   * @return 冒号索引
   */
  private static int findFirstColonIndex(String line) {
    int chineseColonIndex = line.indexOf("：");
    int englishColonIndex = line.indexOf(":");
    if (chineseColonIndex == -1) {
      return englishColonIndex;
    }
    if (englishColonIndex == -1) {
      return chineseColonIndex;
    }
    return Math.min(chineseColonIndex, englishColonIndex);
  }


  /**
   * 解析段落内容为 DialogSingleEntity 对象
   *
   * @param paragraph 段落文本
   * @return 解析后的 DialogSingleEntity 对象列表
   */
  private static List<DialogSingleEntity> parseParagraph(
    List<String> paragraph) {
    List<DialogSingleEntity> dialogs = new ArrayList<>();
    DialogSingleEntity dialog;
    String hostEn;
    String contentEn;
    String hostCn;
    String contentCn;

    String sentenceEn;
    String sentenceCn;
    if (paragraph.size() < 2) {
      log.warn("段落信息行数小于2，无法解析,段落内容：{}", paragraph);
      return null;
    }
    int lineEnIndex = 0;
    // 中文索引位置为段中开始的行数的一半，即英文和中文交替出现时，英文在前，中文在后
    int lineCnIndex = paragraph.size() / 2;
    int sentenceCount = paragraph.size() / 2;
    for (int i = 0; i < sentenceCount; i++) {
      dialog = new DialogSingleEntity();
      try {
        sentenceEn = paragraph.get(lineEnIndex);
        log.info("sentenceEn:{} ; ", sentenceEn);
        // 解析A的英文内容
        int colonIndexA = findFirstColonIndex(sentenceEn);
        if (colonIndexA != -1) {
          hostEn = sentenceEn.substring(0, colonIndexA).trim();
          log.info("hostEn:{}", hostEn);
          dialog.setHostEn(hostEn);
          contentEn = sentenceEn.substring(colonIndexA + 1).trim();
          log.info("contentEn:{}", contentEn);
          dialog.setContentEn(contentEn);
        }

        // 解析A的中文内容
        sentenceCn = paragraph.get(lineCnIndex);
        log.info("sentenceCn:{}", sentenceCn);

        int colonIndexCn = findFirstColonIndex(sentenceCn);
        if (colonIndexCn != -1) {
          hostCn = sentenceCn.substring(0, colonIndexCn).trim();
          log.info("hostCn:{}", hostCn);
          dialog.setHostCn(hostCn);
          contentCn = sentenceCn.substring(colonIndexCn + 1).trim();
          log.info("contentCn:{}", contentCn);
          dialog.setContentCn(contentCn);
        }

        lineEnIndex++;
        lineCnIndex++;
        log.debug("成功解析段落信息：{}", dialog);
        dialogs.add(dialog);
      } catch (Exception e) {
        log.error("解析段落信息出错: {}， 段落内容：{}", e.getMessage(),
          paragraph, e);
        return null;
      }
    }
    return dialogs;
  }


  /**
   * 去除字符串中多余的空格，包括中英文句号和逗号前的空格
   *
   * @param input 需要处理的字符串
   * @return 处理后的字符串
   */
  private static String removeRedundantSpaces(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    // 先去除标点符号前的空格，再去除多个空格
    return MULTIPLE_SPACES.matcher(
        SPACE_BEFORE_PUNCTUATION.matcher(input).replaceAll("$1")).replaceAll(" ")
      .trim();
  }


  /**
   * 获取下一行中文的翻译
   *
   * @param reader BufferedReader
   * @param host   英文host
   * @return 中文翻译
   * @throws IOException
   */
  private static String getChineseTranslation(BufferedReader reader,
    String host) throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      if (line.trim().isEmpty()) {
        return null;
      }
      if (line.startsWith(host)) {
        return line;
      }
    }
    return null;
  }
}
