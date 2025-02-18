package com.coderdream.util.audio;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;

/**
 * 使用FFmpeg合并两个WAV文件的工具类 (特定目录和文件名, 内部处理异常, 使用Slf4j)
 */
@Slf4j
public class WavMergeUtil {

  /**
   * 合并两个WAV文件 (特定目录和文件名, 内部处理异常)
   *
   * @param directory  WAV文件所在的目录
   * @param engName    英文WAV文件名 (不含扩展名)
   * @param chnName    中文WAV文件名 (不含扩展名)
   * @param mergedName 合并后的WAV文件名 (不含扩展名)
   * @return 合并后的WAV文件, 如果合并失败则返回null
   */
  public static File mergeWavFiles(String directory, String engName,
    String chnName, String mergedName) {
    Instant start = Instant.now();

    // 构建文件对象
    File engFile = new File(directory, engName + ".wav");
    File chnFile = new File(directory, chnName + ".wav");
    File mergedFile = new File(directory, mergedName + ".wav");

    // 检查文件是否存在
    if (!engFile.exists() || !chnFile.exists()) {
      log.error("输入文件不存在：engFile={}, chnFile={}",
        engFile.getAbsolutePath(), chnFile.getAbsolutePath());
      return null; // 文件不存在，返回null
    }

    // 构建FFmpeg命令行 (eng.wav 在前, chn.wav 在后)
    ProcessBuilder processBuilder = new ProcessBuilder(
      "ffmpeg",
      "-i",
      "concat:" + engFile.getAbsolutePath() + "|" + chnFile.getAbsolutePath(),
      "-acodec", "copy",
      mergedFile.getAbsolutePath()
    );
    processBuilder.redirectErrorStream(true); // 合并错误输出流

    try {
      // 启动FFmpeg进程
      Process process = processBuilder.start();

      // 使用try-with-resources读取FFmpeg的输出流
      try (InputStream inputStream = process.getInputStream()) {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
          log.info(new String(buffer, 0, bytesRead));
        }
      }

      // 等待FFmpeg进程执行完毕
      int exitCode = process.waitFor();

      // 检查FFmpeg的退出码
      if (exitCode != 0) {
        log.error("FFmpeg合并WAV文件失败，退出码：{}", exitCode);
        return null; // 合并失败，返回null
      }

      // 记录方法调用耗时
      Instant end = Instant.now();
      Duration duration = Duration.between(start, end);
      log.info("WAV文件合并完成，耗时：{}时{}分{}秒{}毫秒",
        duration.toHoursPart(), duration.toMinutesPart(),
        duration.toSecondsPart(), duration.toMillisPart());

      // 返回合并后的文件
      return mergedFile;

    } catch (IOException | InterruptedException e) {
      log.error("合并WAV文件时发生异常: {}", e.getMessage(), e); // 更详细的日志
      return null; // 发生异常，返回null
    }
  }

  /**
   * 测试方法
   *
   * @param args
   */
  public static void main(String[] args) {
    // 指定目录和文件名
    String directory = "D:\\0000\\bgmusic\\";
    String engName = "eng";
    String chnName = "chn";
    String mergedName = "head";

    // 调用合并方法
    File mergedFile = WavMergeUtil.mergeWavFiles(directory, engName, chnName,
      mergedName);
    if (mergedFile != null) {
      log.info("合并后的文件路径：{}", mergedFile.getAbsolutePath());
    } else {
      log.info("合并失败！");
    }
  }
}
