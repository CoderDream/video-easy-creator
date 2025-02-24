package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.coderdream.util.proxy.OperatingSystem;
import org.junit.jupiter.api.Test;

class GenVideoUtilTest {

  @Test
  void process() throws InterruptedException {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch020";
    GenVideoUtil.process(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }


  @Test
  void process0101() throws InterruptedException {
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
  void process02() throws InterruptedException {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String subFolder = "Chapter013";
    GenVideoUtil.process(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }

  @Test
  void processV2_02() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String subFolder = "Chapter013";
    GenVideoUtil.processV2(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }

  // processV3
  @Test
  void processV3_02() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String subFolder = "Chapter014";
    GenVideoUtil.processV3(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }

  //
  @Test
  void processV4_02() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String subFolder = "Chapter010";
    GenVideoUtil.processV4(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }

  @Test
  void genHeadVideo_01() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    GenVideoUtil.genHeadVideo(folderPath);
    // assert that the process completes without any error
    assertTrue(true);
  }

  @Test
  void process03() throws InterruptedException {
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


  @Test
  void process02_Merge() {
//    String bookFolderName = "EnBook002";
//    String folderPath = "D:\\0000\\" + bookFolderName + "\\";

    String bookFolderName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookFolderName);

    String subFolder = "Chapter009";
    String bookName = "一輩子夠用的英語口語大全集";
    String chapterName = "book02_name.txt";
    String shortSubFolder = subFolder.substring(8);
    GenVideoUtil.processVideoMerger(folderPath, subFolder, shortSubFolder,
      bookFolderName, bookName,
      chapterName);
  }

  @Test
  void processBatch02()  {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 14; // 假定你想要处理的章节数
    for (int i = 11; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenVideoUtil.processV4(folderPath, subFolder);
    }
  }
}
