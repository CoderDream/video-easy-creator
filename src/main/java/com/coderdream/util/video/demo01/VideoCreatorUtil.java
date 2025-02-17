package com.coderdream.util.video.demo01;

import com.coderdream.util.video.BatchCreateVideoCommonUtil;
import com.coderdream.util.video.PureCreateVideo;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class VideoCreatorUtil {

    private static final int MAX_RETRIES = 10;
    private static final long RETRY_INTERVAL_MS = 3000; // 3秒重试
    private static final String RETRY_LOG_SUFFIX = "_retry_log.txt";
    private static final String FAILURE_LOG_SUFFIX = "_failure_log.txt";
    private static final String FFMPEG_OUTPUT_LOG_SUFFIX = "_ffmpeg_output.log";

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
            attempt++;
            try {
                log.info("开始第 {} 次尝试创建视频：{}", attempt, new File(videoFile).getAbsolutePath());
                PureCreateVideo.createVideoCore(imageFile, audioFile, videoFile, duration);
                log.info("视频创建成功：{}", new File(videoFile).getAbsolutePath());
                return; // 成功，结束循环
//            } catch (FileNotFoundException e) {
//                log.error("文件未找到异常：{}", e.getMessage());
//                logFinalFailure(new File(imageFile), "文件未找到异常: " + e.getMessage());
//                return; // 文件找不到，不再重试
//            } catch (IOException e) {
//                log.error("IO异常 (文件: {}, 尝试次数: {}): {}", new File(imageFile).getName(), attempt, e.getMessage(), e);
//                logRetryAttempt(new File(imageFile), attempt, e.getMessage());
//                sleepWithInterrupt(RETRY_INTERVAL_MS);
//            } catch (InterruptedException e) {
//                log.warn("线程休眠被中断：{}", e.getMessage());
//                Thread.currentThread().interrupt();
//                return; // 中断，退出
            } catch (Exception e) {
                log.error("创建视频失败 (文件: {}, 尝试次数: {}): {}", new File(imageFile).getName(), attempt, e.getMessage(), e);
                logRetryAttempt(new File(imageFile), attempt, e.getMessage());
                sleepWithInterrupt(RETRY_INTERVAL_MS);
            }
        }

        log.error("达到最大重试次数，视频创建最终失败：{}",new File(videoFile).getAbsolutePath());
        logFinalFailure(new File(imageFile), "达到最大重试次数");
    }

    //封装sleep操作
    private static void sleepWithInterrupt(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            log.error("线程休眠被中断: {}", ie.getMessage());
            Thread.currentThread().interrupt();  // 重新设置中断标志
        }
    }




    public static String readProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }
        return output.toString();
    }

    private static void logFfmpegOutput(File imageFile, String output) {
        String fileName = imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
        String logFileName = fileName + FFMPEG_OUTPUT_LOG_SUFFIX;
        try {
            writeLogToFile(BatchCreateVideoCommonUtil.getVideoPath(fileName), logFileName, output);
        } catch (IOException e) {
            log.error("保存 FFmpeg 输出到文件失败", e);
        }
    }

    private static void logRetryAttempt(File imageFile, int attempt, String errorMessage) {
        try {
            String fileName = imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
            String logFileName = fileName + RETRY_LOG_SUFFIX;
            String logMessage = String.format("文件：%s，尝试次数：%d，错误信息：%s", imageFile.getName(), attempt, errorMessage);
            writeLogToFile(BatchCreateVideoCommonUtil.getVideoPath(fileName), logFileName, logMessage);
        } catch (IOException e) {
            log.error("记录重试日志时出错", e);
        }
    }

    private static void logFinalFailure(File imageFile, String reason) {
        try {
            String fileName = imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
            String logFileName = fileName + FAILURE_LOG_SUFFIX;
            String logMessage = String.format("文件：%s，视频创建最终失败，原因：%s", imageFile.getName(), reason);
            writeLogToFile(BatchCreateVideoCommonUtil.getVideoPath(fileName), logFileName, logMessage);
        } catch (IOException e) {
            log.error("记录最终失败日志时出错", e);
        }
    }

    private static void writeLogToFile(String directoryPath, String logFileName, String message) throws IOException {
        Path logFilePath = Paths.get(directoryPath, logFileName);
        Files.createDirectories(logFilePath.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(logFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(message);
            writer.newLine();
        }
    }
}
