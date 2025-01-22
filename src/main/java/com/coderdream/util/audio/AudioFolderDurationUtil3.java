package com.coderdream.util.audio;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.ffmpeg.FfmpegUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AudioFolderDurationUtil3 {

  // 最大重试次数
  private static final int MAX_RETRIES = 5;
  // 每次重试的间隔时间（单位：毫秒）
  private static final long RETRY_DELAY = 1000;

  /**
   * 创建包含音频文件路径的列表文件
   *
   * @param folderName 音频文件列表
   * @return 包含音频文件路径的临时列表文件
   */
  public static File createAudioDurationFileList(String folderName,
    String durationFileName) {
    List<String> totalFileNames = FileUtil.listFileNames(folderName);
    totalFileNames.sort(String::compareTo); // 按字母排序
    int size = totalFileNames.size();

//    String durationFile  = folderName + durationFileName;
    File durationFile = new File(durationFileName);

    // 创建固定大小的线程池，线程数设置为 CPU 核心数，可以充分利用 CPU 资源
    int numberOfCores = Runtime.getRuntime().availableProcessors();
    ExecutorService executorService = Executors.newFixedThreadPool(
      numberOfCores);

    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(durationFile))) {
      // 创建一个CompletableFuture列表来跟踪所有任务
      List<CompletableFuture<Void>> futures = totalFileNames.stream()
        .map(fileName -> CompletableFuture.runAsync(() -> {
          File audioFile = new File(folderName + File.separator + fileName);
          try {
            String line =
              audioFile.getName() + "\t" + getAudioDurationWithRetry(audioFile)
                + " \n";
            synchronized (writer) {
              writer.write(line);
            }
          } catch (IOException e) {
            log.error("写入音频时长失败, 文件: {}", audioFile.getName(), e);
          }
        }, executorService))
        .toList();

      // 等待所有任务完成
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    } catch (IOException e) {
      log.error("创建音频时长文件时出错", e);
    } finally {
      // 等待所有任务完成并关闭线程池
      executorService.shutdown();
      try {
        if (!executorService.awaitTermination(Long.MAX_VALUE,
          TimeUnit.NANOSECONDS)) {
          log.error("线程池关闭超时！");
        }
      } catch (InterruptedException e) {
        log.error("等待线程池关闭时被中断！", e);
        Thread.currentThread().interrupt();
      }
    }

    log.info("音频文件时长列表已创建：{}", durationFile.getAbsolutePath());
    return durationFile;
  }

  /**
   * 获取音频文件的时长，并引入重试机制
   *
   * @param audioFile 音频文件
   * @return 音频时长（秒）
   */
  private static double getAudioDurationWithRetry(File audioFile) {
    int attempts = 0;
    while (attempts < MAX_RETRIES) {
      try {
        // 尝试获取音频时长
        return FfmpegUtil.getAudioDuration(audioFile);
      } catch (Exception e) {
        attempts++;
        log.error("获取音频时长失败，文件：{}，尝试次数：{}，错误信息：{}",
          audioFile.getName(), attempts, e.getMessage());
        if (attempts < MAX_RETRIES) {
          try {
            Thread.sleep(RETRY_DELAY); // 等待重试
          } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
          }
        } else {
          log.error("获取音频时长失败，超过最大重试次数，文件：{}",
            audioFile.getName());
        }
      }
    }
    return 0.0; // 如果失败，返回0.0
  }
}
