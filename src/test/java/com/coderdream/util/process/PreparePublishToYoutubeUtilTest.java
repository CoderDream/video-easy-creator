package com.coderdream.util.process;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.video.demo06.VideoEncoder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class PreparePublishToYoutubeUtilTest {

  @Test
  void processMerge_0003() {
    String bookFolderName = "0003_PressBriefings"; // D:\0000\0003_PressBriefings\20250326\20250326_白宫简报(12)
    String subFolder = "20250402";
    String chapterName = "20250402_白宮簡報(13)";

    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + bookFolderName + File.separator + subFolder;
    String destinationFileName =
      folderPath + File.separator + chapterName + File.separator + chapterName + ".mp4";
    // 重编码视频文件，用于B站发布
    String outputFilePath = CdFileUtil.addPostfixToFileName(destinationFileName,
      "_new");
    String encodedVideo = VideoEncoder.encodeVideo(destinationFileName,
      outputFilePath);
    log.info(bookFolderName + "视频编码完成: {}", encodedVideo);
  }

  @Test
  void processMerge_0007() {
    String bookFolderName = "0007_Trump";
    String subFolder = "20250331";
    String chapterName = "20250331_美國總統";

    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + bookFolderName + File.separator + subFolder;
    String destinationFileName =
      folderPath + File.separator + chapterName + File.separator + chapterName + ".mp4";
    // 重编码视频文件，用于B站发布
    String outputFilePath = CdFileUtil.addPostfixToFileName(destinationFileName,
      "_new");
    String encodedVideo = VideoEncoder.encodeVideo(destinationFileName,
      outputFilePath);
    log.info("视频编码完成: {}", encodedVideo);
  }


  @Test
  void processMerge_0008() {
    String bookFolderName = "0008_DailyNews";
    String subFolder = "20250321";
    String chapterName = "20250321_DailyNews";

    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + bookFolderName + File.separator + subFolder;
    String destinationFileName =
      folderPath + File.separator + chapterName + File.separator + chapterName + ".mp4";
    // 重编码视频文件，用于B站发布
    String outputFilePath = CdFileUtil.addPostfixToFileName(destinationFileName,
      "_new");
    String encodedVideo = VideoEncoder.encodeVideo(destinationFileName,
      outputFilePath);
    log.info("视频编码完成: {}", encodedVideo);
  }


  @Test
  void processMerge_0009() {
    String bookFolderName = "0009_TechNews";
    String subFolder = "20250319";
    String chapterName = "20250319_英偉達黃仁勳2025";

    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + bookFolderName + File.separator + subFolder;
    String destinationFileName =
      folderPath + File.separator + chapterName + File.separator + chapterName + ".mp4";
    // 重编码视频文件，用于B站发布
    String outputFilePath = CdFileUtil.addPostfixToFileName(destinationFileName,
      "_new");
    String encodedVideo = VideoEncoder.encodeVideo(destinationFileName,
      outputFilePath);
    log.info("视频编码完成: {}", encodedVideo);
  }
}
