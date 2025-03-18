package com.coderdream.util.video.demo07;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SrtToAssConverter {

  static class SubtitleEntry {

    String number;
    String time;
    String text;

    SubtitleEntry(String number, String time, String text) {
      this.number = number;
      this.time = time;
      this.text = text;
    }
  }

  public static void convertSrtToAss(String englishSrtPath,
    String phoneticSrtPath, String chineseSrtPath, String numberSrtPath,
    String outputAssPath, int playResX, int playResY, String englishColor,
    String chineseColor,
    String phoneticColor, String fontName, int englishFontSize,
    int chineseFontSize, int phoneticFontSize) throws IOException {
    // 讀取四個 SRT 文件
    List<SubtitleEntry> englishEntries = readSrtFile(englishSrtPath);
    List<SubtitleEntry> phoneticEntries = readSrtFile(phoneticSrtPath);
    List<SubtitleEntry> chineseEntries = readSrtFile(chineseSrtPath);
    List<SubtitleEntry> numberEntries = readSrtFile(numberSrtPath);

    // 寫入 ASS 文件
    try (BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(new FileOutputStream(outputAssPath),
        StandardCharsets.UTF_8))) {
      // 寫入頭部
      writer.write("[Script Info]\n");
      writer.write("Title: Multi-Language Subtitles\n");
      writer.write("ScriptType: v4.00+\n");
      writer.write("Collisions: Normal\n");
      writer.write("PlayResX: " + playResX + "\n");
      writer.write("PlayResY: " + playResY + "\n\n");

      // 寫入樣式
      writer.write("[V4+ Styles]\n");
      writer.write(
        "Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\n");
      writer.write("Style: English," + fontName + "," + englishFontSize + ","
        + englishColor
        + ",&H000000&,&H000000&,&H000000&,0,0,0,0,100,100,0,0,1,2,2,5,90,90,0,1\n");
      writer.write("Style: Phonetic," + fontName + "," + phoneticFontSize + ","
        + phoneticColor
        + ",&H000000&,&H000000&,&H000000&,0,0,0,0,100,100,0,0,1,2,2,2,90,90,200,1\n");
      writer.write("Style: Chinese," + fontName + "," + chineseFontSize + ","
        + chineseColor
        + ",&H000000&,&H000000&,&H000000&,0,0,0,0,100,100,0,0,1,2,2,2,90,90,300,1\n\n");

      // 寫入事件
      writer.write("[Events]\n");
      writer.write(
        "Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n");

      for (int i = 0; i < englishEntries.size(); i++) {
        String time = englishEntries.get(i).time;
        String[] times = time.split(" --> ");
        String start = times[0].replace(",", ".");
        String end = times[1].replace(",", ".");

        // 英文
        writer.write("Dialogue: 0," + start + "," + end + ",English,,0,0,0,,"
          + englishEntries.get(i).text + "\n");
        // 音標
        writer.write("Dialogue: 1," + start + "," + end + ",Phonetic,,0,0,0,,"
          + phoneticEntries.get(i).text + "\n");
        // 中文
        writer.write("Dialogue: 2," + start + "," + end + ",Chinese,,0,0,0,,"
          + chineseEntries.get(i).text + "\n");
        // 序號（可選）
        writer.write("Dialogue: 3," + start + "," + end + ",Number,,0,0,0,,"
          + numberEntries.get(i).number + "\n");
      }
    }
  }

  private static List<SubtitleEntry> readSrtFile(String srtPath) throws IOException {
    List<SubtitleEntry> entries = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(
      new FileReader(srtPath, StandardCharsets.UTF_8))) {
      String line;
      String number = "", time = "", text = "";
      int lineCount = 0; // 跟踪每組的行數

      while ((line = reader.readLine()) != null) {
        line = line.trim();

        if (line.isEmpty()) {
          // 遇到空行，表示一組結束
          if (!number.isEmpty() && !time.isEmpty() && !text.isEmpty()) {
            entries.add(new SubtitleEntry(number, time, text));
          }
          number = time = text = "";
          lineCount = 0; // 重置行計數
        } else {
          switch (lineCount) {
            case 0: // 第一行：序號
              number = line;
              break;
            case 1: // 第二行：時間戳
              time = line;
              break;
            default: // 第三行及之後：文本（可能多行）
              text = text.isEmpty() ? line : text + "\n" + line;
              break;
          }
          lineCount++;
        }
      }

      // 處理文件末尾的最後一組（如果沒有空行結尾）
      if (!number.isEmpty() && !time.isEmpty() && !text.isEmpty()) {
        entries.add(new SubtitleEntry(number, time, text));
      }
    }
    return entries;
  }

  public static void main(String[] args) throws IOException {

    // 初始化參數 - TxtAndSrtToFourPartSubtitles
    String path = "D:\\0000\\EnBook005\\Chapter001";
    String chapter = "Chapter001";
//    String inputPath = path + File.separator +
      String numberSrtPath = path + File.separator +"number.srt";             // 輸出序號 SRT
    String englishSrtPath = path + File.separator +"english.srt";           // 輸出英文 SRT
    String phoneticSrtPath = path + File.separator +"phonetic.srt";         // 輸出音標 SRT
    String chineseSrtPath = path + File.separator +"chinese.srt";           // 輸出中文 SRT

    // 初始化參數 - SrtToAssConverter
    String outputAssPath = path + File.separator +"subtitles3.ass";         // 輸出 ASS 文件
    int playResX = 1920;                             // 水平解析度
    int playResY = 1080;                             // 垂直解析度
    String englishColor = "&HFFFFFF&";               // 英文顏色（白色）
    String chineseColor = "&H00FFFF&";               // 中文顏色（黃色）
    String phoneticColor = "&H00FF00&";              // 音標顏色（綠色）
    String fontName = "Source Han Sans Heavy";       // 字體名稱
    int englishFontSize = 80;                        // 英文字體大小
    int chineseFontSize = 96;                        // 中文字體大小
    int phoneticFontSize = 60;                       // 音標字體大小

    convertSrtToAss(englishSrtPath, phoneticSrtPath, chineseSrtPath,
      numberSrtPath, outputAssPath, playResX, playResY, englishColor,
      chineseColor, phoneticColor, fontName, englishFontSize, chineseFontSize,
      phoneticFontSize);

    // 示例用法
//        convertSrtToAss(
//            "english.srt", "phonetic.srt", "chinese.srt", "number.srt",
//            "subtitles3.ass", 1920, 1080, "&HFFFFFF&", "&H00FFFF&", "&H00FF00&",
//            "Source Han Sans Heavy", 80, 96, 60
//        );
    System.out.println("ASS 文件生成完成！");
  }
}
