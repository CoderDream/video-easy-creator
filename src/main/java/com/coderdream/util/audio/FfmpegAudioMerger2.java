package com.coderdream.util.audio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FfmpegAudioMerger2 {

  /**
   * 合并多个音频文件到一个文件（确保所有音频文件是24kHz, 16-bit, 单声道 PCM 格式）
   *
   * @param audioFiles 音频文件列表
   * @param outputFile 输出合并后的音频文件
   * @throws IOException          如果合并过程中发生I/O异常
   * @throws InterruptedException 如果进程执行过程中发生中断
   */
  public static void mergeAudioFiles(List<File> audioFiles, File outputFile)
    throws IOException, InterruptedException {
    // 创建一个临时的文本文件，用于存储音频文件列表
    File tempFileList = File.createTempFile("audio_files_list", ".txt");
    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(tempFileList))) {
      for (File audioFile : audioFiles) {
        // 确保音频文件是24kHz，16-bit，Mono PCM格式
        File convertedFile = convertToRequiredFormat(audioFile);

        // 写入音频文件路径，FFmpeg需要的格式是file 'path/to/audio.wav'
        writer.write("file '" + convertedFile.getAbsolutePath() + "'");
        writer.newLine();
      }
    }

    // FFmpeg命令：使用concat协议合并多个音频文件
    String ffmpegCommand = String.format(
      "ffmpeg -f concat -safe 0 -i %s -c copy %s",
      tempFileList.getAbsolutePath(), outputFile.getAbsolutePath());

    // 使用ProcessBuilder执行FFmpeg命令
    ProcessBuilder processBuilder = new ProcessBuilder(
      ffmpegCommand.split(" "));
    processBuilder.redirectErrorStream(true);

    log.info("执行FFmpeg命令：{}", ffmpegCommand);

    Process process = processBuilder.start();

    // 获取并打印FFmpeg的输出信息
    try (BufferedReader reader = new BufferedReader(
      new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.info(line);
      }
    }

    // 等待FFmpeg命令执行完成
    int exitCode = process.waitFor();
    if (exitCode == 0) {
      log.info("音频合并成功，输出文件：{}", outputFile.getAbsolutePath());
    } else {
      log.error("音频合并失败，FFmpeg命令退出代码：{}", exitCode);
    }

    // 删除临时文件
    if (tempFileList.exists()) {
      tempFileList.delete();
    }
  }

  /**
   * 确保音频文件是24kHz，16-bit，Mono PCM格式
   *
   * @param audioFile 音频文件
   * @return 转换后的音频文件
   * @throws IOException 如果转换过程发生异常
   */
  private static File convertToRequiredFormat(File audioFile)
    throws IOException {
    File convertedFile = new File(audioFile.getParent(),
      "converted_" + audioFile.getName());

    // FFmpeg命令：转换为 24kHz, 16-bit, Mono PCM 格式
    String ffmpegCommand = String.format(
      "ffmpeg -i %s -ar 24000 -ac 1 -sample_fmt s16 %s",
      audioFile.getAbsolutePath(), convertedFile.getAbsolutePath());

    ProcessBuilder processBuilder = new ProcessBuilder(
      ffmpegCommand.split(" "));
    processBuilder.redirectErrorStream(true);

    log.info("正在转换音频文件：{}", audioFile.getName());

    Process process = processBuilder.start();

    // 获取并打印FFmpeg的输出信息
    try (BufferedReader reader = new BufferedReader(
      new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.info(line);
      }
    }

    // 等待FFmpeg命令执行完成
    try {
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        log.error("音频转换失败，FFmpeg命令退出代码：{}", exitCode);
        throw new IOException("音频转换失败");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("音频转换过程被中断", e);
      throw new IOException("音频转换中断", e);
    }

    return convertedFile;
  }

  public static void main(String[] args) {
    // 测试代码：假设音频文件已知
    List<File> audioFiles = List.of(
      new File("D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\wav\\en\\CampingInvitation_cht_001_en.wav"),
      new File("D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\wav\\en\\CampingInvitation_cht_001_en.wav"),
      new File("D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\wav\\en\\CampingInvitation_cht_001_en.wav"),
      new File("D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\wav\\cn\\CampingInvitation_cht_001_cn.wav"),
      new File("D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\wav\\en\\CampingInvitation_cht_001_en.wav"),
      new File("D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\wav\\en\\CampingInvitation_cht_002_en.wav"),
      new File("D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\wav\\en\\CampingInvitation_cht_002_en.wav"),
      new File("D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\wav\\en\\CampingInvitation_cht_002_en.wav"),
      new File("D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\wav\\cn\\CampingInvitation_cht_002_cn.wav"),
      new File("D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\wav\\en\\CampingInvitation_cht_002_en.wav")
    );
    File outputFile = new File("D:/output_merged_audio.wav");

    try {
      mergeAudioFiles(audioFiles, outputFile);
    } catch (IOException | InterruptedException e) {
      log.error("音频合并过程中发生错误", e);
    }
  }
}
