package com.coderdream.util.video;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class Mp4SplitterTest {


  @Test
  void splitVideo0003() {
//D:\0000\0003_PressBriefings\250317
    String folderName = "20250401";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + "0009_TechNews" + File.separator
        + folderName;
    String inputFilePath = folderPath + File.separator + folderName + "_raw.mp4";

    // 示例用法
//    String inputFilePath = "D:\\0000\\0003_PressBriefings\\250128\\250128.mp4";
    String timeStr = "00:00:34,000 --> 00:36:47,000";// 00:00:45,560 --> 00:00:49,960

//        00:00:49,960 --> 00:00:53,640
    String[] times = timeStr.split(" --> ");
    String startTime = times[0];//"00:00:03,400";
    String endTime = times[1];//"00:00:13,680";
    String outputFilePath = CdFileUtil.removePostfixToFileName(inputFilePath,
      "_raw");

    String splitFile = Mp4Splitter.splitVideo(inputFilePath, startTime,
      endTime, outputFilePath);

    if (splitFile != null) {
      log.info("视频分割成功，文件保存在: {}", splitFile);
    } else {
      log.error("视频分割失败!");
    }
  }

  @Test
  void splitVideo() {

    String folderName = "vance_0003";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + "0005_VP" + File.separator
        + folderName + File.separator;
    String inputFilePath = folderPath + File.separator + folderName + ".mp4";

    // 示例用法
//    String inputFilePath = "D:\\0000\\0003_PressBriefings\\250128\\250128.mp4";
    String timeStr = "00:02:45,560 --> 00:32:00,000";// 00:00:45,560 --> 00:00:49,960

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
  void splitVideo0007_Trump() {
// D:\0000\0007_Trump\20250307
    String folderName = "20250302";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + "0007_Trump" + File.separator
        + folderName + File.separator;
    String inputFilePath = folderPath + File.separator + folderName + ".mp4";

    // 示例用法
//    String inputFilePath = "D:\\0000\\0003_PressBriefings\\250128\\250128.mp4";
    String timeStr = "00:23:11,000 --> 02:11:20,000";// 00:00:45,560 --> 00:00:49,960

//        00:00:10,000 --> 00:00:53,640
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
  void splitVideo_02() {

//    String folderName = "vance_0003";
//    String folderPath =
//      OperatingSystem.getBaseFolder() + File.separator + "0005_VP" + File.separator
//        + folderName + File.separator;
//    String inputFilePath = folderPath + File.separator + folderName + ".mp4";

    String bookFolderName = "background";
    String subFolder = "ocean_0001";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + bookFolderName + File.separator;
    String inputFilePath = folderPath + File.separator + subFolder + File.separator + subFolder
      + "_pure.mp4"; // 替换为期望的输出路径和文件名

    // 示例用法
//    String inputFilePath = "D:\\0000\\0003_PressBriefings\\250128\\250128.mp4";
    String timeStr = "00:01:06,000 --> 00:01:11,000";// 00:00:45,560 --> 00:00:49,960

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
}
