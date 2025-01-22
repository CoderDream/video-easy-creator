package com.coderdream.util.ppt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PptUtilTest {

  @Test
  void processPpt_01() {
    String folderName = "250116";
    String timeTaken = PptUtil.processPpt(folderName);
    System.out.println("处理完成，耗时：" + timeTaken);
  }

  @Test
  void processPpt_02() {
    String folderName = "181011";
    String timeTaken = PptUtil.processPpt(folderName);
    System.out.println("处理完成，耗时：" + timeTaken);
  }
}
