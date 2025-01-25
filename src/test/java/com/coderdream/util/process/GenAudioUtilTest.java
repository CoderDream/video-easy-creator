package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GenAudioUtilTest {

  @Test
  void process01() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch010";
    GenAudioUtil.process(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }

  @Test
  void process() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch021";
    GenAudioUtil.process(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }

  @Test
  void process02() {
    String folderPath = "D:\\0000\\EnBook002\\";
    String subFolder = "Chapter004";
    GenAudioUtil.process(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }
}
