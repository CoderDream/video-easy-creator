package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.proxy.OperatingSystem;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class PreparePublishUtilTest {

  @Test
  void process01() {
    String bookFolderName = "EnBook001";
    String folderPath =
      OperatingSystem.getVideoBaseFolder() + bookFolderName + File.separator;
    String subFolder = "ch018"; // 商務英語
    String bookName = "商務英語";
    String chapterName = "900_cht_name.txt";
    String shortSubFolder = subFolder.substring(3);
    PreparePublishUtil.process(folderPath, subFolder, shortSubFolder,
      bookFolderName, bookName, chapterName);
  }

  @Test
  void process01_Batch_01() {
//    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String bookFolderName = "EnBook001";
    String folderPath =
      OperatingSystem.getVideoBaseFolder() + bookFolderName + File.separator;

    String bookName = "商務英語";
    String chapterName = "900_cht_name.txt";
    List<String> subFolders = new ArrayList<>();
    int end = 17;
    for (int i = 16; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("ch" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      String shortSubFolder = subFolder.substring(3);
      PreparePublishUtil.process(folderPath, subFolder, shortSubFolder,
        bookFolderName, bookName,
        chapterName);
    }
  }

  @Test
  void process02() {
//    String bookFolderName = "EnBook002";
//    String folderPath = "D:\\0000\\" + bookFolderName + "\\";

    String bookFolderName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookFolderName);

    String subFolder = "Chapter011";
    String bookName = "一輩子夠用的英語口語大全集";
    String chapterName = "book02_name.txt";
    String shortSubFolder = subFolder.substring(8);
    PreparePublishUtil.process(folderPath, subFolder, shortSubFolder,
      bookFolderName, bookName,
      chapterName);
  }

  @Test
  void process000601() {
    String bookFolderName = "EnBook001";
//    String folderPath =
//            OperatingSystem.getBaseFolder() + bookFolderName + File.separator;
    List<String> subFolders = new ArrayList<>();
    int end = 21;
    for (int i = 19; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      PreparePublishUtil.process(bookFolderName, subFolder);
    }
  }

  @Test
  void process000701() {
//        String folderName = "20250227"; // D:\0000\0007_Trump\20250227 D:\0000\EnBook002\Chapter012\video

    String bookFolderName = "EnBook002";
    String subFolder = "Chapter012";
    String folderPath =
      OperatingSystem.getBaseFolder() + bookFolderName + File.separator;

    PreparePublishUtil.process(folderPath, subFolder);
  }

  @Test
  void process000202() {
    String bookFolderName = "EnBook002";

    List<String> subFolders = new ArrayList<>();
    int end = 51; // 51章
    for (int i = 22; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    String chapterName = "book02_name.txt";
    String headContentFileName = "head_content.txt";
    for (String subFolder : subFolders) {
      PreparePublishUtil.process(bookFolderName, subFolder, chapterName, headContentFileName);
    }
  }

  @Test
  void process000502() {
    String bookFolderName = "EnBook005";

    List<String> subFolders = new ArrayList<>();
    int end = 101; // 51章
    for (int i = 1; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    String chapterName = "book05_name.txt";
    String headContentFileName = "head_content.txt";
    for (String subFolder : subFolders) {
      PreparePublishUtil.process(bookFolderName, subFolder, chapterName, headContentFileName);
    }
  }
}
