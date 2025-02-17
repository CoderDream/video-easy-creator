package com.coderdream.util.ffmpeg;

import com.coderdream.util.cd.CdTimeUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

@Slf4j
public class FfmpegUtil {

  /**
   * 获取AUDIO文件的时长
   *
   * @param audioFile AUDIO文件
   * @return AUDIO的时长（秒）
   */
  public static double getAudioDuration(File audioFile) {
    long startTime = System.currentTimeMillis();
    // 检查文件是否存在
    if (audioFile == null || !audioFile.exists()) {
      log.error("提供的文件不存在或为空: {}", audioFile);
      return 0;
    }

    // 构建ffmpeg命令
    String command =
      "ffmpeg -i " + audioFile.getAbsolutePath() + " 2>&1"; // 通过stderr获取时长信息
    log.info("正在执行ffmpeg命令：{}", command);

    // 使用ProcessBuilder来执行命令
    ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-i",
      audioFile.getAbsolutePath());
    processBuilder.redirectErrorStream(true); // 将错误输出与标准输出合并

    try {
      // 启动进程
      Process process = processBuilder.start();

      // 读取ffmpeg输出
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        //  log.debug("ffmpeg输出: {}", line);  // 打印ffmpeg的输出（调试用）

        // 查找包含"Duration"的行，提取音频时长
        if (line.contains("Duration")) {
//          log.info("找到时长信息：{}", line);

          // 从ffmpeg输出中提取时长
          String durationStr = DurationParser.extractDuration(line);

          if (Strings.isBlank(durationStr)) {
            log.error("提取的时长为空: {}", durationStr);
            return 0;  // 如果时长提取失败，返回0秒
          }

          // 分析时长字符串，解析时、分、秒
          String[] timeParts = durationStr.split(":");
          if (timeParts.length != 3) {
            log.error("音频时长格式不正确: {}", durationStr);
            return 0;  // 如果时长格式不正确，返回0秒
          }

          try {
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            double seconds = Double.parseDouble(timeParts[2]);
            double duration = hours * 3600 + minutes * 60 + seconds;
            // 返回总时长（秒）
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime;
            log.info("获取音频时长成功文件: {} 时长 {}, 耗时: {}",
              audioFile.getAbsolutePath(), duration,
              CdTimeUtil.formatDuration(durationMillis));
            return duration;
          } catch (NumberFormatException e) {
            log.error("解析时长时出错，时长字符串: {}", durationStr, e);
            return 0;  // 如果解析失败，返回0秒
          }
        }
      }

    } catch (IOException e) {
      log.error("执行ffmpeg命令时发生异常：{}", e.getMessage(), e);
    }

    // 如果没有找到时长信息，返回0秒
    log.warn("未能提取到音频文件时长: {}", audioFile.getAbsolutePath());
    return 0;
  }

  public static void main(String[] args) {
    File audioFile = new File(
      "D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\wav\\en\\CampingInvitation_cht_001_en.wav");
    double duration = getAudioDuration(audioFile);
    System.out.println("音频时长: " + duration + " 秒");
  }
}
