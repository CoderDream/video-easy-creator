package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.txt.FileFilterUtil;
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
  void genAiFile() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch017";
    BeforeGenerateUtil.genAiFile(folderPath, subFolder);
    assertTrue(true);
    System.out.println("done");
  }

  @Test
  void process() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch020";
    BeforeGenerateUtil.process(folderPath, subFolder);
    assertTrue(true);
    System.out.println("done");
  }

  @Test
  void process00() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
//    String subFolder = "ch012";
//    BeforeGenerateUtil.process(folderPath, subFolder);
//    assertTrue(true);
    System.out.println("done");
    List<String> subFolders = new ArrayList<>();
    int end = 21;
    for (int i = 13; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("ch" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      BeforeGenerateUtil.processGenRawTxt(folderPath, subFolder);
      // assert that the process completes without any error
      assertTrue(true);
    }
  }

  @Test
  void process01() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
//    String subFolder = "ch012";
//    BeforeGenerateUtil.process(folderPath, subFolder);
//    assertTrue(true);
    System.out.println("done");
    List<String> subFolders = new ArrayList<>();
    int end = 21;
    for (int i = 13; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("ch" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      BeforeGenerateUtil.process(folderPath, subFolder);
      // assert that the process completes without any error
      assertTrue(true);
    }

  }

  @Test
  void processBook02() {
    String folderPath = "D:\\0000\\EnBook002\\";
//    String subFolder = "Chapter004";
//    BeforeGenerateUtil.processBook02(folderPath, subFolder);
//    assertTrue(true);
//    System.out.println("done");
    List<String> subFolders = new ArrayList<>();
    int end = 51;
    for (int i = 6; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      BeforeGenerateUtil.processBook02(folderPath, subFolder);
      // assert that the process completes without any error
      assertTrue(true);
    }
  }

  @Test
  void processBook0203() {
    String folderPath = "D:\\0000\\EnBook002\\";
//    String subFolder = "Chapter004";
//    BeforeGenerateUtil.processBook02(folderPath, subFolder);
//    assertTrue(true);
//    System.out.println("done");
    List<String> subFolders = new ArrayList<>();
    int end = 51;
    for (int i = 7; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      FileFilterUtil.filterAndSaveFile(folderPath, subFolder);
    }
  }

  @Test
  void processBook0201() {
    String folderPath = "D:\\0000\\EnBook002\\";
    String subFolder = "Chapter008";
    BeforeGenerateUtil.processBook02(folderPath, subFolder);
    assertTrue(true);
    System.out.println("done");
  }
}
