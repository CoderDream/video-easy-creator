package com.coderdream.util.video;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.ffmpeg.FfmpegUtil;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BatchCreateVideoUtil {


  public static List<File> batchCreateVideo(String fileName, String audioType,
    String lang) {

    long startTime = System.currentTimeMillis(); // 记录视频生成开始时间
    List<File> videoFiles = null;
    try {
      // 1. 获取图片文件夹中的所有图片文件
      List<File> imageFiles = BatchCreateVideoCommonUtil.getImageFiles(
        fileName);
      if (imageFiles.isEmpty()) {
        log.error("图片文件夹为空！");
        return Collections.emptyList();
      }
      // 2. 根据图片文件名提取对应的AUDIO文件
      List<File> audioFiles = BatchCreateVideoCommonUtil.getAudioFiles(
        fileName, audioType, lang);
      if (audioFiles.isEmpty()) {
        log.error("没有找到对应的 {} 文件！{} {}", audioType, imageFiles, lang);
        return Collections.emptyList();
      }

      // 3. 生成视频路径
      File videoPathFolder = new File(
        BatchCreateVideoCommonUtil.getVideoPath(fileName, lang));
      if (!videoPathFolder.exists() && videoPathFolder.mkdirs()) {
        log.info("目录创建成功: {}", videoPathFolder.getAbsolutePath());
      }

      // 3.1 使用FFmpeg命令合成视频
      videoFiles = new ArrayList<>();
      for (int i = 0; i < imageFiles.size(); i++) {
        File imageFile = imageFiles.get(i);
        File audioFile = audioFiles.get(i);
        int number = i + 1;
        File videoFile = new File(
          BatchCreateVideoCommonUtil.getVideoPath(fileName, lang)
            + fileName + "_" + MessageFormat.format("{0,number,000}", number)
            + "_" + lang + ".mp4"
        );

        // 计算AUDIO时长
        double duration = FfmpegUtil.getAudioDuration(audioFile);
        // 生成视频
        VideoCreatorUtil5.createVideo(imageFile, audioFile, videoFile, duration);
        videoFiles.add(videoFile);
      }
    } catch (Exception e) {
      log.error("视频创建过程中出现异常", e);
    }
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("视频批量创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
    return videoFiles;
  }
}
