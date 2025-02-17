package com.coderdream.util.video;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class VideoCreatorUtil {

    private static final int MAX_RETRIES = 10;
    private static final String RETRY_LOG_SUFFIX = "_retry_log.txt";
    private static final String FAILURE_LOG_SUFFIX = "_failure_log.txt";

    /**
     * 使用FFmpeg将图片和无损音频合成一个视频
     *
     * @param imageFile 图片文件
     * @param audioFile 无损音频文件
     * @param videoFile 输出的视频文件
     * @param duration  视频时长
     */
    public static void createVideo(String imageFile, String audioFile, String videoFile,
      double duration) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                PureCreateVideo.createVideoCore(imageFile, audioFile, videoFile, duration);
                if (attempt > 0) {
                    logRetryAttempt(imageFile, attempt);
                }
                return;
            } catch (Exception e) {
                attempt++;
                log.error("创建视频失败 (文件: {}, 尝试次数: {}): {}",
                  imageFile.getName(), attempt, e.getMessage());  // 更详细的错误信息
                if (attempt >= MAX_RETRIES) {
                    logFinalFailure(imageFile);
                } else {
                    logRetryAttempt(imageFile, attempt);
                }
                try {
                    Thread.sleep(1000); // 稍作等待后重试
                } catch (InterruptedException ie) {
                    log.error("线程休眠被中断: {}", ie.getMessage());
                    Thread.currentThread().interrupt();  // 重新设置中断标志
                    return;  // 中断时，直接退出方法
                }
            }
        }
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
}
