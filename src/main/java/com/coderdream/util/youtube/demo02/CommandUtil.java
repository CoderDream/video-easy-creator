package com.coderdream.util.youtube.demo02;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * 命令执行工具类，用于执行 Windows 控制台命令
 */
@Slf4j
public class CommandUtil {

    /**
     * 执行指定的 Windows 控制台命令，并将执行过程中的输出显示在控制台中，并记录执行时间
     *
     * @param command 要执行的命令字符串
     */
    public static void executeCommand(List<String> command) {
        LocalDateTime startTime = LocalDateTime.now(); // 记录开始时间
        String finalCommand = String.join(" ", command);

        try (BufferedReader reader = getCommandReader(command)) {
            log.info("开始执行命令: {}", finalCommand); // 打印由 List 组装后的完整命令
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            Duration duration = Duration.between(startTime, LocalDateTime.now());
            String formattedTime = formatDuration(duration);
            log.info("命令执行完成，执行耗时：{}", formattedTime);
        } catch (IOException e) {
            log.error("执行命令时发生IO异常: {}", e.getMessage(), e);
        }
    }


    /**
     * 执行命令并获取 BufferedReader
     *
     * @param commandList 要执行的命令字符串列表
     * @return BufferedReader
     * @throws IOException
     */
    private static BufferedReader getCommandReader(List<String> commandList) throws IOException {
        ProcessBuilder builder;
        List<String> fullCommandList = new ArrayList<>();

        // 根据操作系统选择不同的命令前缀
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Windows 系统
            fullCommandList.add("cmd.exe");
            fullCommandList.add("/c");
            fullCommandList.addAll(commandList);
        } else {
            // macOS 或 Linux 系统
            // 使用 bash -c 可以执行更复杂的命令，包括管道、重定向等
            fullCommandList.add("bash");
            fullCommandList.add("-c");
            fullCommandList.add(String.join(" ", commandList)); // 合并为单个命令字符串
        }

        builder = new ProcessBuilder(fullCommandList);
        builder.redirectErrorStream(true); // 将错误输出重定向到标准输出
        Process process = builder.start(); // 启动进程

        // 获取控制台的字符编码
        Charset consoleCharset = getConsoleCharset();

