package com.coderdream.util.sentence;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.vo.SentenceVO;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SentenceParser {

  public static List<SentenceVO> parseSentencesFromFile(String filePath) {
    List<SentenceVO> sentenceList = new ArrayList<>();
    int lineCount = 0;

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String english = null;
      String phonetics = null;
      String chinese;
      String line;

      while ((line = br.readLine()) != null) {
        lineCount++;
        switch (lineCount % 3) {
          case 1:
            english = line.trim();
            break;
          case 2:
            phonetics = line.trim();
            break;
          case 0:
            chinese = line.trim();
            SentenceVO sentence = new SentenceVO();
            sentence.setEnglish(english);
            sentence.setPhonetics(phonetics);
            sentence.setChinese(chinese);
            sentenceList.add(sentence);
            break;
        }
      }

    } catch (IOException e) {
      log.error("读取文件失败: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("文件解析失败: {}", e.getMessage());
    }

    if (lineCount % 3 != 0) {
      log.error("文件行数不是3的倍数,无法正确解析");
      throw new IllegalArgumentException("文件行数不是3的倍数，无法解析");
    }
    return sentenceList;
  }

  public static List<SentenceVO> parseSentencesFromFileV2(String filePath) {
    List<SentenceVO> sentenceList = new ArrayList<>();
    List<String> lines = CdFileUtil.readFileContent(filePath);
    assert lines != null;
    int lineCount = lines.size();
    if (lineCount % 3 != 0) {
      log.error("文件行数不是3的倍数，无法正确解析，文件路径:{}", filePath);
      return sentenceList;
    }

    for (int i = 0; i < lineCount; i += 3) {
      String english = lines.get(i).trim();
      if(english.startsWith("A:") || english.startsWith("B:")){
        english = english.substring(2); // 去掉A:或B:
      }
      String phonetics = lines.get(i + 1).trim();
      String chinese = lines.get(i + 2).trim();
      SentenceVO sentence = new SentenceVO();
      sentence.setEnglish(english);
      sentence.setPhonetics(phonetics);
      sentence.setChinese(chinese);
      sentenceList.add(sentence);
    }

    return sentenceList;
  }

  public static void main(String[] args) {
    String filePath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics1.txt"; // 替换为你的文件路径

    List<SentenceVO> sentences = SentenceParser.parseSentencesFromFile(
      filePath);
    for (SentenceVO sentence : sentences) {
      log.info("english: {}, phonetics: {}, chinese: {}", sentence.getEnglish(),
        sentence.getPhonetics(), sentence.getChinese());
    }
    log.info("解析成功，总共解析了{}条句子", sentences.size());
  }
}
