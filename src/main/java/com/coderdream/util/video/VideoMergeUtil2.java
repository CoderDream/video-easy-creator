package com.coderdream.util.video;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdTimeUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VideoMergeUtil2 {

  /**
   * 合并所有生成的视频文件
   *
   * @param fileName 中文视频文件列表
   */
  public static File mergeVideos(String fileName) {
    List<File> videoCnFiles = BatchCreateVideoCommonUtil.getVideoFiles(fileName,
      CdConstants.LANG_CN);
    List<File> videoEnFiles = BatchCreateVideoCommonUtil.getVideoFiles(fileName,
      CdConstants.LANG_EN);
    return mergeVideos(videoCnFiles, videoEnFiles, fileName);
  }

  /**
   * 合并所有生成的视频文件
   *
   * @param videoCnFiles 中文视频文件列表
   * @param videoEnFiles 英文视频文件列表
   */
  public static File mergeVideos(List<File> videoCnFiles,
    List<File> videoEnFiles, String fileName) {
    String mergeFilename =
      BatchCreateVideoCommonUtil.getVideoPath(fileName) + fileName + ".mp4";
    File mergeFile = new File(mergeFilename);
    long startTime = System.currentTimeMillis(); // 记录合并开始时间
    // 如果两个列表为空或者大小不相等，则直接报错退出
    if (videoCnFiles.isEmpty() || videoEnFiles.isEmpty()
      || videoCnFiles.size() != videoEnFiles.size()) {
      log.error("视频文件列表为空或大小不相等，无法合并");
      return null;
    }

    int size = videoCnFiles.size();
    ProcessBuilder processBuilder;

    try {
      // 创建合并文件列表
      String fileListFileName = BatchCreateVideoCommonUtil.getVideoPath(fileName) + "file_list.txt";
      File listFile = new File(fileListFileName);
      try (BufferedWriter writer = new BufferedWriter(
        new FileWriter(listFile))) {
        for (int i = 0; i < size; i++) {
          // 写入每个视频文件的路径
          File videoCnFile = videoCnFiles.get(i);
          File videoEnFile = videoEnFiles.get(i);
          // 先播放3次英文，再播放1次中文，再播放1次英文，以此类推
          writer.write("file '" + videoEnFile.getAbsolutePath() + "'\n");
          writer.write("file '" + videoEnFile.getAbsolutePath() + "'\n");
          writer.write("file '" + videoEnFile.getAbsolutePath() + "'\n");
          writer.write("file '" + videoCnFile.getAbsolutePath() + "'\n");
          writer.write("file '" + videoEnFile.getAbsolutePath() + "'\n");
        }
      }
      // 构建FFmpeg命令
      List<String> command = new ArrayList<>();
      command.add("ffmpeg"); // 1. 使用 ffmpeg 工具
      command.add("-y"); // 2. 自动回答 "yes" 给所有询问（如覆盖输出文件等）
      command.add("-f"); // 3. 设置输入文件格式
      command.add("concat"); // 4. 使用 concat 模式进行文件合并
      command.add("-safe"); // 5. 允许路径包含特殊字符
      command.add("0"); // 6. 开启 "safe" 模式
      command.add("-i"); // 7. 输入参数
      command.add(listFile.getAbsolutePath()); // 输入文件列表
      command.add("-c"); // 8. 指定编码格式
      command.add("copy"); // 9. 使用 copy 模式，不重新编码
      command.add(mergeFile.getAbsolutePath()); // 10. 输出路径

      // 打印命令，便于调试和查看
      String commandString = String.join(" ", command);
      log.info("正在执行命令：{}", commandString);

      // 创建 ProcessBuilder，传入命令和参数
      processBuilder = new ProcessBuilder(command);
      processBuilder.redirectErrorStream(true); // 合并标准输出和标准错误输出

      // 启动进程
      Process process = processBuilder.start();

      // 读取并打印ffmpeg的输出流
//      try (BufferedReader reader = new BufferedReader(
//        new InputStreamReader(process.getInputStream()))) {
//        String line;
//        while ((line = reader.readLine()) != null) {
////          log.info("ffmpeg输出: {}", line);  // 打印ffmpeg的日志输出
//        }
//      }

      // 等待进程执行完毕
      int exitCode = process.waitFor();

      // 检查执行结果
      if (exitCode == 0) {
        long endTime = System.currentTimeMillis(); // 记录合并结束时间
        long durationMillis = endTime - startTime; // 计算总耗时（毫秒）
        log.info("视频合并成功，最终视频路径: {}，耗时: {}",
          mergeFilename, CdTimeUtil.formatDuration(durationMillis));
      } else {
        log.error("视频合并失败");
      }

      // 删除临时的文件列表
      // listFile.delete();

    } catch (Exception e) {
      log.error("合并视频时出错", e);
    }

    return new File(mergeFilename);
  }

}
