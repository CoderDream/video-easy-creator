package com.coderdream.util.audio;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.ffmpeg.FfmpegUtil;
import com.coderdream.util.video.BatchCreateVideoCommonUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class AudioDurationUtil {

    // 最大重试次数
    private static final int MAX_RETRIES = 5;
    // 每次重试的间隔时间（单位：毫秒）
    private static final long RETRY_DELAY = 1000;

    /**
     * 创建包含音频文件路径的列表文件
     *
     * @param fileName 音频文件列表
     * @return 包含音频文件路径的临时列表文件
     */
    public static File createAudioDurationFileList(String fileName) {
        // 获取音频文件列表
        List<File> audioCnFiles = BatchCreateVideoCommonUtil.getAudioFiles(fileName, CdConstants.LANG_CN);
        List<File> audioEnFiles = BatchCreateVideoCommonUtil.getAudioFiles(fileName, CdConstants.LANG_EN);

        List<String> totalFileNames = new ArrayList<>();
        if (audioCnFiles.isEmpty() || audioEnFiles.isEmpty() || audioCnFiles.size() != audioEnFiles.size()) {
            log.error("音频文件列表为空或大小不相等，无法合并");
            return null;
        } else {
            totalFileNames.addAll(audioCnFiles.stream().map(File::getAbsolutePath).toList());
            totalFileNames.addAll(audioEnFiles.stream().map(File::getAbsolutePath).toList());
            log.info("音频文件列表不为空，继续合并");
        }

        // 按字母排序
        totalFileNames.sort(String::compareTo);

        int size = totalFileNames.size();

        // 创建合并文件列表
        String durationFileName = BatchCreateVideoCommonUtil.getAudioPath(fileName) + fileName + "_duration.txt";
        File durationFile = new File(durationFileName);

        // 创建线程池，指定核心线程池大小与最大线程池大小
        ExecutorService executorService = new ThreadPoolExecutor(
                4,  // 核心线程池大小
                8,  // 最大线程池大小
                60L, TimeUnit.SECONDS,  // 空闲线程最大存活时间
                new LinkedBlockingQueue<>(),  // 工作队列
                new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略：让调用者线程执行任务
        );

        // 创建任务列表，用于保存各个音频时长计算任务
        List<Future<String>> futures = new ArrayList<>();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(durationFile))) {
            for (int i = 0; i < size; i++) {
                File audioFile = new File(totalFileNames.get(i));
                // 提交任务，计算音频时长并保存到未来任务中
                futures.add(executorService.submit(() -> {
                    return audioFile.getName() + "\t" + getAudioDurationWithRetry(audioFile) + " \n";
                }));
            }

            // 等待所有任务执行完并写入文件
            for (Future<String> future : futures) {
                try {
                    // 获取任务结果并写入文件
                    writer.write(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    log.error("计算音频时长时出错", e);
                }
            }
        } catch (IOException e) {
            log.error("创建音频时长文件时出错", e);
        } finally {
            // 关闭线程池
            executorService.shutdown();
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
                    log.error("获取音频时长失败，超过最大重试次数，文件：{}", audioFile.getName());
                }
            }
        }
        return 0.0; // 如果失败，返回0.0
    }
}
