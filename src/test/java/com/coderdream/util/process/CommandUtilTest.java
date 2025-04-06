package com.coderdream.util.process;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.video.Mp4Splitter;
import com.coderdream.util.youtube.demo02.CommandUtil;
import com.coderdream.util.youtube.demo02.CommandUtil02;
import com.coderdream.util.youtube.demo03.YoutubeThumbnailFetcher;
import com.coderdream.util.youtube.demo05.CommandUtil05;
import com.coderdream.util.youtube.demo06.CommandUtil06;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class CommandUtilTest {


  // downloadBest1080p 调用前Ctrl+Alt+L打开代理
  @Test
  void downloadBest1080p() {

    // D:\0000\0005_VP\vance_0002

    String categoryName = "background";
    String subFolder = "ocean_0001";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + categoryName
        + File.separator;

    String videoLink = "https://www.youtube.com/watch?v=6Jy7_25opFo"; // 替换为实际的视频链接
    String outputFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + ".mp4"; // 替换为期望的输出路径和文件名
    CommandUtil02.downloadBest1080p(videoLink, outputFileName);
  }


  //  0006_KK
  @Test
  void downloadBest720p_0006_KarolineLeavitt() {

    // D:\0000\0005_VP\vance_0002 D:\0000\0003_PressBriefings\250305

    String categoryName = "0006_KarolineLeavitt";
    String subFolder = "20250222";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + categoryName;

    String videoLink = "https://www.youtube.com/watch?v=8Gd48gDAh5o";// "https://www.youtube.com/watch?v=dCY0b1wWX9A"; // 替换为实际的视频链接
    String outputFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + ".mp4"; // 替换为期望的输出路径和文件名

    String formatName = "png";
    String thumbnailFileName = outputFileName.replace(".mp4", "." + formatName);

    if (CdFileUtil.isFileEmpty(
      folderPath + File.separator + thumbnailFileName)) {
      YoutubeThumbnailFetcher.getThumbnail(videoLink, folderPath,
        thumbnailFileName);
    } else {
      log.info("封面文件已存在，无需重新获取");
    }

    // 下载视频
    CommandUtil.downloadBest720p(videoLink, outputFileName);
  }


  @Test
  void downloadBest720p_000801() {
    // D:\0000\0007_Trump\20250227 D:\0000\0008_DailyNews\0008_0001
    String categoryName = "0008_DailyNews";
    String subFolder = "20250319";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + categoryName
        + File.separator;
    if (!new File(folderPath + File.separator + subFolder).exists()) {
      boolean mkdir = new File(folderPath + File.separator + subFolder).mkdir();
      log.info("0008_DailyNews mkdir: {}", mkdir);
    }

    String videoLink = "https://www.youtube.com/watch?v=3R0swDZgjH8"; // 替换为实际的视频链接
    String outputFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + ".mp4"; // 替换为期望的输出路径和文件名
    CommandUtil02.downloadBest720p(videoLink, outputFileName);

//    String thumbnailPath = outputFileName.replace(".mp4", ".jpg");
    String thumbnailPath =
      CdFileUtil.getPureFileNameWithoutExtensionWithPath(outputFileName)
        + ".jpg";

    thumbnailPath = CdFileUtil.addPostfixToFileName(thumbnailPath,
      "_thumbnail");
//    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//    String thumbnailFileName = dateFormat.format(new Date()) + ".jpg";

    if (CdFileUtil.isFileEmpty(thumbnailPath)) {
      YoutubeThumbnailFetcher.getThumbnail(videoLink, folderPath,
        thumbnailPath);
    } else {
      log.info("封面文件已存在，无需重新获取");
    }
  }

  @Test
  void downloadBest720p_0003_PressBriefings() {
    String categoryName = "0003_PressBriefings"; // D:\0000\0003_PressBriefings
    String subFolder = "20250331";
    String videoLink = "https://www.youtube.com/watch?v=oQE2dgqe_bI"; // // 替换为实际的视频链接
    processSteps(categoryName, subFolder, videoLink);
  }

  //


  @Test
  void downloadBest720p_0007_Trump() {
    String categoryName = "0007_Trump"; // D:\0000\0003_PressBriefings
    String subFolder = "20250403";
    String videoLink = "https://www.youtube.com/watch?v=GaWx82C2jd4"; // // 替换为实际的视频链接
    processSteps(categoryName, subFolder, videoLink);
  }

  // https://www.youtube.com/watch?v=wui5-4VQjY0


  @Test
  void downloadBest720p_0005_Vance() {
    String categoryName = "0005_Vance";
    String subFolder = "20250329";
    String videoLink = "https://www.youtube.com/watch?v=edTYfp5XKDs"; // // 替换为实际的视频链接
    processSteps(categoryName, subFolder, videoLink);
  }


  @Test
  void downloadBest720p_0009_TechNews() {
    String categoryName = "0009_TechNews"; // D:\0000\0003_PressBriefings
    String subFolder = "20250401";
    String videoLink = "https://www.youtube.com/watch?v=wui5-4VQjY0"; // // 替换为实际的视频链接
    processSteps(categoryName, subFolder, videoLink);
  }

  private static void processSteps(String categoryName, String subFolder,
    String videoLink) {
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + categoryName
        + File.separator + subFolder;
    if (!new File(folderPath).exists()) {
      boolean mkdir = new File(folderPath).mkdir();
      log.info("mkdir: {}", mkdir);
    }

    String outputFileName =
      folderPath + File.separator + subFolder + ".mp4"; // 替换为期望的输出路径和文件名
    if (CdFileUtil.isFileEmpty(outputFileName)) {
      CommandUtil06.downloadBest720p(videoLink, outputFileName);
    }

    String thumbnailPath =
      CdFileUtil.changeExtension(outputFileName,"png");

//    thumbnailPath = CdFileUtil.addPostfixToFileName(thumbnailPath,
//      "_thumbnail");

    if (CdFileUtil.isFileEmpty(thumbnailPath)) {
      YoutubeThumbnailFetcher.getThumbnail(videoLink, thumbnailPath);
      log.info("封面文件下载成功: {}", thumbnailPath);
    } else {
      log.info("封面文件已存在，无需重新获取: {}", thumbnailPath);
    }
  }

  @Test
  void downloadBest720p_000901() {
    String categoryName = "0009_TechNews";
    String subFolder = "20250401";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + categoryName
        + File.separator + File.separator + subFolder;
    if (!new File(folderPath).exists()) {
      boolean mkdir = new File(folderPath).mkdir();
      log.info("0009_TechNews mkdir: {}", mkdir);
    }

    String videoLink = "https://www.youtube.com/watch?v=wui5-4VQjY0"; // // 替换为实际的视频链接
    String outputFileName =
      folderPath + File.separator + subFolder + ".mp4"; // 替换为期望的输出路径和文件名
    if (CdFileUtil.isFileEmpty(outputFileName)) {
      CommandUtil06.downloadBest720p(videoLink, outputFileName);
    }

    String thumbnailPath =
      CdFileUtil.getPureFileNameWithoutExtensionWithPath(outputFileName)
        + ".jpg";

    thumbnailPath = CdFileUtil.addPostfixToFileName(thumbnailPath,
      "_thumbnail");

    if (CdFileUtil.isFileEmpty(thumbnailPath)) {
      YoutubeThumbnailFetcher.getThumbnail(videoLink, thumbnailPath);
    } else {
      log.info("封面文件已存在，无需重新获取: {}", thumbnailPath);
    }
  }

  @Test
  void downloadBest720p_000802() {
//    String videoLink = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    String categoryName = "0008_DailyNews";
    String subFolder = "20250321";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + categoryName
        + File.separator;
    if (!new File(folderPath + File.separator + subFolder).exists()) {
      boolean mkdir = new File(folderPath + File.separator + subFolder).mkdir();
      log.info("0008_DailyNews mkdir: {}", mkdir);
    }

    String videoLink = "https://www.youtube.com/watch?v=QGJysv_Qzkw"; // 替换为实际的视频链接
    String outputFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + ".mp4"; // 替换为期望的输出路径和文件名
//    String proxyProtocol = "http"; // 或 "https", "socks5"
//    String proxyAddress = "127.0.0.1";
//    String proxyPort = "1080";
//    String resolution = "1280x720";
//    String proxyProtocol = "https";
//    String proxyAddress = "127.0.0.1";
//    int proxyPort = 1080;

// 分别下载音视频并合并
    CommandUtil.downloadBest720p(videoLink, outputFileName);

    String thumbnailPath =
      OperatingSystem.getBaseFolder() + File.separator
        + CdConstants.COVER_FOLDER + File.separator;
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    String formatName = "png"; // D:\0000\0007_Trump\20250227
    String thumbnailFileName = dateFormat.format(new Date()) + "." + formatName;

    if (CdFileUtil.isFileEmpty(thumbnailFileName)) {
      YoutubeThumbnailFetcher.getThumbnail(videoLink, thumbnailPath,
        thumbnailFileName);
    } else {
      log.info("封面文件已存在，无需重新获取");
    }
  }

  @Test
  void downloadBest1080p_03() {

    // D:\0000\0007_Trump\20250227

    String categoryName = "0007_Trump";
    String subFolder = "20250308";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + categoryName
        + File.separator;
    if (!new File(folderPath + File.separator + subFolder).exists()) {
      boolean mkdir = new File(folderPath + File.separator + subFolder).mkdir();
      log.info("mkdir: {}", mkdir);
    }

    String videoLink = "https://www.youtube.com/watch?v=omP3GPxaVog"; // 替换为实际的视频链接
    String outputFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + ".mp4"; // 替换为期望的输出路径和文件名
    CommandUtil02.downloadBest1080p(videoLink, outputFileName);
  }

  @Test
  void splitVideo_000703() {
    String categoryName = "0007_Trump";
    String subFolder = "20250303";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + categoryName
        + File.separator;
    String inputFilePath =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + ".mp4"; // 替换为期望的输出路径和文件名

    // 示例用法
//    String inputFilePath = "D:\\0000\\0003_PressBriefings\\250128\\250128.mp4";
    String timeStr = "00:00:03,000 --> 00:28:13,000";// 00:00:45,560 --> 00:00:49,960

//        00:00:49,960 --> 00:00:53,640
    String[] times = timeStr.split(" --> ");
    String startTime = times[0];//"00:00:03,400";
    String endTime = times[1];//"00:00:13,680";
    String outputFilePath = CdFileUtil.addPostfixToFileName(inputFilePath,
      "_part");

    String splitFile = Mp4Splitter.splitVideo(inputFilePath, startTime,
      endTime, outputFilePath);

    if (splitFile != null) {
      log.info("视频分割成功，文件保存在: {}", splitFile);
    } else {
      log.error("视频分割失败!");
    }
  }


  @Test
  void extractLeftChannel_0101() {
    // D:\0000\0007_Trump\20250227
    String categoryName = "0007_Trump";
    String subFolder = "20250228";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + categoryName
        + File.separator;
    String inputMp4 =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + ".mp4"; // 替换为期望的输出路径和文件名

    String outputMp3 = CdFileUtil.changeExtension(inputMp4, "mp3");
    if (CdFileUtil.isFileEmpty(outputMp3)) {
      CommandUtil02.extractLeftChannel(inputMp4, outputMp3);
    } else {
      log.info("outputMp3已存在: {}", outputMp3);
    }
  }

  @Test
  void extractPureVideo_0101() {
    // D:\0000\0007_Trump\20250227
    String categoryName = "0007_Trump";
    String subFolder = "20250228";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + categoryName
        + File.separator;
    String inputMp4 =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + ".mp4"; // 替换为期望的输出路径和文件名

    String pureMp4 = CdFileUtil.addPostfixToFileName(inputMp4, "_pure");
    if (CdFileUtil.isFileEmpty(pureMp4)) {
      CommandUtil02.extractPureVideo(inputMp4, pureMp4);
    } else {
      log.info("pureMp4 已存在: {}", pureMp4);
    }
  }

  @Test
  void extractPureVideo_0201() {
    // D:\0000\0007_Trump\20250227
    String categoryName = "background";
    String subFolder = "ocean_0001";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + categoryName
        + File.separator;
    String inputMp4 =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + ".mp4"; // 替换为期望的输出路径和文件名

    String pureMp4 = CdFileUtil.addPostfixToFileName(inputMp4, "_pure");
    if (CdFileUtil.isFileEmpty(pureMp4)) {
      CommandUtil02.extractPureVideo(inputMp4, pureMp4);
    } else {
      log.info("pureMp4 已存在: {}", pureMp4);
    }
  }
}
