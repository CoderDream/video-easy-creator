package com.coderdream.util.audio;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AudioMergerDualEnBatch {

  /**
   * 合并指定目录下中英文wav文件，并输出到指定目录
   *
   * @param inputDirEn     英文wav文件目录
   * @param outputFilePath 输出文件路径
   */
  public static File mergeWavFiles(String inputDirEn,
    String outputFilePath) {
    long startTime = System.currentTimeMillis(); // 记录方法开始时间
    log.info("开始合并WAV文件，英文目录：{}, 输出目录：{}",
      inputDirEn, outputFilePath);

    List<String> wavFilesEn = listWavFiles(inputDirEn); // 获取英文音频文件列表
    if (wavFilesEn.isEmpty()) {
      log.warn("英文目录{}下没有找到wav文件", inputDirEn);
      return null;
    }

    //

    File file = new File(outputFilePath);
    if (!file.exists()) {
      boolean mkdir = file.mkdirs();
      log.info("文件夹创建成功 {}", mkdir);
    } else {
      log.info("文件夹已存在");
    }

    List<String> listFiles = createListFile(wavFilesEn,       outputFilePath); // 创建 ffmpeg 需要的 list 文件

    int corePoolSize = 16; // 核心线程数，设为CPU核心数
    // 最大线程数，和核心线程数保持一致
    long keepAliveTime = 60L; // 线程空闲后的存活时间
    TimeUnit timeUnit = TimeUnit.SECONDS; // 时间单位
    // 使用 LinkedBlockingQueue 避免线程池队列过长
    ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize,
      corePoolSize,
      keepAliveTime, timeUnit, new LinkedBlockingQueue<>(listFiles.size()),
      new ThreadPoolExecutor.CallerRunsPolicy());

    try {
      for (String fileListFileName : listFiles) {
        executor.execute(() -> {
          long taskStartTime = System.currentTimeMillis();
          String indexTag = StringExtractor.extractLastNumber(fileListFileName);
          String outputFilePathWithIndex = outputFilePath + indexTag + ".wav";
          File outputFile = new File(outputFilePathWithIndex);
          if (!outputFile.exists() && outputFile.length() == 0) {

            try {
              FfmpegUtil2.executeFfmpegMerge(fileListFileName,
                outputFilePathWithIndex);
              log.info("合并 {} 完成, 耗时: {} ms", outputFilePathWithIndex,
                (System.currentTimeMillis() - taskStartTime));
            } catch (IOException e) {
              log.error("合并文件 {} 失败: {}", outputFilePathWithIndex,
                e.getMessage());
            }
          } else {
            log.info("待合并文件 {} 已经存在，跳过合并",
              outputFilePathWithIndex);
          }
          boolean delete = new File(fileListFileName).delete();
          log.info("删除临时文件 {} 成功: {}", fileListFileName, delete);
        });
      }

      executor.shutdown();
      try {
        if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
          log.warn("线程池关闭超时");
        }
      } catch (InterruptedException e) {
        log.error("等待线程池结束时发生中断：{}", e.getMessage());
        Thread.currentThread().interrupt();
      }

      log.info("WAV文件合并完成，输出路径为: {}", outputFilePath);
    } finally {
      executor.shutdownNow(); // 立即关闭线程池，防止资源泄漏
      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;
      String formattedTime = CdTimeUtil.formatDuration(duration);
      log.info("合并WAV文件任务完成, 总耗时: {}", formattedTime);
    }
    return file;
  }



  /**
   * 格式化耗时为时分秒毫秒
   *
   * @param duration 耗时，单位毫秒
   * @return 格式化的耗时字符串
   */
  private static String formatDuration(long duration) {
    long milliseconds = duration % 1000;
    long seconds = (duration / 1000) % 60;
    long minutes = (duration / (1000 * 60)) % 60;
    long hours = (duration / (1000 * 60 * 60));
    return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds,
      milliseconds);
  }

  /**
   * 列出指定目录下所有 .wav 文件
   *
   * @param inputDir 输入目录
   * @return 文件路径列表
   */
  public static List<String> listWavFiles(String inputDir) {
    File dir = new File(inputDir);
    if (!dir.exists() || !dir.isDirectory()) {
      log.error("目录不存在或不是目录: {}", inputDir);
      return new ArrayList<>();
    }
    File[] files = dir.listFiles();
    if (Objects.isNull(files)) {
      return new ArrayList<>();
    }

    List<String> collect = Arrays.stream(files)
      .filter(
        file -> file.isFile() && file.getName().toLowerCase().endsWith(".wav"))
      .map(File::getAbsolutePath)
      .collect(Collectors.toList());

    collect= collect.stream().sorted().collect(Collectors.toList());
    log.info("找到 {} 个 .wav 文件", collect.size());
    return collect;
  }

  /**
   * 创建 ffmpeg 需要的 list.txt 文件
   *
   * @param wavFilesEn     英文wav文件列表
   * @param outputFilePath 输出路径
   * @return list文件路径列表
   */
  private static List<String> createListFile(List<String> wavFilesEn,
    String outputFilePath) {
    List<String> listFiles = new ArrayList<>();
    // 如果两个列表大小不一致则立即退出
    String wavFileNameEn;
    for (String s : wavFilesEn) {
      wavFileNameEn = s;
      String indexTag = StringExtractor.extractNumber(wavFileNameEn);
      // 三遍英文 一遍中文，一遍英文
      String listFilePath = outputFilePath + "list_" + indexTag + ".txt";
      try (BufferedWriter writer = new BufferedWriter(
        new FileWriter(listFilePath))) {
        // 增加翻页间隔，此处仅为示例
        writer.write(
          "file '" + OperatingSystem.getBaseFolderWav("page.wav") + "'");
        writer.newLine();
        writer.write("file '" + wavFileNameEn + "'");
        writer.newLine();
        writer.write("file '" + wavFileNameEn + "'");
        writer.newLine();
      } catch (IOException e) {
        log.error("创建文件 {} 失败: {}", listFilePath, e.getMessage());
      }
      listFiles.add(listFilePath);
    }

    return listFiles;
  }



}
