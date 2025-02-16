package com.coderdream.util.video;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.coderdream.util.cd.CdConstants.OS_MAC;
import static com.coderdream.util.cd.CdConstants.OS_WINDOWS;

@Slf4j
public class VideoCreatorUtil8 {

    private static final int MAX_RETRIES = 3; // 减少重试次数，加速排错
    private static final int RETRY_INTERVAL_MS = 10000; // 重试间隔，10秒
    private static final String RETRY_LOG_SUFFIX = "_retry_log.txt";
    private static final String FAILURE_LOG_SUFFIX = "_failure_log.txt";
    private static final String FFMPEG_LOG_SUFFIX = "_ffmpeg_log.txt";

    // ThreadPoolExecutor 参数
    private static final int CORE_POOL_SIZE = Math.max(2, Runtime.getRuntime().availableProcessors() / 2); // 核心线程数
    private static final int MAXIMUM_POOL_SIZE = Runtime.getRuntime().availableProcessors(); // 最大线程数
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
            String fileName = imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
            try {
                // 参数校验
                if (duration <= 0) {
                    log.error("文件：{}，视频时长必须大于 0", imageFile.getName());
                    logFinalFailure(imageFile, "视频时长必须大于 0");
                    return;
                }
                createVideoWithRetry(imageFile, audioFile, videoFile, duration);

            } catch (Exception e) {
                log.error("文件：{}，创建视频出现严重错误", imageFile.getName(), e);
                logFinalFailure(imageFile, "创建视频出现严重错误：" + e.getMessage());
            }
        });
    }

    private static void createVideoWithRetry(File imageFile, File audioFile, File videoFile, double duration) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            attempt++;
            try {
                log.info("文件：{}，开始第 {} 次尝试创建视频", imageFile.getName(), attempt);
                createVideoCore(imageFile, audioFile, videoFile, duration);
                log.info("文件：{}，第 {} 次尝试成功创建视频", imageFile.getName(), attempt);
                return; // 成功创建视频，退出循环
            } catch (Exception e) {
                log.error("文件：{}，第 {} 次尝试创建视频失败: {}", imageFile.getName(), attempt, e.getMessage(), e);
                logRetryAttempt(imageFile, attempt, e.getMessage());
                try {
                    Thread.sleep(RETRY_INTERVAL_MS); // 增加重试间隔
                } catch (InterruptedException ie) {
                    log.warn("文件：{}，线程休眠被中断: {}", imageFile.getName(), ie.getMessage());
                    Thread.currentThread().interrupt();
                    return; // 中断时，直接退出方法
                }
            }
        }
        log.error("文件：{}，达到最大重试次数，视频创建最终失败", imageFile.getName());
        logFinalFailure(imageFile, "达到最大重试次数，视频创建最终失败");
    }

    private static void createVideoCore(File imageFile, File audioFile, File videoFile,
                                        double duration) throws Exception {
        long startTime = System.currentTimeMillis();

        // 校验文件是否存在
        if (!imageFile.exists() || !audioFile.exists()) {
            throw new IOException("图片或音频文件不存在");
        }

        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-y"); // 覆盖输出文件（如果存在）
        command.add("-loop");
        command.add("1"); // 循环输入图片
        command.add("-framerate");
        command.add(String.format("%.6f", 1.0 / duration)); // 根据时长计算帧率
        command.add("-t");
        command.add(String.format("%.2f", duration)); // 设置视频时长
        command.add("-i");
        command.add(imageFile.getAbsolutePath()); // 输入图片文件
        command.add("-i");
        command.add(audioFile.getAbsolutePath()); // 输入音频文件
        command.add("-s");
        command.add("3840x2160"); // 设置分辨率为 4K (3840x2160)

        // 根据操作系统选择不同的编码器和参数
        String os = OperatingSystem.getOS();
        if (OS_WINDOWS.equals(os)) {
            // Windows 使用 NVIDIA 硬件加速 (h264_nvenc)
            command.add("-c:v");
            command.add("h264_nvenc");
            command.add("-preset");
            command.add("p4"); // 使用 p4 预设.  可以根据需要调整 (p1 - p7, slow, medium, fast, etc.)
            command.add("-b:v"); //设置比特率
            command.add("10000k");
            //command.add("-rc");  // 如果需要, 可以尝试使用 VBR (可变比特率)
            //command.add("vbr");
            //command.add("-cq");
            //command.add("19");

        } else if (OS_MAC.equals(os)) {
            // macOS 使用 VideoToolbox 硬件加速 (h264_videotoolbox)
            command.add("-c:v");
            command.add("h264_videotoolbox");
            command.add("-q:v");
            command.add("40"); // 调整质量值 (原60，现在尝试40，可以进一步调整)

            //  更精细的码率控制 (可选, 如果 -q:v 效果不好)
            // command.add("-b:v");
            // command.add("10M");  // 目标比特率 (例如 10 Mbps)
            // command.add("-maxrate:v");
            // command.add("12M"); // 最大比特率
            // command.add("-bufsize:v");
            // command.add("24M"); // 缓冲区大小

        } else {
            // 其他操作系统使用 libx264 软件编码
            command.add("-c:v");
            command.add("libx264");
            command.add("-preset");
            command.add("medium"); // 可以是 ultrafast, superfast, veryfast, faster, fast, medium, slow, slower, veryslow
            command.add("-crf");
            command.add("19");     // 0-51, 0 是无损, 23 是默认值, 18-28 是常用范围. 值越小质量越高.
        }

        command.add("-c:a");
        command.add("aac"); // 使用 AAC 音频编码
        command.add("-ac");
        command.add("2");    // 设置音频通道数为 2 (立体声)
        command.add("-shortest"); // 以最短的输入流（音频或视频）为准结束
        command.add(videoFile.getAbsolutePath()); // 输出视频文件

        String commandString = String.join(" ", command);
        log.info("执行 FFmpeg 命令: {}", commandString);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 将错误输出合并到标准输出

        Process process = processBuilder.start();

        // 读取 FFmpeg 的输出（包括标准输出和错误输出）
        String ffmpegOutput = readProcessOutput(process);
        logFfmpegOutput(imageFile, ffmpegOutput); // 记录 FFmpeg 输出到单独的文件

        int exitCode = process.waitFor(); // 等待 FFmpeg 进程结束

        if (exitCode == 0) {
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime;
            log.info("视频创建成功 (文件: {}), 耗时: {}", videoFile.getAbsolutePath(), CdTimeUtil.formatDuration(durationMillis));
        } else {
            throw new Exception("FFmpeg 进程执行失败，退出代码: " + exitCode + ", 输出: " + ffmpegOutput);
        }
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

    private static void logFfmpegOutput(File imageFile, String ffmpegOutput) {
        try {
            String fileName = imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
            String logFileName = fileName + FFMPEG_LOG_SUFFIX;
            writeLogToFile(BatchCreateVideoCommonUtil.getVideoPath(fileName), logFileName, ffmpegOutput);
        } catch (IOException e) {
            log.error("记录 FFmpeg 输出日志时出错", e);
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