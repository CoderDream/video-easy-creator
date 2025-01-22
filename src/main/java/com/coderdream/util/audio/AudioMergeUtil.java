package com.coderdream.util.audio;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.video.BatchCreateVideoCommonUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AudioMergeUtil {

  /**
   * 合并多个无损WAV格式的音频文件
   *
   * @param audioFiles 音频文件的路径列表，必须为WAV格式
   * @param outputFile 合并后的输出文件路径
   * @throws IOException 如果IO操作出错
   */
  public static void mergeAudio(List<String> audioFiles, String fileName,
    String outputFile)
    throws IOException {
    // 记录合并开始时间
    long startTime = System.currentTimeMillis();
    log.info("开始合并音频文件：{}", String.join(", ", audioFiles));

    // 创建临时的文件列表，用于FFmpeg concat命令
    File fileList = createAudioFileList(audioFiles, fileName);

    // 使用ProcessBuilder执行FFmpeg命令进行合并
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command("ffmpeg", "-y", "-f", "concat", "-safe", "0", "-i",
      fileList.getAbsolutePath(), "-c", "copy", outputFile);

    // 打印命令，便于调试和查看
    log.info("执行命令：{}", String.join(" ", processBuilder.command()));

    // 启动FFmpeg进程
    Process process = processBuilder.start();
    try {
      int exitCode = process.waitFor();
      if (exitCode == 0) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("音频合并成功，输出文件：{}，耗时：{} 毫秒", outputFile, duration);
      } else {
        log.error("音频合并失败，FFmpeg进程退出码：{}", exitCode);
      }
    } catch (InterruptedException e) {
      log.error("音频合并过程中进程被中断", e);
    } finally {
      // 删除临时的文件列表
//        fileList.delete();
    }
  }

  /**
   * 创建包含音频文件路径的列表文件
   *
   * @param audioFiles 音频文件列表
   * @return 包含音频文件路径的临时列表文件
   * @throws IOException 如果IO操作出错
   */
  private static File createAudioFileList(List<String> audioFiles,
    String fileName)
    throws IOException {
    // 创建临时文件
    File tempFile = File.createTempFile(
      BatchCreateVideoCommonUtil.getAudioPath(fileName) + fileName  + "_audio_list_",
      ".txt");
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
      // 将每个音频文件路径写入文件，格式为FFmpeg concat协议要求的格式
      for (String filePath : audioFiles) {
        writer.write("file '" + filePath + "'\n");
      }
    }
    log.info("音频文件列表已创建：{}", tempFile.getAbsolutePath());
    return tempFile;
  }

  public static void main(String[] args) {
    try {
      // 示例音频文件列表
//            List<String> audioFiles = List.of(
//                "path/to/audio1.wav",
//                "path/to/audio2.wav",
//                "path/to/audio3.wav"
//            );

      String fileName = "CampingInvitation_cht_03";
      List<File> audioCnFiles = BatchCreateVideoCommonUtil.getAudioFiles(
        fileName,
        CdConstants.LANG_CN);
      List<File> audioEnFiles = BatchCreateVideoCommonUtil.getAudioFiles(
        fileName,
        CdConstants.LANG_EN);

      // 示例：传入视频文件路径列表
      List<String> audioCnFileNames = audioCnFiles.stream()
        .map(File::getAbsolutePath).toList();
      List<String> audioEnFileNames = audioEnFiles.stream()
        .map(File::getAbsolutePath).toList();

      List<String> audioFiles = new ArrayList<>();
      audioFiles.addAll(audioCnFileNames);
      audioFiles.addAll(audioEnFileNames);

      // 合并后的输出文件路径
      String outputFile =
        BatchCreateVideoCommonUtil.getAudioPath(fileName) + fileName
          + "_output_merged_audio.wav";
      mergeAudio(audioFiles, fileName, outputFile);
    } catch (IOException e) {
      log.error("合并音频时出错", e);
    }
  }
}
