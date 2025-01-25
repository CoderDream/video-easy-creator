package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class GenVideoUtilTest {

  @Test
  void process() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch010";
    GenVideoUtil.process(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }

  @Test
  void process02() {
    String folderPath = "D:\\0000\\EnBook002\\";
    String subFolder = "Chapter003";
    GenVideoUtil.process(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }

  @Test
  void process03() {
    String folderPath = "D:\\0000\\EnBook002\\";
    List<String> subFolders = Arrays.asList("Chapter004", "Chapter004");
    for (String subFolder : subFolders) {
//    String subFolder = "Chapter003";
      GenVideoUtil.process(folderPath, subFolder);
      // assert that the process completes without any error
      assertTrue(true);
    }

  }
}
