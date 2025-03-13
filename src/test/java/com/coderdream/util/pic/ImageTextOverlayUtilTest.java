package com.coderdream.util.pic;

import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import org.junit.jupiter.api.Test;

class ImageTextOverlayUtilTest {

  @Test
  void addTextOverlay_0003_20250311() {
    // 专辑名称
    String albumName = "0003_PressBriefings";
    String folderName = "20250311"; // D:\0000\0007_Trump\20250227

    String folderPath = OperatingSystem.getFolderPath(albumName) + File.separator + folderName;
    String backgroundImagePath =
      folderPath + File.separator + "20250311.mp4_000005.866.png";
    String outputImagePath = folderPath + File.separator + "cover_008.jpg";
    String mainTitle = "股市波動該如何解讀？";
    String bottomTitle = "全球化正在摧毀美國製造業";

    ImageTextOverlayUtil.addTextOverlay(backgroundImagePath, outputImagePath,
      mainTitle,
      bottomTitle);
    System.out.println("Image processing completed!");
  }
}
