package com.coderdream.util.youtube.demo02;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class CommandUtilTest {

  @Test
  void downloadBest720p() {

    // D:\0000\0005_VP\vance_0002

    String bookFolderName = "0005_VP";
    String subFolder = "vance_0003";
    String folderPath =
      OperatingSystem.getBaseFolder() + bookFolderName + File.separator;

    String videoLink = "https://www.youtube.com/watch?v=dCY0b1wWX9A"; // 替换为实际的视频链接
    String outputFileName = folderPath + subFolder + File.separator + subFolder
      + ".mp4"; // 替换为期望的输出路径和文件名
    CommandUtil.downloadBest720p(videoLink, outputFileName);
  }

  // https://www.youtube.com/watch?v=S_YtXWVfkJE

  @Test
  void downloadBest720p_02() {

    // D:\0000\0007_Trump\20250227

    String bookFolderName = "0007_Trump";
    String subFolder = "20250228";
    String folderPath =
      OperatingSystem.getBaseFolder() + bookFolderName + File.separator;
    if (!new File(folderPath + subFolder).exists()) {
      boolean mkdir = new File(folderPath + subFolder).mkdir();
      log.info("mkdir: {}", mkdir);
    }

    String videoLink = "https://www.youtube.com/watch?v=S_YtXWVfkJE"; // 替换为实际的视频链接
    String outputFileName = folderPath + subFolder + File.separator + subFolder
      + ".mp4"; // 替换为期望的输出路径和文件名
    CommandUtil.downloadBest720p(videoLink, outputFileName);
  }

  @Test
  void extractLeftChannel_0101() {
    // D:\0000\0007_Trump\20250227
    String bookFolderName = "0007_Trump";
    String subFolder = "20250228";
    String folderPath =
      OperatingSystem.getBaseFolder() + bookFolderName + File.separator;
    String inputMp4 = folderPath + subFolder + File.separator + subFolder
      + ".mp4"; // 替换为期望的输出路径和文件名

    String outputMp3 = CdFileUtil.changeExtension(inputMp4, "mp3");
    if (CdFileUtil.isFileEmpty(outputMp3)) {
      CommandUtil.extractLeftChannel(inputMp4, outputMp3);
    } else {
      log.info("outputMp3已存在: {}", outputMp3);
    }
  }

  @Test
  void extractPureVideo_0101() {
    // D:\0000\0007_Trump\20250227
    String bookFolderName = "0007_Trump";
    String subFolder = "20250228";
    String folderPath =
      OperatingSystem.getBaseFolder() + bookFolderName + File.separator;
    String inputMp4 = folderPath + subFolder + File.separator + subFolder
      + ".mp4"; // 替换为期望的输出路径和文件名

    String pureMp4 = CdFileUtil.addPostfixToFileName(inputMp4, "_pure");
    if (CdFileUtil.isFileEmpty(pureMp4)) {
      CommandUtil.extractPureVideo(inputMp4, pureMp4);
    } else {
      log.info("pureMp4 已存在: {}", pureMp4);
    }
  }
}
