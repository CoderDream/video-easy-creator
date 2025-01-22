package com.coderdream.util.audio;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.ffmpeg.FfmpegUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * 音频文件夹时长工具类
 * 用于计算指定文件夹下所有音频文件的时长，并将结果写入文件。
 */
@Slf4j
public class AudioFolderDurationUtil {

    // 最大重试次数
    private static final int MAX_RETRIES = 5;
    // 每次重试的间隔时间（单位：毫秒）
    private static final long RETRY_DELAY = 1000;

    /**
     * 创建包含音频文件路径和时长的列表文件
     *
     * @param folderName 音频文件所在的文件夹路径
     * @return 包含音频文件路径和时长的临时列表文件
     */
    public static File createAudioDurationFileList(String folderName,
      String durationFileName) {
        log.info("开始创建音频文件时长列表，文件夹路径：{}", folderName);
        long startTime = System.currentTimeMillis(); // 记录开始时间

        List<String> totalFileNames = FileUtil.listFileNames(folderName);
        totalFileNames.sort(String::compareTo); // 按字母排序
        int size = totalFileNames.size();

        File durationFile = new File(durationFileName);

        // 创建 ThreadPoolExecutor 线程池，可更细粒度地控制线程池的参数
        int numberOfCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = new ThreadPoolExecutor(
                numberOfCores, // 核心线程数
                numberOfCores * 2, // 最大线程数
                60L, // 线程空闲时间
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), // 任务队列
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略，将任务添加到调用者的线程执行
        );

        List<String> durationLines = new ArrayList<>(); // 用于存储所有音频时长信息

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(durationFile))) {

            // 使用并行流处理每个音频文件，实现更高效的并发处理
            totalFileNames.parallelStream().forEach(fileName -> {
                executorService.submit(() -> {
                    File audioFile = new File(folderName + File.separator + fileName);
                    try {
                        String line = audioFile.getName() + "\t" + getAudioDurationWithRetry(audioFile) + " \n";
                        synchronized (durationLines) { // 同步添加到列表中
                            durationLines.add(line);
                        }
                        log.debug("音频文件时长计算完成：{}", audioFile.getName());
                    } catch (Exception e) {
                         log.error("处理音频文件 {} 时发生错误", audioFile.getName(), e);
                    }
                });
            });

            // 等待所有任务完成，并关闭线程池
            executorService.shutdown();
            if(!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)){
                log.error("线程池关闭超时！");
            }

            // 对时长信息进行排序
            durationLines.sort(Comparator.comparing(line -> line.split("\t")[0]));

            // 一次性写入排序后的数据
            for (String line : durationLines) {
                writer.write(line);
            }

        } catch (IOException | InterruptedException e) {
            log.error("创建音频时长文件时出错", e);
        } finally {

        }

        long endTime = System.currentTimeMillis(); // 记录结束时间
        String elapsedTime = formatElapsedTime(endTime - startTime); // 格式化耗时
        log.info("音频文件时长列表已创建：{}，耗时：{}", durationFile.getAbsolutePath(), elapsedTime);
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


    /**
     * 格式化时间差为 时:分:秒.毫秒 格式
     *
     * @param timeDiffMillis 时间差，单位毫秒
     * @return 格式化后的时间字符串
     */
    private static String formatElapsedTime(long timeDiffMillis) {
        long totalSeconds = timeDiffMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        long milliseconds = timeDiffMillis % 1000;
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }
}
