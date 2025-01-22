package com.coderdream.util.subtitle.ass;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用ffmpeg给mp4视频添加ass字幕的工具类.
 */
@Slf4j
public class AddSubtitleToVideo2 {

    /**
     * 给mp4视频添加ass字幕，生成新的视频文件.
     * 新文件名格式为 原文件名_with_subtitle.mp4
     *
     * @param videoPath    视频文件路径
     * @param subtitlePath 字幕文件路径
     * @throws IOException          如果执行ffmpeg命令失败
     * @throws InterruptedException 如果执行过程中被打断
     */
    public static void addSubtitle(String videoPath, String subtitlePath) throws IOException, InterruptedException {
        Instant start = Instant.now();
        String outputPath = generateOutputPath(videoPath);


        List<String> command = new ArrayList<>();
        command.add("ffmpeg");                   // ffmpeg 命令
        command.add("-y");                      // 覆盖输出文件
        command.add("-i");                      // 指定输入文件
        command.add(videoPath);                 // 视频输入文件路径
        command.add("-vf");                     // 指定视频滤镜
        command.add("ass=" + subtitlePath);    // 添加字幕
        command.add("-c:v");                     // 指定视频编码器
        command.add("libx264");                  // 使用 libx264 软件编码器
        command.add("-pix_fmt");                // 添加 pix_fmt 参数
        command.add("yuv420p");                  // yuv420p 可以兼容大部分播放器
        command.add(outputPath);             // 输出视频文件路径


        String commandString = String.join(" ", command);
        log.info("执行ffmpeg命令: {}", commandString);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try  {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("ffmpeg output: {}", line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("ffmpeg执行失败，退出代码: {}", exitCode);
                throw new IOException("ffmpeg执行失败，退出代码: " + exitCode);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("ffmpeg执行被中断", e);
            throw new IOException("ffmpeg执行被中断", e);
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        log.info("添加字幕完成，输出文件：{}，耗时: {}分{}秒{}毫秒", outputPath, duration.toMinutesPart(), duration.toSecondsPart(), duration.toMillisPart());
    }

    /**
     * 生成输出文件路径.  e.g.,  xxx.mp4 -> xxx_with_subtitle.mp4
     *
     * @param videoPath 原视频路径
     * @return 新视频路径
     */
    private static String generateOutputPath(String videoPath) {
        return Paths.get(videoPath).getParent().resolve(
                Paths.get(videoPath).getFileName().toString().replaceFirst("\\.mp4$", "_with_subtitle.mp4")).toString();
    }

    /**
     * 主方法.
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        String videoPath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.mp4";
        String subtitlePath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.ass";

        try {
            addSubtitle(videoPath, subtitlePath);

        } catch (IOException | InterruptedException e) {
            log.error("添加字幕失败", e);
        }
    }
}
