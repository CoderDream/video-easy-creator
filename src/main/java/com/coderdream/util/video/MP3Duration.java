package com.coderdream.util.video;

import com.coderdream.util.ffmpeg.DurationParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MP3Duration {

    public static void main(String[] args) {
        try {
            String mp3FilePath = "D:/04_GitHub/video-easy-creator/src/main/resources/mp3/cn/CampingInvitation_001_cn.mp3";
            String ffmpegPath = "D:\\Download\\一点红语音合成3.1版本\\ffmpeg.exe";  // 这里指定ffmpeg.exe的完整路径
            double duration = getMp3Duration(mp3FilePath, ffmpegPath);
            System.out.println("MP3 时长: " + duration + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取MP3文件的时长
     *
     * @param mp3FilePath MP3文件的路径
     * @param ffmpegPath  ffmpeg.exe的完整路径
     * @return 时长（秒）
     */
    public static double getMp3Duration(String mp3FilePath, String ffmpegPath) throws Exception {
        // 构建ffmpeg命令
        String[] commandArgs = new String[]{
            ffmpegPath,  // ffmpeg的完整路径
            "-i", mp3FilePath,
            "2>&1" // 将stderr重定向到stdout，以便捕获ffmpeg的日志输出
        };

        // 使用ProcessBuilder执行ffmpeg命令
        ProcessBuilder processBuilder = new ProcessBuilder(commandArgs);
        processBuilder.redirectErrorStream(true);  // 将错误流和输出流合并，避免分开处理
        Process process = processBuilder.start();

        // 获取ffmpeg的输出流（时长信息包含在标准错误输出中）
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 识别时长信息的行
                if (line.contains("Duration")) {
                    log.info("line: {}", line);
                    // 示例输出: Duration: 00:03:45.67, start: 0.000000, bitrate: 128 kb/s
                    String durationStr = DurationParser.extractDuration(line);// line.split(",")[0].split(" ")[1];
                    log.info("时长字符串: {}", durationStr);
                    if (durationStr.isEmpty()) {
                        log.error("时长字符串为空");
                        return 0;  // 或者你可以抛出一个异常
                    }
                    String[] timeParts = durationStr.split(":");
                    int hours = Integer.parseInt(timeParts[0]);
                    int minutes = Integer.parseInt(timeParts[1]);
                    double seconds = Double.parseDouble(timeParts[2]);
                    return hours * 3600 + minutes * 60 + seconds;
                }
            }
        }

        // 等待进程结束并检查退出代码
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("ffmpeg 执行失败，退出码: " + exitCode);
        }

        return 0;  // 如果未能获取到时长，则返回0
    }
}
