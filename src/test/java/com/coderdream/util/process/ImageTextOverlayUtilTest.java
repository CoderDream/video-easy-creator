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
    String folderName = "20250326"; // D:\0000\0007_Trump\20250227

    String folderPath =
      OperatingSystem.getFolderPath(albumName) + File.separator + folderName;
    String formatName = "png";
    String backgroundImagePath =
      folderPath + File.separator + folderName + "." + formatName;

    String outputImagePath =
      folderPath + File.separator + folderName + "_cover." + formatName;
    String title = "白宮新聞簡報(12)";
    String mainTitle = "宣佈汽車關稅 製造業將崛起";
    String bottomTitle = "揭露 大西洋月刊 惡意攻擊";

    ImageTextOverlayUtil.addTextOverlay(backgroundImagePath, outputImagePath,
      title,
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
    String title = "";
    String mainTitle = "勇敢追夢，無視質疑！";
    String bottomTitle = "白宮新聞祕書的勵志演講";// "全球化正在摧毀美國製造業";

    ImageTextOverlayUtil.addTextOverlay(backgroundImagePath, outputImagePath,title,
      mainTitle,
      bottomTitle, formatName);
    System.out.println("Image processing completed!");
  }

  @Test
  void addTextOverlay_0008_20250314() {
    // 专辑名称
    String albumName = "0008_DailyNews";//
    String folderName = "20250321"; // D:\0000\0007_Trump\20250227
    String formatName = "png"; // D:\0000\0007_Trump\20250227
    String folderPath =
      OperatingSystem.getFolderPath(albumName) + File.separator + folderName;
    String backgroundImagePath =
      folderPath + File.separator + folderName + "." + formatName;
    String outputImagePath =
      folderPath + File.separator + folderName + "_cover." + formatName;

    String title = "";
    String mainTitle = "馬斯克引領永續能源與AI革命";
    String bottomTitle = "特斯拉如何重塑地球的未來";// "全球化正在摧毀美國製造業";

    ImageTextOverlayUtil.addTextOverlay(backgroundImagePath, outputImagePath,title,
      mainTitle,
      bottomTitle, formatName);
    System.out.println("Image processing completed!");
  }
}
