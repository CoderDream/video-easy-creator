package com.coderdream.util.subtitle;

import com.coderdream.entity.SubtitleEntity;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * 字幕解析器，负责读取文本并生成字幕文件
 */
@Slf4j
public class SubtitleParser10 {

    /**
     * 主方法，执行文本解析和字幕文件生成
     *
     * @param args 命令行参数，未使用
     */
    public static void main(String[] args) {
        String filePath = "D:\\Download\\纳瓦尔宝典：财富与幸福指南+(埃里克．乔根森+(Eric+Jorgenson))+(Z-Library)\\v1.txt";
        parseTextToSubtitle(filePath);
    }

    /**
     * 将文本文件解析为字幕对象列表，并保存到文件中
     *
     * @param filePath 文本文件路径
     */
    public static void parseTextToSubtitle(String filePath) {
        log.info("开始解析文本文件：{}", filePath);
        List<SubtitleEntity> subtitles = parseText(filePath);
        if (subtitles != null && !subtitles.isEmpty()) {
            String outputFilePath = generateOutputFilePath(filePath);
            saveSubtitlesToFile(subtitles, outputFilePath);
        } else {
            log.warn("未解析到字幕内容或解析过程出现异常。");
        }
        log.info("解析完成");
    }


    /**
     * 生成输出文件路径，保持源文件目录和名称规则
     *
     * @param inputFilePath 输入文件路径
     * @return 输出文件路径
     */
    private static String generateOutputFilePath(String inputFilePath) {
        Path path = Paths.get(inputFilePath);
        String fileName = path.getFileName().toString();
        String outputFileName = fileName.replace(".txt", "_srt.txt");
        Path outputDir = path.getParent();
        if (outputDir == null) {
            return outputFileName; // 如果没有父目录，则直接返回文件名
        }
        return outputDir.resolve(outputFileName).toString();
    }


    /**
     * 解析文本文件，返回字幕对象列表
     *
     * @param filePath 文本文件路径
     * @return 字幕对象列表
     */
    private static List<SubtitleEntity> parseText(String filePath) {
        List<SubtitleEntity> subtitles = new ArrayList<>();
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(" "); // 将所有行内容追加到 StringBuilder
            }
            String text = content.toString();
            // 替换 — 和 —— 为换行符，并移除所有空格
            text = text.replace("—", "\n").replace("——", "\n").replaceAll("\\s+", " ").trim();
            // 替换序号格式
            text = replaceNumberFormat(text);
            // 使用正则表达式匹配句子结束符：句号、感叹号、问号、中文分号、中文冒号， 匹配中文左双引号开始
            String[] sentences = Pattern.compile("(?<=[。！？；：])|(?=“)").split(text);


             // 检查句子末尾是否缺少标点符号，并添加中文句号
            for (int i = 0; i < sentences.length; i++) {
                String sentence = sentences[i].trim();
                if(!sentence.isEmpty() && !Pattern.matches(".*[。！？；：”]$", sentence)) {
                    sentences[i] = sentence + "。";
                }

            }
            int index = 1;
            for (String sentence : sentences) {
                if (sentence.trim().isEmpty()) {
                    continue; // 跳过空句子
                }
                List<String> subSentences = splitSentence(sentence.trim());
                 for(String subSentence : subSentences){
                    if (subSentence.trim().isEmpty()) {
                        continue; // 跳过空子句子
                    }
                    SubtitleEntity subtitle = new SubtitleEntity();
                    subtitle.setSubIndex(index++);
                    //移除字符串中的所有空格
                    subtitle.setSubtitle(subSentence.replaceAll("\\s+", ""));
                    subtitles.add(subtitle);
                }


            }

        } catch (IOException e) {
            log.error("读取文件失败: {}", e.getMessage(), e);
            return null;
        }

        if (subtitles.isEmpty()) {
            log.warn("未找到任何字幕内容");
        } else {
            log.info("解析到 {} 条字幕", subtitles.size());
        }

        return subtitles;
    }

    /**
     * 替换序号格式，如将 1. 替换为 一、
     *
     * @param text 输入文本
     * @return 替换后的文本
     */
    private static String replaceNumberFormat(String text) {
        String[] chineseNumerals = {"一、", "二、", "三、", "四、", "五、", "六、", "七、", "八、", "九、", "十、"};
        Pattern pattern = Pattern.compile("(\\d+)\\."); // 匹配 "数字." 格式的序号

        StringBuilder result = new StringBuilder();
        int lastMatchEnd = 0;
        java.util.regex.Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            int matchStart = matcher.start();
            int matchEnd = matcher.end();
            result.append(text, lastMatchEnd, matchStart); // 添加匹配前的文本

            String numberStr = matcher.group(1); // 获取数字部分
            int number = Integer.parseInt(numberStr); // 解析数字

            if (number > 0 && number <= chineseNumerals.length) {
                result.append(chineseNumerals[number - 1]); // 替换为中文数字序号
            } else {
                result.append(matcher.group());// 如果超出范围，则不替换
            }

            lastMatchEnd = matchEnd;
        }
        result.append(text.substring(lastMatchEnd));// 添加匹配后的文本
        return result.toString();

    }


    /**
     * 根据句子长度和中文逗号分割句子
     *
     * @param sentence 输入句子
     * @return 分割后的句子列表
     */
    private static List<String> splitSentence(String sentence) {
        if (sentence.length() > 20) {
            return Arrays.stream(Pattern.compile("(?<=[，])").split(sentence))
                   .map(String::trim)
                   .filter(sub -> !sub.isEmpty())
                   .collect(Collectors.toList());

        } else {
             return List.of(sentence);
        }
    }



    /**
     * 将字幕列表保存到文件中
     *
     * @param subtitles     字幕对象列表
     * @param outputFilePath 输出文件路径
     */
    private static void saveSubtitlesToFile(List<SubtitleEntity> subtitles, String outputFilePath) {
        log.info("开始保存字幕到文件：{}", outputFilePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (SubtitleEntity subtitle : subtitles) {
                writer.write(subtitle.getSubtitle());
                writer.newLine(); // 添加换行符
            }
            log.info("字幕保存完成，共保存 {} 条字幕", subtitles.size());

        } catch (IOException e) {
            log.error("保存字幕文件失败：{}", e.getMessage(), e);
        }
    }
}
