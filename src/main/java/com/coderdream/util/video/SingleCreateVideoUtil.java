package com.coderdream.util.video;

import com.coderdream.util.CdConstants;
import com.coderdream.util.CdTimeUtil;
import com.coderdream.util.ffmpeg.FfmpegUtil;
import java.io.File;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SingleCreateVideoUtil {

  /**
   * 单个视频生成
   * @param fileName  文件名，不带后缀
   * @return  视频文件
   */
  public static File singleCreateVideo(String fileName) {

    long startTime = System.currentTimeMillis(); // 记录视频生成开始时间
    File videoFile = null;
    try {
      // 1. 使用背景图片生成视频文件
      String imagePath = CdConstants.RESOURCES_BASE_PATH + File.separator
        + CdConstants.BACKGROUND_IMAGE_FILENAME;
      File imageFile = new File(imagePath);

      // 2. 根据文件名提取对应的AUDIO文件
      String audioFileName =
        BatchCreateVideoCommonUtil.getAudioPath(fileName) + fileName + "."
          + CdConstants.AUDIO_TYPE_WAV;
      File audioFile = new File(audioFileName);

      // 计算AUDIO时长
      double duration = FfmpegUtil.getAudioDuration(audioFile);
      log.info("音频时长：{}", duration);

      String videoFileName =
        BatchCreateVideoCommonUtil.getVideoPath(fileName) + fileName
          + "_no_content.mp4";

      videoFile = new File(videoFileName);

      // 生成视频
      VideoCreatorUtil.createVideo(imageFile, audioFile, videoFile, duration);

    } catch (Exception e) {
      log.error("视频创建过程中出现异常", e);
    }
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("视频批量创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
    return videoFile;
  }
}
