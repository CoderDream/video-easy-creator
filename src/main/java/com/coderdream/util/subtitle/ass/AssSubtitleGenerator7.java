package com.coderdream.util.subtitle.ass;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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
public class AssSubtitleGenerator7 {

    // 时间偏移量常量
    private static final int START_TIME_OFFSET = 100; // 毫秒
    private static final int END_TIME_OFFSET = 100;   // 毫秒
    private static final int MERGE_COUNT = 5; // 合并字幕条数

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");


    public static void main(String[] args) {
        //  指定输入和输出文件路径
        String indexSrtPath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix_index.srt";
        String englishSrtPath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix_english.srt";
        String phoneticsSrtPath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix_phonetics.srt";
        String chineseSrtPath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix_chinese.srt";
        String outputAssPath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.ass";

        generateAssFile(indexSrtPath, englishSrtPath, phoneticsSrtPath, chineseSrtPath, outputAssPath);
    }


    public static void generateAssFile(String indexSrtPath, String englishSrtPath, String phoneticsSrtPath, String chineseSrtPath, String outputAssPath) {
        Instant start = Instant.now();
        log.info("开始生成ASS字幕文件：{}", outputAssPath);
        try {
            List<SubtitleEntry> indexEntries = parseSrtFile(indexSrtPath);
            List<SubtitleEntry> englishEntries = parseSrtFile(englishSrtPath);
            List<SubtitleEntry> phoneticsEntries = parseSrtFile(phoneticsSrtPath);
            List<SubtitleEntry> chineseEntries = parseSrtFile(chineseSrtPath);

            // 检查字幕条目数量是否一致，数量不一致，不合并
            if (indexEntries.size() != englishEntries.size() ||
                    indexEntries.size() != phoneticsEntries.size() ||
                    indexEntries.size() != chineseEntries.size()) {
                log.error("SRT 文件字幕条目数量不一致，无法生成ASS文件");
                return;
            }

            // 检查时间戳是否一致
            checkTimeConsistency(indexEntries, englishEntries, phoneticsEntries, chineseEntries);


            // 生成ass内容
            String assContent = generateAssContent(indexEntries, englishEntries, phoneticsEntries, chineseEntries);
            if (assContent.isEmpty()){
                log.error("生成ASS字幕内容失败");
                return;
            }



            // 分离并校验ass字幕时间是否有重叠
            checkAssTimeOverlap(assContent);


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


        for (int i = 0; i < indexEntries.size(); i += MERGE_COUNT) {
            // 检查是否足够 5 条字幕
            if (i + MERGE_COUNT > indexEntries.size()) {
                log.warn("剩余字幕不足 {} 条，跳过合并。", MERGE_COUNT);
                break;
            }

            // 校验 5 条字幕内容是否一致
            if (!checkContentConsistency(indexEntries, englishEntries, phoneticsEntries, chineseEntries, i)) {
                log.error("字幕内容不一致，索引：{}", i);
                return ""; // 或抛出异常
            }

            SubtitleEntry firstIndexEntry = indexEntries.get(i);
            SubtitleEntry lastIndexEntry = indexEntries.get(i + MERGE_COUNT - 1);

            String startTime = formatAssTime(firstIndexEntry.startTime);
            String endTime = formatAssTime(lastIndexEntry.endTime);


            // index 字幕
            String indexDialogue = String.format("Dialogue: 0,%s,%s,Index,,0,0,0,,{\\an7}%s",
                    offsetTime(startTime, START_TIME_OFFSET),
                    offsetTime(endTime, -END_TIME_OFFSET),
                    escapeAssText(firstIndexEntry.text)); // 使用第一条字幕的文本
            assContent.append(indexDialogue).append("\n");

            // english
            String englishDialogue = String.format("Dialogue: 0,%s,%s,English,,0,0,0,,{\\an8}%s",
                    offsetTime(startTime, START_TIME_OFFSET),
                    offsetTime(endTime, -END_TIME_OFFSET),
                    escapeAssText(englishEntries.get(i).text));
            assContent.append(englishDialogue).append("\n");


            String phoneticsDialogue = String.format("Dialogue: 0,%s,%s,Phonetic,,0,0,0,,{\\an5}%s",
                    offsetTime(startTime, START_TIME_OFFSET),
                    offsetTime(endTime, -END_TIME_OFFSET),
                    escapeAssText(phoneticsEntries.get(i).text));
            assContent.append(phoneticsDialogue).append("\n");



            String chineseDialogue = String.format("Dialogue: 0,%s,%s,Chinese,,0,0,0,,{\\an2}%s",
                    offsetTime(startTime, START_TIME_OFFSET),
                    offsetTime(endTime, -END_TIME_OFFSET),
                    escapeAssText(chineseEntries.get(i).text));
            assContent.append(chineseDialogue).append("\n");

        }

        return assContent.toString();
    }

    private static String offsetTime(String time, int offset) {
        try {
            LocalTime localTime = LocalTime.parse(time, TIME_FORMATTER);
            Duration duration = Duration.ofMillis(offset);
            return localTime.plus(duration).format(TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("时间解析失败：{},{}", time, e.getMessage(), e);
            return time;
        }
    }


    private static List<SubtitleEntry> parseSrtFile(String srtPath) {
        List<SubtitleEntry> entries = new ArrayList<>();
        String regex = "(\\d{2}:\\d{2}:\\d{2},\\d{3}) --> (\\d{2}:\\d{2}:\\d{2},\\d{3})\\s*\\n(.*?)(?=\\n\\d+|\\Z)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(srtPath))) {
            StringBuilder content = new StringBuilder();  //更高效的字符串拼接
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
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


    private static String formatAssTime(String srtTime) {
        return srtTime.replace(",", ".");
    }


    private static String escapeAssText(String text) {
        return text.replace("{", "\\{").replace("}", "\\}").replace("\n", "\\N");
    }


    private static void checkTimeConsistency(List<SubtitleEntry> indexEntries, List<SubtitleEntry> englishEntries, List<SubtitleEntry> phoneticsEntries, List<SubtitleEntry> chineseEntries) {
        for (int i = 0; i < indexEntries.size(); i++) {
            SubtitleEntry indexEntry = indexEntries.get(i);
            SubtitleEntry englishEntry = englishEntries.get(i);
            SubtitleEntry phoneticsEntry = phoneticsEntries.get(i);
            SubtitleEntry chineseEntry = chineseEntries.get(i);

            if (!indexEntry.startTime.equals(englishEntry.startTime) ||
                    !indexEntry.endTime.equals(englishEntry.endTime) ||
                    !indexEntry.startTime.equals(phoneticsEntry.startTime) ||
                    !indexEntry.endTime.equals(phoneticsEntry.endTime) ||
                    !indexEntry.startTime.equals(chineseEntry.startTime) ||
                    !indexEntry.endTime.equals(chineseEntry.endTime)
            ) {
                log.error("SRT 文件时间戳不一致：index:{}, english:{}, phonetics:{}, chinese:{}", indexEntry, englishEntry, phoneticsEntry, chineseEntry);
                System.exit(1);
            }
        }
    }


    private static void checkAssTimeOverlap(String assContent) {
        List<AssDialogueEntry> indexDialogues = new ArrayList<>();
        List<AssDialogueEntry> englishDialogues = new ArrayList<>();
        List<AssDialogueEntry> phoneticDialogues = new ArrayList<>();
        List<AssDialogueEntry> chineseDialogues = new ArrayList<>();
        List<AssDialogueEntry> allDialogues = parseAssDialogues(assContent);

        for (AssDialogueEntry assDialogueEntry : allDialogues) {
            if (assDialogueEntry.text.startsWith("{\\an7}")) {
                indexDialogues.add(assDialogueEntry);
            } else if (assDialogueEntry.text.startsWith("{\\an8}")) {
                englishDialogues.add(assDialogueEntry);
            } else if (assDialogueEntry.text.startsWith("{\\an5}")) {
                phoneticDialogues.add(assDialogueEntry);
            } else if (assDialogueEntry.text.startsWith("{\\an2}")) {
                chineseDialogues.add(assDialogueEntry);
            }
        }


        checkDialogueTimeOverlap(indexDialogues, "index");
        checkDialogueTimeOverlap(englishDialogues, "english");
        checkDialogueTimeOverlap(phoneticDialogues, "phonetic");
        checkDialogueTimeOverlap(chineseDialogues, "chinese");
    }


    private static void checkDialogueTimeOverlap(List<AssDialogueEntry> entries, String type) {
        for (int i = 0; i < entries.size(); i++) {
            AssDialogueEntry current = entries.get(i);
            try {
                LocalTime currentStartTime = LocalTime.parse(current.startTime, TIME_FORMATTER);
                LocalTime currentEndTime = LocalTime.parse(current.endTime, TIME_FORMATTER);
                for (int j = i + 1; j < entries.size(); j++) {
                    AssDialogueEntry next = entries.get(j);
                    LocalTime nextStartTime = LocalTime.parse(next.startTime, TIME_FORMATTER);
                    LocalTime nextEndTime = LocalTime.parse(next.endTime, TIME_FORMATTER);

                    if (currentEndTime.isAfter(nextStartTime) && currentStartTime.isBefore(nextEndTime)) {
                        log.error("ASS {}字幕时间重叠： current:{}, next:{}", type, current, next);
                        System.exit(1);
                    }
                }

            } catch (DateTimeParseException e) {
                log.error("解析时间失败：{}", e.getMessage(), e);
                System.exit(1);
            }
        }
    }


    private static List<AssDialogueEntry> parseAssDialogues(String assContent) {
        List<AssDialogueEntry> entries = new ArrayList<>();
        String regex = "Dialogue: \\d,(.*?),(.*?),.*?,.*?,\\d,\\d,\\d,,(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(assContent);
        while (matcher.find()) {
            String startTime = matcher.group(1);
            String endTime = matcher.group(2);
            String text = matcher.group(3).trim();
            entries.add(new AssDialogueEntry(startTime, endTime, text));
        }
        return entries;
    }


//    private static boolean checkContentConsistency(List<SubtitleEntry> indexEntries, List<SubtitleEntry> englishEntries,
//                                                   List<SubtitleEntry> phoneticsEntries, List<SubtitleEntry> chineseEntries, int startIndex) {
//        for (int i = startIndex + 1; i < startIndex + MERGE_COUNT; i++) {
//            if (!indexEntries.get(startIndex).text.equals(indexEntries.get(i).text) ||
//                    !englishEntries.get(startIndex).text.equals(englishEntries.get(i).text) ||
//                    !phoneticsEntries.get(startIndex).text.equals(phoneticsEntries.get(i).text) ||
//                    !chineseEntries.get(startIndex).text.equals(chineseEntries.get(i).text)) {
//                return false;
//            }
//        }
//        return true;
//    }

    private static boolean checkContentConsistency(List<SubtitleEntry> indexEntries, List<SubtitleEntry> englishEntries,
      List<SubtitleEntry> phoneticsEntries, List<SubtitleEntry> chineseEntries, int startIndex) {
        for (int i = startIndex + 1; i < startIndex + MERGE_COUNT; i++) {
            String indexText1 = indexEntries.get(startIndex).text;
            String indexText2 = indexEntries.get(i).text;
            String englishText1 = englishEntries.get(startIndex).text;
            String englishText2 = englishEntries.get(i).text;
            String phoneticsText1 = phoneticsEntries.get(startIndex).text;
            String phoneticsText2 = phoneticsEntries.get(i).text;
            String chineseText1 = chineseEntries.get(startIndex).text;
            String chineseText2 = chineseEntries.get(i).text;

            log.debug("indexText1: {}, indexText2: {}", indexText1, indexText2);
            log.debug("englishText1: {}, englishText2: {}", englishText1, englishText2);
            log.debug("phoneticsText1: {}, phoneticsText2: {}", phoneticsText1, phoneticsText2);
            log.debug("chineseText1: {}, chineseText2: {}", chineseText1, chineseText2);

// !indexText1.equals(indexText2) ||
            if (
              !englishText1.equals(englishText2) ||
              !phoneticsText1.equals(phoneticsText2) ||
              !chineseText1.equals(chineseText2)) {
                return false;
            }
        }
        return true;
    }

    private static class SubtitleEntry {
        String startTime;
        String endTime;
        String text;


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


    private static class AssDialogueEntry {
        String startTime;
        String endTime;
        String text;


        public AssDialogueEntry(String startTime, String endTime, String text) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.text = text;
        }

        @Override
        public String toString() {
            return "AssDialogueEntry{" +
                    "startTime='" + startTime + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", text='" + text + '\'' +
                    '}';
        }
    }
}
