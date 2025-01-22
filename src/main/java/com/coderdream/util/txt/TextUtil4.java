package com.coderdream.util.txt;

import cn.hutool.core.collection.CollectionUtil;
import com.coderdream.entity.DialogDualEntity;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 文本处理工具类
 */
@Slf4j
public class TextUtil4 {

    /**
     * 文本文件的路径
     */
    private static final String FILE_PATH = "D:\\0000\\EnBook001\\900";

    /**
     * 文本文件的文件名
     */
    private static final String FILE_NAME = "900V1_ch02_v1.txt";

    /**
     *  用于匹配多个空格的正则表达式
     */
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

    /**
     * 用于匹配中英文句号和逗号前的空格的正则表达式
     */
    private static final Pattern SPACE_BEFORE_PUNCTUATION = Pattern.compile("\\s+([。，,.？?])");


    /**
     *  私有构造方法，防止实例化
     */
    private TextUtil4() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    /**
     * 查询所有段落的第一个中文/英文冒号前的名字，并去重后存储到相同文件夹的host.txt文档中
     */
    public static void extractHosts() {
        Path inputFilePath = Paths.get(FILE_PATH, FILE_NAME);
        Path outputFilePath = Paths.get(FILE_PATH, "host.txt");

        Set<String> hosts = new LinkedHashSet<>();
        try (BufferedReader reader = Files.newBufferedReader(inputFilePath, StandardCharsets.UTF_8)) {
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

            log.info("从文件 {} 中成功读取并提取host信息,共{}条", inputFilePath, hosts.size());
        } catch (IOException e) {
            log.error("读取文件 {} 发生异常：{}", inputFilePath, e.getMessage(), e);
            return; // 发生异常直接返回，不再继续写入文件
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8)) {
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

    /**
     * 解析文本文件中的对话内容，并返回 DialogDualEntity 列表
     *
     * @return 包含对话信息的 DialogDualEntity 列表
     */
    public static List<DialogDualEntity> parseDialogs() {
        Path inputFilePath = Paths.get(FILE_PATH, FILE_NAME);
        List<DialogDualEntity> dialogs = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(inputFilePath, StandardCharsets.UTF_8)) {
            String line;
            List<String> paragraph = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                 if (line.trim().isEmpty()) {
                   //处理段落
                     List<DialogDualEntity> dialogDualEntityList = parseParagraph(paragraph);
                    if (CollectionUtil.isNotEmpty(dialogDualEntityList)) {
                        dialogs.addAll(dialogDualEntityList);
                    }
                    paragraph.clear();// 清空段落
                    continue; // 跳过空行
                }
                 paragraph.add(line);
             }
             //处理最后一段
             List<DialogDualEntity> dialogDualEntityList = parseParagraph(paragraph);
              if (CollectionUtil.isNotEmpty(dialogDualEntityList)) {
                  dialogs.addAll(dialogDualEntityList);
             }
            log.info("从文件 {} 中成功读取并解析对话信息,共{}条", inputFilePath, dialogs.size());
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
    public static void writeDialogsToFile(List<DialogDualEntity> dialogs) {
        Path outputFilePath = Paths.get(FILE_PATH, "dialog.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8)) {
            for (DialogDualEntity dialog : dialogs) {
                writer.write(dialog.getContentAEn() != null ?  removeRedundantSpaces(dialog.getContentAEn()) : "");
                writer.newLine();
                writer.write(dialog.getContentACn() != null ? removeRedundantSpaces(dialog.getContentACn()) : "");
                writer.newLine();
                writer.write(dialog.getContentBEn() != null ? removeRedundantSpaces(dialog.getContentBEn()) : "");
                writer.newLine();
                writer.write(dialog.getContentBCn() != null ? removeRedundantSpaces(dialog.getContentBCn()) : "");
                writer.newLine();
            }
            log.info("对话信息已成功写入到文件: {}", outputFilePath);
        } catch (IOException e) {
            log.error("写入文件 {} 发生异常：{}", outputFilePath, e.getMessage(), e);
        }
    }

    /**
     *  查找字符串中第一个中文或英文冒号的索引位置
     * @param line 一行文本
     * @return  冒号索引
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
     *  解析段落内容为 DialogDualEntity 对象
     * @param paragraph 段落文本
     * @return 解析后的 DialogDualEntity 对象列表
     */
    private static List<DialogDualEntity> parseParagraph(List<String> paragraph) {
        List<DialogDualEntity> dialogs = new ArrayList<>();
         DialogDualEntity dialog;
         String hostAEn;
         String contentAEn;
         String hostBEn;
         String contentBEn;

        String hostACn;
        String contentACn;
        String hostBCn;
        String contentBCn;


        String sentenceEnA;
       String sentenceCnA;
       String sentenceEnB;
       String sentenceCnB;
         if (paragraph.size() < 2) {
             log.warn("段落信息行数小于2，无法解析,段落内容：{}", paragraph);
           return null;
        }
       int lineEnIndex = 0;
       // 中文索引位置为段中开始的行数的一半，即英文和中文交替出现时，英文在前，中文在后
       int lineCnIndex = paragraph.size() / 2;
       int sentenceCount = paragraph.size() / 4;
      for (int i = 0; i < sentenceCount; i++) {
            dialog = new DialogDualEntity();
            try {
                sentenceEnA = paragraph.get(lineEnIndex);
                sentenceEnB = paragraph.get(lineEnIndex + 1);
                log.info("sentenceEnA:{} ; sentenceEnB:{}", sentenceEnA, sentenceEnB);
               // 解析A的英文内容
              int colonIndexA = findFirstColonIndex(sentenceEnA);
                if (colonIndexA != -1) {
                   hostAEn = sentenceEnA.substring(0, colonIndexA).trim();
                   log.info("hostAEn:{}", hostAEn);
                  dialog.setHostAEn(hostAEn);
                   contentAEn = sentenceEnA.substring(colonIndexA + 1).trim();
                   log.info("contentAEn:{}", contentAEn);
                    dialog.setContentAEn(contentAEn);
                }
                // 解析B的英文内容
                if (lineEnIndex < paragraph.size()) {
                     int colonIndexB = findFirstColonIndex(sentenceEnB);
                     if (colonIndexB != -1) {
                         hostBEn = sentenceEnB.substring(0, colonIndexB).trim();
                        log.info("hostBEn:{}", hostBEn);
                       dialog.setHostBEn(hostBEn);
                        contentBEn = sentenceEnB.substring(colonIndexB + 1).trim();
                        log.info("contentBEn:{}", contentBEn);
                        dialog.setContentBEn(contentBEn);
                    }
                }

              // 解析A的中文内容
            sentenceCnA = paragraph.get(lineCnIndex);
               sentenceCnB = "";
               try {
                   sentenceCnB = paragraph.get(lineCnIndex + 1);
                } catch (IndexOutOfBoundsException e) {
                    log.warn("段落信息行数小于2，无法解析,段落内容：{},lineCnIndex {}",paragraph, lineCnIndex);
                }
             log.info("sentenceCnA:{} ; sentenceCnB:{}", sentenceCnA, sentenceCnB);

              int colonIndexCnA = findFirstColonIndex(sentenceCnA);
                if (colonIndexCnA != -1) {
                  hostACn = sentenceCnA.substring(0, colonIndexCnA).trim();
                  log.info("hostACn:{}", hostACn);
                   dialog.setHostACn(hostACn);
                   contentACn = sentenceCnA.substring(colonIndexCnA + 1).trim();
                   log.info("contentACn:{}", contentACn);
                    dialog.setContentACn(contentACn);
                }

               // 解析B的中文内容
                if (lineCnIndex < paragraph.size()) {
                    int colonIndexCnB = findFirstColonIndex(sentenceCnB);
                    if (colonIndexCnB != -1) {
                         hostBCn = sentenceCnB.substring(0, colonIndexCnB).trim();
                       log.info("hostBCn:{}", hostBCn);
                      dialog.setHostBCn(hostBCn);
                       contentBCn = sentenceCnB.substring(colonIndexCnB + 1).trim();
                      log.info("contentBCn:{}", contentBCn);
                       dialog.setContentBCn(contentBCn);
                   }
               }
              lineEnIndex += 2;
              lineCnIndex += 2;
               log.debug("成功解析段落信息：{}", dialog);
               dialogs.add(dialog);
          } catch (Exception e) {
                log.error("解析段落信息出错: {}， 段落内容：{}", e.getMessage(), paragraph, e);
                return null;
          }
       }
      return dialogs;
    }

    /**
     *  去除字符串中多余的空格，包括中英文句号和逗号前的空格
     * @param input  需要处理的字符串
     * @return  处理后的字符串
     */
     private static String removeRedundantSpaces(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        // 先去除标点符号前的空格，再去除多个空格
        return MULTIPLE_SPACES.matcher(SPACE_BEFORE_PUNCTUATION.matcher(input).replaceAll("$1")).replaceAll(" ").trim();
     }
}
