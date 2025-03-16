package com.coderdream.util.pic;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
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


  @Test
  void testGenerateImages_03() {
    String bookName = "EnBook008";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String subFolder = "Chapter001";
    String backgroundImageName =
      OperatingSystem.getBaseFolder() + File.separator + "bgmusic" + File.separator
        + "content_bg.png";
    String phoneticsFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + ".txt";
    String filePath = folderPath + File.separator + subFolder
      + File.separator;
    String language = "en";
    List<File> files = null;
//    files = HighResImageVideoUtil.generateImages(backgroundImageName,
//      filePath,
//      CdFileUtil.getPureFileNameWithoutExtensionWithPath(phoneticsFileName),
//      language);

    language = "dual";
    // 生成带英文和中文的图片
//    files = HighResImageVideoUtil.generateImages(backgroundImageName,
//      filePath,
//      CdFileUtil.getPureFileNameWithoutExtensionWithPath(phoneticsFileName),
//      language, CdConstants.PIC_TYPE_DUAL_EN);
  }
}
