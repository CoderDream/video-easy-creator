package com.coderdream.util.video;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 使用 ffmpeg 将图片和音频合成 MP4 视频
 */
@Slf4j
public class FfmpegVideoGenerator1 {

    private static final int MAX_RETRIES = 10; // 最大重试次数

    /**
     * 将图片和音频合成 MP4 视频
     *
     * @param imagePath     图片路径
     * @param audioFileName 音频文件路径 (wav格式)
     * @param videoFileName 输出视频文件路径 (mp4格式)
     * @return 生成视频耗时，格式为 时:分:秒.毫秒
     * @throws IOException 如果文件操作或 ffmpeg 执行过程中出现错误
     */
    public String generateVideo(String imagePath, String audioFileName, String videoFileName) throws IOException {
        Instant start = Instant.now(); // 记录方法开始时间

        // 检查输入文件是否存在
        Path imagePathFile = Paths.get(imagePath);
        if (!Files.exists(imagePathFile)) {
            log.error("图片文件不存在: {}", imagePath);
            throw new IOException("图片文件不存在: " + imagePath);
        }
        Path audioPathFile = Paths.get(audioFileName);
        if (!Files.exists(audioPathFile)) {
             log.error("音频文件不存在: {}", audioFileName);
            throw new IOException("音频文件不存在: " + audioFileName);
        }

        int retryCount = 0;
        boolean success = false;
        while (retryCount < MAX_RETRIES) {
            try {
                executeFfmpegCommand(imagePath, audioFileName, videoFileName);
                success = true;
                break; // 执行成功，退出循环
            } catch (IOException e) {
                retryCount++;
                log.error("ffmpeg执行失败 (第 {} 次重试), 原因: {}", retryCount, e.getMessage());
                if (retryCount == MAX_RETRIES) {
                    log.error("达到最大重试次数，放弃生成视频");
                    throw e; // 如果达到最大重试次数，抛出异常
                }
                try {
                    TimeUnit.SECONDS.sleep(1); // 等待 1 秒后重试
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("等待重试被中断", ie);
                    throw new IOException("等待重试被中断", ie);
                }

            }
        }

        Instant end = Instant.now(); // 记录方法结束时间
        Duration duration = Duration.between(start, end);
        String elapsedTime = formatDuration(duration);
        log.info("视频生成成功，耗时: {}", elapsedTime);

        return elapsedTime; // 返回方法执行耗时
    }


    /**
     * 执行 ffmpeg 命令
     *
     * @param imagePath     图片路径
     * @param audioFileName 音频文件路径
     * @param videoFileName 输出视频文件路径
     * @throws IOException 如果 ffmpeg 执行过程中出现错误
     */
    private void executeFfmpegCommand(String imagePath, String audioFileName, String videoFileName) throws IOException {
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-y");
        command.add("-loop");
        command.add("1");
        command.add("-i");
        command.add(imagePath);
        command.add("-i");
        command.add(audioFileName);
        command.add("-c:v");
        command.add("h264_nvenc"); // 使用英伟达编码器
        command.add("-preset");
        command.add("fast");
        command.add("-tune");
        command.add("stillimage");
        command.add("-vf");
        command.add("scale=3840:2160"); // 4K 分辨率
        command.add("-r");
        command.add("60"); // 60 帧
        command.add("-c:a");
        command.add("copy"); // 直接复制音频流，因为是wav格式
        command.add("-shortest"); //以短的那个为准
        command.add("-y"); // 覆盖输出文件
        command.add(videoFileName);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 将错误输出也重定向到标准输出

        try {
            Process process = processBuilder.start(); // 启动进程

            // 读取 ffmpeg 输出信息
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("ffmpeg output: {}", line); // 输出 ffmpeg 的日志
                }
            }

            int exitCode = process.waitFor(); // 等待进程执行结束
            if (exitCode != 0) {
                log.error("ffmpeg执行失败，退出代码: {}", exitCode);
                throw new IOException("ffmpeg执行失败，退出代码: " + exitCode);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("ffmpeg执行被中断", e);
            throw new IOException("ffmpeg执行被中断", e);
        }
    }


    /**
     * 将 Duration 对象格式化为 时:分:秒.毫秒 字符串
     *
     * @param duration Duration 对象
     * @return 格式化的时间字符串
     */
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        long milliseconds = duration.toMillisPart();
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }


    public static void main(String[] args) {
        String imagePath = "D:\\0000\\bgmusic\\background.png";
        String audioFileName = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.wav";
        String videoFileName = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.mp4";

        FfmpegVideoGenerator1 generator = new FfmpegVideoGenerator1();
        try {
            String elapsedTime = generator.generateVideo(imagePath, audioFileName, videoFileName);
            log.info("视频生成耗时：{}", elapsedTime);

        } catch (IOException e) {
            log.error("生成视频失败: ", e);
        }

    }
}
