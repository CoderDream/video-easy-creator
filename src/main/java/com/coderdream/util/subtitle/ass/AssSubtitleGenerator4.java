package com.coderdream.util.subtitle.ass;

import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ASS字幕生成器
 * 读取多个 SRT 文件，并生成指定样式的 ASS 字幕文件
 */
@Slf4j
public class AssSubtitleGenerator4 {

    /**
     * 主方法，用于启动 ASS 字幕生成
     * 入参在 main 方法中写死
     */
    public static void main(String[] args) {
        //  指定输入和输出文件路径
        String indexSrtPath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix_index.srt";
        String englishSrtPath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix_english.srt";
        String phoneticsSrtPath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix_phonetics.srt";
        String chineseSrtPath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix_chinese.srt";
        String outputAssPath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.ass";

        generateAssFile(indexSrtPath, englishSrtPath, phoneticsSrtPath, chineseSrtPath, outputAssPath);
    }


    /**
     * 生成 ASS 字幕文件
     *
     * @param indexSrtPath   index 字幕 SRT 文件路径
     * @param englishSrtPath english 字幕 SRT 文件路径
     * @param phoneticsSrtPath phonetics 字幕 SRT 文件路径
     * @param chineseSrtPath chinese 字幕 SRT 文件路径
     * @param outputAssPath  输出 ASS 文件路径
     */
    public static void generateAssFile(String indexSrtPath, String englishSrtPath, String phoneticsSrtPath, String chineseSrtPath, String outputAssPath) {
        Instant start = Instant.now();
        log.info("开始生成ASS字幕文件：{}", outputAssPath);
        try {
            List<SubtitleEntry> indexEntries = parseSrtFile(indexSrtPath);
            List<SubtitleEntry> englishEntries = parseSrtFile(englishSrtPath);
            List<SubtitleEntry> phoneticsEntries = parseSrtFile(phoneticsSrtPath);
            List<SubtitleEntry> chineseEntries = parseSrtFile(chineseSrtPath);


            // 检查字幕条目数量是否一致
            if (indexEntries.size() != englishEntries.size() ||
                    indexEntries.size() != phoneticsEntries.size() ||
                    indexEntries.size() != chineseEntries.size()) {
                log.error("SRT 文件字幕条目数量不一致，无法生成ASS文件");
                return;
            }


            // 检查时间重叠
            checkTimeOverlap(indexEntries);
            checkTimeOverlap(englishEntries);
            checkTimeOverlap(phoneticsEntries);
            checkTimeOverlap(chineseEntries);

            // 生成ass内容
            String assContent = generateAssContent(indexEntries, englishEntries, phoneticsEntries, chineseEntries);

            // 写入 ass 文件
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputAssPath))) {
                writer.write(assContent);
            } catch (IOException e) {
                log.error("写入 ASS 文件失败：{}", e.getMessage(), e);
            }

        } catch (Exception e) {
            log.error("生成 ASS 字幕文件失败：{}", e.getMessage(), e);
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        long seconds = duration.getSeconds();
        long milliSeconds = duration.toMillisPart();
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        log.info("生成ASS字幕文件完成，耗时：{}分{}秒{}毫秒", minutes, remainingSeconds, milliSeconds);

    }

    /**
     * 生成 ASS 字幕内容
     *
     * @param indexEntries   index 字幕条目列表
     * @param englishEntries english 字幕条目列表
     * @param phoneticsEntries phonetics 字幕条目列表
     * @param chineseEntries chinese 字幕条目列表
     * @return ASS 字幕内容字符串
     */
    private static String generateAssContent(List<SubtitleEntry> indexEntries, List<SubtitleEntry> englishEntries, List<SubtitleEntry> phoneticsEntries, List<SubtitleEntry> chineseEntries) {
        StringBuilder assContent = new StringBuilder();

        // ASS 文件头
        assContent.append("[Script Info]\n");
        assContent.append("Title: Multilayer Subtitles Example\n");
        assContent.append("ScriptType: v4.00+\n");
        assContent.append("Collisions: Normal\n");
        assContent.append("PlayDepth: 0\n");
        assContent.append("\n");
        assContent.append("[V4+ Styles]\n");
        assContent.append("Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\n");
        assContent.append("Style: Index,Arial,10,&H00FFFFFF,&H000000FF,&H00000000,&H80000000,-1,0,0,0,100,100,0,0,1,1.5,0,5,10,10,50,1\n");
        assContent.append("Style: English,Arial,10,&H00FFFFFF,&H000000FF,&H00000000,&H80000000,-1,0,0,0,100,100,0,0,1,1.5,0,5,10,10,50,1\n");
        assContent.append("Style: Phonetic,Arial,10,&H00FFFF00,&H000000FF,&H00000000,&H80000000,-1,0,0,0,100,100,0,0,1,1.5,0,5,10,10,10,1\n");
        assContent.append("Style: Chinese,Arial,10,&H00FF00FF,&H000000FF,&H00000000,&H80000000,-1,0,0,0,100,100,0,0,1,1.5,0,5,10,10,50,1\n");
        assContent.append("\n");
        assContent.append("[Events]\n");
        assContent.append("Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n");


        for (int i = 0; i < indexEntries.size(); i++) {
            SubtitleEntry indexEntry = indexEntries.get(i);
            SubtitleEntry englishEntry = englishEntries.get(i);
            SubtitleEntry phoneticsEntry = phoneticsEntries.get(i);
            SubtitleEntry chineseEntry = chineseEntries.get(i);

            // 检查时间戳是否一致，不一致则跳过
            if (!indexEntry.startTime.equals(englishEntry.startTime) ||
                    !indexEntry.endTime.equals(englishEntry.endTime) ||
                    !indexEntry.startTime.equals(phoneticsEntry.startTime) ||
                    !indexEntry.endTime.equals(phoneticsEntry.endTime) ||
                    !indexEntry.startTime.equals(chineseEntry.startTime) ||
                    !indexEntry.endTime.equals(chineseEntry.endTime)
            ) {
                log.warn("时间戳不一致，跳过此条字幕：index:{}, english:{}, phonetics:{}, chinese:{}", indexEntry, englishEntry, phoneticsEntry, chineseEntry);
                continue;
            }

            // index 字幕
            String indexDialogue = String.format("Dialogue: 0,%s,%s,Index,,0,0,0,,{\\an7}%s",
                    formatAssTime(indexEntry.startTime),
                    formatAssTime(indexEntry.endTime),
                    escapeAssText(indexEntry.text));

            assContent.append(indexDialogue).append("\n");

            // english 字幕
            String englishDialogue = String.format("Dialogue: 0,%s,%s,English,,0,0,0,,{\\an8}%s",
                    formatAssTime(englishEntry.startTime),
                    formatAssTime(englishEntry.endTime),
                    escapeAssText(englishEntry.text));
            assContent.append(englishDialogue).append("\n");

            // phonetics 字幕
            String phoneticsDialogue = String.format("Dialogue: 0,%s,%s,Phonetic,,0,0,0,,{\\an5}%s",
                    formatAssTime(phoneticsEntry.startTime),
                    formatAssTime(phoneticsEntry.endTime),
                    escapeAssText(phoneticsEntry.text));
            assContent.append(phoneticsDialogue).append("\n");


            // chinese 字幕
            String chineseDialogue = String.format("Dialogue: 0,%s,%s,Chinese,,0,0,0,,{\\an2}%s",
                    formatAssTime(chineseEntry.startTime),
                    formatAssTime(chineseEntry.endTime),
                    escapeAssText(chineseEntry.text));
            assContent.append(chineseDialogue).append("\n");
        }
        return assContent.toString();
    }


    /**
     * 解析 SRT 文件
     *
     * @param srtPath SRT 文件路径
     * @return 字幕条目列表
     */
    private static List<SubtitleEntry> parseSrtFile(String srtPath) {
        List<SubtitleEntry> entries = new ArrayList<>();
        String regex = "(\\d{2}:\\d{2}:\\d{2},\\d{3}) --> (\\d{2}:\\d{2}:\\d{2},\\d{3})\\s*\\n(.*?)(?=\\n\\d+|\\Z)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(srtPath))) {
            String content = "";
            String line;
            while ((line = reader.readLine()) != null) {
                content += line + "\n";
            }

            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                String startTime = matcher.group(1);
                String endTime = matcher.group(2);
                String text = matcher.group(3).trim();
                entries.add(new SubtitleEntry(startTime, endTime, text));
            }

        } catch (IOException e) {
            log.error("解析 SRT 文件失败：{}", e.getMessage(), e);
        }
        return entries;
    }


    /**
     * 格式化 ASS 时间
     *
     * @param srtTime SRT 时间字符串
     * @return ASS 时间字符串
     */
    private static String formatAssTime(String srtTime) {
        return srtTime.replace(",", ".");
    }


    /**
     * 转义 ASS 文本
     *
     * @param text 原始文本
     * @return 转义后的 ASS 文本
     */
    private static String escapeAssText(String text) {
        return text.replace("{", "\\{").replace("}", "\\}").replace("\n", "\\N");
    }

    /**
     *  检查字幕时间是否有重叠
     * @param entries 字幕条目列表
     */
    private static void checkTimeOverlap(List<SubtitleEntry> entries) {
        // 修改时间格式，使用逗号
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss,SSS");
        for (int i = 0; i < entries.size(); i++) {
            SubtitleEntry current = entries.get(i);
            try {
                LocalTime currentStartTime = LocalTime.parse(current.startTime, formatter);
                LocalTime currentEndTime = LocalTime.parse(current.endTime, formatter);
                 for (int j = i + 1; j < entries.size(); j++) {
                    SubtitleEntry next = entries.get(j);
                    LocalTime nextStartTime = LocalTime.parse(next.startTime, formatter);
                    LocalTime nextEndTime = LocalTime.parse(next.endTime, formatter);

                   if (currentEndTime.isAfter(nextStartTime) && currentStartTime.isBefore(nextEndTime)) {
                         log.error("字幕时间重叠： current:{}, next:{}",current,next);
                       System.exit(1);
                    }
                }

            }catch (DateTimeParseException e){
                log.error("解析时间失败：{}",e.getMessage(),e);
                 System.exit(1);
            }
        }
    }


    /**
     * 字幕条目内部类，用于存储字幕的开始时间、结束时间和文本
     */
    private static class SubtitleEntry {
        String startTime;
        String endTime;
        String text;

        /**
         * 字幕条目构造函数
         *
         * @param startTime 字幕开始时间
         * @param endTime   字幕结束时间
         * @param text      字幕文本
         */
        public SubtitleEntry(String startTime, String endTime, String text) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.text = text;
        }

        @Override
        public String toString() {
            return "SubtitleEntry{" +
                    "startTime='" + startTime + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", text='" + text + '\'' +
                    '}';
        }
    }
}
