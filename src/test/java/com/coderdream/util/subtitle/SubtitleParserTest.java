package com.coderdream.util.subtitle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SubtitleParserTest {

  @Test
  void parseTextToSubtitle() {
    String filePath = "D:\\Download\\纳瓦尔宝典：财富与幸福指南+(埃里克．乔根森+(Eric+Jorgenson))+(Z-Library)\\v1.txt";
    SubtitleParser.parseTextToSubtitle(filePath);
  }

  @Test
  void parseTextToSubtitle_02() {
    String filePath = "D:\\0000\\BooK02\\Boo02_v2.txt";
    SubtitleParser.parseTextToSubtitle(filePath);
  }
}
