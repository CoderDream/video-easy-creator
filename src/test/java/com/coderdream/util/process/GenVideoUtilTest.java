package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.coderdream.util.proxy.OperatingSystem;
import org.junit.jupiter.api.Test;

class GenVideoUtilTest {

  @Test
  void process() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch020";
    GenVideoUtil.process(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }


  @Test
  void process0101() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    List<String> subFolders = new ArrayList<>();
    int end = 21;
    for (int i = 15; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("ch" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenVideoUtil.process(folderPath, subFolder);
    }
  }

  @Test
  void process02() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String subFolder = "Chapter008";
    GenVideoUtil.process(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }

  @Test
  void process03() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    List<String> subFolders = Arrays.asList("Chapter004", "Chapter004");
    for (String subFolder : subFolders) {
//    String subFolder = "Chapter003";
      GenVideoUtil.process(folderPath, subFolder);
      // assert that the process completes without any error
      assertTrue(true);
    }

  }
}
