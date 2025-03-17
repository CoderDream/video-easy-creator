package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.coderdream.util.proxy.OperatingSystem;
import org.junit.jupiter.api.Test;

class GenAudioUtilTest {

  @Test
  void process01() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch020";
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
  void process0101() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    List<String> subFolders = new ArrayList<>();
    int end = 21;
    for (int i = 15; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("ch" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenAudioUtil.process(folderPath, subFolder);
    }
  }

  @Test
  void process02() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String subFolder = "Chapter012";
    GenAudioUtil.process(folderPath, subFolder);
  }

  @Test
  void processBatch02() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 51; // 假定总共50章 51
    for (int i = 20; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenAudioUtil.process(folderPath, subFolder);
    }
  }

  @Test
  void processBatchEnBook_0004() {
    String bookName = "EnBook004";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 101; // 假定总共101章 51


    for (int i = 1; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenAudioUtil.process(folderPath, subFolder);
    }
  }

  @Test
  void processBatchEnBook_0005() {
    String bookName = "EnBook005";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 101; // 假定总共50章 51
    for (int i = 3; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenAudioUtil.process(folderPath, subFolder);
    }
  }

  @Test
  void processBatchEnBook_0008() {
    String bookName = "EnBook008";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 3; // 假定总共50章 51
    for (int i = 2; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenAudioUtil.processV20250317(folderPath, subFolder);
    }
  }

  // GenDualAudioUtil.genHeadAudio();

  @Test
  void genHeadAudioBatch() {
      GenAudioUtil.genHeadAudio();
  }


  @Test
  void genHeadAudio_EnBook008() {
    String bookName = "EnBook008";
    GenAudioUtil.genHeadAudio(bookName);
  }
}
