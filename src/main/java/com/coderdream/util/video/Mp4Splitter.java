package com.coderdream.util.video;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Mp4Splitter 工具类
 *
 * 用于通过 FFmpeg 分割 MP4 视频，支持自定义起止时间，避免切片前2秒卡顿。
 */
@Slf4j
public class Mp4Splitter {

    /**
     * 分割 MP4 视频
     *
     * @param inputFilePath  输入 MP4 文件路径
     * @param startTime      开始时间（格式: HH:mm:ss.SSS 或 HH:mm:ss）
     * @param endTime        结束时间（格式: HH:mm:ss.SSS 或 HH:mm:ss）
     * @param outputFilePath 输出 MP4 文件路径
     * @return 切片后的文件路径，如果失败返回 null
     */
    public static String splitVideo(String inputFilePath, String startTime, String endTime, String outputFilePath) {
        long startMillis = System.currentTimeMillis();

        // 确保输入文件存在
        File inputFile = new File(inputFilePath);
        if (!inputFile.exists() || !inputFile.isFile()) {
            log.error("输入文件不存在: {}", inputFilePath);
            return null;
        }

        // 确保输出目录存在
        File outputFile = new File(outputFilePath);
        File outputDir = outputFile.getParentFile();
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            log.error("输出目录创建失败: {}", outputDir.getAbsolutePath());
            return null;
        }

        // 转换时间格式（将逗号替换为点，符合 FFmpeg 要求）
        startTime = startTime.replace(",", ".");
        endTime = endTime.replace(",", ".");

        // 计算持续时间（endTime - startTime）
        long durationInMillis = parseTimeToMillis(endTime) - parseTimeToMillis(startTime);
        String duration = formatDuration(durationInMillis);

        // FFmpeg 命令，避免前2秒卡顿，确保音画同步
        String command = String.format(
                "ffmpeg -y -accurate_seek -ss %s -i \"%s\" -t %s -c:v libx264 -c:a aac -strict experimental -avoid_negative_ts make_zero \"%s\"",
                startTime, inputFilePath, duration, outputFilePath
        );

        log.info("执行命令: {}", command);

        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        processBuilder.redirectErrorStream(true);

        // 执行命令并读取输出
        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
            }

            // 等待进程完成
            if (process.waitFor() == 0) {
                long durationMillis = System.currentTimeMillis() - startMillis;
                log.info("视频分割完成: {}，耗时: {}", outputFilePath, formatDuration(durationMillis));
                return outputFilePath;
            } else {
                log.error("视频分割失败，退出码: {}", process.exitValue());
            }

        } catch (IOException | InterruptedException e) {
            log.error("执行 FFmpeg 命令异常", e);
            Thread.currentThread().interrupt();
        }

        return null;
    }

    /**
     * 将时间字符串转换为毫秒
     *
     * @param time 时间字符串，格式 HH:mm:ss.SSS
     * @return 时间对应的毫秒值
     */
    private static long parseTimeToMillis(String time) {
        String[] parts = time.split("[:.]");
        long hours = Long.parseLong(parts[0]) * 3600 * 1000;
        long minutes = Long.parseLong(parts[1]) * 60 * 1000;
        long seconds = Long.parseLong(parts[2]) * 1000;
        long milliseconds = parts.length > 3 ? Long.parseLong(parts[3]) : 0;
        return hours + minutes + seconds + milliseconds;
    }

    /**
     * 将毫秒转换为 时:分:秒.毫秒 格式
     *
     * @param millis 时间毫秒数
     * @return 格式化后的时间字符串
     */
    private static String formatDuration(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        long milliseconds = millis % 1000;
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

    /**
     * 主方法示例
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 示例用法
        String inputFilePath = "D:\\0000\\0003_PressBriefings\\250128\\250128.mp4";
        String timeStr = "00:00:45,560 --> 00:00:53,640";// 00:00:45,560 --> 00:00:49,960

//        00:00:49,960 --> 00:00:53,640
        String[] times = timeStr.split(" --> ");
        String startTime = times[0];//"00:00:03,400";
        String endTime = times[1];//"00:00:13,680";
        String outputFilePath = "D:\\0000\\0003_PressBriefings\\250128\\250128_part004.mp4";

        String splittedFile = splitVideo(inputFilePath, startTime, endTime, outputFilePath);

        if (splittedFile != null) {
            System.out.println("视频分割成功，文件保存在: " + splittedFile);
        } else {
            System.out.println("视频分割失败!");
        }
    }
}
