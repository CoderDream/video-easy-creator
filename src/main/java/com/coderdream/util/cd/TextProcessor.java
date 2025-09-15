package com.coderdream.util.cd;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.coderdream.entity.SubtitleEntity;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextProcessor {

//    public static void main(String[] args) {
//        String fileName = "D:\\14_LearnEnglish\\6MinuteEnglish\\2018\\180503\\eng_raw.srt";
//        try {
//            String result = TextProcessor.processFile(fileName);
//            if (result != null) {
//                System.out.println(result);
//            } else {
//                System.out.println("未找到符合条件的字符串。");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

  /**
   * 解析字幕文件，提取第一部分和第二部分的时间戳。
   *
   * @param fileName 字幕文件的路径。
   * @return 第一部分和第二部分的时间戳，以制表符分隔。
   */
    public static String processFile(String fileName) {
        List<SubtitleEntity> subtitles = CdFileUtil.readSrtFileContent(fileName);
        if (subtitles == null || subtitles.isEmpty()) {
            log.error("SRT文件读取失败或内容为空: {}", fileName);
            return null;
        }

        Optional<String> startTime = findStartTime(subtitles);
        Optional<String> endTime = findEndTime(subtitles);

        if (startTime.isPresent() && endTime.isPresent()) {
            String folderName = FileUtil.mainName(FileUtil.getParent(fileName, 1));
            return folderName + "\t" + startTime.get() + "\t" + endTime.get();
        } else {
            if (startTime.isEmpty()) {
                log.error("在文件 {} 中未找到包含 'I'm' 或 'I am' 的起始字幕。", fileName);
            }
            if (endTime.isEmpty()) {
                log.error("在文件 {} 中未找到包含 'bye' 的结束字幕。", fileName);
            }
            return null;
        }
    }

    /**
     * 查找第一个包含 "I'm" 或 "I am" 的字幕的开始时间。
     * @param subtitles 字幕实体列表
     * @return 包含开始时间的 Optional，如果找不到则为空
     */
    private static Optional<String> findStartTime(List<SubtitleEntity> subtitles) {
        for (SubtitleEntity subtitle : subtitles) {
            String text = subtitle.getSubtitle().toLowerCase();
            if (text.contains("i'm") || text.contains("i am")) {
                return Optional.of(subtitle.getTimeStr().substring(0, 12));
            }
        }
        return Optional.empty();
    }

    /**
     * 从后向前查找最后一个包含 "bye" 的字幕的结束时间。
     * @param subtitles 字幕实体列表
     * @return 包含结束时间的 Optional，如果找不到则为空
     */
    private static Optional<String> findEndTime(List<SubtitleEntity> subtitles) {
        ListIterator<SubtitleEntity> iterator = subtitles.listIterator(subtitles.size());
        while (iterator.hasPrevious()) {
            SubtitleEntity subtitle = iterator.previous();
            String text = subtitle.getSubtitle().toLowerCase();
            if (text.contains("bye")) {
                return Optional.of(subtitle.getTimeStr().substring(17, 29));
            }
        }
        return Optional.empty();
    }

  /**
   * 给定时间字符串增加时间间隔并返回新的时间字符串
   *
   * @param timeTag    原始时间字符串，格式为"HH:mm:ss,SSS"
   * @param timePeriod 增加的时间间隔（毫秒）
   * @return 更新后的时间字符串
   */
  public static String addTimePeriod(String timeTag, Integer timePeriod) {
    // 解析时间字符串为DateTime对象
    DateTime dateTime = DateUtil.parse(timeTag, "HH:mm:ss,SSS");

    // 增加时间间隔（毫秒）
    dateTime = dateTime.offset(DateField.MILLISECOND, timePeriod);

    // 格式化输出为所需的时间字符串格式
    return dateTime.toString("HH:mm:ss,SSS");
  }

  public static void main(String[] args) {
    String originalTime = "00:05:55,640";
    int timePeriod = 123;
    String updatedTime = addTimePeriod(originalTime, timePeriod);
    System.out.println("更新后的时间: " + updatedTime);
  }
}
