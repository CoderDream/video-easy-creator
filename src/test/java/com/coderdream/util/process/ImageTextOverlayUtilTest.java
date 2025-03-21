package com.coderdream.util.process;

import com.coderdream.util.pic.ImageTextOverlayUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import org.junit.jupiter.api.Test;

class ImageTextOverlayUtilTest {

  @Test
  void addTextOverlay_0003_20250311() {
    // 专辑名称
    String albumName = "0003_PressBriefings";
    String folderName = "20250319"; // D:\0000\0007_Trump\20250227

    String folderPath =
      OperatingSystem.getFolderPath(albumName) + File.separator + folderName;
    String formatName = "png";
    String backgroundImagePath =
      folderPath + File.separator + "20250319.mp4_000025.711." + formatName;

    String outputImagePath =
      folderPath + File.separator + folderName + "_cover." + formatName;
    String mainTitle = "機密文件公開 真相即將揭曉";
    String bottomTitle = "8 萬頁肯尼迪遇刺檔案解密";

    ImageTextOverlayUtil.addTextOverlay(backgroundImagePath, outputImagePath,
      mainTitle,
      bottomTitle, formatName);
    System.out.println("Image processing completed!");
  }

  @Test
  void addTextOverlay_0006_20250314() {
    // 专辑名称
    String albumName = "0006_KK";// 0008_DailyNews
    String folderName = "20250224"; // D:\0000\0007_Trump\20250227

    String folderPath =
      OperatingSystem.getFolderPath(albumName) + File.separator + folderName;
    String formatName = "png";
    String backgroundImagePath =
      folderPath + File.separator + "0001.mp4_000012.904.png";
    String outputImagePath =
      folderPath + File.separator + folderName + "_cover.jpg";
    String mainTitle = "勇敢追夢，無視質疑！";
    String bottomTitle = "白宮新聞祕書的勵志演講";// "全球化正在摧毀美國製造業";

    ImageTextOverlayUtil.addTextOverlay(backgroundImagePath, outputImagePath,
      mainTitle,
      bottomTitle, formatName);
    System.out.println("Image processing completed!");
  }

  @Test
  void addTextOverlay_0008_20250314() {
    // 专辑名称
    String albumName = "0008_DailyNews";//
    String folderName = "20250314"; // D:\0000\0007_Trump\20250227
    String formatName = "png";
    String folderPath =
      OperatingSystem.getFolderPath(albumName) + File.separator + folderName;
    String backgroundImagePath =
      folderPath + File.separator + "20250314.f232.mp4_001515.035.png";
    String outputImagePath =
      folderPath + File.separator + folderName + "_cover.jpg";
    String mainTitle = "美國務卿解析中國政策";
    String bottomTitle = "呼籲公平貿易與責任共擔";// "全球化正在摧毀美國製造業";

    ImageTextOverlayUtil.addTextOverlay(backgroundImagePath, outputImagePath,
      mainTitle,
      bottomTitle, formatName);
    System.out.println("Image processing completed!");
  }
}
