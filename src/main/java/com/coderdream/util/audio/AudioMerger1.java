package com.coderdream.util.audio;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class AudioMerger1 {
  public static void mergeWavFiles(String inputDir, String outputFilePath)
    throws IOException {
    List<String> wavFiles = listWavFiles(inputDir);
    if (wavFiles.isEmpty()) {
      log.warn("目录{}下没有找到wav文件", inputDir);
      return;
    }

    String listFilePath = createListFile(wavFiles, inputDir);
    FfmpegUtil2.executeFfmpegMerge(listFilePath, outputFilePath);
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

  // 创建 ffmpeg 需要的 list.txt 文件
  private static String createListFile(List<String> wavFiles, String inputDir)
    throws IOException {
    String listFilePath = inputDir + "list.txt";
    String pageFile = "D:\\0000\\bgmusic\\page.wav";
    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(listFilePath))) {
      for (String file : wavFiles) {
        writer.write("file '" + pageFile + "'");
        writer.newLine();
        writer.write("file '" + file + "'");
        writer.newLine();
      }
    }
    return listFilePath;
  }


  public static void main(String[] args) {
    String inputDir = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\mix"; // 替换为你的输入目录
    String outputFilePath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\mix.wav"; // 替换为你的输出文件路径
    try {
      mergeWavFiles(inputDir, outputFilePath);
    } catch (IOException e) {
      log.error("合并文件失败: {}", e.getMessage());
    }
  }
}
