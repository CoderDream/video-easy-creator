package com.coderdream.util.video;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.*;

@Slf4j
public class VideoCreatorUtil7 {

    private static final int MAX_RETRIES = 10;
    private static final int RETRY_INTERVAL_MS = 5000; // 重试间隔，5秒
    private static final String RETRY_LOG_SUFFIX = "_retry_log.txt";
    private static final String FAILURE_LOG_SUFFIX = "_failure_log.txt";

    // ThreadPoolExecutor 参数
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors(); // 核心线程数
    private static final int MAXIMUM_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2; // 最大线程数
    private static final long KEEP_ALIVE_TIME = 60L; // 空闲线程存活时间 (秒)
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>(100); // 工作队列
    private static final RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy(); // 拒绝策略


    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            UNIT,
            WORK_QUEUE,
            HANDLER
    );

    /**
     * 使用FFmpeg将图片和无损音频合成一个视频 (支持多线程)
     *
     * @param imageFile 图片文件
     * @param audioFile 无损音频文件
     * @param videoFile 输出的视频文件
     * @param duration  视频时长
     */
    public static void createVideo(File imageFile, File audioFile, File videoFile, double duration) {
        executor.submit(() -> {
            int attempt = 0;
            while (attempt < MAX_RETRIES) {
                try {
                    PureCreateVideo.createVideoCore(imageFile, audioFile, videoFile, duration);
                    if (attempt > 0) {
                        logRetryAttempt(imageFile, attempt);
                    }
                    return; // 成功创建视频，退出循环
                } catch (Exception e) {
                    attempt++;
                    log.error("创建视频失败 (文件: {}, 尝试次数: {}): {}",
                            imageFile.getName(), attempt, e.getMessage());
                    if (attempt >= MAX_RETRIES) {
                        logFinalFailure(imageFile);
                    } else {
                        logRetryAttempt(imageFile, attempt);
                    }
                    try {
                        Thread.sleep(RETRY_INTERVAL_MS); // 增加重试间隔
                    } catch (InterruptedException ie) {
                        log.error("线程休眠被中断: {}", ie.getMessage());
                        Thread.currentThread().interrupt();
                        return; // 中断时，直接退出方法
                    }
                }
            }
        });
    }


    private static void logRetryAttempt(File imageFile, int attempt) {
        try {
            String fileName =
                    imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
            String logFileName = fileName + RETRY_LOG_SUFFIX;
            String logMessage = String.format("文件：%s，尝试次数：%d", imageFile.getName(), attempt);
            writeLogToFile(BatchCreateVideoCommonUtil.getVideoPath(fileName), logFileName, logMessage);
        } catch (IOException e) {
            log.error("记录重试日志时出错", e);
        }
    }


    private static void logFinalFailure(File imageFile) {
        try {
            String fileName =
                    imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
            String logFileName = fileName + FAILURE_LOG_SUFFIX;
            String logMessage = String.format("文件：%s，视频创建最终失败", imageFile.getName());
            writeLogToFile(BatchCreateVideoCommonUtil.getVideoPath(fileName), logFileName, logMessage);
        } catch (IOException e) {
            log.error("记录失败日志时出错", e);
        }
    }


    private static void writeLogToFile(String directoryPath, String logFileName, String message) throws IOException {
        Path logFilePath = Paths.get(directoryPath, logFileName);
        Files.createDirectories(logFilePath.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(logFilePath,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(message);
            writer.newLine();
        }
    }

    // 关闭线程池的方法
    public static void shutdown() {
        executor.shutdown();
        try {
            // 等待一段时间（例如60秒）让任务完成，或者强制关闭
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
