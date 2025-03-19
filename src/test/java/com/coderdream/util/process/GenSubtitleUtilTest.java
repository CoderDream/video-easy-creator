package com.coderdream.util.process;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.subtitle.GenSubtitleUtil;
import com.coderdream.util.subtitle.SubtitleUtil;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class GenSubtitleUtilTest {

  @Test
  void process() {
    //    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.txt";
    String folderName = "250305";
    String filePath =
      OperatingSystem.getBaseFolder() + File.separator + "0003_PressBriefings" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";
    int maxLineLength = 80; // 生成mp3
    GenSubtitleUtil.process(filePath, maxLineLength);
  }


  @Test
  void process_02() {
    //    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.txt";
    String folderName = "250219";
    String filePath =
      OperatingSystem.getBaseFolder() + File.separator + "0004_AppleEvents" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";
    int maxLineLength = 80;
    GenSubtitleUtil.process(filePath, maxLineLength);
  }

  @Test
  void processRawSrtFile_02() {
    //    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.txt";
    String folderName = "250219";
    String filePath =
      OperatingSystem.getBaseFolder() + File.separator + "0004_AppleEvents" + File.separator
        + folderName + File.separator + folderName
        + ".srt";
    String sourceFilePath = CdFileUtil.addPostfixToFileName(filePath, ".eng");
    String targetPath = CdFileUtil.addPostfixToFileName(filePath, ".eng2");
    GenSubtitleUtil.processRawSrtFile(sourceFilePath, targetPath);
  }

  @Test
  void processSrtAndGenDescription_01() {
    //    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.mp4";
    String folderName = "250219";
    String mp4FilePath =
      OperatingSystem.getBaseFolder() + File.separator + "0004_AppleEvents" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";
    GenSubtitleUtil.processSrtAndGenDescription(mp4FilePath);
  }

  @Test
  void processSrtAndGenDescription_02() {
    //    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.mp4";
    String folderName = "250305";
    String mp4FilePath =
      OperatingSystem.getBaseFolder() + File.separator + "0003_PressBriefings" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";

    GenSubtitleUtil.processSrtAndGenDescription(mp4FilePath);
  }

  @Test
  void processSrtAndGenDescription_0501() {
    //    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.mp4";
    String folderName = "vance_0003";
    String mp4FilePath =
      OperatingSystem.getBaseFolder() + File.separator + "0005_VP" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";
    GenSubtitleUtil.processSrtAndGenDescription(mp4FilePath);
  }

  @Test
  void processSrtAndGenDescription_0601() {
    //    String filePath = "D:\\0000\\0006_KK\\250128\\250131.mp4";
    String folderName = "20250314";
    String mp4FilePath =
      OperatingSystem.getBaseFolder() + File.separator + "0006_KK" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";

    GenSubtitleUtil.processSrtAndGenDescription(mp4FilePath);
  }

  @Test
  void processSrtAndGenDescription_0003_20230311() {
    //    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.mp4";
    // 专辑名称
    String albumName = "0003_PressBriefings";
    String folderName = "20250311"; // D:\0000\0007_Trump\20250227
    String mp4FilePath =
      OperatingSystem.getBaseFolder() + File.separator + albumName + File.separator
        + folderName + File.separator + folderName
        + ".mp4";

    GenSubtitleUtil.processSrtAndGenDescription(mp4FilePath);
  }

  @Test
  void processSrtAndGenDescription_0701() {
    //    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.mp4";
    String folderName = "20250307"; // D:\0000\0007_Trump\20250227
    String mp4FilePath =
      OperatingSystem.getBaseFolder() + File.separator + "0007_Trump" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";

    GenSubtitleUtil.processSrtAndGenDescription(mp4FilePath);
  }

  @Test
  void processSrtAndGenDescriptionV2_0701() {
    //    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.mp4";
    String folderName = "20250307"; // D:\0000\0007_Trump\20250227
    String mp4FilePath =
      OperatingSystem.getBaseFolder() + File.separator + "0007_Trump" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";

    GenSubtitleUtil.processSrtAndGenDescription(mp4FilePath);
  }

  @Test
  void processSrtAndGenDescription_0703() {
    //    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.mp4";
    String folderName = "20250303"; // D:\0000\0007_Trump\20250227
    String mp4FilePath =
      OperatingSystem.getBaseFolder() + File.separator + "0007_Trump" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";

    GenSubtitleUtil.processSrtAndGenDescription(mp4FilePath);
  }

  @Test
  void processSrtAndGenDescription_000801() {
    //  D:\0000\0008_DailyNews\2025030401  String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.mp4";
    String folderName = "20250314"; // D:\0000\0007_Trump\20250227
    String mp4FilePath =
      OperatingSystem.getBaseFolder() + File.separator + "0008_DailyNews" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";
    GenSubtitleUtil.processSrtAndGenDescription(mp4FilePath);
  }

  @Test
  void processRawSrtFile_03() {
    //    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.txt";
    String folderName = "250219";
    String filePath =
      OperatingSystem.getBaseFolder() + File.separator + "0004_AppleEvents" + File.separator
        + folderName + File.separator + folderName
        + ".srt";
    String sourceFilePath = CdFileUtil.addPostfixToFileName(filePath, ".chn");
    String targetPath = CdFileUtil.addPostfixToFileName(filePath, ".chn2");
    GenSubtitleUtil.processRawSrtFile(sourceFilePath, targetPath);
  }

  @Test
  void mergeSubtitleFile_01() {
    //    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.txt";
    String folderName = "250219";
    String filePath =
      OperatingSystem.getBaseFolder() + File.separator + "0004_AppleEvents" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";
    String srtFilePath = CdFileUtil.changeExtension(filePath, "srt");
    String srcFileNameEng = CdFileUtil.addPostfixToFileName(srtFilePath,
      ".eng2");
    String srcFileNameChn = CdFileUtil.addPostfixToFileName(srtFilePath,
      ".chn2");

    //  生成中文SRT文件
    if (CdFileUtil.isFileEmpty(srtFilePath)) {
      SubtitleUtil.mergeSubtitleFile(srcFileNameEng, srcFileNameChn,
        srtFilePath);
    } else {
      log.info("srtFilePath 文件已存在: {}", srtFilePath);
    }
  }

  @Test
  void filterContentFile_01() {
    //    String filePath = "D:\\0000\\0003_PressBriefings\\250128\\250131.txt";
    String folderName = "250219";
    String filePath =
      OperatingSystem.getBaseFolder() + File.separator + "0004_AppleEvents" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";
    String srtFilePath = CdFileUtil.changeExtension(filePath, "srt");
    String srcFileNameEng = CdFileUtil.addPostfixToFileName(srtFilePath,
      ".eng");
    GenSubtitleUtil.filterContentFile(srcFileNameEng, srcFileNameEng);
  }

}
