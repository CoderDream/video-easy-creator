package com.coderdream.util.pic;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class HighResImageVideoUtilTest {
//
//  @Test
//  void generateImages() {
//
////    String fileName = "test";
////    List<File> files = HighResImageVideoUtil5.generateImages(fileName);
////    assertNotNull(files);
////    assertFalse(files.isEmpty());
//  }
//
//  @Test
//  void testGenerateImages() {
//  }
//
//  @Test
//  void testGenerateImages1() {
//
//  }

  @Test
  void testGenerateImages_01() {
    String backgroundImageName = "D:\\0000\\bgmusic\\background.png";
    String filePath = "D:\\0000\\EnBook001\\900\\ch01\\";
    String contentFileName = "ch01"; // 生成图片
    List<File> files = HighResImageVideoUtil.generateImages(backgroundImageName,
      filePath, contentFileName);
    assertNotNull(files);
    assertFalse(files.isEmpty());
  }

  @Test
  void testGenerateImages_02() {
    String backgroundImageName = "D:\\0000\\商务英语(5)\\bgv4.png";// "D:\\0000\\bgmusic\\background.png";
    String filePath = "D:\\0000\\EnBook001\\900\\ch01\\";
    String chapter = "ch01";
    String language = "cht";
    String contentFileName = chapter + "_" + language;// "ch01_cht"; // 生成图片
    List<File> files = HighResImageVideoUtil.generateImages(backgroundImageName,
      filePath, contentFileName, language);
    assertNotNull(files);
    assertFalse(files.isEmpty());
  }
}
