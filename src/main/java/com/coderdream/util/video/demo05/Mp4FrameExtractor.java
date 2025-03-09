package com.coderdream.util.video.demo05;

import com.coderdream.util.cd.CdTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Mp4FrameExtractor {

    private static final String FFMPEG_COMMAND = "ffmpeg"; // 可配置参数

    /**
     * 从 MP4 视频中提取指定时间的帧并保存为 PNG 图片 (跨平台版本)
     *
     * @param inputFilePath  输入 MP4 文件路径
     * @param time          提取帧的时间点（格式: HH:mm:ss.SSS 或 HH:mm:ss）
     * @param outputFilePath 输出 PNG 图片文件路径
     * @return 提取的图片文件路径，如果失败返回 null
     */
    public static String extractFrame(String inputFilePath, String time, String outputFilePath) {
        long startMillis = System.currentTimeMillis();

        // 确保输出目录存在
        File outputFile = new File(outputFilePath);
        File outputDir = outputFile.getParentFile();
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            log.error("输出目录创建失败: {}", outputDir.getAbsolutePath());
            return null;
        }

        // 转换时间格式（将逗号替换为点，符合 FFmpeg 要求）
        time = time.replace(",", ".");

        // 构建 FFmpeg 命令
        List<String> commandList = new ArrayList<>();
        commandList.add(FFMPEG_COMMAND);
        commandList.add("-y"); // 覆盖输出文件（如果存在）
        commandList.add("-ss"); // 指定提取帧的时间
        commandList.add(time);
        commandList.add("-i"); // 指定输入文件
        commandList.add(inputFilePath);
        commandList.add("-vframes"); // 指定提取的帧数
        commandList.add("1");
        commandList.add(outputFilePath); // 指定输出文件

        String command = String.join(" ", commandList); // 为了方便日志输出，将命令转换为字符串
        log.info("执行命令: {}", command);

        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        processBuilder.redirectErrorStream(true); // 将错误输出与标准输出合并

        // 执行命令并读取输出
        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator()); //  记录输出，方便调试
                    log.info(line); // 打印每一行输出
                }
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                long durationMillis = System.currentTimeMillis() - startMillis;
                log.info("帧提取成功: {}，耗时: {}", outputFilePath, CdTimeUtil.formatDuration(durationMillis));
                return outputFilePath;
            } else {
                log.error("帧提取失败，退出码: {}", exitCode);
                log.error("FFmpeg 输出:\n{}", output.toString()); // 打印完整的 FFmpeg 输出，方便调试
            }

        } catch (IOException | InterruptedException e) {
            log.error("执行 FFmpeg 命令异常", e);
            Thread.currentThread().interrupt();
        }

        return null;
    }

    /**
     * 合并两张图片，将图片 A 覆盖到图片 B 的底部对齐位置。
     *
     * @param imageAFilePath  图片 A 的文件路径 (覆盖在上层)
     * @param imageBFilePath  图片 B 的文件路径 (作为背景)
     * @param outputFilePath  输出图片的文件路径
     * @return  合并后的图片文件路径，如果失败返回 null
     */
    public static String mergeImagesBottomAligned(String imageAFilePath, String imageBFilePath, String outputFilePath) {
        long startMillis = System.currentTimeMillis();

        // 确保输入文件存在
        File imageAFile = new File(imageAFilePath);
        if (!imageAFile.exists() || !imageAFile.isFile()) {
            log.error("图片 A 不存在: {}", imageAFilePath);
            return null;
        }

        File imageBFile = new File(imageBFilePath);
        if (!imageBFile.exists() || !imageBFile.isFile()) {
            log.error("图片 B 不存在: {}", imageBFilePath);
            return null;
        }

        // 确保输出目录存在
        File outputFile = new File(outputFilePath);
        File outputDir = outputFile.getParentFile();
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            log.error("输出目录创建失败: {}", outputDir.getAbsolutePath());
            return null;
        }

        // 构建 FFmpeg 命令
        List<String> commandList = new ArrayList<>();
        commandList.add(FFMPEG_COMMAND);
        commandList.add("-y"); // 覆盖输出文件（如果存在）
        commandList.add("-i"); // 输入图片 B (背景)
        commandList.add(imageBFilePath);
        commandList.add("-i"); // 输入图片 A (覆盖层)
        commandList.add(imageAFilePath);  //  关键: 加上图片A 的路径
        commandList.add("-filter_complex"); // 使用滤镜图
        // overlay=x=0:y=H-h  x=0：水平位置为左对齐。 y=H-h：垂直位置为底端对齐。 H: 背景图片B的高度。 h: 覆盖图片A的高度。main_w: 图片B的宽度，overlay_w: 图片A的宽度
        commandList.add("[1:v]scale=iw*sar:ih*sar[ovrl];[0:v][ovrl]overlay=x=(main_w-overlay_w)/2:y=H-h");
        commandList.add(outputFilePath); // 输出文件

        String command = String.join(" ", commandList); // 为了方便日志输出，将命令转换为字符串
        log.error("执行命令: {}", command);

        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        processBuilder.redirectErrorStream(true);

        // 执行命令并读取输出
        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                    log.info(line);
                }
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                long durationMillis = System.currentTimeMillis() - startMillis;
                log.info("图片合并成功: {}，耗时: {}", outputFilePath, CdTimeUtil.formatDuration(durationMillis));
                return outputFilePath;
            } else {
                log.error("图片合并失败，退出码: {}", exitCode);
                log.error("FFmpeg 输出:\n{}", output.toString());
            }

        } catch (IOException | InterruptedException e) {
            log.error("执行 FFmpeg 命令异常", e);
            Thread.currentThread().interrupt();
        }

        return null;
    }

    public static void main(String[] args) {
        // 示例用法
        String imageAFilePath = "D:\\0000\\blueLine_1280_150.png"; //  要覆盖的图片
        String imageBFilePath = "D:\\0000\\0007_Trump\\20250307\\frame_at_50s.png"; //  背景图片
        String outputFilePath = "D:\\0000\\0007_Trump\\20250307\\20250307.png"; //  输出图片

        String mergedImage = mergeImagesBottomAligned(imageAFilePath, imageBFilePath, outputFilePath);

        if (mergedImage != null) {
            System.out.println("图片合并成功，文件保存在: " + mergedImage);
        } else {
            System.out.println("图片合并失败!");
        }
    }
}
