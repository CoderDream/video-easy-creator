package com.coderdream.util.video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FfmpegDurationParser {
    public static void main(String[] args) {
        try {
            // 构建ffmpeg命令，这里要替换成你实际的MP3文件路径
//            String command = "ffmpeg -i D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\mp3\\cn\\CampingInvitation_001_cn.mp3 2>&1";
            String command = "ffmpeg -i D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\mp3\\cn\\CampingInvitation_001_cn.mp3";
            Process process = Runtime.getRuntime().exec(command);

            // 读取命令执行的输出内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String duration = null;
            log.info("开始读取ffmpeg输出...{}", reader);
            while ((line = reader.readLine())!= null) {
                log.info("line: {}", line);
                if (line.contains("Duration")) {
                    // 从包含Duration的行中提取时长部分，格式如：Duration: 00:00:06.62, start: 0.000000, bitrate: 160 kb/s
                    String[] parts = line.split(",");
                    duration = parts[0].split(":")[1].trim();
                    break;
                }
            }
            reader.close();

            if (duration!= null) {
                System.out.println("MP3文件时长为: " + duration);
            } else {
                System.out.println("未找到时长信息");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
