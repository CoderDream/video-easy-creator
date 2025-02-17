package com.coderdream.util.video;

import static java.lang.Thread.sleep;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.ffmpeg.FfmpegUtil;

import java.io.File;
import java.util.List;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.xalan.res.XSLTErrorResources_en;

@Slf4j
public class SingleCreateVideoUtil {

  /**
   * 单个视频生成
   *
   * @param fileName 文件名，不带后缀
   * @return 视频文件
   */
  public static synchronized File singleCreateVideo(String fileName) {

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
      VideoCreatorUtil.createVideo(imagePath, audioFileName, videoFileName, duration);

    } catch (Exception e) {
      log.error("视频创建过程中出现异常", e);
    }
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("视频批量创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
    return videoFile;
  }

  /**
   * 单个视频生成
   *
   * @param imagePath     图片路径
   * @param audioFileName 音频文件名
   * @param videoFileName 视频文件名
   * @return 视频文件
   */
  public static synchronized File singleCreateVideo(String imagePath,
    String audioFileName,
    String videoFileName, double duration) {

    File videoFile = new File(videoFileName);
    if (!CdFileUtil.isFileEmpty(videoFileName)) {
      log.info("视频文件已存在，无需重新生成，{}", videoFileName);
      return videoFile;
    }

    long startTime = System.currentTimeMillis(); // 记录视频生成开始时间
    // File videoFile = null;
    try {
      // 1. 使用背景图片生成视频文件
      File imageFile = new File(imagePath);

      // 2. 根据文件名提取对应的AUDIO文件
      File audioFile = new File(audioFileName);

      videoFile = new File(videoFileName);

      // 生成视频
      VideoCreatorUtil.createVideo(imagePath, audioFileName, videoFileName, duration);

    } catch (Exception e) {
      log.error("视频创建过程中出现异常，{}", videoFileName, e);
    }
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("单个视频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
    return videoFile;
  }

  /**
   * 单个视频生成
   *
   * @param imagePath 图片路径
   * @param audioPath 音频文件路径
   * @param videoPath 视频文件路径 调取2号方法
   */
//  public static void batchCreateSingleVideo(String imagePath, String audioPath,
//    String videoPath) {
//
//
//    // 创建videoPath目录
//    File videoDir = new File(videoPath);
//    if (!videoDir.exists()) {
//      boolean isSuccess = videoDir.mkdirs();
//      log.info("创建目录：{}，结果：{}", videoPath, isSuccess);
//    }
//
//    List<String> imagePathNameList = FileUtil.listFileNames(imagePath);
//    List<String> audioPathNameList = FileUtil.listFileNames(audioPath);
//
//    // 如果图片列表数量和音频列表数量不一致，则抛出异常
//    if (CollectionUtil.isEmpty(imagePathNameList) || CollectionUtil.isEmpty(
//      audioPathNameList)
//      || imagePathNameList.size() != audioPathNameList.size()) {
//      log.error("图片列表数量和音频列表数量不一致");
//      return;
//    } else {
//        imagePathNameList.sort(String::compareTo);
//        audioPathNameList.sort(String::compareTo);
//    }
//
//    long startTime = System.currentTimeMillis(); // 记录视频生成开始时间
//
//    int size = imagePathNameList.size();
//    // 下列的任务提交给线程池执行
//    for (int i = 0; i < size; i++) {
//      String imagePathName = imagePath + File.separator
//        + imagePathNameList.get(i);
//      String audioFileName = audioPath + File.separator
//        + audioPathNameList.get(i);
//      String videoFileName =
//        videoPath + CdFileUtil.getPureFileNameWithoutExtensionWithPath(
//          audioFileName) + ".mp4";
//      log.info("第{}个视频开始生成", i + 1);
//      SingleCreateVideoUtil.singleCreateVideo(imagePathName, audioFileName,
//        videoFileName);
//    }
//
//    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
//    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
//    log.info("批量视频批量创建成功，共创建创建 {} 个视频， 耗时: {}", size,
//      CdTimeUtil.formatDuration(durationMillis));
//  }
  public static void batchCreateSingleVideo(String imagePath, String audioPath,
    String videoPath) throws InterruptedException {
    int corePoolSize = 8; // 核心线程数
    int maximumPoolSize = 8; // 最大线程数
    long keepAliveTime = 10; // 非核心线程空闲超时时间（秒）
    TimeUnit unit = TimeUnit.SECONDS;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(100); // 工作队列

    ThreadPoolExecutor executor = new ThreadPoolExecutor(
      corePoolSize,
      maximumPoolSize,
      keepAliveTime,
      unit,
      workQueue
    );

    // 创建videoPath目录
    File videoDir = new File(videoPath);
    if (!videoDir.exists()) {
      boolean isSuccess = videoDir.mkdirs();
      log.info("创建目录：{}，结果：{}", videoPath, isSuccess);
    }

    List<String> imagePathNameList = FileUtil.listFileNames(imagePath);
    List<String> audioPathNameList = FileUtil.listFileNames(audioPath);

    // 如果图片列表数量和音频列表数量不一致，则抛出异常
    if (CollectionUtil.isEmpty(imagePathNameList) || CollectionUtil.isEmpty(
      audioPathNameList)
      || imagePathNameList.size() != audioPathNameList.size()) {
      log.error("图片列表数量和音频列表数量不一致");
      return;
    } else {
      imagePathNameList.sort(String::compareTo);
      audioPathNameList.sort(String::compareTo);
    }

    long startTime = System.currentTimeMillis(); // 记录视频生成开始时间

    int size = imagePathNameList.size();
    int flag = 0;
    // 下列的任务提交给线程池执行
    for (int i = 0; i < size; i++) {
      String imagePathName = imagePath + File.separator
        + imagePathNameList.get(i);
      String audioFileName = audioPath + File.separator
        + audioPathNameList.get(i);
      String videoFileName =
        videoPath + CdFileUtil.getPureFileNameWithoutExtensionWithPath(
          audioFileName) + ".mp4";

      Runnable task = () -> {
        // 计算AUDIO时长
        double duration = FfmpegUtil.getAudioDuration(new File(audioFileName));
        log.info("本音频时长：{}", duration);
        SingleCreateVideoUtil.singleCreateVideo(imagePathName, audioFileName,
          videoFileName, duration);
      };

      if (i % 8 == 0 && flag != 0) {
        sleep(20000);
        log.info("阻塞20秒");

      } else {
        flag++;
        executor.execute(task);
        log.info("第{}个视频开始生成", i + 1);
      }

    }

    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("批量视频批量创建成功，共创建创建 {} 个视频， 耗时: {}", size,
      CdTimeUtil.formatDuration(durationMillis));
    executor.shutdown();
  }
}
