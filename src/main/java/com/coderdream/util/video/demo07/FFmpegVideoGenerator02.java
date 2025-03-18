package com.coderdream.util.video.demo07;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FFmpegVideoGenerator02 {

  public static void generateVideo(String inputVideoPath,
    String outputVideoPath, String assPath,
    int width, int height, String maskColor, double maskOpacity,
    String videoCodec, String audioCodec, boolean overwrite)
    throws IOException, InterruptedException {
    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    if (overwrite) {
      command.add("-y"); // 覆蓋輸出文件
    }
    command.add("-i");
    command.add(inputVideoPath);
    command.add("-vf");
    command.add(
      String.format("drawbox=x=0:y=0:w=%d:h=%d:c=%s@%.1f:t=fill,subtitles='%s'",
        width, height, maskColor, maskOpacity, assPath));
    command.add("-c:v");
    command.add(videoCodec);
    command.add("-c:a");
    command.add(audioCodec);
    command.add(outputVideoPath);

    ProcessBuilder pb = new ProcessBuilder(command);
    pb.inheritIO(); // 將 FFmpeg 輸出顯示在控制台
    Process process = pb.start();
    int exitCode = process.waitFor();
    if (exitCode == 0) {
      System.out.println("視頻生成成功：" + outputVideoPath);
    } else {
      System.err.println("FFmpeg 執行失敗，退出碼：" + exitCode);
    }
  }

  public static void main(String[] args)
    throws IOException, InterruptedException {

    String path = "D:\\0000\\EnBook005\\Chapter001";
    String chapterName = "Chapter001";
//      String inputPath = path + File.separator + chapterName + ".srt";
    // 初始化參數 - FFmpegVideoGenerator
    String inputVideoPath =
      path + File.separator + chapterName + ".mp4";             // 輸入視頻
    String outputVideoPath =
      path + File.separator + "output6.mp4";          // 輸出視頻
    String assPath =
      path + File.separator + "subtitles3.ass";               // ASS 文件路徑
    int width = 1920;                                // 蒙版寬度
    int height = 1080;                               // 蒙版高度
    String maskColor = "black";                      // 蒙版顏色
    double maskOpacity = 0.5;                        // 蒙版透明度
//    String videoCodec = "libx264";                   // 視頻編碼器
    String videoCodec = "h264_nvenc"; // 使用 NVIDIA NVENC H.264 編碼器
    String audioCodec = "copy";                      // 音頻編碼器
    boolean overwrite = true;                        // 是否覆蓋輸出文件
    // 調用方法生成視頻
    generateVideo(
      inputVideoPath, outputVideoPath, assPath, width, height, maskColor,
      maskOpacity, videoCodec, audioCodec, overwrite);

    // 示例用法
//        generateVideo(
//            "input.mp4",           // 輸入視頻
//            "output6.mp4",         // 輸出視頻
//            "subtitles3.ass",      // ASS 文件
//            1920, 1080,            // 分辨率
//            "black", 0.5,          // 蒙版顏色和透明度
//            "libx264", "copy",     // 視頻和音頻編碼器
//            true                   // 是否覆蓋
//        );
  }
}
