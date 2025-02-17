package com.coderdream.util.video.demo02;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.ffmpeg.FfmpegUtil;
import com.coderdream.util.video.PureCreateVideo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FFmpegParallelOptimized {

  public static void process(String imagePath, String audioPath,
    String videoPath) {

    // --- 线程池配置 ---
    int corePoolSize = 2; // 8;  // 核心线程数：与 CPU 核心数相同
    int maximumPoolSize = 4;// 32; // 最大线程数：允许的峰值线程数，根据实际情况调整
    long keepAliveTime = 60; // 空闲线程存活时间（秒）：超过核心线程数的线程，空闲时间超过此值会被回收
    TimeUnit unit = TimeUnit.SECONDS;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(
      8); //  100 工作队列：用于存放待执行的任务，这里使用有界队列，防止任务过多导致内存溢出
    ThreadFactory threadFactory = Executors.defaultThreadFactory(); // 线程工厂：用于创建新线程，可以使用自定义的线程工厂
    RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy(); // 拒绝策略：当任务无法被提交到线程池时（队列已满且线程数达到最大），由提交任务的线程直接执行

    // 创建 ThreadPoolExecutor
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
      corePoolSize,
      maximumPoolSize,
      keepAliveTime,
      unit,
      workQueue,
      threadFactory,
      handler
    );

    // --- 预热线程池 (可选) ---
    // 预先创建核心线程，减少首次任务提交时的延迟。
    executor.prestartAllCoreThreads();

    // --- FFmpeg 任务 ---
    // 假设你有多个输入文件和输出文件
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

    // 为每个 FFmpeg 命令创建一个任务 (Callable 可以获取返回值)
    List<Future<Integer>> futures = new ArrayList<>(); //用于存储任务的执行结果
    for (int i = 0; i < imagePathNameList.size(); i++) {
      String imagePathName = imagePath + File.separator
        + imagePathNameList.get(i);
      String audioFileName = audioPath + File.separator
        + audioPathNameList.get(i);
      String videoFileName =
        videoPath + CdFileUtil.getPureFileNameWithoutExtensionWithPath(
          audioFileName) + ".mp4";

      Callable<Integer> task = () -> {  //使用Callable
        try {
          File videoFile = new File(videoFileName);
          if (!CdFileUtil.isFileEmpty(videoFileName)) {
            log.info("视频文件已存在，无需重新生成，{}", videoFileName);
            return -1; // 如果文件已存在，则返回 -1
          }
          // 计算AUDIO时长
          double duration = FfmpegUtil.getAudioDuration(
            new File(audioFileName));
          PureCreateVideo.createVideoCore(new File(imagePathName),
            new File(audioFileName), videoFile, duration);
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
          return -1; // 出现异常时返回 -1
        }
        return 0;
      };

      // 提交任务到线程池
      Future<Integer> future = executor.submit(task);
      futures.add(future); //将future添加到列表中
    }

    // --- 获取任务结果 (可选) ---
    for (Future<Integer> future : futures) {
      try {
        Integer exitCode = null; // 获取任务的返回值（阻塞，直到任务完成）
        try {
          exitCode = future.get();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        System.out.println("Task completed with exit code: " + exitCode);
      } catch (ExecutionException e) {
        System.err.println("Task execution failed: " + e.getCause());
      }
    }

    // --- 关闭线程池 ---
    executor.shutdown(); // 停止接收新任务
    try {
      // 等待一段时间，让已提交的任务完成（或者直到超时）
      if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        executor.shutdownNow(); // 如果超时，尝试强制关闭
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
          System.err.println("Pool did not terminate");
        }
      }
    } catch (InterruptedException ie) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }

    System.out.println("All FFmpeg tasks completed.");
  }
}
