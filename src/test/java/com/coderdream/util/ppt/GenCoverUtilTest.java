package com.coderdream.util.ppt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GenCoverUtilTest {

  @Test
  void process() {
    String presentationName = "D:\\0000\\ppt\\Book02\\Book02模板.pptx";
    String chapterFileName = "book02_name.txt";
    GenCoverUtil.process(chapterFileName, presentationName);
  }
}
