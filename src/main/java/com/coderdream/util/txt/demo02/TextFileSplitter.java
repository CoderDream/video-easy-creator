package com.coderdream.util.txt.demo02;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFileSplitter {

  private static final String DAY_PATTERN = "^【Day (\\d+)】.*";
  private static final Pattern DAY_REGEX = Pattern.compile(DAY_PATTERN);
  private static final String FILE_SUFFIX_FORMAT = "%03d"; // 文件后缀格式，保持3位

  /**
   * 将一个文本文件分割成多个文本文件，以 "【Day X】" 开头的行为每个文件的第一行，到下一个"【Day Y】"的前一行结束。 仅考虑 【Day
   * X】作为分割标志，忽略数字开头
   *
   * @param inputFilePath   输入文件路径
   * @param outputDirectory 输出目录路径
   * @throws IOException 如果发生 I/O 错误
   */
  public static void splitFile(String inputFilePath, String outputDirectory)
    throws IOException {
    splitFile(inputFilePath, outputDirectory, StandardCharsets.UTF_8);
  }

  /**
   * 将一个文本文件分割成多个文本文件，以 "【Day X】" 开头的行为每个文件的第一行，到下一个"【Day Y】"的前一行结束。 仅考虑 【Day
   * X】作为分割标志，忽略数字开头
   *
   * @param inputFilePath   输入文件路径
   * @param outputDirectory 输出目录路径
   * @param charset         文件字符集
   * @throws IOException 如果发生 I/O 错误
   */
  public static void splitFile(String inputFilePath, String outputDirectory,
    Charset charset) throws IOException {
    File inputFile = new File(inputFilePath);
    if (!inputFile.exists()) {
      throw new FileNotFoundException("输入文件不存在：" + inputFilePath);
    }

    File outputDir = new File(outputDirectory);
    if (!outputDir.exists()) {
      if (!outputDir.mkdirs()) {
        throw new IOException("创建输出目录失败：" + outputDirectory);
      }
    }

    BufferedReader reader = null;
    BufferedWriter writer = null;
    String line;
    int fileCounter = 0;
    String currentFileName = null;
    StringBuilder buffer = new StringBuilder();

    try {
      reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(inputFile), charset));

      while ((line = reader.readLine()) != null) {
        line = line.replace("\"", ""); // 去掉双引号

        Matcher dayMatcher = DAY_REGEX.matcher(line);

        if (dayMatcher.matches()) {
          // 如果是新 Day 的开始
          if (writer != null) {
            // 如果已经有文件在写入，先关闭并写入缓存
            writer.write(buffer.toString());
            writer.close();
          }

          fileCounter = Integer.parseInt(dayMatcher.group(1)); // 获取 Day 的序号
          currentFileName = String.format(FILE_SUFFIX_FORMAT,
            fileCounter); // 生成新的文件名

          File outputFile = new File(outputDirectory, currentFileName + ".txt");
          writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(outputFile), charset));
          buffer.setLength(0); // 清空 buffer

          buffer.append(line).append(System.lineSeparator()); // 将当前行添加到缓存
        } else {
          // 如果不是以 "【Day X】" 开头，则追加到缓冲区
          buffer.append(line).append(System.lineSeparator());
        }
      }

      // 处理最后一个文件
      if (writer != null) {
        writer.write(buffer.toString());
        writer.close();
      }

    } finally {
      // 确保 reader 和 writer 都会被关闭
      try {
        if (reader != null) {
          reader.close();
        }
        if (writer != null) {
          writer.close();
        }
      } catch (IOException e) {
        e.printStackTrace(); // 记录关闭流时可能发生的异常
      }
    }
  }

  public static void main(String[] args) {
    String inputFilePath = "D:\\0000\\EnBook007\\EnBook007.txt";
    String outputDirectory = "D:\\0000\\EnBook007\\output"; // 修改为你的输出目录
    try {
      TextFileSplitter.splitFile(inputFilePath, outputDirectory);
      System.out.println("文件分割完成！");
    } catch (IOException e) {
      System.err.println("发生错误：" + e.getMessage());
      e.printStackTrace();
    }
  }
}
