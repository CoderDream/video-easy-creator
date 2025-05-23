package com.coderdream.util.ppt;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.pic.PngToJpgConverter;
import com.coderdream.util.proxy.OperatingSystem;

import java.io.File;

import org.junit.jupiter.api.Test;

class GenCoverUtilTest {

  @Test
  void process0101() {
    // D:\0000\ppt\Book01 D:\0000\ppt\Book01\商务英语.pptx
    String bookName1 = "Book01";
//        String folderPath = OperatingSystem.getFolderPath(bookName1);

    String presentationName =
      OperatingSystem.getBaseFolder() + File.separator + "ppt" + File.separator + bookName1
        + File.separator + "商务英语.pptx";
    String chapterFileName = "900_cht_name.txt";
    String bookName = "EnBook001";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String imageFormat = "png";
    GenCoverUtil.process(folderPath, chapterFileName, presentationName,
      imageFormat, 1280, 720);
  }

  @Test
  void process() {
    String presentationName = "D:\\0000\\ppt\\Book02\\Book02模板.pptx";
    String chapterFileName = "book02_name2.txt";
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String imageFormat = "png";
    GenCoverUtil.process(folderPath, chapterFileName, presentationName,
      imageFormat);
  }


  @Test
  void process_0102() {
    String presentationName =
      OperatingSystem.getBaseFolder() + File.separator + "ppt" + File.separator + "Book02"
        + File.separator + "Book02模板.pptx";
    String chapterFileName = "book02_name.txt";
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String imageFormat = "png";
    GenCoverUtil.process(folderPath, chapterFileName, presentationName,
      imageFormat, 1280, 720);
  }

  @Test
  void process_EnBook004_0102() {
    String bookName = "EnBook004";
    String presentationName =
      OperatingSystem.getBaseFolder() + File.separator + "ppt" + File.separator + bookName
        + File.separator + bookName + "模板.pptx";
    String chapterFileName = "book04_name.txt";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String imageFormat = "png";
    GenCoverUtil.process(folderPath, chapterFileName, presentationName,
      imageFormat, 1280, 720);
  }

  @Test
  void process_EnBook005_0102() {
    String bookName = "EnBook005";
    String presentationName =
      OperatingSystem.getBaseFolder() + File.separator + "ppt" + File.separator + bookName
        + File.separator + bookName + "模板.pptx";
    String chapterFileName = "book05_name.txt";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String imageFormat = "png";
    GenCoverUtil.process(folderPath, chapterFileName, presentationName,
      imageFormat, 1280, 720);
  }

  @Test
  void process_EnBook008() {
    String bookName = "EnBook008";
    String presentationName =
      OperatingSystem.getBaseFolder() + File.separator + "ppt" + File.separator + bookName
        + File.separator + bookName + "模板.pptx";
    String chapterFileName = bookName+ "_name.txt";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String imageFormat = "png";
    GenCoverUtil.process(folderPath, chapterFileName, presentationName,
      imageFormat, 1280, 720);
  }

