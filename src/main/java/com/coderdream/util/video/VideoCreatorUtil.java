package com.coderdream.util.video;

import com.coderdream.util.cd.CdTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

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
    public static void createVideo(File imageFile, File audioFile, File videoFile,
        double duration) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                createVideoCore(imageFile, audioFile, videoFile, duration);
                if (attempt > 0) {
                    logRetryAttempt(imageFile, attempt);
                }
                return;
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
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    log.error("线程休眠被中断: {}", ie.getMessage());
                    Thread.currentThread().interrupt();  // 重新设置中断标志
                    return;  // 中断时，直接退出方法
                }
            }
        }
    }

    private static void createVideoCore(File imageFile, File audioFile, File videoFile,
        double duration) throws Exception {
        long startTime = System.currentTimeMillis();
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-y");
        command.add("-loop");
        command.add("1");
        command.add("-framerate");
        command.add("60");
        command.add("-t");
        command.add(String.format("%.2f", duration));
        command.add("-i");
        command.add(imageFile.getAbsolutePath());
        command.add("-i");
        command.add(audioFile.getAbsolutePath());
        command.add("-s");
        command.add("3840x2160");
        command.add("-c:v");
        command.add("h264_nvenc");
        command.add("-preset");
        command.add("p4"); // 使用 p4 预设
        command.add("-rc");
        command.add("vbr");  // 使用 vbr 可变比特率
        command.add("-cq");
        command.add("19");
        command.add("-b:v");
        command.add("10000k");
        command.add("-c:a");
        command.add("aac");
        command.add("-ac");
        command.add("2");
        command.add("-shortest");
        command.add(videoFile.getAbsolutePath());

        String commandString = String.join(" ", command);
        log.info("执行 FFmpeg 命令: {}", commandString);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 这里不打印 ffmpeg 的输出
                log.info("{}", line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode == 0) {
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime;
            log.info("视频创建成功 (文件: {}), 耗时: {}", videoFile.getAbsolutePath(),
                CdTimeUtil.formatDuration(durationMillis));
        } else {
            throw new Exception("FFmpeg 进程执行失败，退出代码: " + exitCode);
        }
    }


    private static void logRetryAttempt(File imageFile, int attempt) {
        try {
             String fileName =
                imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
            String logFileName = fileName + RETRY_LOG_SUFFIX;
            String logMessage = String.format("文件：%s，尝试次数：%d", imageFile.getName(), attempt);
             writeLogToFile(BatchCreateVideoCommonUtil.getVideoPath(fileName),logFileName, logMessage);
        } catch (IOException e) {
            log.error("记录重试日志时出错", e);
        }
    }


    private static void logFinalFailure(File imageFile) {
        try {
           String fileName =
                imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
           String logFileName =  fileName + FAILURE_LOG_SUFFIX;
            String logMessage = String.format("文件：%s，视频创建最终失败", imageFile.getName());
              writeLogToFile(BatchCreateVideoCommonUtil.getVideoPath(fileName),logFileName, logMessage);
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
