package com.coderdream.util.video.demo07;

import com.coderdream.util.cmd.CommandUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FFmpegVideoGenerator {

  public static void generateVideo(String basePath, String inputVideoPath,
    String outputVideoPath, String assPath,
    int width, int height, String maskColor, double maskOpacity,
    String videoCodec, String audioCodec, boolean overwrite)
    throws IOException, InterruptedException {
    // 構建完整的文件路徑
    File inputFile = new File(basePath, inputVideoPath);
    File assFile = new File(basePath, assPath);
    File outputFile = new File(basePath, outputVideoPath);

    // 文件存在性檢查
    if (!inputFile.exists()) {
      log.error("輸入視頻文件不存在：{}", inputFile.getAbsolutePath());
      throw new IOException(
        "輸入視頻文件不存在：" + inputFile.getAbsolutePath());
    }
    if (!assFile.exists()) {
      log.error("ASS 文件不存在：{}", assFile.getAbsolutePath());
      throw new IOException("ASS 文件不存在：" + assFile.getAbsolutePath());
    }

    // 使用規範化路徑
//        String normalizedInputPath = inputFile.getCanonicalPath();
//        String normalizedOutputPath = outputFile.getCanonicalPath();
//        String normalizedAssPath = assFile.getCanonicalPath();

    // 構建 FFmpeg 命令
    List<String> command = buildFFmpegCommand(
      inputVideoPath, outputVideoPath, assPath,
      width, height, maskColor, maskOpacity, videoCodec, audioCodec, overwrite
    );

    String fullCommand = String.join(" ", command);
    log.info("執行 FFmpeg 命令：{}", fullCommand);

    // 執行命令
    String cdCommand = "cd " + basePath + " && " + fullCommand;
    CommandUtil.executeCommand(cdCommand);
    int exitCode = 0;

    if (exitCode == 0) {
      log.info("視頻生成成功：{}", outputVideoPath);
    } else {
      log.error("FFmpeg 執行失敗，退出碼：{}", exitCode);
      throw new IOException("FFmpeg 執行失敗，退出碼：" + exitCode);
    }
  }

  //    private static List<String> buildFFmpegCommand(String inputPath, String outputPath, String assPath,
//                                                  int width, int height, String maskColor, double maskOpacity,
//                                                  String videoCodec, String audioCodec, boolean overwrite) {
//        List<String> command = new ArrayList<>();
//        command.add("ffmpeg");
//        if (overwrite) {
//            command.add("-y");
//        }
//        command.add("-i");
//        command.add(inputPath);
//        command.add("-vf");
//        // 使用單引號包裹字幕路徑，確保特殊字符正確處理，與目標命令一致
//        String filter = String.format(
//          "drawbox=x=0:y=0:w=%d:h=%d:c=%s@%.1f:t=fill,subtitles=%s",
//          width, height, maskColor, maskOpacity, assPath
//        );
//        command.add("\"" + filter + "\""); // 手動添加雙引號
//        command.add("-c:v");
//        command.add(videoCodec);
//        command.add("-c:a");
//        command.add(audioCodec);
//        command.add(outputPath);
//        return command;
//    }
  private static List<String> buildFFmpegCommand(String inputPath,
    String outputPath, String assPath,
    int width, int height, String maskColor, double maskOpacity,
    String videoCodec, String audioCodec, boolean overwrite) {
    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    if (overwrite) {
      command.add("-y");
    }
    command.add("-i");
    command.add(inputPath);
    command.add("-vf");

    // 使用單引號包裹字幕路徑，確保特殊字符正確處理，與目標命令一致
    // 添加 force_style 选项，强制 subtitles 滤镜使用指定的分辨率
    String filter = String.format(
      "drawbox=x=0:y=0:w=%d:h=%d:c=%s@%.1f:t=fill,subtitles=%s:force_style='PlayResX=%d,PlayResY=%d'",
      width, height, maskColor, maskOpacity, assPath, width, height
    );

    command.add("\"" + filter + "\""); // 手動添加雙引號
    command.add("-c:v");
    command.add(videoCodec);
    command.add("-c:a");
    command.add(audioCodec);
    command.add(outputPath);
    return command;
  }

  public static void main(String[] args) {
    try {
      String path = "D:\\0000\\EnBook005\\Chapter001";
      String chapterName = "Chapter001";

      String inputVideoPath = chapterName + ".mp4";
      String outputVideoPath = "output7.mp4";
      String assPath = "subtitles4.ass";
      int width = 1920;
      int height = 1080;
      String maskColor = "black";
      double maskOpacity = 0.5;
      String videoCodec = "h264_nvenc";
//                        String videoCodec = "libx264";
      String audioCodec = "copy";
      boolean overwrite = true;

      generateVideo(path, inputVideoPath, outputVideoPath, assPath, width,
        height, maskColor,
        maskOpacity, videoCodec, audioCodec, overwrite);
    } catch (IOException | InterruptedException e) {
      log.error("視頻生成過程發生錯誤", e);
    }
  }
}
