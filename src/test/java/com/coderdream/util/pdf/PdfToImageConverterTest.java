package com.coderdream.util.pdf;

import org.junit.jupiter.api.Test;

class PdfToImageConverterTest {

  @Test
  void convertPdfToImages_01() {
    String folder = "C:\\Users\\CoderDream\\Documents\\WeChat Files\\coderdream\\FileStorage\\File\\2024-11\\";
    folder = "D:\\0003_油管\\";

    folder = "E:\\BaiduPan\\唐纳德·特朗普在赢得 2024 年总统大选后发表讲话\\";
    folder = "D:\\Download\\";
    String fileName = "40篇短文记完高中3500核心词汇120页 【单词批注版】【高清精美排版】.pdf";
    fileName = "001杨门女将.pdf";
    fileName = "Part01.pdf";
    fileName = "REACT学习手册（第二版）.pdf";
    String pureFileName = fileName.substring(0, fileName.lastIndexOf("."));
    String outputDir = folder + pureFileName + "_pic\\";
    int dpi = 300; // 设置 DPI 为 300，以获得高清图片

    PdfToImageConverterUtil.convertPdfToImages(folder, fileName, outputDir, dpi);
  }

  @Test
  void convertPdfToImages_02() {

    String folder = "D:\\0000\\pdf\\";
    String fileName = "花甲老头20241226岁末总结.pdf";
    String pureFileName = fileName.substring(0, fileName.lastIndexOf("."));
    String outputDir = folder + pureFileName + "_pic\\";
    int dpi = 300; // 设置 DPI 为 300，以获得高清图片

    PdfToImageConverterUtil.convertPdfToImages(folder, fileName, outputDir, dpi);
  }

  @Test
  void convertPdfToImages_03() {

    String folder = "D:\\0000\\pdf\\";
    String fileName = "花甲老头20241226岁末总结.pdf";
    String pureFileName = fileName.substring(0, fileName.lastIndexOf("."));
    String outputDir = folder + pureFileName + "_pic\\";
    int dpi = 100; // 设置 DPI 为 300，以获得高清图片

    PdfToImageConverterUtil.convertPdfToImages(folder, fileName, outputDir, dpi);
  }
}
