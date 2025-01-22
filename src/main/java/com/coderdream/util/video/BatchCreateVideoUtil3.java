package com.coderdream.util.video;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.ffmpeg.FfmpegUtil;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BatchCreateVideoUtil3 {

  private static final int MAX_RETRIES = 10; // 最大重试次数
  private static final int RETRY_DELAY = 10; // 每次重试的延迟时间，单位毫秒

  /**
   * 批量生成视频，并行处理任务，支持失败重试机制。
   *
   * @param fileName  视频文件的基本名称
   * @param audioType 音频类型（如 MP3、WAV 等）
   * @param lang      语言
   * @return 生成的视频文件列表
   */
  public static List<File> batchCreateVideo(String fileName, String audioType,
    String lang) {
    long startTime = System.currentTimeMillis(); // 记录视频生成开始时间
    List<File> videoFiles = new ArrayList<>();
    ExecutorService executor = null; // 线程池

    try {
      // 0.创建文件夹
      File videoDir = new File(
        BatchCreateVideoCommonUtil.getVideoPath(fileName, lang));
      if (!videoDir.exists()) {
        boolean created = videoDir.mkdirs();
        log.info("创建文件夹：{}，结果：{}", videoDir.getAbsolutePath(),
          created);
      }

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

      // 3. 使用线程池并行处理视频创建
//      int cpuCores = Runtime.getRuntime().availableProcessors();
//      // 设置核心线程数为CPU核心数，最大线程数为CPU核心数 * 2
//      int maximumPoolSize = cpuCores * 2;

      int corePoolSize = 5120;  // 设置为 5120 核心
      int maximumPoolSize = corePoolSize * 2;  // 可根据需要调整最大线程数

      long keepAliveTime = 60;
      TimeUnit unit = TimeUnit.SECONDS; // 空闲线程的存活时间
      executor = new ThreadPoolExecutor(
        corePoolSize, // 核心线程数
        maximumPoolSize, // 最大线程数
        keepAliveTime, unit,
        new LinkedBlockingQueue<>(imageFiles.size()), // 任务队列
        new ThreadPoolExecutor.CallerRunsPolicy() // 如果线程池已满，主线程执行任务
      );

      List<Callable<File>> tasks = new ArrayList<>();
      for (int i = 0; i < imageFiles.size(); i++) {
        final int index = i;
        tasks.add(() -> {
          File imageFile = imageFiles.get(index);
          File audioFile = audioFiles.get(index);
          int number = index + 1;
          File videoFile = new File(
            BatchCreateVideoCommonUtil.getVideoPath(fileName, lang)
              + fileName + "_" + MessageFormat.format(
              "{0,number,000}",
              number) + "_" + lang + ".mp4"
          );

          int attempt = 0;
//          while (attempt < MAX_RETRIES) {
          while (true) {
            try {
              // 计算AUDIO时长
              double duration = FfmpegUtil.getAudioDuration(audioFile);
              // 生成视频
              VideoCreatorUtil5.createVideo(imageFile, audioFile, videoFile,
                duration);
              // 超过1次才说尝试次数
              if (attempt > 0) {
                log.info("视频创建成功：{}，尝试次数：{}", videoFile.getName(),
                  attempt + 1);
              } else {
                log.info("视频创建成功：{}", videoFile.getName());
              }

              return videoFile; // 成功则返回视频文件
            } catch (Exception e) {
              attempt++;
              log.error("创建视频失败，文件：{}，尝试次数：{}，错误信息：{}",
                imageFile.getName(), attempt, e.getMessage());
              if (attempt >= MAX_RETRIES) {
                log.error("视频创建最终失败，文件：{}", imageFile.getName());
                return null; // 如果重试次数达到最大值，则返回null
              }
              try {
                Thread.sleep(RETRY_DELAY); // 重试前的延迟
              } catch (InterruptedException interruptedException) {
                log.error("线程休眠被中断：{}",
                  interruptedException.getMessage());
              }
            }
          }
          //return null; // 万一代码执行到这里也返回null
        });
      }

      // 提交所有任务并等待结果
      List<Future<File>> futures = executor.invokeAll(tasks);

      // 收集生成的视频文件
      for (Future<File> future : futures) {
        try {
          File videoFile = future.get();
          if (videoFile != null) {
            videoFiles.add(videoFile);
          }
        } catch (InterruptedException | ExecutionException e) {
          log.error("获取视频生成结果时出错", e);
        }
      }

    } catch (Exception e) {
      log.error("视频创建过程中出现异常", e);
    } finally {
      if (executor != null) {
        executor.shutdown(); // 关闭线程池
        try {
          if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow(); // 强制关闭
          }
        } catch (InterruptedException e) {
          executor.shutdownNow();
        }
      }
    }

    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("视频批量创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
    return videoFiles;
  }
}