  @Test
  void process_02() {
    String presentationName =
      OperatingSystem.getBaseFolder() + File.separator + "ppt" + File.separator + "Book02"
        + File.separator + "Book02模板.pptx";
    String chapterFileName = "book02_name.txt";
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String imageFormat = "jpg";
    GenCoverUtil.process(folderPath, chapterFileName, presentationName,
      imageFormat);

    // 示例用法
//    String inputDirectory = "D:\\0000\\EnBook002\\cover"; // 存放 PNG 文件的目录
//    String outputDirectory = "D:\\0000\\EnBook002\\cover"; // 存放 JPG 文件的目录
    String inputDirectory =
      folderPath + File.separator + bookName + File.separator + CdConstants.COVER_FOLDER; // 存放 PNG 文件的目录
    String outputDirectory = inputDirectory; // 存放 JPG 文件的目录
    float jpgQuality = 1.0f;// 0.8f; // JPG 图像质量

//        // 创建测试用的目录和文件（可移除，仅用于测试）
//        new File(inputDirectory).mkdirs();
//        try {
//            new File(inputDirectory, "test.png").createNewFile();
//            new File(inputDirectory, "test2.PNG").createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    PngToJpgConverter.batchConvertPngToJpg(inputDirectory, outputDirectory,
      jpgQuality);
  }

  void process_0201() {
    String presentationName =
      OperatingSystem.getBaseFolder() + File.separator + "ppt" + File.separator + "Book02"
        + File.separator + "Book02模板.pptx";
    String chapterFileName = "book02_name.txt";
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String imageFormat = "png";
    GenCoverUtil.process(folderPath, chapterFileName, presentationName,
      imageFormat);

    // 示例用法
//    String inputDirectory = "D:\\0000\\EnBook002\\cover"; // 存放 PNG 文件的目录
//    String outputDirectory = "D:\\0000\\EnBook002\\cover"; // 存放 JPG 文件的目录
    String inputDirectory =
      folderPath + File.separator + bookName + File.separator + CdConstants.COVER_FOLDER; // 存放 PNG 文件的目录
    String outputDirectory = inputDirectory; // 存放 JPG 文件的目录
    float jpgQuality = 1.0f;// 0.8f; // JPG 图像质量

//        // 创建测试用的目录和文件（可移除，仅用于测试）
//        new File(inputDirectory).mkdirs();
//        try {
//            new File(inputDirectory, "test.png").createNewFile();
//            new File(inputDirectory, "test2.PNG").createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    PngToJpgConverter.batchConvertPngToJpg(inputDirectory, outputDirectory,
      jpgQuality);
  }


  @Test
  void process_03() {
    String presentationName =
      OperatingSystem.getBaseFolder() + File.separator + "ppt" + File.separator + "Book02"
        + File.separator + "Book02模板.pptx";

    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    // 示例用法
//    String inputDirectory = "D:\\0000\\EnBook002\\cover"; // 存放 PNG 文件的目录
//    String outputDirectory = "D:\\0000\\EnBook002\\cover"; // 存放 JPG 文件的目录
    String inputDirectory =
      folderPath + File.separator + CdConstants.COVER_FOLDER; // 存放 PNG 文件的目录
    String outputDirectory = inputDirectory; // 存放 JPG 文件的目录
    float jpgQuality = 1.0f;// 0.8f; // JPG 图像质量

//        // 创建测试用的目录和文件（可移除，仅用于测试）
//        new File(inputDirectory).mkdirs();
//        try {
//            new File(inputDirectory, "test.png").createNewFile();
//            new File(inputDirectory, "test2.PNG").createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    PngToJpgConverter.batchConvertPngToJpg(inputDirectory, outputDirectory,
      jpgQuality);
  }



//  @Test
//  void processEnBook008() {
//    String presentationName =
//      OperatingSystem.getBaseFolder() + File.separator + "ppt" + File.separator + "Book02"
//        + File.separator + "Book02模板.pptx";
//
//    String bookName = "EnBook008";
//    String folderPath = OperatingSystem.getFolderPath(bookName);
//
//    // 示例用法
////    String inputDirectory = "D:\\0000\\EnBook002\\cover"; // 存放 PNG 文件的目录
////    String outputDirectory = "D:\\0000\\EnBook002\\cover"; // 存放 JPG 文件的目录
//    String inputDirectory =
//      folderPath + File.separator + CdConstants.COVER_FOLDER; // 存放 PNG 文件的目录
//    String outputDirectory = inputDirectory; // 存放 JPG 文件的目录
//    float jpgQuality = 1.0f;// 0.8f; // JPG 图像质量
//
////        // 创建测试用的目录和文件（可移除，仅用于测试）
////        new File(inputDirectory).mkdirs();
////        try {
////            new File(inputDirectory, "test.png").createNewFile();
////            new File(inputDirectory, "test2.PNG").createNewFile();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//    PngToJpgConverter.batchConvertPngToJpg(inputDirectory, outputDirectory,
//      jpgQuality);
//  }
}
