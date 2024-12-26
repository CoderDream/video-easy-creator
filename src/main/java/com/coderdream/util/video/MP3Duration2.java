package com.coderdream.util.video;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MP3Duration2 {

    public static void main(String[] args) {
        try {
            String mp3FilePath = "D:/04_GitHub/video-easy-creator/src/main/resources/mp3/cn/CampingInvitation_001_cn.mp3";
            double duration = getMp3Duration(mp3FilePath);
            System.out.println("MP3 时长: " + duration + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取MP3文件的时长
     *
     * @param mp3FilePath MP3文件的路径
     * @return 时长（秒）
     */
    private static double getMp3Duration(String mp3FilePath) throws Exception {
        // 构建ffmpeg命令
        String command = "ffmpeg";
        String[] commandArgs = new String[]{
            command,
            "-i", mp3FilePath,
            "2>&1" // 将stderr重定向到stdout，以便捕获ffmpeg的日志输出
        };

        // 使用ProcessBuilder执行ffmpeg命令
        ProcessBuilder processBuilder = new ProcessBuilder(commandArgs);
        Process process = processBuilder.start();

        // 获取ffmpeg的错误流（时长信息包含在标准错误输出中）
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 识别时长信息的行
                if (line.contains("Duration")) {
                    // 示例输出: Duration: 00:03:45.67, start: 0.000000, bitrate: 128 kb/s
                    String durationStr = line.split(",")[0].split(" ")[1];
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

        return 0;  // 如果未能获取到时长，则返回0
    }
}
