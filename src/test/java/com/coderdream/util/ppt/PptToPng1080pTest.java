package com.coderdream.util.ppt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PptToPng1080pTest {

  @Test
  void savePptToPng1080p() {

    String pptFilePath = "D:\\0000\\ppt\\Book02\\Book02模板_02.pptx";
    String outputDir = "D:\\0000\\ppt\\Book02\\xxx";
    PptToPng1080p.savePptToPng1080p(pptFilePath, outputDir);
  }
}
