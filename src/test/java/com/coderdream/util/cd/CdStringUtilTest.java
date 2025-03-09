package com.coderdream.util.cd;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class CdStringUtilTest {

  @Test
  void findFirstChineseCharOrPunctuationIndex() {
    String str = "I'm in heaven. 我好像到了天堂。";
    // 查询是否有中文
    int firstIndex = CdStringUtil.findFirstChineseCharOrPunctuationIndex(
      str);
    log.info("firstIndex: {}", firstIndex);
  }

  @Test
  void findFirstChineseCharIndex() {
    String str = "I'm in heaven. 我好像到了天堂。";
    // 查询是否有中文
    int firstIndex = CdStringUtil.findFirstChineseCharIndex(
      str);
    log.info("findFirstChineseCharIndex firstIndex: {}", firstIndex);
    log.info("findFirstChineseCharIndex firstIndex: {}", str.substring(firstIndex));
  }

  @Test
  void replaceImagesLinks() {
    String originalString = "![luoxiang](/images/Image00002.jpg)";
    String result =
      CdStringUtil.replaceImagesLinks(originalString);
    log.info("result: {}", result);
  }

  // replaceImagesFirstLinks

  @Test
  void replaceImagesFirstLinks() {
    String originalString = "![luoxiang](/images/Image00002.jpg)";
    String result =
      CdStringUtil.replaceImagesFirstLinks(originalString);
    log.info("replaceImagesFirstLinks result: {}", result);
  }

  @Test
  void leftImagesLinks() {
  }
}
