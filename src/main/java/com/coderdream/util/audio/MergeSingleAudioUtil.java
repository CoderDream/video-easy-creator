package com.coderdream.util.audio;

import com.coderdream.util.CdConstants;
import com.coderdream.util.CdTimeUtil;
import com.coderdream.util.video.BatchCreateVideoCommonUtil;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MergeSingleAudioUtil {

  /**
   * 合并多个无损WAV格式的音频文件
   *
   * @param fileName 合并后的输出文件路径 return 合并后的音频文件
   */
  public static File mergeAudio(String fileName) {

    String outputFileName =
      BatchCreateVideoCommonUtil.getAudioPath(fileName) + fileName + "."
        + CdConstants.AUDIO_TYPE_WAV;

    File mergeFile = new File(outputFileName);

    // 记录合并开始时间
    long startTime = System.currentTimeMillis();
    log.info("开始合并音频文件：{}", String.join(", ", fileName));

    // 创建临时的文件列表，用于FFmpeg concat命令
    File fileList = BatchCreateVideoCommonUtil.createAudioFileList(fileName);

    // 使用ProcessBuilder执行FFmpeg命令进行合并
// 使用 StringBuilder 构建 FFmpeg 命令
    StringBuilder commandBuilder = new StringBuilder();

// 添加 FFmpeg 执行命令的各个部分
    commandBuilder.append("ffmpeg "); // 启动 FFmpeg

// 添加 '-y' 参数，表示自动确认所有询问（例如覆盖文件等）
    commandBuilder.append("-y ");

// 添加 '-f concat' 参数，指定使用 concat 协议来合并文件
    commandBuilder.append("-f concat ");

// 添加 '-safe 0' 参数，允许路径包含特殊字符
    commandBuilder.append("-safe 0 ");

// 添加 '-i' 参数，指定输入文件列表
    commandBuilder.append("-i ").append(fileList.getAbsolutePath()).append(" ");

// 添加 '-c copy' 参数，确保音频或视频流不被转码
    commandBuilder.append("-c copy ");

// 添加输出文件路径
    commandBuilder.append(mergeFile.getAbsolutePath());

// 转换为字符串并打印生成的命令，便于调试
    String commandString = commandBuilder.toString();
    log.info("构建的 FFmpeg 命令：{}", commandString);

// 使用 ProcessBuilder 执行命令
    ProcessBuilder processBuilder = new ProcessBuilder(
      commandString.split(" "));
    processBuilder.redirectErrorStream(true); // 合并标准输出和标准错误输出

// 启动进程并等待执行完毕
    try {
      Process process = processBuilder.start();
      int exitCode = process.waitFor();
      if (exitCode == 0) {
        long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
        long durationMillis = endTime - startTime; // 计算耗时（毫秒）
        log.info("音频合并成功: {}，耗时: {}", mergeFile.getAbsolutePath(),
          CdTimeUtil.formatDuration(durationMillis));
      } else {
        log.error("音频合并失败，FFmpeg 进程退出码：{}", exitCode);
      }
    } catch (IOException | InterruptedException e) {
      log.error("执行 FFmpeg 命令时出错", e);
    }

    return mergeFile;
  }


  public static void main(String[] args) {
//    try {
//      // 示例音频文件列表
////            List<String> audioFiles = List.of(
////                "path/to/audio1.wav",
////                "path/to/audio2.wav",
////                "path/to/audio3.wav"
////            );
//
//      String fileName = "CampingInvitation_cht_03";
//      List<File> audioCnFiles = BatchCreateVideoCommonUtil.getAudioFiles(
//        fileName,
//        CdConstants.LANG_CN);
//      List<File> audioEnFiles = BatchCreateVideoCommonUtil.getAudioFiles(
//        fileName,
//        CdConstants.LANG_EN);
//
//      // 示例：传入视频文件路径列表
//      List<String> audioCnFileNames = audioCnFiles.stream()
//        .map(File::getAbsolutePath).toList();
//      List<String> audioEnFileNames = audioEnFiles.stream()
//        .map(File::getAbsolutePath).toList();
//
//      List<String> audioFiles = new ArrayList<>();
//      audioFiles.addAll(audioCnFileNames);
//      audioFiles.addAll(audioEnFileNames);
//
//      // 合并后的输出文件路径
//      String outputFile =
//        BatchCreateVideoCommonUtil.getAudioPath(fileName) + fileName
//          + "_output_merged_audio.wav";
//      mergeAudio(audioFiles, fileName, outputFile);
//    } catch (IOException e) {
//      log.error("合并音频时出错", e);
//    }
  }
}
