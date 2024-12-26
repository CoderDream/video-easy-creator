package com.coderdream.util.chatgpt;

import com.coderdream.vo.SentenceVO;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TextParserUtil {

  /**
   * 解析文本文件内容，将其转换为 SentenceVO 对象的列表。
   *
   * @param filePath 文本文件的路径
   * @return 包含 SentenceVO 对象的列表
   */
  public static List<SentenceVO> parseFileToSentenceVOs(String filePath) {
    List<SentenceVO> sentenceVOs = new ArrayList<>();
    Path path = Paths.get(filePath);

    try (BufferedReader reader = Files.newBufferedReader(path)) {
      String line;
      while ((line = reader.readLine()) != null) {
        // 匹配结构 "Speaker: English [Phonetics] Chinese"
        int colonIndex = line.indexOf(": ");
        int bracketStart = line.indexOf("[");
        int bracketEnd = line.indexOf("]");

        if (colonIndex > -1 && bracketStart > colonIndex
          && bracketEnd > bracketStart) {
          String english = line.substring(colonIndex + 2, bracketStart).trim();
          String phonetics = line.substring(bracketStart, bracketEnd + 1)
            .trim(); // 包括方括号
          String chinese = line.substring(bracketEnd + 1).trim();

          SentenceVO sentenceVO = new SentenceVO(english, phonetics, chinese);
          sentenceVOs.add(sentenceVO);
//          log.info("成功解析一句话: {}", sentenceVO);
        } else {
          log.warn("无法解析的行: {}", line);
        }
      }
    } catch (IOException e) {
      log.error("读取文件时发生错误: {}", filePath, e);
    }

    return sentenceVOs;
  }

  /**
   * 主方法：用于测试文件解析功能。
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    String filePath = "src/main/resources/CampingInvitation.txt";
    log.info("开始解析文件: {}", filePath);

    List<SentenceVO> sentenceVOs = parseFileToSentenceVOs(filePath);

    // 打印解析结果以验证
    for (SentenceVO sentenceVO : sentenceVOs) {
      System.out.println("English: " + sentenceVO.getEnglish());
      System.out.println("Phonetics: " + sentenceVO.getPhonetics());
      System.out.println("Chinese: " + sentenceVO.getChinese());
      System.out.println("--------------------------------------");
    }

    log.info("文件解析完成，共解析出 {} 条句子。", sentenceVOs.size());
  }
}
