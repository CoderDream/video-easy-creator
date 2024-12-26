package com.coderdream.util.audio;

import com.coderdream.util.CdConstants;
import com.coderdream.util.ffmpeg.FfmpegUtil;
import com.coderdream.util.video.BatchCreateVideoCommonUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AudioDurationUtil2 {

  /**
   * 创建包含音频文件路径的列表文件
   *
   * @param fileName 音频文件列表
   * @return 包含音频文件路径的临时列表文件
   */
  public static File createAudioDurationFileList(String fileName) {

    List<File> audioCnFiles = BatchCreateVideoCommonUtil.getAudioFiles(
      fileName,
      CdConstants.LANG_CN);
    List<File> audioEnFiles = BatchCreateVideoCommonUtil.getAudioFiles(
      fileName,
      CdConstants.LANG_EN);
    // 如果两个列表为空或者大小不相等，则直接报错退出
    List<String> totalFileNames = new ArrayList<>();
    if (audioCnFiles.isEmpty() || audioEnFiles.isEmpty()
      || audioCnFiles.size() != audioEnFiles.size()) {
      log.error("视频文件列表为空或大小不相等，无法合并");
      return null;
    } else {
      // 如果两个列表不为空，则继续合并
      totalFileNames.addAll(audioCnFiles.stream().map(File::getAbsolutePath).toList());
      totalFileNames.addAll(audioEnFiles.stream().map(File::getAbsolutePath).toList());

      log.info("音频文件列表不为空，继续合并");
    }

    // 按字母排序
    totalFileNames.sort(String::compareTo);

    int size = totalFileNames.size();

    // 创建合并文件列表
    String fileListFileName =
      BatchCreateVideoCommonUtil.getAudioPath(fileName) + fileName
        + "_duration.txt";
    File listFile = new File(fileListFileName);
    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(listFile))) {
      for (int i = 0; i < size; i++) {
        // 写入每个视频文件的路径
        // 写入每个视频文件的路径
        File audioFile = new File(totalFileNames.get(i));
        writer.write(audioFile.getName() + "\t"
          + FfmpegUtil.getAudioDuration(audioFile) + " \n");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    log.info("音频文件时长列表已创建：{}", listFile.getAbsolutePath());
    return listFile;
  }

}
