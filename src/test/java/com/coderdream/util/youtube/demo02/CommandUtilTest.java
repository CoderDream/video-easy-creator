package com.coderdream.util.youtube.demo02;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.video.Mp4Splitter;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class CommandUtilTest {


  // downloadBest1080p 调用前Ctrl+Alt+L打开代理
  @Test
  void downloadBest1080p() {

    // D:\0000\0005_VP\vance_0002

    String bookFolderName = "background";
    String subFolder = "ocean_0001";
    String folderPath =
      OperatingSystem.getBaseFolder() + bookFolderName + File.separator;

    String videoLink = "https://www.youtube.com/watch?v=6Jy7_25opFo"; // 替换为实际的视频链接
    String outputFileName = folderPath + subFolder + File.separator + subFolder
      + ".mp4"; // 替换为期望的输出路径和文件名
    CommandUtil.downloadBest1080p(videoLink, outputFileName);
  }

  //
  @Test
  void downloadBest720p_() {

    // D:\0000\0005_VP\vance_0002 D:\0000\0003_PressBriefings\250305

    String bookFolderName = "0003_PressBriefings";
    String subFolder = "250305";
    String folderPath =
      OperatingSystem.getBaseFolder() + bookFolderName + File.separator;

    String videoLink = "https://www.youtube.com/watch?v=mQMKP2cSdIk";// "https://www.youtube.com/watch?v=dCY0b1wWX9A"; // 替换为实际的视频链接
    String outputFileName = folderPath + subFolder + File.separator + subFolder
      + ".mp4"; // 替换为期望的输出路径和文件名
    CommandUtil.downloadBest720p(videoLink, outputFileName);
  }


  @Test
  void downloadBest720p() {

    // D:\0000\0005_VP\vance_0002

    String bookFolderName = "0005_VP";
    String subFolder = "vance_0004";
    String folderPath =
      OperatingSystem.getBaseFolder() + bookFolderName + File.separator;

    String videoLink = "https://www.youtube.com/watch?v=6Jy7_25opFo";// "https://www.youtube.com/watch?v=dCY0b1wWX9A"; // 替换为实际的视频链接
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
  void downloadBest720p_03() {

    // D:\0000\0007_Trump\20250227

    String bookFolderName = "0007_Trump";
    String subFolder = "20250303";
    String folderPath =
      OperatingSystem.getBaseFolder() + bookFolderName + File.separator;
    if (!new File(folderPath + subFolder).exists()) {
      boolean mkdir = new File(folderPath + subFolder).mkdir();
      log.info("mkdir: {}", mkdir);
    }

    String videoLink = "https://www.youtube.com/watch?v=kQO50OiUcrw"; // 替换为实际的视频链接
    String outputFileName = folderPath + subFolder + File.separator + subFolder
      + ".mp4"; // 替换为期望的输出路径和文件名
    CommandUtil.downloadBest720p(videoLink, outputFileName);
  }

  //

  @Test
  void downloadBest720p_000801() {
    // D:\0000\0007_Trump\20250227 D:\0000\0008_DailyNews\0008_0001
    String bookFolderName = "0008_DailyNews";
    String subFolder = "2025030401";
    String folderPath =
      OperatingSystem.getBaseFolder() + bookFolderName + File.separator;
    if (!new File(folderPath + subFolder).exists()) {
      boolean mkdir = new File(folderPath + subFolder).mkdir();
      log.info("mkdir: {}", mkdir);
    }

    String videoLink = "https://www.youtube.com/watch?v=1ImCFVMgOPU"; // 替换为实际的视频链接
    String outputFileName = folderPath + subFolder + File.separator + subFolder
      + ".mp4"; // 替换为期望的输出路径和文件名
    CommandUtil.downloadBest720p(videoLink, outputFileName);
  }

  @Test
  void downloadBest1080p_03() {

    // D:\0000\0007_Trump\20250227

    String bookFolderName = "0007_Trump";
    String subFolder = "20250304";
    String folderPath =
      OperatingSystem.getBaseFolder() + bookFolderName + File.separator;
    if (!new File(folderPath + subFolder).exists()) {
      boolean mkdir = new File(folderPath + subFolder).mkdir();
      log.info("mkdir: {}", mkdir);
    }

    String videoLink = "https://www.youtube.com/watch?v=-ooIQ1R99nQ"; // 替换为实际的视频链接
    String outputFileName = folderPath + subFolder + File.separator + subFolder
      + ".mp4"; // 替换为期望的输出路径和文件名
    CommandUtil.downloadBest1080p(videoLink, outputFileName);
  }

  @Test
  void splitVideo_000703() {
    String bookFolderName = "0007_Trump";
    String subFolder = "20250303";
    String folderPath =
      OperatingSystem.getBaseFolder() + bookFolderName + File.separator;
    String inputFilePath = folderPath + subFolder + File.separator + subFolder
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

  @Test
  void extractPureVideo_0201() {
    // D:\0000\0007_Trump\20250227
    String bookFolderName = "background";
    String subFolder = "ocean_0001";
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
