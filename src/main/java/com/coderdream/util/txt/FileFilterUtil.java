package com.coderdream.util.txt;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileFilterUtil {

  /**
   * 按行读取文件，并根据规则过滤和处理后保存到新文件。
   *
   * @param folderPath 输入文件路径
   * @param subFolder  输出文件路径
   */
  public static void filterAndSaveFile(String folderPath, String subFolder) {
    String inputFilePath =
      folderPath + subFolder + File.separator + subFolder + "_temp.txt";

    String outputFilePath =
      folderPath + subFolder + File.separator + subFolder + ".txt";

    File inputFile = new File(inputFilePath);
    File outputFile = new File(outputFilePath);

    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
      BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String filteredLine = filterLine(line);
        if (filteredLine != null) {
          writer.write(filteredLine);
          writer.newLine();
        }
      }
    } catch (IOException e) {
      System.err.println("发生 IO 异常: " + e.getMessage());
    }
  }

  /**
   * 过滤和处理行，返回处理后的行。如果该行应该被移除，则返回 null。
   *
   * @param line 要过滤的行
   * @return 处理后的行，如果应该被移除，则返回 null
   */
  private static String filterLine(String line) {
    // 情况 1：保留以 "Scene " 开头的行
    if (line.startsWith("Scene ")) {
      return line;
    }

    // 情况 2：保留以 ❶、❷、...、❿ 开头的行，并移除这些标记
    Pattern pattern = Pattern.compile("^[❶❷❸❹❺❻❼❽❾❿](.*)");
    Matcher matcher = pattern.matcher(line);
    if (matcher.matches()) {
      return line;//matcher.group(1).trim(); // 移除标记并去除首尾空格
    }

    // 其他情况：移除该行 (返回 null)
    return null;
  }

  public static void main(String[] args) {
    String folderPath = "D:\\0000\\EnBook002\\"; // 替换为你的输入文件路径
    String subFolder = "Chapter008"; // 替换为你的输出文件路径
    FileFilterUtil.filterAndSaveFile(folderPath, subFolder);
    System.out.println("文件过滤和保存成功！");
  }
}
