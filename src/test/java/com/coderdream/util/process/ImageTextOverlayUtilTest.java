package com.coderdream.util.process;

import com.coderdream.util.pic.ImageTextOverlayUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ImageTextOverlayUtilTest {

  @Test
  void addTextOverlay_0003_20250311() {
    // 专辑名称
    String albumName = "0003_PressBriefings";
    String folderName = "20250402"; // D:\0000\0007_Trump\20250227

    String folderPath =
      OperatingSystem.getFolderPath(albumName) + File.separator + folderName;
    String formatName = "png";
    String backgroundImagePath =
      folderPath + File.separator + folderName + "." + formatName;

    String outputImagePath =
      folderPath + File.separator + folderName + "_cover." + formatName;
    String title = "白宮新聞簡報(13)";
    String mainTitle = "重拳出擊 貿易逆差即將終結";
    String bottomTitle = "MS-13恐怖份子被驅逐出境";

    ImageTextOverlayUtil.addTextOverlay(backgroundImagePath, outputImagePath,
      title, mainTitle, bottomTitle, formatName);

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

    ImageTextOverlayUtil.addTextOverlay(backgroundImagePath, outputImagePath,
      title,
      mainTitle,
      bottomTitle, formatName);
    System.out.println("Image processing completed!");
  }

  @Test
  void addTextOverlay_0007_20250331() {
    // 专辑名称 D:\0000\0007_Trump\20250331
    String albumName = "0007_Trump";//
    String folderName = "20250331"; // D:\0000\0007_Trump\20250227
    String formatName = "png"; // D:\0000\0007_Trump\20250227
    String folderPath =
      OperatingSystem.getFolderPath(albumName) + File.separator + folderName;
    String backgroundImagePath =
      folderPath + File.separator + folderName + "." + formatName;
    String outputImagePath =
      folderPath + File.separator + folderName + "_cover." + formatName;

    String title = ZhConverterUtil.toTraditional("川普签署行政命令");
    String mainTitle = "聯手K&R整治演唱會門票亂象";
    String bottomTitle = "中東萬億美元投資即將落地";// "全球化正在摧毀美國製造業";

    ImageTextOverlayUtil.addTextOverlay(backgroundImagePath, outputImagePath,
      title,
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

    ImageTextOverlayUtil.addTextOverlay(backgroundImagePath, outputImagePath,
      title,
      mainTitle,
      bottomTitle, formatName);
    System.out.println("Image processing completed!");
  }


  @Test
  void addTextOverlay_0009_20250319() {
    // 专辑名称 D:\0000\0009_TechNews\20250319
    String albumName = "0009_TechNews";
    String folderName = "20250319"; // D:\0000\0007_Trump\20250227

    String folderPath =
      OperatingSystem.getFolderPath(albumName) + File.separator + folderName;
    String formatName = "png";
    String backgroundImagePath =
      folderPath + File.separator + folderName + "." + formatName;

    String outputImagePath =
      folderPath + File.separator + folderName + "_cover." + formatName;
    String title = "黃仁勳GTC主題演講";
    String mainTitle = "Groot開源 加速機器人開發";
    String bottomTitle = "新架構揭祕 AI新時代來臨";

    ImageTextOverlayUtil.addTextOverlay(backgroundImagePath, outputImagePath,
      title,
      mainTitle,
      bottomTitle, formatName);
    System.out.println("Image processing completed!");
  }
}
