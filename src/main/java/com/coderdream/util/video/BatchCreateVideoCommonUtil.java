package com.coderdream.util.video;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.ffmpeg.FfmpegUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BatchCreateVideoCommonUtil {

  /**
   * 获取图片文件夹下所有图片文件
   *
   * @return 图片文件列表
   */
  public static List<File> getImageFiles(String fileName) {
    File dir = new File(getPicPath(fileName));

    File[] files = dir.listFiles();
    if (files == null) {
      return Collections.emptyList();
    }
    List<File> filesResult = new ArrayList<>();
    for (File file : files) {
      // name最后一个下划线之前的字符串等于fileName
      String fileNameTemp = file.getName();
      int lastIndex = fileNameTemp.lastIndexOf("_");
      if (lastIndex != -1 && fileNameTemp.substring(0, lastIndex)
        .equals(fileName)) {
        filesResult.add(file);
      }
    }

    return filesResult;
  }

  /**
   * 根据图片文件提取对应的AUDIO文件
   *
   * @param fileName  图片文件列表
   * @param audioType 音频类型
   * @param lang      语言
   * @return 音频文件列表
   */
  public static List<File> getAudioFiles(String fileName, String audioType,
    String lang) {

    List<File> imageFiles = getImageFiles(fileName);
    List<File> audioFiles = new ArrayList<>();
    for (File imageFile : imageFiles) {
      // 从图片文件名中提取数字部分
      String imageName = imageFile.getName();
      int indexBegin = imageName.lastIndexOf("_"); // 找到最后一个下划线
      int indexEnd = imageName.lastIndexOf("."); // 找到最后一个点
      String indexStr = imageName.substring(indexBegin + 1, indexEnd);
      // 提取图片名中的数字部分（比如 CampingInvitation_001.png => 001）
//      log.info("indexStr: {}", indexStr);
      int number = Integer.parseInt(indexStr); // "CampingInvitation_"
      String audioName =
        imageName.substring(0, indexBegin + 1) + MessageFormat.format(
          "{0,number,000}",
          number) + "_" + lang + "." + audioType;  // 对应的AUDIO文件名

      String audioPath = getAudioPath(fileName, lang);
      File audioFile = new File(audioPath, audioName);
      if (audioFile.exists()) {
        audioFiles.add(audioFile);
      } else {
        log.warn("getAudioFiles 未找到对应的 {} 文件: {} \\ {}", audioType,
          audioPath, audioName);
      }
    }
    return audioFiles;
  }

  /**
   * 根据图片文件提取对应的AUDIO文件
   *
   * @param fileName 图片文件列表
   * @param lang     语言
   * @return 音频文件列表
   */
  public static List<File> getAudioFiles(String fileName,
    String lang) {
    return getAudioFiles(fileName, CdConstants.AUDIO_TYPE_WAV, lang);
  }

  /**
   * 根据文件提取对应的视频文件
   *
   * @param fileName 文件名
   * @param lang     语言
   * @return 音频文件列表
   */
  public static List<File> getVideoFiles(String fileName, String lang) {
    List<File> videoFiles = new ArrayList<>();
    List<File> imageFiles = getImageFiles(fileName);
    for (File imageFile : imageFiles) {
      // 从图片文件名中提取数字部分
      String imageName = imageFile.getName();
      int indexBegin = imageName.lastIndexOf("_"); // 找到最后一个下划线
      int indexEnd = imageName.lastIndexOf("."); // 找到最后一个点
      String indexStr = imageName.substring(indexBegin + 1, indexEnd);
      // 提取图片名中的数字部分（比如 CampingInvitation_001.png => 001）
//      log.info("indexStr: {}", indexStr);
      int number = Integer.parseInt(indexStr); // "CampingInvitation_"
      String videoName =
        imageName.substring(0, indexBegin + 1) + MessageFormat.format(
          "{0,number,000}",
          number) + "_" + lang + ".mp4";  // 对应的AUDIO文件名

      String videoPath = getVideoPath(fileName, lang);
      File audioFile = new File(videoPath, videoName);
      if (audioFile.exists()) {
        videoFiles.add(audioFile);
      } else {
        log.warn("getVideoFiles 未找到对应的文件: {}{}",
          videoPath, videoName);
      }
    }
    return videoFiles;
  }

  public static String getPicPath(String fileName) {
    String folder = CdConstants.RESOURCES_BASE_PATH + File.separator
      + fileName + File.separator
      + CdConstants.PIC_FOLDER;

    // 创建输出目录
    File dir = new File(folder);
    if (!dir.exists() && dir.mkdirs()) {
      log.info("getPicPath 目录创建成功: {}", dir.getAbsolutePath());
    }

    return dir.getAbsolutePath() + File.separator;
  }

  public static String getAudioPath(String fileName, String lang) {
    return getAudioPath(fileName) + lang + File.separator;
  }

  public static String getAudioPath(String fileName) {
    String folder = CdConstants.RESOURCES_BASE_PATH + File.separator
      + fileName + File.separator
      + CdConstants.AUDIO_FOLDER + File.separator;

    // 创建输出目录
    File dir = new File(folder);
    if (!dir.exists() && dir.mkdirs()) {
      log.info("getAudioPath 目录创建成功: {}", dir.getAbsolutePath());
    }

    return dir.getAbsolutePath() + File.separator;
  }

  public static String getVideoPath(String fileName, String lang) {
    return getVideoPath(fileName) + lang + File.separator;
  }

  public static String getVideoPath(String fileName) {
    String folder = CdConstants.RESOURCES_BASE_PATH + File.separator
      + fileName + File.separator
      + CdConstants.VIDEO_FOLDER + File.separator;

    // 创建输出目录
    File dir = new File(folder);
    if (!dir.exists() && dir.mkdirs()) {
      log.info("getVideoPath 目录创建成功: {}", dir.getAbsolutePath());
    }

    return dir.getAbsolutePath() + File.separator;
  }

  /**
   * 创建包含音频文件路径的列表文件
   *
   * @param fileName 音频文件列表
   * @return 包含音频文件路径的临时列表文件
   */
  public static File createAudioFileList(String fileName) {
    List<File> audioCnFiles = BatchCreateVideoCommonUtil.getAudioFiles(
      fileName,
      CdConstants.LANG_CN);
    List<File> audioEnFiles = BatchCreateVideoCommonUtil.getAudioFiles(
      fileName,
      CdConstants.LANG_EN);
    // 如果两个列表为空或者大小不相等，则直接报错退出
    if (audioCnFiles.isEmpty() || audioEnFiles.isEmpty()
      || audioCnFiles.size() != audioEnFiles.size()) {
      log.error("视频文件列表为空或大小不相等，无法合并");
      return null;
    }

    int size = audioCnFiles.size();

    // 创建合并文件列表
    String fileListFileName =
      BatchCreateVideoCommonUtil.getAudioPath(fileName) + fileName
        + "_file_list.txt";
    File listFile = new File(fileListFileName);
    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(listFile))) {
      for (int i = 0; i < size; i++) {
        // 写入每个视频文件的路径
        File audioCnFile = audioCnFiles.get(i);
        File audioEnFile = audioEnFiles.get(i);
        // 先播放3次英文，再播放1次中文，再播放1次英文，以此类推
        writer.write("file '" + audioEnFile.getAbsolutePath() + "'\n");
        writer.write("file '" + audioEnFile.getAbsolutePath() + "'\n");
        writer.write("file '" + audioEnFile.getAbsolutePath() + "'\n");
        writer.write("file '" + audioCnFile.getAbsolutePath() + "'\n");
        writer.write("file '" + audioEnFile.getAbsolutePath() + "'\n");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    log.info("音频文件列表已创建：{}", listFile.getAbsolutePath());
    return listFile;
  }

  /**
   * 创建包含音频文件路径的列表文件
   *
   * @param fileName 音频文件列表
   * @return 包含音频文件路径的临时列表文件
   */
  public static File createAudioDurationFileList(String fileName, String lang) {

//    List<File> audioFiles = BatchCreateVideoCommonUtil.getAudioFiles(
//      fileName,
//      CdConstants.LANG_CN);
    List<File> audioFiles = BatchCreateVideoCommonUtil.getAudioFiles(
      fileName, lang);

    int size = audioFiles.size();

    // 创建合并文件列表
    String fileListFileName =
      BatchCreateVideoCommonUtil.getAudioPath(fileName) + fileName
        + "_" + lang + "_duration.txt";
    File listFile = new File(fileListFileName);
    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(listFile))) {
      for (int i = 0; i < size; i++) {
        // 写入每个视频文件的路径
        File audioFile = audioFiles.get(i);
        writer.write("'" + audioFile.getAbsolutePath() + "' "
          + FfmpegUtil.getAudioDuration(audioFile) + " \n");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    log.info("音频文件时长列表已创建：{}", listFile.getAbsolutePath());
    return listFile;
  }

}
