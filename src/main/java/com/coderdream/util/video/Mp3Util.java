package com.coderdream.util.video;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mp3Util {

  public static void main(String[] args) {
    File mp3File = new File("D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\mp3\\cn\\CampingInvitation_001_cn.mp3");
    try {
      File adjustedAudio = createAudioWithSilence(mp3File);
      log.info("调整后的音频文件: {}", adjustedAudio.getAbsolutePath());
    } catch (IOException | InterruptedException e) {
      log.error("处理音频文件时发生错误: {}", e.getMessage(), e);
    }
  }

  public static File createAudioWithSilence(File mp3File)
    throws IOException, InterruptedException {
    File silenceFile1 = new File(mp3File.getParent(), "silence_400ms.wav");
    File silenceFile2 = new File(mp3File.getParent(), "silence_300ms.wav");
    File adjustedAudioFile = new File(mp3File.getParent(),
      "adjusted_" + mp3File.getName());

    // 1. 生成前400ms静音
    executeFFmpegCommand(Arrays.asList(
      "ffmpeg", "-y",
      "-f", "lavfi",
      "-i", "anullsrc=channel_layout=stereo:sample_rate=44100",
      "-t", "0.4",
      silenceFile1.getAbsolutePath()
    ), "生成400ms静音");

    // 2. 生成后300ms静音
    executeFFmpegCommand(Arrays.asList(
      "ffmpeg", "-y",
      "-f", "lavfi",
      "-i", "anullsrc=channel_layout=stereo:sample_rate=44100",
      "-t", "0.3",
      silenceFile2.getAbsolutePath()
    ), "生成300ms静音");

    // 3. 合并静音和原始音频
    executeFFmpegCommand(Arrays.asList(
      "ffmpeg", "-y",
      "-i", "concat:" +
        silenceFile1.getAbsolutePath() + "|" +
        mp3File.getAbsolutePath() + "|" +
        silenceFile2.getAbsolutePath(),
      "-acodec", "copy",
      adjustedAudioFile.getAbsolutePath()
    ), "合并静音与原始音频");

    // 校验生成文件
    if (!adjustedAudioFile.exists() || adjustedAudioFile.length() == 0) {
      throw new IOException(
        "生成的音频文件为空: " + adjustedAudioFile.getAbsolutePath());
    }

    // 清理临时文件
    silenceFile1.delete();
    silenceFile2.delete();

    return adjustedAudioFile;
  }

  // 通用的FFmpeg命令执行方法
  private static void executeFFmpegCommand(List<String> command,
    String stepDescription) throws IOException, InterruptedException {
    log.info("执行命令 [{}]: {}", stepDescription, String.join(" ", command));

    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true); // 将错误流和输出流合并，方便日志收集

    Process process = processBuilder.start(); // 启动进程

    // 读取并打印日志输出
    try (BufferedReader reader = new BufferedReader(
      new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.info("[FFmpeg] {}", line);
      }
    }

    // 等待进程结束
    int exitCode = process.waitFor();
    if (exitCode != 0) {
      throw new IOException("FFmpeg 命令失败，退出码: " + exitCode);
    }
  }

}
