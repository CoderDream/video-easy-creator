package com.coderdream.util.video.demo03;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class Mp4SplitterTest {

  @Test
  void splitVideo() {

    String folderName = "vance_0003";
    String folderPath =
      OperatingSystem.getBaseFolder() + "0005_VP" + File.separator
        + folderName + File.separator;
    String inputFilePath = folderPath + folderName + ".mp4";

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
}
