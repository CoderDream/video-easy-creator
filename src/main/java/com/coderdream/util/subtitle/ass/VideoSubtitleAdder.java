package com.coderdream.util.subtitle.ass;

import com.coderdream.util.CdConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class VideoSubtitleAdder {

    public static void main(String[] args) {
        // 输入视频文件和字幕文件的路径
        String videoFilePath = CdConstants.RESOURCES_BASE_PATH + "ass/input.mp4";  // 替换为你的输入视频路径
        String subtitleFilePath = CdConstants.RESOURCES_BASE_PATH + "ass/input.ass";  // 替换为你的字幕文件路径
        String outputFilePath = CdConstants.RESOURCES_BASE_PATH + "ass/video_with_subtitles.mkv";  // 输出的视频路径

        // 创建文件对象
        File outputFile = new File(outputFilePath);
        File videoFile = new File(videoFilePath);
        File subtitleFile = new File(subtitleFilePath);

        // 调用方法给视频添加字幕
        addSubtitleToVideo(videoFile.getAbsolutePath(), subtitleFile.getAbsolutePath(), outputFile.getAbsolutePath());
    }

    /**
     * 给视频添加字幕
     * @param videoFilePath 输入视频文件路径
     * @param subtitleFilePath 字幕文件路径（.ass 格式）
     * @param outputFilePath 输出的视频文件路径
     */
    public static void addSubtitleToVideo(String videoFilePath, String subtitleFilePath, String outputFilePath) {
        // 构建 ffmpeg 命令的各个参数
        List<String> command = new ArrayList<>();

        // 添加 ffmpeg 执行命令
        command.add("ffmpeg");

        // 添加 -y 参数，自动覆盖输出文件
        command.add("-y");

        // 添加输入视频文件
        command.add("-i");
        command.add(videoFilePath);  // 输入的视频文件路径

        // 添加字幕文件
        command.add("-i");
        command.add(subtitleFilePath);  // 输入的字幕文件路径

        // 设置视频流复制（不进行编码）
        command.add("-c:v");
        command.add("copy");  // 视频流直接复制，不重新编码

        // 设置音频流复制（不进行编码）
        command.add("-c:a");
        command.add("copy");  // 音频流直接复制，不重新编码

        // 设置字幕编码为 ass（适用于 MKV 容器）
        command.add("-c:s");
        command.add("ass");  // 使用 .ass 字幕格式进行嵌入

        // 设置输出文件路径
        command.add(outputFilePath);  // 输出文件的路径

        // 创建 ProcessBuilder 执行命令
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(System.getProperty("user.dir")));  // 设置当前工作目录为项目根目录

        try {
            // 打印构建的 ffmpeg 命令
            log.info("构建并执行 ffmpeg 命令：{}", String.join(" ", command));

            // 启动进程执行命令
            Process process = processBuilder.start();

            // 等待命令执行完成并返回退出码
            int exitCode = process.waitFor();

            // 根据退出码判断命令是否执行成功
            if (exitCode == 0) {
                log.info("字幕成功添加到视频，输出文件为: {}", outputFilePath);
            } else {
                log.error("添加字幕时出现错误，退出码: {}", exitCode);
            }
        } catch (IOException | InterruptedException e) {
            log.error("执行 ffmpeg 命令时发生异常", e);
        }
    }
}
