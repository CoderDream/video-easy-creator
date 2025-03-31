package com.coderdream.util.audio;

import com.coderdream.util.cd.CdTimeUtil;
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

import com.coderdream.util.proxy.OperatingSystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AudioMergerSingleBatch {

  /**
   * 合并指定目录下中英文wav文件，并输出到指定目录
   *
   * @param inputDirCn     中文wav文件目录
   * @param inputDirEn     英文wav文件目录
   * @param outputFilePath 输出文件路径
   */
  public static File mergeWavFiles(String inputDirCn, String inputDirEn,
    String outputFilePath) {
    long startTime = System.currentTimeMillis(); // 记录方法开始时间
    log.info("开始合并WAV文件，中文目录：{}, 英文目录：{}, 输出目录：{}",
      inputDirCn, inputDirEn, outputFilePath);

    List<String> wavFilesCn = listWavFiles(inputDirCn); // 获取中文音频文件列表
    if (wavFilesCn.isEmpty()) {
      log.warn("中文目录{}下没有找到wav文件", inputDirCn);
      return null;
    }

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

    List<String> listFiles = createListFile(wavFilesEn, wavFilesCn,
      outputFilePath); // 创建 ffmpeg 需要的 list 文件

    int corePoolSize = 16; // 核心线程数，设为CPU核心数
    int maximumPoolSize = corePoolSize; // 最大线程数，和核心线程数保持一致
    long keepAliveTime = 60L; // 线程空闲后的存活时间
    TimeUnit timeUnit = TimeUnit.SECONDS; // 时间单位
    // 使用 LinkedBlockingQueue 避免线程池队列过长
    ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize,
      maximumPoolSize,
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
   * 合并指定目录下中英文wav文件，并输出到指定目录
   *
   * @param inputDir      中文wav文件目录
   * @param outputFileDir 输出文件路径
   */
  public static File mergeWavFile(String inputDir, String outputFileDir,
    String outputFileName) {
    long startTime = System.currentTimeMillis(); // 记录方法开始时间
    log.info("开始合并WAV文件，输入目录：{}, , 输出目录：{}",
      inputDir, outputFileDir);

    List<String> wavFiles = listWavFiles(inputDir); // 获取中文音频文件列表
    if (wavFiles.isEmpty()) {
      log.warn("中文目录{}下没有找到wav文件", inputDir);
      return null;
    }

    File file = new File(outputFileDir);
    if (!file.exists()) {
      boolean mkdir = file.mkdirs();
      log.info("文件夹创建成功 {}", mkdir);
    } else {
      log.info("文件夹已存在");
    }
    // 创建 ffmpeg 需要的 list 文件
    String fileListFileName = createListFile(wavFiles, outputFileDir);

    long taskStartTime = System.currentTimeMillis();
    String outputFileNameStr = outputFileDir + outputFileName;
    File outputFile = new File(outputFileNameStr);
    if (!outputFile.exists() && outputFile.length() == 0) {
      try {
        FfmpegUtil2.executeFfmpegMerge(fileListFileName,
          outputFileNameStr);
        log.info("合并 {} 完成, 耗时: {} ms", outputFileNameStr,
          (System.currentTimeMillis() - taskStartTime));
      } catch (IOException e) {
        log.error("合并文件 {} 失败: {}", outputFileNameStr,
          e.getMessage());
      }
    } else {
      log.info("待合并文件 {} 已经存在，跳过合并",
        outputFileNameStr);
    }
//    boolean delete = new File(fileListFileName).delete();
//    log.info("删除临时文件 {} 成功: {}", fileListFileName, delete);

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    String formattedTime = CdTimeUtil.formatDuration(duration);
    log.info("合并WAV文件任务完成，输出路径为: {}，总耗时: {}", outputFileDir,
      formattedTime);
    return file;
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
   * @param wavFilesCn     中文wav文件列表
   * @param outputFilePath 输出路径
   * @return list文件路径列表
   */
  private static List<String> createListFile(List<String> wavFilesEn,
    List<String> wavFilesCn, String outputFilePath) {
    List<String> listFiles = new ArrayList<>();
    // 如果两个列表大小不一致则立即退出
    if (wavFilesCn.size() != wavFilesEn.size()) {
      log.error("两个列表大小不一致，无法合并，中文音频大小：{}；英文音频大小：{}",
        wavFilesCn.size(), wavFilesEn.size());
      return listFiles;
    }
    int size = wavFilesCn.size();
    String wavFileNameCn;
    String wavFileNameEn;
    for (int i = 0; i < size; i++) {
      wavFileNameCn = wavFilesCn.get(i);
      wavFileNameEn = wavFilesEn.get(i);
      String indexTag = StringExtractor.extractNumber(wavFileNameCn);
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
        writer.write("file '" + wavFileNameEn + "'");
        writer.newLine();
        writer.write("file '" + wavFileNameCn + "'");
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

  /**
   * 创建 ffmpeg 需要的 list.txt 文件
   *
   * @param wavFiles      中文wav文件列表
   * @param outputFileDir 输出路径
   * @return list文件路径列表
   */
  private static String createListFile(List<String> wavFiles,
    String outputFileDir) {
    wavFiles = wavFiles.stream().sorted().collect(Collectors.toList());
    int size = wavFiles.size();
    String wavFileName;
    String listFilePath = outputFileDir + "list_file.txt";
    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(listFilePath))) {
      for (int i = 0; i < size; i++) {
        wavFileName = wavFiles.get(i);
        writer.write("file '" + wavFileName + "'");
        writer.newLine();
      }
    } catch (IOException e) {
      log.error("创建文件 {} 失败: {}", listFilePath, e.getMessage());
    }

    return listFilePath;
  }


  public static void main(String[] args) {
    String inputDirCn = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\cn"; // 替换为你的输入目录
    String inputDirEn = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\en"; // 替换为你的输入目录
    String outputFilePath = "D:\\0000\\EnBook001\\900\\ch01\\audio\\"; // 替换为你的输出文件路径

    mergeWavFiles(inputDirCn, inputDirEn, outputFilePath);

  }
}
