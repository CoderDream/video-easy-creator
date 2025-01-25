package com.coderdream.util.process;

import com.coderdream.util.video.SingleCreateVideoUtil;
import java.io.File;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenVideoUtil {

  public static void process(String folderPath, String subFolder) {
    String imagePath = folderPath + subFolder + File.separator + "pic_cht\\";
    String audioPath = folderPath + subFolder + File.separator + "audio_mix\\";
    String videoPath = folderPath + subFolder + File.separator + "video_cht\\";
    SingleCreateVideoUtil.batchCreateSingleVideo(imagePath, audioPath,
      videoPath);
  }
}
