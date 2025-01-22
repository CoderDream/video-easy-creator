package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GenVideoUtilTest {

  @Test
  void process() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch021";
    GenVideoUtil.process(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }

  @Test
  void process02() {
    String folderPath = "D:\\0000\\EnBook002\\";
    String subFolder = "Chapter001";
    GenVideoUtil.process(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }
}
