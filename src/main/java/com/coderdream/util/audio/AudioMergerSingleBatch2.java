package com.coderdream.util.audio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AudioMergerSingleBatch2 {

  public static void mergeWavFiles(String inputDirCn, String inputDirEn,
    String outputFilePath) throws IOException {
    List<String> wavFilesCn = listWavFiles(inputDirCn);
    if (wavFilesCn.isEmpty()) {
      log.warn("Cn目录{}下没有找到wav文件", inputDirCn);
      return;
    }

    List<String> wavFilesEn = listWavFiles(inputDirEn);
    if (wavFilesEn.isEmpty()) {
      log.warn("En目录{}下没有找到wav文件", inputDirEn);
      return;
    }
    List<String> listFiles = createListFile(wavFilesEn, wavFilesCn,
      outputFilePath);
    for (String fileListFileName : listFiles) {
      String indexTag = StringExtractor.extractLastNumber(fileListFileName);
//      log.info("indexTag: {}, listFileName: {}", indexTag, fileListFileName);
      String outputFilePathWithIndex = outputFilePath + indexTag + ".wav";
//      log.info("outputFilePathWithIndex: {}", outputFilePathWithIndex);
      FfmpegUtil2.executeFfmpegMerge(fileListFileName, outputFilePathWithIndex);
    }

    // 开始提示音，两遍男声两遍无字幕，女声一遍有字幕，男声一遍有字幕 dialog_single_with_phonetics_001_cn.wav

    log.info("WAV文件合并完成，输出路径为: {}", outputFilePath);
  }

  // 列出目录下所有 .wav 文件
  private static List<String> listWavFiles(String inputDir) {
    File dir = new File(inputDir);
    if (!dir.exists() || !dir.isDirectory()) {
      log.error("目录不存在或不是目录: {}", inputDir);
      return new ArrayList<>();
    }
    File[] files = dir.listFiles();
    if (Objects.isNull(files)) {
      return new ArrayList<>();
    }

    return Arrays.stream(files)
      .filter(
        file -> file.isFile() && file.getName().toLowerCase().endsWith(".wav"))
      .map(File::getAbsolutePath)
      .collect(Collectors.toList());
  }

  // 创建 ffmpeg 需要的 list.txt 文件 开始提示音，两遍男声两遍无字幕，女声一遍有字幕，男声一遍有字幕
  private static List<String> createListFile(List<String> wavFilesEn,
    List<String> wavFilesCn, String outputFilePath) throws IOException {
    List<String> listFiles = new ArrayList<>();
    // 如果两个列表大小不一致则立即退出

    if (wavFilesCn.size() != wavFilesEn.size()) {
      log.error("两个列表大小不一致，无法合并");
      return listFiles;
    }
    int size = wavFilesCn.size();
    String wavFileNameCn = "";
    String wavFileNameEn = "";
    for (int i = 0; i < size; i++) {
      wavFileNameCn = wavFilesCn.get(i);
      wavFileNameEn = wavFilesEn.get(i);
      String indexTag = StringExtractor.extractNumber(wavFileNameCn);
      // 三遍英文 一遍中文，一遍英文
      String listFilePath = outputFilePath + "list_" + indexTag + ".txt";
      try (BufferedWriter writer = new BufferedWriter(
        new FileWriter(listFilePath))) {
        writer.write("file '" + wavFileNameEn + "'");
        writer.newLine();
        writer.write("file '" + wavFileNameEn + "'");
        writer.newLine();
        writer.write("file '" + wavFileNameCn + "'");
        writer.newLine();
        writer.write("file '" + wavFileNameEn + "'");
        writer.newLine();
      }

      listFiles.add(listFilePath);
    }

    return listFiles;
  }


  public static void main(String[] args) {
    String inputDirCn = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\cn"; // 替换为你的输入目录
    String inputDirEn = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\en"; // 替换为你的输入目录
    String outputFilePath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\"; // 替换为你的输出文件路径
    try {
      mergeWavFiles(inputDirCn, inputDirEn, outputFilePath);
    } catch (IOException e) {
      log.error("合并文件失败: {}", e.getMessage());
    }
  }
}
