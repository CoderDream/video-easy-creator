package com.coderdream.util;

import com.coderdream.util.cd.CdStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class CdStringUtilTest {

  @Test
  void countLeftParentheses() {
  }

  @Test
  void countRightParentheses() {
  }

  @Test
  void removeNewLinesAndBlankLines() {
  }

  @Test
  void removePrefix() {
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
