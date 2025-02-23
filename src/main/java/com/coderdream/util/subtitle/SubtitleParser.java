package com.coderdream.util.subtitle;

import com.coderdream.entity.SubtitleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubtitleParser {

    private static final Logger logger = LoggerFactory.getLogger(SubtitleParser.class);
    private static final Pattern SUBTITLE_INDEX_PATTERN = Pattern.compile("^(\\d+)$");
    private static final Pattern TIME_STR_PATTERN = Pattern.compile("^(\\d{2}:\\d{2}:\\d{2},\\d{3}\\s-->\\s\\d{2}:\\d{2}:\\d{2},\\d{3})$");
    /**
     * 解析字幕文件
     * @param filePath 字幕文件路径
     * @return 字幕实体列表
     */
    public static List<SubtitleEntity> parseSubtitleFile(String filePath) {
        Instant start = Instant.now();
        List<SubtitleEntity> subtitleList = new ArrayList<>();
        Path path = Paths.get(filePath);
        if (!Files.exists(path) || Files.isDirectory(path)) {
            logger.error("文件不存在或为目录: {}", filePath);
            return subtitleList;
        }
        int subIndex = 0; // 字幕序号
        String timeStr = null;
        String subtitle = null;
        String subtitleSecond = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    // 空行，表示一个字幕段落结束
                    if (subIndex > 0 && timeStr != null && subtitle != null) {
                        SubtitleEntity subtitleEntity = new SubtitleEntity();
                        subtitleEntity.setSubIndex(subIndex);
                        subtitleEntity.setTimeStr(timeStr);
                        subtitleEntity.setSubtitle(subtitle);
                        subtitleEntity.setSecondSubtitle(subtitleSecond);
                        subtitleList.add(subtitleEntity);
//                        logger.info("解析字幕段落: {}", subtitleEntity);
                    }
                    subIndex = 0;
                    timeStr = null;
                    subtitle = null;
                    subtitleSecond = null;
                } else {
                    Matcher indexMatcher = SUBTITLE_INDEX_PATTERN.matcher(line);
                    Matcher timeStrMatcher = TIME_STR_PATTERN.matcher(line);

                    if(indexMatcher.matches()){
                        //解析字幕序号
                        subIndex = Integer.parseInt(indexMatcher.group(1));
                        logger.debug("解析字幕序号: {}", subIndex);
                    } else if (timeStrMatcher.matches()) {
                        //解析时间字符串
                        timeStr = timeStrMatcher.group(1);
                        logger.debug("解析时间字符串: {}", timeStr);
                    } else if(subtitle == null){
                        //解析第一字幕内容
                        subtitle = line.trim();
                        logger.debug("解析第一字幕内容: {}", subtitle);
                    } else{
                        //解析第二字幕内容
                        subtitleSecond = line.trim();
                        logger.debug("解析第二字幕内容: {}", subtitleSecond);
                    }
                }
            }
            // 处理最后一个字幕段落
            if (subIndex > 0 && timeStr != null && subtitle != null) {
                SubtitleEntity subtitleEntity = new SubtitleEntity();
                subtitleEntity.setSubIndex(subIndex);
                subtitleEntity.setTimeStr(timeStr);
                subtitleEntity.setSubtitle(subtitle);
                subtitleEntity.setSecondSubtitle(subtitleSecond);
                subtitleList.add(subtitleEntity);
//                logger.info("解析字幕段落: {}", subtitleEntity);
            }
        } catch (IOException e) {
            logger.error("读取文件失败: {}", filePath, e);
        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start,end);
        long millis = duration.toMillis();
        long second = millis/1000;
        long minute = second/60;
        long hour = minute/60;
        long finalMillis = millis%1000;
        long finalSecond = second%60;
        long finalMinute = minute%60;

        logger.info("方法调用耗时: {}时{}分{}秒{}毫秒",hour,finalMinute,finalSecond,finalMillis );
        return subtitleList;
    }


    public static void main(String[] args) {
        String filePath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.srt";
        List<SubtitleEntity> subtitleList = SubtitleParser.parseSubtitleFile(filePath);

        if(subtitleList != null && !subtitleList.isEmpty()){
            subtitleList.forEach(System.out::println);
        }else {
            System.out.println("字幕解析失败或为空");
        }

    }

}
