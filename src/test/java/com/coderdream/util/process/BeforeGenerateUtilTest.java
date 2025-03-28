package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.txt.FileFilterUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class BeforeGenerateUtilTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }


  @Test
  void genAiFile() {
    String bookName = "EnBook001";
    String folderPath =
      OperatingSystem.getFolderPath(bookName) + "900" + File.separator;
    String subFolder = "ch017";
    BeforeGenerateUtil.genAiFile(folderPath, subFolder);
    assertTrue(true);
    System.out.println("done");
  }

  @Test
  void process() {
    String bookName = "EnBook001";
    String folderPath =
      OperatingSystem.getFolderPath(bookName) + "900" + File.separator;
    String subFolder = "ch020";
    BeforeGenerateUtil.process(folderPath, subFolder);
    assertTrue(true);
    System.out.println("done");
  }

  @Test
  void process00() {
    String bookName = "EnBook001";
    String folderPath =
      OperatingSystem.getFolderPath(bookName) + "900" + File.separator;
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
//    String folderPath = "D:\\0000\\EnBook001\\900\\";
//
    String bookName = "EnBook001";
    String folderPath =
      OperatingSystem.getFolderPath(bookName) + "900" + File.separator;
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
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
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
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
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
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String subFolder = "Chapter007";
    BeforeGenerateUtil.processBook02(folderPath, subFolder);
    assertTrue(true);
    System.out.println("done");
  }

  @Test
  void processBook020201() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String subFolder = "Chapter007";
    BeforeGenerateUtil.processBook0202(folderPath, subFolder);
    assertTrue(true);
    System.out.println("done");
  }

  @Test
  void processBook020101() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String subFolder = "Chapter008";
    BeforeGenerateUtil.processBook0201(folderPath, subFolder);
    assertTrue(true);
    System.out.println("done");
  }

  @Test
  void processBook020101_02() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    List<String> subFolders = new ArrayList<>();
    int end = 51;
    for (int i = 11; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      BeforeGenerateUtil.processBook0201(folderPath, subFolder);
    }
  }

  @Test
  void processBook002_AI_00() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String subFolder = "Chapter033";
    BeforeGenerateUtil.processBook002_AI(folderPath, subFolder);
  }

  @Test
  void processBook002_AI_01() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    List<String> subFolders = new ArrayList<>();
    int end = 51;
    for (int i = 28; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      BeforeGenerateUtil.processBook002_AI(folderPath, subFolder);
    }
  }

  //
  @Test
  void processBook002Phonetics_01() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    List<String> subFolders = new ArrayList<>();
    int end = 51;
    for (int i = 11; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      BeforeGenerateUtil.processBook002Phonetics(folderPath, subFolder);
    }
  }

  /**
   * 生成 total 和 basic
   */
  @Test
  void processGenDialogTxt() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String subFolder = "Chapter031";
    BeforeGenerateUtil.processGenDialogTxt(folderPath, subFolder, false);
    assertTrue(true);
    System.out.println("done");
  }

  @Test
  void processGenDialogTxt_02() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
//    String subFolder = "Chapter004";
//    BeforeGenerateUtil.processBook02(folderPath, subFolder);
//    assertTrue(true);
//    System.out.println("done");
    List<String> subFolders = new ArrayList<>();
    int end = 51;
    for (int i = 9; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    boolean replaceFlag = true;
    for (String subFolder : subFolders) {
      BeforeGenerateUtil.processGenDialogTxt(folderPath, subFolder,
        replaceFlag);
    }
  }

  @Test
  void processBook00402() {
    String bookName = "EnBook004";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    List<String> subFolders = new ArrayList<>();
    int end = 101;
    for (int i = 1; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      BeforeGenerateUtil.process(folderPath, subFolder);
    }

    assertTrue(true);
    System.out.println("done");
  }

  @Test
  void processBook00501() {
    String bookName = "EnBook005";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String subFolder = "Chapter001";
    BeforeGenerateUtil.process(folderPath, subFolder);
    assertTrue(true);
    System.out.println("done");
  }

  @Test
  void processBook00502() {
    String bookName = "EnBook005";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    List<String> subFolders = new ArrayList<>();
    int end = 101;// 101;
    for (int i = 3; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      BeforeGenerateUtil.process(folderPath, subFolder);
    }

    assertTrue(true);
    System.out.println("done");
  }

  //
  @Test
  void deleteFolder_Book00502() {
    String bookName = "EnBook005";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    List<String> subFolders = new ArrayList<>();
    int end = 101;// 101;
    for (int i = 3; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      BeforeGenerateUtil.deleteFolder(folderPath, subFolder);
    }

    assertTrue(true);
    System.out.println("done");
  }

  @Test
  void deleteFolder_Book008() {
    String bookName = "EnBook008";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    List<String> subFolders = new ArrayList<>();
    int end = 15;// 15;
    for (int i = 2; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }
    // , "video_cht",
    //            "video_list_file", "subtitle", "video_cht_1", "video_cht_2", "video_cht_3", "video_cht_4"
    List<String> folderNameList = Arrays.asList("audio_dual_en", "audio_mix");
    for (String subFolder : subFolders) {
      BeforeGenerateUtil.deleteFolder(folderPath, subFolder, folderNameList);
    }

    assertTrue(true);
    System.out.println("done");
  }
}
