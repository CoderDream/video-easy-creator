package com.coderdream.util.sentence;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串标点符号替换工具类
 */
@Slf4j
public class PunctuationUtil2 {

    // 定义英文标点符号和中文标点符号的映射关系
    private static final Map<String, String> ENGLISH_TO_CHINESE_MAP = new HashMap<>();
    private static final Map<String, String> CHINESE_TO_ENGLISH_MAP = new HashMap<>();


    static {
        // 初始化英文到中文标点符号的映射
        ENGLISH_TO_CHINESE_MAP.put(",", "，");
        ENGLISH_TO_CHINESE_MAP.put("\\.", "。"); // 注意 . 需要转义
        ENGLISH_TO_CHINESE_MAP.put("!", "！");
        ENGLISH_TO_CHINESE_MAP.put("\\?", "？");
        ENGLISH_TO_CHINESE_MAP.put(":", "：");
        ENGLISH_TO_CHINESE_MAP.put(";", "；");
        ENGLISH_TO_CHINESE_MAP.put("'", "‘");
        ENGLISH_TO_CHINESE_MAP.put("\"", "“");
        ENGLISH_TO_CHINESE_MAP.put("\\(", "（"); // 转义 (
        ENGLISH_TO_CHINESE_MAP.put("\\)", "）");// 转义 )
        // 初始化中文到英文标点符号的映射
        CHINESE_TO_ENGLISH_MAP.put("，", ",");
        CHINESE_TO_ENGLISH_MAP.put("。", ".");
        CHINESE_TO_ENGLISH_MAP.put("！", "!");
        CHINESE_TO_ENGLISH_MAP.put("？", "?");
        CHINESE_TO_ENGLISH_MAP.put("：", ":");
        CHINESE_TO_ENGLISH_MAP.put("；", ";");
        CHINESE_TO_ENGLISH_MAP.put("‘", "'");
        CHINESE_TO_ENGLISH_MAP.put("“", "\"");
        CHINESE_TO_ENGLISH_MAP.put("（", "(");
        CHINESE_TO_ENGLISH_MAP.put("）", ")");
    }


    /**
     * 替换英文标点符号为中文符号并去掉标点符号前面的空格
     *
     * @param input  待处理的字符串
     * @return 处理后的字符串
     */
    public static String replaceEnglishPunctuationToChinese(String input) {
        long startTime = System.currentTimeMillis();
        if (input == null || input.isEmpty()) {
            log.warn("输入字符串为空，无需处理");
            return input;
        }
        try {
            String result = input;
            for (Map.Entry<String, String> entry : ENGLISH_TO_CHINESE_MAP.entrySet()) {
                 // 使用正则表达式替换，同时去除前面的空格
                String key = entry.getKey();
                Pattern pattern = Pattern.compile("\\s*(" + key + ")"); // 先匹配空格，然后匹配标点符号
                Matcher matcher = pattern.matcher(result);
               result = matcher.replaceAll(entry.getValue()); // 只替换标点符号
            }
           log.info("英文标点替换为中文，耗时：{}", formatTime(System.currentTimeMillis() - startTime));
           return result;
        } catch (Exception e) {
             log.error("替换英文标点符号为中文符号时发生异常", e);
            return input;
        }
    }


    /**
     * 替换中文标点符号为英文符号并去掉标点符号前面的空格
     *
     * @param input 待处理的字符串
     * @return 处理后的字符串
     */
    public static String replaceChinesePunctuationToEnglish(String input) {
          long startTime = System.currentTimeMillis();
        if (input == null || input.isEmpty()) {
            log.warn("输入字符串为空，无需处理");
            return input;
        }
        try {
            String result = input;
              for (Map.Entry<String, String> entry : CHINESE_TO_ENGLISH_MAP.entrySet()) {
                   // 使用正则表达式替换，同时去除前面的空格
                 String key = entry.getKey();
                   Pattern pattern = Pattern.compile("\\s*(" + key + ")");
                    Matcher matcher = pattern.matcher(result);
                    result = matcher.replaceAll(entry.getValue());
             }
           log.info("中文标点替换为英文，耗时：{}", formatTime(System.currentTimeMillis() - startTime));
             return result;

        } catch (Exception e) {
             log.error("替换中文标点符号为英文符号时发生异常", e);
            return input;
        }
    }



    /**
     * 格式化时间，返回时分秒毫秒
     * @param milliseconds 毫秒数
     * @return 格式化后的时间字符串
     */
    private static String formatTime(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        long ms = milliseconds % 1000;
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, ms);
    }


    public static void main(String[] args) {
        String text1 = "你好， 世界 !  测试 。  ：    ；‘’“”  （）";
        String text2 ="你好, world ! test . :  ;  ' '\" \" ( )";
          String text3 = "Oh, my God. That was a close shave , wasn't it?";
        String text4 = "Smell ice, can ya ? Bleeding Christ !";
        String text5 = "Note the time and enter it in the log .";
        String result1 = PunctuationUtil2.replaceEnglishPunctuationToChinese(text2);
        String result2 = PunctuationUtil2.replaceChinesePunctuationToEnglish(text1);
         String result3 = PunctuationUtil2.replaceEnglishPunctuationToChinese(text3);
         String result4 = PunctuationUtil2.replaceEnglishPunctuationToChinese(text4);
         String result5 = PunctuationUtil2.replaceEnglishPunctuationToChinese(text5);
        System.out.println("替换英文标点为中文后:" + result1);
        System.out.println("替换中文标点为英文后:" + result2);
         System.out.println("替换英文标点为中文后:" + result3);
         System.out.println("替换英文标点为中文后:" + result4);
        System.out.println("替换英文标点为中文后:" + result5);
    }
}