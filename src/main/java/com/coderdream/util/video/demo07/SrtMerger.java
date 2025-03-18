package com.coderdream.util.video.demo07;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SrtMerger {

    private static final Pattern SRT_TIME_PATTERN = Pattern.compile("(\\d{2}:\\d{2}:\\d{2},\\d{3}) --> (\\d{2}:\\d{2}:\\d{2},\\d{3})");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss,SSS");

    public static void mergeSrtFile(File inputFile, File outputFile) throws IOException, ParseException {
        List<SrtEntry> allEntries = parseSrtFile(inputFile);
        if (allEntries == null || allEntries.size() == 0) {
            System.out.println("No SRT entries to merge.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            int groupCount = 0;
            for (int i = 0; i < allEntries.size(); i += 5) {
                groupCount++;
                List<SrtEntry> group = allEntries.subList(i, Math.min(i + 5, allEntries.size()));  // 處理少於5個字幕的情形

                // 取得一組srt的第一個與最後一個的時間
                Date startTime = group.get(0).startTime;
                Date endTime = group.get(group.size() - 1).endTime;

                // 组合字幕内容
                Set<String> combinedText = new HashSet<>();
                for (SrtEntry entry : group) {
                    combinedText.add(entry.text);
                }
                String mergedText = String.join("\n", combinedText);

                // 寫入合併的字幕
                writer.write(String.valueOf(groupCount)); // 字幕序号
                writer.newLine();
                writer.write(TIME_FORMAT.format(startTime) + " --> " + TIME_FORMAT.format(endTime)); // 時間戳
                writer.newLine();
                writer.write(mergedText); // 字幕內容
                writer.newLine();
                writer.newLine(); // 兩個換行符分割不同的字幕條目
            }
        }
    }

    private static List<SrtEntry> parseSrtFile(File srtFile) throws IOException, ParseException {
        List<SrtEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(srtFile))) {
            String line;
            int sequenceNumber = 0;
            while ((line = reader.readLine()) != null) {
                try {
                    sequenceNumber = Integer.parseInt(line.trim()); // 字幕的流水號
                } catch (NumberFormatException e) {
                    // 如果不是数字，就忽略，但繼續往後處理
                    continue;
                }

                String timeLine = reader.readLine();
                Matcher matcher = SRT_TIME_PATTERN.matcher(timeLine);

                if (!matcher.matches()) {
                    continue;
                }

                Date startTime = TIME_FORMAT.parse(matcher.group(1));
                Date endTime = TIME_FORMAT.parse(matcher.group(2));

                StringBuilder textBuilder = new StringBuilder();
                String textLine;
                while ((textLine = reader.readLine()) != null && !textLine.trim().isEmpty()) {
                    textBuilder.append(textLine).append("\n");
                }
                String text = textBuilder.toString().trim();

                entries.add(new SrtEntry(startTime, endTime, text));
            }
        }
        return entries;
    }

    private static class SrtEntry {
        Date startTime;
        Date endTime;
        String text;

        public SrtEntry(Date startTime, Date endTime, String text) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.text = text;
        }
    }

    public static void main(String[] args) {

        String path = "D:\\0000\\EnBook005\\Chapter001";
        String chapter = "Chapter001";
        String inputPath = path + File.separator + chapter + ".srt";
        String outputPath = path + File.separator + "merged_" + chapter + ".srt";
        //  範例
        File inputFile = new File(inputPath); // 替换为您的实际输入文件路径
        File outputFile = new File(outputPath); // 替换为您的输出文件路径

        try {
            mergeSrtFile(inputFile, outputFile);
            System.out.println("SRT file merged successfully!");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
