package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeforeGenerateUtilTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void process() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch010";
    BeforeGenerateUtil.process(folderPath, subFolder);
    assertTrue(true);
    System.out.println("done");
  }

  @Test
  void processBook02() {
    String folderPath = "D:\\0000\\EnBook002\\";
//    String subFolder = "Chapter004";
//    BeforeGenerateUtil.processBook02(folderPath, subFolder);
//    assertTrue(true);
//    System.out.println("done");
    List<String> subFolders = new ArrayList<>();
    int end = 8;
    for (int i = 5; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      BeforeGenerateUtil.processBook02(folderPath, subFolder);
      // assert that the process completes without any error
      assertTrue(true);
    }
  }
}
