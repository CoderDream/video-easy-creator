package com.coderdream.util.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FFmpegVideoOverlay02 {

    /**
     * 使用 FFmpeg 将图片和音频叠加到背景视频上生成新的视频。
     *
     * @param backgroundVideoPath 背景视频的路径
     * @param imagePath           要叠加的图片的路径
     * @param audioPath           要叠加的音频的路径
     * @param outputPath          输出视频的路径
     * @param duration            输出视频的持续时间 (秒) ，如果为 -1 ，则使用背景视频的长度
     * @throws IOException 如果 FFmpeg 命令执行失败
     * @throws InterruptedException 如果 FFmpeg 命令被中断
     */
    public static void createVideoWithOverlay(String backgroundVideoPath, String imagePath, String audioPath, String outputPath, int duration) throws IOException, InterruptedException {

        // 1. 构建 FFmpeg 命令
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");

        // 2. 循环播放背景视频的参数
        command.add("-stream_loop");
        command.add("-1"); // -1 表示无限循环

        // 3. 输入背景视频
        command.add("-i");
        command.add(backgroundVideoPath);

        // 4. 输入图片
        command.add("-i");
        command.add(imagePath);

        // 5. 输入音频
        command.add("-i");
        command.add(audioPath);

        // 6. 设置视频滤镜
        //    -filter_complex "[1:v]format=rgba,colorchannelmixer=aa=0.8[ovrl];[0:v][ovrl]overlay=x=0:y=0"
        //    [1:v]format=rgba：将图片转换为 RGBA 格式，以便支持透明度。
        //    colorchannelmixer=aa=0.8：设置 alpha 通道的值为 0.8，实现 80% 的透明度。 1是完全不透明, 0是完全透明
        //    [ovrl]：为经过透明度处理的图片流命名为 ovrl。
        //    [0:v][ovrl]overlay=x=0:y=0：将具有透明度的图片叠加到背景视频上。

        String filterComplex = "[1:v]format=rgba,colorchannelmixer=aa=0.8[ovrl];[0:v][ovrl]overlay=x=0:y=0";
        command.add("-filter_complex");
        command.add(filterComplex);

        // 7. 映射视频和音频流
        command.add("-map");
        command.add("[outv]");
        command.add("-map");
        command.add("2:a");

        // 8. 视频编码器设置
        command.add("-vcodec");
        command.add("libx264");
        command.add("-preset");
        command.add("ultrafast");
        command.add("-crf");
        command.add("23");
        command.add("-pix_fmt");
        command.add("yuv420p");

        // 9. 音频编码器设置
        command.add("-acodec");
        command.add("aac");
        command.add("-b:a");
        command.add("128k");

        // 10. 设置输出视频的持续时间 (可选)
        if (duration > 0) {
            command.add("-t");
            command.add(String.valueOf(duration));
        }

        // 11. 输出视频
        command.add(outputPath);

        // 12. 执行 FFmpeg 命令
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg 命令执行失败，退出代码: " + exitCode);
        } else {
            System.out.println("视频生成成功！");
        }
    }


    public static void main(String[] args) {
        String backgroundVideo = "D:\\0000\\background\\ocean_0001\\ocean_0001_pure_part.mp4";
        String image = "D:\\0000\\EnBook002\\Chapter001\\pic_cht\\Chapter001_total_phonetics_013.png";
        String audio = "D:\\0000\\EnBook002\\Chapter001\\audio_mix\\013.wav";
        String outputVideo = "D:\\0000\\background\\ocean_0001\\ocean_0001_pure_part_output.mp4";
        int duration = 23;

        try {
            createVideoWithOverlay(backgroundVideo, image, audio, outputVideo,
                    duration);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error creating video: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
