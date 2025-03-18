package com.coderdream.util.video.demo07;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TxtAndSrtToFourPartSubtitles {

  static class SrtEntry {

    String number;
    String time;

    SrtEntry(String number, String time) {
      this.number = number;
      this.time = time;
    }
  }

  public static void generateFourPartSubtitles(String srtPath, String txtPath,
    String numberSrtPath, String englishSrtPath,
    String phoneticSrtPath, String chineseSrtPath) throws IOException {
    // 讀取 SRT 文件（時間戳和序號）
    List<SrtEntry> srtEntries = readSrtFile(srtPath);

    // 讀取 TXT 文件（每 3 行一組）
    List<String> txtLines = readTxtFile(txtPath);
    if (txtLines.size() % 3 != 0) {
      throw new IllegalArgumentException("TXT 文件行數必須是 3 的倍數！");
    }
    if (txtLines.size() / 3 > srtEntries.size()) {
      throw new IllegalArgumentException(
        "TXT 文件的組數超過 SRT 文件的條目數！");
    }

    // 分割 TXT 內容到各個字幕列表
    List<String> numberLines = new ArrayList<>();
    List<String> englishLines = new ArrayList<>();
    List<String> phoneticLines = new ArrayList<>();
    List<String> chineseLines = new ArrayList<>();

    for (int i = 0; i < txtLines.size(); i += 3) {
      int groupIndex = i / 3 + 1; // 第 n 組的 n
      numberLines.add(String.valueOf(groupIndex));
      englishLines.add(txtLines.get(i));
      phoneticLines.add(txtLines.get(i + 1));
      chineseLines.add(txtLines.get(i + 2));
    }

    // 寫入四個 SRT 文件
    writeSrtFile(numberSrtPath, srtEntries, numberLines);
    writeSrtFile(englishSrtPath, srtEntries, englishLines);
    writeSrtFile(phoneticSrtPath, srtEntries, phoneticLines);
    writeSrtFile(chineseSrtPath, srtEntries, chineseLines);
  }

  private static List<SrtEntry> readSrtFile(String srtPath) throws IOException {
    List<SrtEntry> entries = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(
      new FileReader(srtPath, StandardCharsets.UTF_8))) {
      String line;
      String number = "", time = "";
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) {
          if (!number.isEmpty() && !time.isEmpty()) {
            entries.add(new SrtEntry(number, time));
          }
          number = time = "";
        } else if (line.matches("\\d+")) {
          number = line;
        } else if (line.contains(" --> ")) {
          time = line;
        }
      }
      if (!number.isEmpty() && !time.isEmpty()) {
        entries.add(new SrtEntry(number, time));
      }
    }
    return entries;
  }

  private static List<String> readTxtFile(String txtPath) throws IOException {
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(
      new FileReader(txtPath, StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (!line.trim().isEmpty()) { // 忽略空行
          lines.add(line.trim());
        }
      }
    }
    return lines;
  }

  private static void writeSrtFile(String outputPath, List<SrtEntry> srtEntries,
    List<String> texts) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(new FileOutputStream(outputPath),
        StandardCharsets.UTF_8))) {
      for (int i = 0; i < texts.size(); i++) {
        writer.write(srtEntries.get(i).number);
        writer.newLine();
        writer.write(srtEntries.get(i).time);
        writer.newLine();
        writer.write(texts.get(i));
        writer.newLine();
        writer.newLine();
      }
    }
  }

  public static void main(String[] args) throws IOException {

    // 初始化參數 - TxtAndSrtToFourPartSubtitles

    String path = "D:\\0000\\EnBook005\\Chapter001";

    String templateSrtPath =
      path + File.separator + "merged_Chapter001.srt";         // 提供時間戳和序號的 SRT 文件
    String txtPath = path + File.separator
      + "Chapter001_phonetics.txt";                // 包含英文、音標、中文的 TXT 文件
    String numberSrtPath =
      path + File.separator + "number.srt";             // 輸出序號 SRT
    String englishSrtPath =
      path + File.separator + "english.srt";           // 輸出英文 SRT
    String phoneticSrtPath =
      path + File.separator + "phonetic.srt";         // 輸出音標 SRT
    String chineseSrtPath =
      path + File.separator + "chinese.srt";           // 輸出中文 SRT

    generateFourPartSubtitles(templateSrtPath, txtPath, numberSrtPath,
      englishSrtPath, phoneticSrtPath, chineseSrtPath);

//    // 示例用法
//    generateFourPartSubtitles(
//      "template.srt",         // 提供時間戳和序號的 SRT 文件
//      "subtitles.txt",        // 包含英文、音標、中文的 TXT 文件
//      "number.srt",           // 輸出序號 SRT
//      "english.srt",          // 輸出英文 SRT
//      "phonetic.srt",         // 輸出音標 SRT
//      "chinese.srt"           // 輸出中文 SRT
//    );
    System.out.println("四分字幕文件生成完成！");
  }
}
