package com.coderdream.util.video.demo06;

import static com.coderdream.util.cd.CdConstants.OS_MAC;
import static com.coderdream.util.cd.CdConstants.OS_WINDOWS;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class VideoEncoder {

    private static final String FFMPEG_COMMAND = "ffmpeg"; // 可配置
    private static final String DEFAULT_CRF = "23";  // Default CRF value
    private static final String DEFAULT_PRESET = "medium"; // Default Preset

    /**
     * 使用 FFmpeg 对视频进行编码 (根据操作系统选择命令)
     *
     * @param inputFilePath  输入视频文件路径
     * @param outputFilePath 输出视频文件路径
     * @return 编码后的视频文件路径，如果失败返回 null
     */
    public static String encodeVideo(String inputFilePath, String outputFilePath) {
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

        // 获取操作系统类型
        String osType = OperatingSystem.getOS();
//        log.info("操作系统类型: {}", osType);

        // 构建 FFmpeg 命令
        List<String> commandList = new ArrayList<>();
        commandList.add(FFMPEG_COMMAND);
        commandList.add("-y"); // 覆盖输出文件（如果存在）
        commandList.add("-i"); // 输入文件
        commandList.add(inputFilePath);

        // 根据操作系统选择不同的编码参数
        switch (osType) {
            case OS_WINDOWS:
                addWindowsEncodingParams(commandList);
                break;
            case OS_MAC:
                addMacEncodingParams(commandList);
                break;
            default:
                addLinuxEncodingParams(commandList); // Linux or other
                break;
        }

        // 通用的音频和格式参数
        commandList.add("-c:a");
        commandList.add("aac");
        commandList.add("-b:a");
        commandList.add("128k");
        commandList.add("-pix_fmt");
        commandList.add("yuv420p");
        commandList.add("-movflags");
        commandList.add("+faststart");
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
                    log.error("{}", line);
                    output.append(line).append(System.lineSeparator());
                }
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                long durationMillis = System.currentTimeMillis() - startMillis;
                log.info("视频编码成功: {}，耗时: {}", outputFilePath, CdTimeUtil.formatDuration(durationMillis));
                return outputFilePath;
            } else {
                log.error("视频编码失败，退出码: {}", exitCode);
                log.error("FFmpeg 输出:\n{}", output);
            }

        } catch (IOException | InterruptedException e) {
            log.error("执行 FFmpeg 命令异常", e);
            Thread.currentThread().interrupt();
        }

        return null;
    }

    private static void addWindowsEncodingParams(List<String> commandList) {
        // Windows with NVENC
        commandList.add("-c:v");
        commandList.add("h264_nvenc"); // Use NVIDIA's NVENC encoder
        commandList.add("-preset");
        commandList.add("slow");  // or p7 for slowest
        commandList.add("-rc:v");  //Rate control mode
        commandList.add("vbr_hq");  //Variable bitrate, high quality
        commandList.add("-cq");    //Constant quality rate control
        commandList.add("20"); // Constant Quantizer (CQ) value
    }

    private static void addMacEncodingParams(List<String> commandList) {
        // macOS with VideoToolbox
        commandList.add("-c:v");
        commandList.add("h264_videotoolbox"); // Use VideoToolbox encoder
        commandList.add("-q:v");
        commandList.add("23"); // Constant Quantizer (CQ) value
    }

    private static void addLinuxEncodingParams(List<String> commandList) {
        // Linux or other platforms with libx264
        commandList.add("-c:v");
        commandList.add("libx264"); // Use libx264 encoder
        commandList.add("-preset");
        commandList.add(DEFAULT_PRESET);
        commandList.add("-crf");
        commandList.add(DEFAULT_CRF);
    }

    public static void main(String[] args) {
        // 示例用法
        String inputFilePath = "D:\\0000\\EnBook001\\Chapter019\\video\\Chapter019.mp4"; // 替换为你的输入文件
        String outputFilePath = "D:\\0000\\EnBook001\\Chapter019\\video\\Chapter019_new.mp4"; // 替换为你的输出文件

        String encodedVideo = encodeVideo(inputFilePath, outputFilePath);

        if (encodedVideo != null) {
            System.out.println("视频编码成功，文件保存在: " + encodedVideo);
        } else {
            System.out.println("视频编码失败!");
        }
    }
}
