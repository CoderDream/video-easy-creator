package com.coderdream.util.subtitle.ass;

import com.coderdream.util.sentence.SentenceParser;
import com.coderdream.vo.SentenceVO;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AssSubtitleGeneratorUtil2 {

    // 开始时间推迟的毫秒数
    private static final double START_TIME_OFFSET = 1.0;
    // 结束时间提前的毫秒数
    private static final double END_TIME_OFFSET = 0.8;

    /**
     * 根据句子列表、ASS文件路径和时长文件路径生成新的ASS字幕文件
     *
     * @param sentenceList    句子列表
     * @param assFilePath     ASS文件路径
     * @param durationFilePath 时长文件路径
     */
    public static void generateAssSubtitles(List<SentenceVO> sentenceList, String assFilePath,
        String durationFilePath) {
        Instant start = Instant.now();
        log.info("开始生成字幕文件，ASS文件路径: {}, 时长文件路径: {}", assFilePath, durationFilePath);
        // 1. 读取时长文件
        Map<Integer, Double> durationMap = readDurationFile(durationFilePath);
        // 2. 生成ASS文件内容
        String assContent = generateAssContent(sentenceList, durationMap);
        // 3. 将修改后的ASS内容写入文件
        writeAssFile(assContent, assFilePath);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        log.info("字幕文件生成完成，耗时: {}时{}分{}秒{}毫秒", timeElapsed.toHoursPart(), timeElapsed.toMinutesPart(),
            timeElapsed.toSecondsPart(), timeElapsed.toMillisPart());

    }

    /**
     * 读取时长文件，解析文件名和时长
     *
     * @param durationFilePath 时长文件路径
     * @return Map<Integer, Double> key: 文件序号, value: 时长
     */
    private static Map<Integer, Double> readDurationFile(String durationFilePath) {
        Instant start = Instant.now();
        log.info("开始读取时长文件: {}", durationFilePath);

        Map<Integer, Double> durationMap = null;
        try (java.io.BufferedReader reader = Files.newBufferedReader(Paths.get(durationFilePath))) {
            durationMap = reader.lines()
                .map(line -> line.split("\t")) // 使用制表符分割
                .filter(parts -> parts.length == 2) // 确保每行都有文件名和时长
                .collect(Collectors.toMap(
                    parts -> {
                        String fileName = parts[0];
                        // 从文件名中提取序号
                        Pattern pattern = Pattern.compile("(\\d{3})\\.wav$");
                        Matcher matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            return Integer.parseInt(matcher.group(1));
                        }
                        return -1; // 如果解析失败，返回-1或者抛出异常
                    },
                    parts -> Double.parseDouble(parts[1]) // 时长转换成Double
                ))
                .entrySet().stream()
                .filter(entry -> entry.getKey() != -1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException | NumberFormatException e) {
            log.error("读取时长文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("读取时长文件失败", e);
        }
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        log.info("读取时长文件完成，耗时: {}时{}分{}秒{}毫秒", timeElapsed.toHoursPart(), timeElapsed.toMinutesPart(),
            timeElapsed.toSecondsPart(), timeElapsed.toMillisPart());
        return durationMap;
    }


    /**
     * 根据句子列表，时长map 生成ASS文件内容
     *
     * @param sentenceList 句子列表
     * @param durationMap  时长map
     * @return 生成的ASS文件内容
     */
    private static String generateAssContent(List<SentenceVO> sentenceList, Map<Integer, Double> durationMap) {
        Instant start = Instant.now();
        log.info("开始生成ASS文件内容");
        StringBuilder assContent = new StringBuilder();
        double currentTime = 0.0;

        // 添加ASS文件头部信息
        assContent.append("[Script Info]\n");
        assContent.append("Title: Multilayer Subtitles Example\n");
        assContent.append("ScriptType: v4.00+\n");
        assContent.append("Collisions: Normal\n");
        assContent.append("PlayDepth: 0\n\n");
        assContent.append("[V4+ Styles]\n");
        assContent.append(
            "Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\n");
        assContent.append(
            "Style: Index,Arial,10,&H00FFFFFF,&H000000FF,&H00000000,&H80000000,-1,0,0,0,100,100,0,0,1,1.5,0,5,10,10,0,1\n");
        assContent.append(
            "Style: English,Arial,18,&H00FFFFFF,&H000000FF,&H00000000,&H80000000,-1,0,0,0,100,100,0,0,1,1.5,0,5,10,10,250,1\n");
        assContent.append(
            "Style: Phonetic,Arial,18,&H00FFFF00,&H000000FF,&H00000000,&H80000000,-1,0,0,0,100,100,0,0,1,1.5,0,5,0,0,0,1\n");
        assContent.append(
            "Style: Chinese,微软雅黑,20,&H00FF00FF,&H000000FF,&H00000000,&H80000000,-1,0,0,0,100,100,0,0,1,1.5,0,5,10,10,250,1\n\n");

        assContent.append("[Events]\n");
        assContent.append("Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n");


        int index = 1;
        for (SentenceVO sentence : sentenceList) {
            double duration = durationMap.get(index);

            if (duration <= 0) {
                log.warn("第{}个音频文件时长读取失败，使用默认时长2秒", index);
                duration = 2.0;
            }
            // 计算偏移后的开始和结束时间
            double startTimeWithOffset = currentTime + START_TIME_OFFSET;
            double endTimeWithOffset = currentTime + duration - END_TIME_OFFSET;

            String startTime = formatTime(startTimeWithOffset);
            String endTime = formatTime(endTimeWithOffset);


            // 生成字幕内容
            assContent.append(generateDialogueLine(0, startTime, endTime, "Index", String.valueOf(index), 7))
                .append("\n");
            assContent.append(generateDialogueLine(0, startTime, endTime, "English", sentence.getEnglish(), 8))
                .append("\n");
            assContent.append(generateDialogueLine(0, startTime, endTime, "Phonetic", sentence.getPhonetics(), 5))
                .append("\n");
            assContent.append(generateDialogueLine(0, startTime, endTime, "Chinese", sentence.getChinese(), 2))
                .append("\n");
            currentTime += duration;
            index++;
        }


        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        log.info("生成ASS文件内容完成，耗时: {}时{}分{}秒{}毫秒", timeElapsed.toHoursPart(), timeElapsed.toMinutesPart(),
            timeElapsed.toSecondsPart(), timeElapsed.toMillisPart());
        return assContent.toString();
    }


    /**
     * 生成单行字幕内容
     *
     * @param layer     图层
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param style     样式
     * @param text      文本内容
     * @param alignment 对齐方式
     * @return 单行字幕内容
     */
    private static String generateDialogueLine(int layer, String startTime, String endTime, String style, String text,
        int alignment) {
        String escapedText = text.replace("{\\","{\\\\}").replace("\\","\\\\").replace("}", "\\}").replace("{", "\\{");
        return String.format("Dialogue: %d,%s,%s,%s,,0,0,0,,{\\an%d}%s", layer, startTime, endTime, style, alignment,
            escapedText);
    }


    /**
     * 格式化时间
     *
     * @param time 时间(秒)
     * @return 格式化后的时间字符串 (HH:mm:ss.SS)
     */
    private static String formatTime(double time) {
        int hours = (int) time / 3600;
        int minutes = (int) (time % 3600) / 60;
        double seconds = time % 60;
        return String.format("%01d:%02d:%05.2f", hours, minutes, seconds);
    }


    /**
     * 将ASS内容写入文件
     *
     * @param assContent ASS内容
     * @param assFilePath ASS文件路径
     */
    private static void writeAssFile(String assContent, String assFilePath) {
        Instant start = Instant.now();
        log.info("开始写入ASS文件: {}", assFilePath);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(assFilePath))) {
            writer.write(assContent);
        } catch (IOException e) {
            log.error("写入ASS文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("写入ASS文件失败", e);
        }
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        log.info("写入ASS文件完成，耗时: {}时{}分{}秒{}毫秒", timeElapsed.toHoursPart(), timeElapsed.toMinutesPart(),
            timeElapsed.toSecondsPart(), timeElapsed.toMillisPart());
    }


    public static void main(String[] args) {
        String fileName = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics.txt";
        List<SentenceVO> sentenceList = SentenceParser.parseSentencesFromFile(
          fileName);
        String assFilePath = "D:\\0000\\EnBook001\\900\\ch01\\ch01_new_v2.ass";
        String durationFilePath = "D:\\0000\\EnBook001\\900\\ch01\\audio_duration.txt";
        AssSubtitleGeneratorUtil2.generateAssSubtitles(sentenceList, assFilePath, durationFilePath);
    }
}
