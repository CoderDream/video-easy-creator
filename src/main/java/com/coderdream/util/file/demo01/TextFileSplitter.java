package com.coderdream.util.file.demo01;

import cn.hutool.core.util.StrUtil;
import java.io.*;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFileSplitter {

  /**
   * 分割文本文件到多个文件中。
   *
   * @param inputFile       输入文件路径 (例如: D:\\0000\\EnBook010\\EnBook010.txt)
   * @param outputDirectory 输出目录路径 (例如: D:\\0000\\EnBook010)
   * @throws IOException 如果发生IO异常
   */
  public static void splitFileByChapter(String inputFile,
    String outputDirectory) throws IOException {

    Path inputFilePath = Paths.get(inputFile);
    Path outputDirPath = Paths.get(outputDirectory);

    if (!Files.exists(inputFilePath)) {
      throw new FileNotFoundException("输入文件不存在: " + inputFile);
    }

    if (!Files.exists(outputDirPath)) {
      throw new IllegalArgumentException("输出目录不存在: " + outputDirectory);
    }

    String line;
    int chapterNumber = 1;
    BufferedWriter writer = null;
    Path chapterDir = null;

    try (BufferedReader reader = new BufferedReader(
      new FileReader(inputFilePath.toFile()))) {
      while ((line = reader.readLine()) != null) {
        // 检查是否是章节开头
        if (line.startsWith("CHAPTER")) {
          // 关闭之前的 Writer (如果存在)
          if (writer != null) {
            writer.close();
          }

          // 创建新的章节文件夹
          String chapterNumberStr = String.format("%03d", chapterNumber);
          chapterDir = outputDirPath.resolve("Chapter" + chapterNumberStr);

          // 创建目录，如果存在则不创建。这里要处理一下如果存在，就不重新创建了
          if (!Files.exists(chapterDir)) {
            Files.createDirectory(chapterDir);
          }

          // 创建新的 Writer
          Path chapterFilePath = chapterDir.resolve(
            "Chapter" + chapterNumberStr + ".txt");
          writer = new BufferedWriter(new FileWriter(chapterFilePath.toFile()));

          //写入章节标题
          writer.write(line);
          writer.newLine();

          chapterNumber++;

        } else {
          // 写入内容 (如果 Writer 已经初始化)
          if (writer != null) {
            // 去掉*
            line = line.replaceAll("\\*", "");
            if (StrUtil.isNotBlank(line)) {
              writer.write(line);
              writer.newLine();
            }
          }
        }
      }
    } finally {
      // 确保关闭最后一个 Writer
      if (writer != null) {
        writer.close();
      }
    }
  }

  public static void main(String[] args) {
    String inputFile = "D:\\0000\\EnBook010\\EnBook010.txt";
    String outputDirectory = "D:\\0000\\EnBook010";

    // 创建一个示例文本文件
    try {
      Path exampleFile = Paths.get(inputFile);
      if (!Files.exists(exampleFile)) {
        Files.createDirectories(exampleFile.getParent());
        try (BufferedWriter writer = new BufferedWriter(
          new FileWriter(exampleFile.toFile()))) {
          writer.write("Chapter001\n");
          writer.write("This is the first chapter.\n");
          writer.write("Chapter002\n");
          writer.write("This is the second chapter.\n");
          writer.write("Chapter003\n");
          writer.write("This is the third chapter.\n");
        }
      }

      TextFileSplitter.splitFileByChapter(inputFile, outputDirectory);
      System.out.println("文件分割完成!");
    } catch (IOException e) {
      System.err.println("发生错误: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
