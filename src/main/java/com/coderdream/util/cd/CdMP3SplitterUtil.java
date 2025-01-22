package com.coderdream.util.cd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CdMP3SplitterUtil {

    public static void main(String[] args) {

        CdMP3SplitterUtil.splitMP3("D:\\14_LearnEnglish\\6MinuteEnglish\\2018\\180329\\180329_manbags.mp3",
            "D:\\14_LearnEnglish\\6MinuteEnglish\\2018\\180329\\audio5.mp3",
            "00:00:20,480",
            "00:06:10,300"); // 例如从10.123秒到20.345秒
    }


    public static void splitMP3V2(String inputFile, String outputFile, String startTime, String endTime)
        throws IOException, InterruptedException {
        // 把逗号替换成点
        startTime = startTime.replace(",", ".");
        endTime = endTime.replace(",", ".");
        List<String> commands = Arrays.asList("ffmpeg", "-y", "-i", inputFile, "-ss", startTime, "-to", endTime, "-c:a",
            "copy", outputFile);
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        Process process = processBuilder.start();

        try {
            int exitCode = process.waitFor();
            System.out.println("\nFFmpeg exit code: " + exitCode);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static String splitMP3(String inputFile, String outputFile, String startTime, String endTime) {
        long methodStartTime = System.currentTimeMillis();
        Instant start = Instant.now();
        log.info("开始分割音频文件...");
        startTime = startTime.replace(",", ".");
        endTime = endTime.replace(",", ".");
        // 构建 FFmpeg 命令
        List<String> command = new ArrayList<>();
        command.add("ffmpeg"); //
        command.add("-y"); // 覆盖输出文件
        command.add("-i"); // 输入文件
        command.add(inputFile);
        command.add("-ss"); // 从
        command.add(startTime);
        command.add("-to"); // 到
        command.add(endTime);
        command.add("-c:a"); // 复制音频
        command.add("copy"); // 复制音频

        // 设置输出文件路径
        command.add(outputFile);

        try {
            // 创建 ProcessBuilder 对象
//            log.info("command: {}", command);
            log.info("开始合并 FFmpeg command: {}", String.join(" ", command));
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // 启动进程
            Process process = processBuilder.start();

            // 读取标准输出
            Thread outputThread = new Thread(() -> {
                try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String s;
                    while ((s = stdInput.readLine()) != null) {
                        System.out.println(s);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // 读取标准错误
            Thread errorThread = new Thread(() -> {
                try (BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String s;
                    while ((s = stdError.readLine()) != null) {
//                        System.err.println(s);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // 启动线程
            outputThread.start();
            errorThread.start();

            // 等待线程完成
            outputThread.join();
            errorThread.join();

            // 设置超时时间（例如 10 分钟）
            long timeout = 10 * 60 * 1000; // 10 minutes in milliseconds
            boolean finished = process.waitFor(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);

            if (finished) {
                int exitCode = process.exitValue();
                System.out.println("Video creation completed with exit code: " + exitCode);
            } else {
                // 如果超时，强制终止进程
                process.destroy();
                System.err.println("Command timed out and was terminated.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        long methodEndTime = System.currentTimeMillis();
        System.out.println("耗时: " + (methodEndTime - methodStartTime) + "ms");

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        // 毫秒
//        long remainingMillis = (duration % 1000) / 1000;
//
//        System.out.println(hours + "小时 " + minutes % 60 + "分 " + remainingSeconds + "秒 " + remainingMillis + "毫秒");
//        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
//        log.info("视频文件合并完成，耗时: {}", formattedTime);

//        long hours = seconds / 3600;
        long remainingSeconds = seconds % 3600;
//        long minutes = remainingSeconds / 60;

        long remainingMillis = duration.toMillis() % 1000;
        System.out.println(hours + "小时 " + minutes + "分 " + remainingSeconds + "秒 " + remainingMillis + "毫秒");
        System.out.println(hours + "小时 " + minutes + "分 " + remainingSeconds + "秒 " + remainingMillis + "毫秒");
        log.info("命令执行完成，耗时: {} 小时 {}分 {} 秒 {} 毫秒", hours, minutes, remainingSeconds, remainingMillis);

        return duration.toString();
    }

//    public static void splitMP3(String inputFile, String outputFile, String startTime, String endTime) throws IOException, InterruptedException {
//        String ffmpegPath = "ffmpeg"; // 这里应该是FFmpeg可执行文件的路径
////        long startMillis = convertTimeToMillis(startTime);
////        long endMillis = convertTimeToMillis(endTime);
//        // 把逗号替换成点
//        startTime = startTime.replace(",", ".");
//        endTime = endTime.replace(",", ".");
//
//
//        String[] cmd = new String[4];
//        cmd[0] = ffmpegPath;
//        cmd[1] = "-y -i";
//        cmd[2] = inputFile;
//        cmd[3] = "-ss " +  startTime  + " -to " + endTime + " -c:a copy " + outputFile;
//        // ffmpeg -i input.mp3 -ss 00:00:05.123 -to 00:00:10.456 -c:a copy output.mp3
//
//        System.out.println("Running command: " + String.join(" ", cmd));
//        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
//        Process process = processBuilder.start();
//
//        // 读取输出流
//        InputStream is = process.getInputStream();
//        InputStreamReader isr = new InputStreamReader(is);
//        BufferedReader br = new BufferedReader(isr);
//        String line;
//        while ((line = br.readLine()) != null) {
//            System.out.println(line);
//        }
//
//        // 等待进程结束
//        int exitCode = process.waitFor();
//        System.out.println("\nFFmpeg exit code: " + exitCode);
//    }


}
