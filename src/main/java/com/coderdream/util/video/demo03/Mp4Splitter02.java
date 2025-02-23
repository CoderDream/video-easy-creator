package com.coderdream.util.video.demo03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * MP4 分割工具类，使用 FFmpeg 命令分割视频
 */
@Slf4j
public class Mp4Splitter02 {

    private static final String TIME_FORMAT = "HH:mm:ss,SSS"; // 定义允许的时间格式

    /**
     * 分割 MP4 视频
     *
     * @param inputFilePath  MP4 文件名
     * @param startTime      开始时间，格式为 HH:mm:ss,SSS
     * @param endTime        结束时间，格式为 HH:mm:ss,SSS
     * @param outputFilePath 输出文件路径
     * @return 分割后的 MP4 文件名, 如果失败返回 null
     */
    public static String splitVideo(String inputFilePath, String startTime, String endTime, String outputFilePath) {

        long startTimeMillis = System.currentTimeMillis(); // 记录开始时间

        try {
            if (!isValidTimeFormat(startTime) || !isValidTimeFormat(endTime)) {
                log.error("时间格式错误，正确格式为: {}", TIME_FORMAT);
                throw new IllegalArgumentException("时间格式错误，正确格式为: " + TIME_FORMAT);  //抛出异常
            }

            // 转换时间格式为 FFmpeg 可识别的格式
            String ffmpegStartTime = startTime.replace(",", ".");
            String ffmpegEndTime = endTime.replace(",", ".");

            String duration = calculateDuration(startTime, endTime); // 计算持续时间

            if (duration == null) {
                log.error("计算持续时间失败，请检查开始时间和结束时间是否有效。");
                return null;
            }

            // 构建 FFmpeg 命令
            List<String> command = new ArrayList<>();
            command.add("ffmpeg");
            command.add("-i");
            command.add(inputFilePath);
            command.add("-ss");
            command.add(ffmpegStartTime); // 使用转换后的时间
            command.add("-to");
            command.add(ffmpegEndTime);   // 使用转换后的时间
            command.add("-c");
            command.add("copy"); // 使用 copy 避免重新编码
            command.add(outputFilePath);

            // 打印完整的 FFmpeg 命令
            String commandString = command.stream().collect(Collectors.joining(" "));
            log.info("执行 FFmpeg 命令: {}", commandString);

            executeCommand(command); // 执行命令

            long endTimeMillis = System.currentTimeMillis();  //记录结束时间

            long elapsedTimeMillis = endTimeMillis - startTimeMillis;

            String elapsedTime = formatElapsedTime(elapsedTimeMillis); //格式化耗时时间

            log.info("视频分割成功，输出文件：{}，耗时: {}", outputFilePath, elapsedTime);

            return outputFilePath;

        } catch (IOException | InterruptedException e) {
            log.error("视频分割失败: {}", e.getMessage(), e);
            return null;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());  // 记录参数异常
            return null; // 或者抛出异常，取决于你的需求
        }
    }

    /**
     * 校验时间格式是否合法
     *
     * @param time 时间字符串
     * @return true 如果格式合法，false 否则
     */
    private static boolean isValidTimeFormat(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        sdf.setLenient(false); // 严格模式，不容忍日期/时间的不准确
        try {
            sdf.parse(time);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 执行 FFmpeg 命令
     *
     * @param command FFmpeg 命令列表
     * @throws IOException          IO 异常
     * @throws InterruptedException 中断异常
     */
    private static void executeCommand(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 将错误流合并到输出流
        Process process = processBuilder.start();


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug(line); // 输出 FFmpeg 日志
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg 命令执行失败，退出代码: " + exitCode);
        }
    }


    /**
     * 计算持续时间
     *
     * @param startTime 开始时间，格式 HH:mm:ss,SSS
     * @param endTime   结束时间，格式 HH:mm:ss,SSS
     * @return 持续时间字符串，格式 HH:mm:ss,SSS,  如果失败返回 null.
     */
    private static String calculateDuration(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);

            long durationMillis = endDate.getTime() - startDate.getTime();

            if (durationMillis < 0) {
                log.error("结束时间必须晚于开始时间");
                return null;
            }

            long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;
            long milliseconds = durationMillis % 1000;

            return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, milliseconds);

        } catch (Exception e) {
            log.error("时间解析失败: {}", e.getMessage(), e);
            return null;
        }
    }


    /**
     * 格式化耗时时间
     *
     * @param elapsedTimeMillis 耗时毫秒数
     * @return 格式化后的时间字符串，格式为 HH:mm:ss.SSS
     */
    private static String formatElapsedTime(long elapsedTimeMillis) {

        long hours = TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis) % 60;
        long milliseconds = elapsedTimeMillis % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }


    public static void main(String[] args) {
        // 示例用法
        String inputFilePath = "D:\\0000\\0003_PressBriefings\\250128\\250128.mp4"; // 替换为你的 MP4 文件路径
        String startTime = "00:00:00,000";
        String endTime = "00:00:13,680";
        String outputFilePath = "D:\\0000\\0003_PressBriefings\\250128\\250128_part001.mp4"; // 替换为你的输出文件路径

        String splittedFile = Mp4Splitter02.splitVideo(inputFilePath, startTime, endTime, outputFilePath);

        if (splittedFile != null) {
            System.out.println("视频分割成功，文件保存在: " + splittedFile);
        } else {
            System.out.println("视频分割失败!");
        }
    }
}
