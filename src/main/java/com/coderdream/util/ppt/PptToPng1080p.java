package com.coderdream.util.ppt;

public class PptToPng1080p {

  /**
   * 将PPT演示文稿保存为1080p分辨率的PNG图像
   *
   * @param pptFilePath PPT文件路径
   * @param outputDir   输出目录
   */
  public static void savePptToPng1080p(String pptFilePath, String outputDir) {
    PptToPng.savePptToPng(pptFilePath, outputDir, 1920, 1080);
  }

}
