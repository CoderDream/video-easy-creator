package com.coderdream.util.video;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.awt.image.BufferedImage;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import lombok.extern.slf4j.Slf4j;

//import static com.coderdream.util.cd.Constants.OS_MAC;
//import static com.coderdream.util.cd.Constants.OS_WINDOWS;

@Slf4j
public class VideoCreatorUtil11 {

    public static final String OS_WINDOWS = "Windows";
    public static final String OS_MAC = "Mac";
    private static final int MAX_RETRIES = 3;
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
    private static final int MAXIMUM_POOL_SIZE = 8; // 限制最大并发任务数
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>(100);

    // 自定义 RejectedExecutionHandler
    private static final RejectedExecutionHandler HANDLER = (r, executor1) -> {
        if (!executor1.isShutdown()) {
            log.warn("任务被拒绝：{}，线程池状态：[active: {}, queue: {}, completed: {}]",
                    r.toString(),
                    executor1.getActiveCount(),
                    executor1.getQueue().size(),
                    executor1.getCompletedTaskCount());
        }
    };
    //private static final RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.DiscardPolicy(); // 尝试 DiscardPolicy
    //private static final RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

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
        log.info("提交任务：{}，活跃线程数：{}，等待队列：{}，已完成任务：{}",
                videoFile.getAbsolutePath(),
                executor.getActiveCount(),
                executor.getQueue().size(),
                executor.getCompletedTaskCount());

        executor.submit(() -> {
            long startTime = System.currentTimeMillis();
            log.info("任务开始执行：{}", videoFile.getAbsolutePath());

            try {
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

                // 校验音频文件
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

            } catch (Exception e) { // 捕获更外层的异常
                log.error("任务执行过程中发生异常：{}", videoFile.getAbsolutePath(), e);
            } finally {
                log.info("任务执行完成：{}", videoFile.getAbsolutePath());
            }
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
            // 等待所有任务完成或超时
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                log.warn("线程池关闭超时，尝试强制关闭");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("线程池关闭被中断", e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            log.info("线程池已关闭，活跃线程数：{}，等待队列：{}，已完成任务：{}",
                    executor.getActiveCount(),
                    executor.getQueue().size(),
                    executor.getCompletedTaskCount());
        }
    }

    //示例用法
    public static void main(String[] args) throws InterruptedException {
        // 假设有多个视频需要创建
        File imageFile = new File("path/to/your/image.jpg");
        File audioFile = new File("path/to/your/audio.mp3");
        String outputPath = "path/to/output/";

        // 确保输出目录存在
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            if (outputDir.mkdirs()) {
                log.info("成功创建输出目录：{}", outputPath);
            } else {
                log.error("创建输出目录失败：{}", outputPath);
                return; // 目录创建失败，直接退出
            }
        }

        for (int i = 0; i < 88; i++) { // 修改循环次数为 88
            String videoFileName = String.format("video_%03d.mp4", i); // 确保文件名唯一
            File videoFile = new File(outputPath + videoFileName);
            log.info("提交视频创建任务：{}", videoFile.getAbsolutePath());  // 打印提交任务的日志
            createVideo(imageFile, audioFile, videoFile, 10.0); // 创建88个视频
        }

        // 等待所有任务执行完成
        Thread.sleep(5000); // 确保任务被提交
        shutdown();
        System.out.println("All tasks submitted. Shutting down the executor.");
    }
}