        return new BufferedReader(new InputStreamReader(process.getInputStream(), consoleCharset));
    }


    /**
     * 获取控制台字符编码 (Java 9+)
     *
     * @return Charset
     */
    private static Charset getConsoleCharset() {
        Console console = System.console();
        if (console != null) {
            return console.charset();
        } else {
            return Charset.defaultCharset();
        }
    }

    /**
     * 格式化时长为时分秒
     *
     * @param duration 时长
     * @return 格式化后的时长字符串 HH:mm:ss
     */
    private static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        return String.format("%02d:%02d:%02d",
                absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60);
    }

    /**
     * 使用 yt-dlp 下载最佳 720p 视频。
     *
     * @param videoLink   视频链接
     * @param outputFileName  目标 MP4 文件名 (包含路径)
     */
    public static void downloadBest720p(String videoLink, String outputFileName) {
        String listFormatsCommand = "yt-dlp -F \"" + videoLink + "\"";
        List<String> formats = listFormats(listFormatsCommand);

        String bestVideoFormat = null;
        String bestAudioFormat = null;

        for (String format : formats) {
            if (format.contains("1280x720") && format.contains("video only")) {
                if (bestVideoFormat == null || getBitrate(format) > getBitrate(bestVideoFormat)) {
                    bestVideoFormat = format;
                }
            }

            if (format.contains("audio only")) {
                if (bestAudioFormat == null || getAudioBitrate(format) > getAudioBitrate(bestAudioFormat)) {
                    bestAudioFormat = format;
                }
            }
        }

        if (bestVideoFormat != null && bestAudioFormat != null) {
            String videoId = extractFormatId(bestVideoFormat);
            String audioId = extractFormatId(bestAudioFormat);

            List<String> downloadCommand = new ArrayList<>();
            downloadCommand.add("yt-dlp");
            downloadCommand.add("-f");
            downloadCommand.add(videoId + "+" + audioId);
            downloadCommand.add("--merge-output-format");
            downloadCommand.add("mp4");
            downloadCommand.add("-o");
            downloadCommand.add(outputFileName);
            downloadCommand.add(videoLink);

            executeCommand(downloadCommand);

            log.info("成功下载最佳720p视频到：{}", outputFileName);
        } else {
            log.error("未找到最佳720p视频或音频格式.");
        }
    }

    /**
     * 从格式信息中提取格式 ID
     * @param format 格式信息字符串
     * @return 格式 ID
     */
    private static String extractFormatId(String format) {
        String id = format.substring(0, format.indexOf(" "));
        return id.trim();
    }

    /**
     * 获取比特率
     *
     * @param format 格式信息
     * @return 比特率
     */
    private static int getBitrate(String format) {
        Pattern pattern = Pattern.compile("(\\d+)k");
        Matcher matcher = pattern.matcher(format);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    /**
     * 从格式信息中提取音频比特率
     * @param format 格式信息字符串
     * @return 音频比特率
     */
    private static int getAudioBitrate(String format) {
        Pattern pattern = Pattern.compile("(\\d+)k");
        Matcher matcher = pattern.matcher(format);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }


    /**
     * 列出可用格式，返回格式列表。
     * @param listFormatsCommand 列表命令
     * @return
     */
    private static List<String> listFormats(String listFormatsCommand) {
        List<String> formats = new ArrayList<>();
        try (BufferedReader reader = getCommandReader(Arrays.asList(listFormatsCommand))) {
            String line;
            boolean startCapture = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID")) {
                    startCapture = true;
                    continue;
                }
                if (startCapture && !line.startsWith("---")) { // Skip separator lines
                    formats.add(line);
                }
            }
        } catch (IOException e) {
            log.error("列出格式时发生错误: {}", e.getMessage(), e);
        }
        return formats;
    }

   /**
     * 使用 FFmpeg 提取 MP4 文件的左声道到指定的 MP3 文件。
     *
     * @param inputMp4  输入 MP4 文件的完整路径
     * @param outputMp3 输出 MP3 文件的完整路径
     */
    public static void extractLeftChannel(String inputMp4, String outputMp3) {
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(inputMp4);
        command.add("-filter_complex");
        command.add("channelsplit=channel_layout=stereo:channels=FL[left]");
        command.add("-map");
        command.add("[left]");
        command.add("-ac");
        command.add("1");
        command.add("-acodec");
        command.add("libmp3lame");
        command.add(outputMp3);

        executeCommand(command);

        log.info("成功提取左声道到: {}", outputMp3);
    }

    /**
     * 使用 FFmpeg 探测 MP4 文件是否有音频流
     *
     * @param inputMp4 输入 MP4 文件的完整路径
     * @return true 有音频流，false 没有音频流
     */
    public static boolean hasAudioStream(String inputMp4) {
        List<String> command = new ArrayList<>();
        command.add("ffprobe");
        command.add("-i");
        command.add(inputMp4);
        command.add("-show_streams");
        command.add("-select_streams");
        command.add("a");

        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 如果ffprobe 有输出，说明有音频流，如果没有任何流信息输出，说明没有
                    return true;
                }
            }

            int exitCode = process.waitFor();
            return false; // 如果命令执行成功且没有输出，则认为没有音频流
        } catch (IOException | InterruptedException e) {
            log.error("探测音频流时发生错误: {}", e.getMessage(), e);
            return false; // 发生异常也返回 false
        }
    }

    public static void main(String[] args) {

        // 示例用法：
//        String videoLink = "https://www.youtube.com/watch?v=aayJ6wlyfII"; // 替换为实际的视频链接
//        String outputFileName = "c:\\abcd.mp4"; // 替换为期望的输出路径和文件名
//        downloadBest720p(videoLink, outputFileName);

        String inputMp4 = "D:\\0000\\0007_Trump\\20250227\\20250227.mp4"; // 替换为实际的 MP4 文件路径
        String outputMp3 = "D:\\0000\\0007_Trump\\20250227\\left_channel.mp3"; // 替换为期望的 MP3 文件路径

        if (hasAudioStream(inputMp4)) {
            extractLeftChannel(inputMp4, outputMp3);
        } else {
            log.warn("文件 {} 不包含音频流，无法提取左声道。", inputMp4);
        }
    }

    /**
     * 修改了executeCommand的入参类型为List<String>
     * 为了兼容
     * @param command 要执行的命令字符串
     */
    @Deprecated
    public static void executeCommand(String command) {
        executeCommand(Arrays.asList(command));
    }

    @Deprecated
    private static BufferedReader getCommandReader(String command) throws IOException {
        return getCommandReader(Arrays.asList(command));
    }
}
