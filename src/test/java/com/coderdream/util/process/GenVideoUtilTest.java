package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.video.demo04.Mp4MergeUtil;
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
    String subFolder = "Chapter011";
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
    String subFolder = "Chapter011";
    GenVideoUtil.processV4(bookName, folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }

//  @Test
//  void genHeadVideo_01() {
//    String bookName = "EnBook002";
//    String folderPath = OperatingSystem.getFolderPath(bookName);
//    GenVideoUtil.genHeadVideo(folderPath);
//    // assert that the process completes without any error
//    assertTrue(true);
//  }

  @Test
  void genHeadVideo_EnBook001_01() {
    String bookName = "EnBook001";
    GenVideoUtil.genHeadVideo(bookName);
    // assert that the process completes without any error
    assertTrue(true);
  }

  @Test
  void genHeadVideo_EnBook005_01() {
    String bookName = "EnBook005";
    GenVideoUtil.genHeadVideo(bookName);
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

  /**
   * 未实现
   */
  @Test
  void process02_Merge() {
//    String bookFolderName = "EnBook002";
//    String folderPath = "D:\\0000\\" + bookFolderName + "\\";

//    String bookFolderName = "EnBook002";
//    String folderPath = OperatingSystem.getFolderPath(bookFolderName);
//
//    String subFolder = "Chapter009";
//    String bookName = "一輩子夠用的英語口語大全集";
//    String chapterName = "book02_name.txt";
//    String shortSubFolder = subFolder.substring(8);
//    GenVideoUtil.processVideoMerger(folderPath, subFolder, shortSubFolder,
//      bookFolderName, bookName,
//      chapterName);
  }

  @Test
  void processBatch01()  {
    String bookName = "EnBook001";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 21; // 假定你想要处理的章节数 51
    for (int i = 19; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenVideoUtil.processV4(bookName, folderPath, subFolder);
    }
  }

  @Test
  void processBatch02()  {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 51; // 假定你想要处理的章节数 51
    for (int i = 11; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenVideoUtil.processV4(bookName, folderPath, subFolder);
    }
  }

  @Test
  void processBatchEnBook_0005() {
    String bookName = "EnBook005";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 2; // 假定总共50章 51
    for (int i = 1; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenVideoUtil.processV4(bookName, folderPath, subFolder);
    }
  }

  // ******************************************************************* //


  @Test
  void processBatch_Merge_0101()  {
    String bookName = "EnBook001";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 21; // 假定你想要处理的章节数
    for (int i = 2; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("ch" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      Mp4MergeUtil.processMerge(folderPath, subFolder);
    }
  }

  @Test
  void process02_Merge_0201() {
    String bookFolderName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookFolderName);
    String subFolder = "Chapter001";
    Mp4MergeUtil.processMerge(folderPath, subFolder);
  }

  @Test
  void processBatch_Merge_EnBook00101()  {
    String bookName = "EnBook001";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 21; // 假定你想要处理的章节数
    for (int i = 19; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      Mp4MergeUtil.processMerge(folderPath, subFolder);
    }
  }

  @Test
  void processBatch_Merge_0201()  {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 51; // 假定你想要处理的章节数
    for (int i = 7; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      Mp4MergeUtil.processMerge(folderPath, subFolder);
    }
  }


  @Test
  void processMerge_EnBook_0005() {
    String bookName = "EnBook005";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 2; // 假定总共50章 51
    for (int i = 1; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      Mp4MergeUtil.processMerge(folderPath, subFolder);
    }
  }

  @Test
  void processBatchAndMergeEnBook_0002() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 51; // 假定总共100章 101
    for (int i = 7; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenVideoUtil.processV4(bookName, folderPath, subFolder);
      Mp4MergeUtil.processMerge(folderPath, subFolder);
    }
  }


  @Test
  void processBatchAndMergeEnBook_0004() {
    String bookName = "EnBook004";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 101; // 假定总共100章 101
    for (int i = 1; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenVideoUtil.processV4(bookName, folderPath, subFolder);
      Mp4MergeUtil.processMerge(folderPath, subFolder);
    }
  }

  @Test
  void processBatchAndMergeEnBook_0005() {
    String bookName = "EnBook005";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 101; // 假定总共100章 101
    for (int i = 3; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenVideoUtil.processV4(bookName, folderPath, subFolder);
      Mp4MergeUtil.processMerge(folderPath, subFolder);
    }
  }

  @Test
  void processBatchAndMergeEnBook008() {
    String bookName = "EnBook008";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 3; // 假定总共100章 101
    for (int i = 2; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenVideoUtil.processV20250317(bookName, folderPath, subFolder);
      Mp4MergeUtil.processMerge(folderPath, subFolder);
    }
  }
}
