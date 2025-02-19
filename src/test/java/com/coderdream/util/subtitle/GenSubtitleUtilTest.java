package com.coderdream.util.subtitle;

import org.junit.jupiter.api.Test;

class GenSubtitleUtilTest {

  @Test
  void process() {
    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250128.txt";
    int maxLineLength = 100;
    GenSubtitleUtil.process(filePath, maxLineLength);
  }
}
