package com.coderdream.util;

import cn.hutool.core.io.FileUtil;
import com.coderdream.entity.VocInfo;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
@Slf4j
class DictUtilsTest {

  @Test
  void getVocInfoList() {

  }

  @Test
  void testGetVocInfoList() {
  }

  @Test
  void writeVocCnExcel() {
  }

  @Test
  void testWriteVocCnExcel() {
  }

  /**
   * TODO: 获取单词列表详情
   */
  @Test
  void processVoc01() {
    String folderName = "D:\\14_LearnEnglish\\u11_frankenstein\\u11_frankenstein_episode1\\";
    String fileName = "u11_frankenstein_episode1_voc";
    DictUtil.processVoc(folderName, fileName);
  }

  /**
   * TODO: 获取单词列表详情
   */
  @Test
  void processVoc02() {
    String folderName = "180802";
    String folderPath = CommonUtil.getFullPath(folderName);
    String fileName = "voc";
    log.info("processVoc02 folderPath: {}", folderPath);
    DictUtil.processVoc(folderPath, fileName);
  }

  /**
   * TODO: 获取单词列表详情
   */
  @Test
  void processVocWithGemini_01() {
    String folderName = "181101";
    String folderPath = CommonUtil.getFullPath(folderName);
    String fileName = "voc";
    log.info("processVocWithGemini 01 folderPath: {}", folderPath);
    DictUtil.processVocWithGemini(folderPath, fileName);
  }

  /**
   * TODO: 获取单词列表详情
   */
  @Test
  void processVocWithGemini_02() {
    List<String> folderNameList = Arrays.asList("180906","180913", "180920", "180927");
    for (String folderName : folderNameList) {
      String folderPath = CommonUtil.getFullPath(folderName);
      String fileName = "voc";
      log.info("processVocWithGemini 02 folderPath: {}", folderPath);
      DictUtil.processVocWithGemini(folderPath, fileName);
    }
  }



  /**
   * TODO: 获取单词列表详情
   */
  @Test
  void processVocWithGemini_03() {
//    List<String> folderNameList = Arrays.asList("180906","180913", "180920", "180927");
    String todoFileName = "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\data\\bbc\\todo.txt";
    List<String> folderNameList = FileUtil.readLines(todoFileName, "UTF-8");
    for (String folderName : folderNameList) {
      String folderPath = CommonUtil.getFullPath(folderName);
      String fileName = "voc";
      log.info("processVocWithGemini 03 folderPath: {}", folderPath);
      DictUtil.processVocWithGemini(folderPath, fileName);
    }
  }


  /**
   * TODO: 获取单词列表详情
   */
  @Test
  void processVoc03() {
//    List<String> folderNameList = Arrays.asList("180705", "180712", "180719",
//      "180726");
    String todoFileName = "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\data\\bbc\\todo.txt";
    List<String> folderNameList = FileUtil.readLines(todoFileName, "UTF-8");
    for (String folderName : folderNameList) {
//      String folderName = "180705";
      String folderPath = CommonUtil.getFullPath(folderName);
      String fileName = "voc";
      log.info("3 folderPath: {}", folderPath);
      DictUtil.processVoc(folderPath, fileName);
    }
  }

  // D:\04_GitHub\java-architect-util\free-apps\src\main\resources\data\bbc

  @Test
  void processVoc04() {
    String todoFileName = "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\data\\bbc\\todo.txt";
    List<String> folderNameList = FileUtil.readLines(todoFileName, "UTF-8");
//    List<String> folderNameList = Arrays.asList("180705", "180712", "180719",
//      "180726");
    for (String folderName : folderNameList) {
//      String folderName = "180705";
      String folderPath = CommonUtil.getFullPath(folderName);
      String fileName = "voc";
      log.info("folderPath: {}", folderPath);
      DictUtil.processVoc(folderPath, fileName);
    }
  }

  @Test
  void queryVocInfoFromDictionary() {
    String word = "abandon";
    VocInfo vocInfo = DictUtil.queryVocInfoFromDictionary(word);
    log.info("vocInfo: {}", vocInfo);
  }

  @Test
  void queryVocInfo() {
    VocInfo vocInfo = new VocInfo();
    vocInfo.setWord("phubber");
    vocInfo.setWordExplainEn(
      "person who ignores the real people around them because they are concentrating on their phones");

// phubber
//person who ignores the real people around them because they are concentrating on their phones
    VocInfo result = DictUtil.queryVocInfo(vocInfo);
    log.info("vocInfo: {}", result);
  }


  @Test
  void testProcessVoc() {
  }

  @Test
  void specialUnicode() {
  }

  @Test
  void shortStr() {
  }

  @Test
  void queryVocInfoList() {
  }

  @Test
  void queryWords() {
  }

  @Test
  void queryWord() {
    String word = "phubber";
    VocInfo vocInfo = DictUtil.queryWord(word);
    log.info("vocInfo: {}", vocInfo);
  }

  @Test
  void queryWordFromYoudao() {
    String word = "phubber";
    VocInfo vocInfo = DictUtil.queryWordFromYoudao(word);
    log.info("vocInfo: {}", vocInfo);
  }

  @Test
  void fillExplain() {
  }
}
