package com.coderdream.util;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.entity.VocInfo;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class CdVocInfoUtilTest {

  @Test
  void fillVocInfo0101() {
    VocInfo vocInfo = new VocInfo();
    // FOMO
    vocInfo.setWord("FOMO");
    //(acronym) Fear of Missing Out
    vocInfo.setWordExplainEn("(acronym) Fear of Missing Out");
    CdVocInfoUtil.fillVocInfo(vocInfo);
    log.info("{}", vocInfo);
  }

  @Test
  void fillVocInfo0201() {
    VocInfo vocInfo = new VocInfo();
    vocInfo.setWord("chauvinist");
    vocInfo.setWordExplainEn("someone who believes their country, race or sex is better than any others");
    CdVocInfoUtil.fillVocInfo(vocInfo);
    log.info("{}", vocInfo);
  }

  @Test
  void fillVocInfo0202() {
    VocInfo vocInfo = new VocInfo();
    vocInfo.setWord("technochauvinism");
    vocInfo.setWordExplainEn("the belief that a technological solution is always a better solution to a problem");
    CdVocInfoUtil.fillVocInfo(vocInfo);
    log.info("{}", vocInfo);
  }

  @Test
  void fillVocInfo0203() {
    VocInfo vocInfo = new VocInfo();
    vocInfo.setWord("FOMO");
    vocInfo.setWordExplainEn("(acronym) Fear of Missing Out");
    CdVocInfoUtil.fillVocInfo(vocInfo);
    log.info("{}", vocInfo);
  }

  /**
   * TODO: 获取单词列表详情
   */
  @Test
  void genVocInfoCnFile01() {
    String folderName = "241219";// "180705";
    String folderPath =  CommonUtil.getFullPath(folderName);
    String fileName = "voc";
    CdVocInfoUtil.genVocInfoCnFile(folderPath, fileName);
  }

  /**
   * TODO: 获取单词列表详情
   */
  @Test
  void genVocInfoCnFile02() {
    String folderName = "180712";
    String folderPath =  CommonUtil.getFullPath(folderName);
    String fileName = "voc";
    CdVocInfoUtil.genVocInfoCnFile(folderPath, fileName);
  }

  /**
   * TODO: 获取单词列表详情
   */
  @Test
  void genVocInfoCnFile03() {
    String folderName = "180719";
    String folderPath =  CommonUtil.getFullPath(folderName);
    String fileName = "voc";
    CdVocInfoUtil.genVocInfoCnFile(folderPath, fileName);
  }

  /**
   * TODO: 获取单词列表详情
   */
  @Test
  void genVocInfoCnFile04() {
    String folderName = "180726";
    String folderPath =  CommonUtil.getFullPath(folderName);
    String fileName = "voc";
    CdVocInfoUtil.genVocInfoCnFile(folderPath, fileName);
  }

  /**
   * TODO: 获取单词列表详情
   */
  @Test
  void genVocInfoCnFile05() {
    List<String> folderNameList = Arrays.asList("180705", "180712","180719", "180726");
    for (String folderName : folderNameList) {
      String folderPath =  CommonUtil.getFullPath(folderName);
      String fileName = "voc";
      log.info("folderPath: {}", folderPath);
      CdVocInfoUtil.genVocInfoCnFile(folderPath, fileName);
    }
  }
}
