package com.coderdream.util.video;

import com.coderdream.util.cd.CdConstants;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;

@Slf4j
public class VideoUtil {

    private static final String OUTPUT_FILE_PATH = "video_fps_info.txt";  // 保存视频帧率信息的文件路径

    /**
     * 查询视频帧率并保存到文本文件
     *
     * @param videoFiles 视频文件的路径列表
     */
    public static void queryAndSaveFps(List<String> videoFiles) {
        // 遍历每个视频文件，查询帧率
        for (String videoFile : videoFiles) {
            // 查询帧率并保存结果
            String fps = queryFps(videoFile);
            if (fps != null) {
                // 如果成功查询到帧率，保存到文件
                saveFpsInfo(videoFile, fps);
            }
        }
    }

    /**
     * 使用 FFmpeg 查询视频帧率
     *
     * @param videoFile 视频文件的路径
     * @return 返回视频的帧率，失败时返回 null
     */
    private static String queryFps(String videoFile) {
        // 构造 FFmpeg 命令
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", "-i", videoFile
        );
        processBuilder.redirectErrorStream(true);  // 合并标准错误流和标准输出流

        try {
            // 启动进程并等待执行结果
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                // FFmpeg 的输出信息中包含帧率信息，我们可以从中提取
                if (line.contains("fps")) {
                    log.info("获取到帧率信息: {}", line);  // 打印日志，便于调试
                    // 使用正则表达式提取帧率
                    String fps = extractFps(line);
                    if (fps != null) {
                        log.info("视频 {} 的帧率为: {}", videoFile, fps);
                        return fps;  // 返回提取到的帧率
                    }
                }
            }

            // 如果没有找到帧率信息，打印错误并返回 null
            log.error("未能获取到视频 {} 的帧率信息", videoFile);
            return null;
        } catch (IOException e) {
            log.error("执行 FFmpeg 命令时出错: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 提取视频帧率信息
     *
     * @param ffmpegOutput FFmpeg 的输出字符串
     * @return 提取到的帧率，格式为 "xx.xx fps" 或 "xx.0 fps"
     */
    private static String extractFps(String ffmpegOutput) {
        // 使用正则表达式查找帧率（如：25.00 fps 或 60 fps）
        String fpsPattern = "(\\d+\\.?\\d*) fps";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(fpsPattern);
        java.util.regex.Matcher matcher = pattern.matcher(ffmpegOutput);
        if (matcher.find()) {
            // 提取帧率值，并确保返回浮动类型（如 60 转为 60.0）
            String fpsValue = matcher.group(1);
            if (!fpsValue.contains(".")) {
                fpsValue += ".0";  // 如果没有小数部分，手动加上 .0
            }
            return fpsValue;
        }
        return null;
    }

    /**
     * 保存帧率信息到文本文件
     *
     * @param videoFile 视频文件的路径
     * @param fps       查询到的帧率
     */
    private static void saveFpsInfo(String videoFile, String fps) {
        // 拼接输出信息
        String info = "文件名: " + new File(videoFile).getName() + "，帧率: " + fps;

        // 写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH, true))) {
            writer.write(info);
            writer.newLine();  // 换行
            log.info("帧率信息已保存: {}", info);
        } catch (IOException e) {
            log.error("保存帧率信息到文件时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 测试示例，模拟执行查询帧率并保存到文件
     *
     * @param args 程序入口
     */
    public static void main(String[] args) {
        String fileName = "CampingInvitation_cht_03";
        List<File> videoCnFiles = BatchCreateVideoCommonUtil.getVideoFiles(fileName,
          CdConstants.LANG_CN);
        List<File> videoEnFiles = BatchCreateVideoCommonUtil.getVideoFiles(fileName,
          CdConstants.LANG_EN);

        // 示例：传入视频文件路径列表
        List<String> videoCnFileNames = videoCnFiles.stream().map(File::getAbsolutePath).toList();
        List<String> videoEnFileNames = videoEnFiles.stream().map(File::getAbsolutePath).toList();

        List<String> videoFiles = new ArrayList<>();
        videoFiles.addAll(videoCnFileNames);
        videoFiles.addAll(videoEnFileNames);



        // 查询视频的帧率并保存到文本文件
        queryAndSaveFps(videoFiles);
    }
}
