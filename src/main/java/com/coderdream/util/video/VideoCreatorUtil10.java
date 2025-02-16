package com.coderdream.util.video;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class VideoCreatorUtil10 {

    public static final String OS_WINDOWS = "Windows";
    public static final String OS_MAC = "Mac";
    private static final int MAX_RETRIES = 10;
    private static final long RETRY_INTERVAL_MS = 3000;
    private static final String RETRY_LOG_SUFFIX = "_retry_log.txt";
    private static final String FAILURE_LOG_SUFFIX = "_failure_log.txt";
    private static final String FFMPEG_OUTPUT_LOG_SUFFIX = "_ffmpeg_output.log";
    private static final String DEFAULT_VIDEO_CODEC = "libx264";
    private static final String DEFAULT_AUDIO_CODEC = "aac";
    private static final int DEFAULT_AUDIO_SAMPLE_RATE = 44100;
    private static final int DEFAULT_AUDIO_CHANNELS = 2;
    private static final int DEFAULT_VIDEO_FRAMERATE = 30;

    // 根据 M4 芯片特性调整线程池参数 (假设核心数为8)
    private static final int CORE_POOL_SIZE = 8; // M4核心数
    private static final int MAXIMUM_POOL_SIZE = 16; // 允许的最大线程数
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>(100);
    private static final RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            UNIT,
            WORK_QUEUE,
            HANDLER
    );


    /**
     * 使用FFmpeg将图片和无损音频合成一个视频
     *
     * @param imageFile 图片文件
     * @param audioFile 无损音频文件
     * @param videoFile 输出的视频文件
     * @param duration  视频时长
     */
    public static void createVideo(File imageFile, File audioFile, File videoFile, double duration) {
        executor.submit(() -> { // 提交到线程池
            long startTime = System.currentTimeMillis(); // 记录开始时间

            // 校验输入文件是否存在
            if (!isFileValid(imageFile) || !isFileValid(audioFile)) {
                log.error("图片文件或音频文件不存在，无法创建视频");
                logFinalFailure(imageFile, "图片文件或音频文件不存在");
                return;
            }

            // 校验文件格式是否支持
            if (!isImageFormatSupported(imageFile)) {
                log.error("不支持的图片格式: {}", imageFile.getAbsolutePath());
                logFinalFailure(imageFile, "不支持的图片格式");
                return;
            }

            if (!isAudioFormatSupported(audioFile)) {
                log.error("不支持的音频格式: {}", audioFile.getAbsolutePath());
                logFinalFailure(imageFile, "不支持的音频格式");
                return;
            }

            int attempt = 0;
            while (attempt < MAX_RETRIES) {
                attempt++;
                try {
                    log.info("开始第 {} 次尝试创建视频：{}", attempt, videoFile.getAbsolutePath());
                    createVideoCore(imageFile, audioFile, videoFile, duration);
                    log.info("视频创建成功：{}", videoFile.getAbsolutePath());

                    // 耗时统计
                    long endTime = System.currentTimeMillis();
                    long durationMillis = endTime - startTime;
                    log.info("视频创建成功 (文件: {}), 耗时: {}", videoFile.getAbsolutePath(), CdTimeUtil.formatDuration(durationMillis));

                    return; // 成功，结束循环

                } catch (Exception e) {
                    log.error("创建视频失败 (文件: {}, 尝试次数: {}): {}", imageFile.getName(), attempt, e.getMessage(), e);
                    logRetryAttempt(imageFile, attempt, e.getMessage());
                    sleepWithInterrupt(RETRY_INTERVAL_MS);
                }
            }

            log.error("达到最大重试次数，视频创建最终失败：{}", videoFile.getAbsolutePath());
            logFinalFailure(imageFile, "达到最大重试次数");
        });
    }

    // 校验文件是否存在且可读
    private static boolean isFileValid(File file) {
        return file != null && file.exists() && file.canRead();
    }

    // 检查图片文件格式
    private static boolean isImageFormatSupported(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            return image != null; // 如果能读取到图像，说明格式支持
        } catch (IOException e) {
            log.warn("不支持的图片格式: {}", imageFile.getAbsolutePath(), e);
            return false;
        }
    }

    // 检查音频文件格式
    private static boolean isAudioFormatSupported(File audioFile) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            return true; // 如果能获取到音频格式，说明支持
        } catch (UnsupportedAudioFileException | IOException e) {
            log.warn("不支持的音频格式: {}", audioFile.getAbsolutePath(), e);
            return false;
        }
    }

    //封装sleep操作
    private static void sleepWithInterrupt(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            log.error("线程休眠被中断: {}", ie.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private static void createVideoCore(File imageFile, File audioFile, File videoFile, double duration) throws Exception {
        List<String> command = buildFFmpegCommand(imageFile, audioFile, videoFile, duration);

        String commandString = String.join(" ", command);
        log.info("执行 FFmpeg 命令: {}", commandString);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        String ffmpegOutput = readProcessOutput(process);
        logFfmpegOutput(imageFile, ffmpegOutput);

        int exitCode = process.waitFor();

        if (exitCode == 0) {
            // 移除耗时统计
            // long endTime = System.currentTimeMillis();  // 移除，移到线程submit处
            // long durationMillis = endTime - startTime;
            // log.info("视频创建成功 (文件: {}), 耗时: {}", videoFile.getAbsolutePath(), CdTimeUtil.formatDuration(durationMillis));
        } else {
            throw new Exception("FFmpeg 进程执行失败，退出代码: " + exitCode + ", FFmpeg 输出: " + ffmpegOutput);
        }
    }

    //构建FFmpeg命令
    private static List<String> buildFFmpegCommand(File imageFile, File audioFile, File videoFile, double duration) {
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-y"); // 覆盖输出文件
        command.add("-loop");
        command.add("1");  // 循环图片
        command.add("-framerate");
        command.add(String.valueOf(DEFAULT_VIDEO_FRAMERATE));
        command.add("-t");
        command.add(String.format("%.2f", duration)); // 视频时长
        command.add("-i");
        command.add(imageFile.getAbsolutePath());
        command.add("-i");
        command.add(audioFile.getAbsolutePath());

        // 视频编码器
        String os = OperatingSystem.getOS();
        String videoCodec = DEFAULT_VIDEO_CODEC;
        if (OS_WINDOWS.equals(os)) {
            videoCodec = "h264_nvenc"; // Windows NVIDIA
        } else if (OS_MAC.equals(os)) {
            videoCodec = "h264_videotoolbox"; // macOS VideoToolbox
        }
        command.add("-c:v");
        command.add(videoCodec);

        // 视频编码器参数
        if (videoCodec.equals("h264_nvenc")) {
            command.add("-preset");
            command.add("p4");
            command.add("-b:v");
            command.add("10000k");
        } else if (videoCodec.equals("h264_videotoolbox")) {
            command.add("-q:v");
            command.add("60");
        } else {
            command.add("-preset");
            command.add("medium");
            command.add("-crf");
            command.add("23");
        }

        // 音频编码器和参数 (明确指定)
        command.add("-c:a");
        command.add(DEFAULT_AUDIO_CODEC); // 明确指定音频编码器
        command.add("-ac");
        command.add(String.valueOf(DEFAULT_AUDIO_CHANNELS));
        command.add("-ar");
        command.add(String.valueOf(DEFAULT_AUDIO_SAMPLE_RATE));

        command.add("-shortest"); // 以最短流为准

        command.add(videoFile.getAbsolutePath());
        return command;
    }

    private static String readProcessOutput(Process process) throws IOException {
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