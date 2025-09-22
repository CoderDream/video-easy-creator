package com.coderdream.util.subtitle;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 字幕处理工具类，用于合并多行字幕。
 *
 * @author Gemini Code Assist
 */
public class SubtitleMergerUtil {

    /**
     * 将一个标准字幕文件（如.srt, .txt）解析为SubtitleBlock对象的列表。
     *
     * @param filePath 字幕文件的路径。
     * @return SubtitleBlock对象的列表。
     */
    public static List<SubtitleBlock> parseSrtFile(String filePath) {
        List<String> lines = FileUtil.readLines(filePath, StandardCharsets.UTF_8);
        List<SubtitleBlock> subtitles = new ArrayList<>();
        int i = 0;
        while (i < lines.size()) {
            String line = lines.get(i).trim();
            // 检查是否为一个数字，这通常是字幕块的开始
            if (StrUtil.isNumeric(line)) {
                SubtitleBlock subtitle = new SubtitleBlock();
                // 1. 解析序号
                subtitle.setIndex(Integer.parseInt(line));
                i++;

                // 2. 解析时间戳
                if (i < lines.size()) {
                    subtitle.setTimestamp(lines.get(i).trim());
                    i++;
                }

                // 3. 解析文本行，直到遇到空行
                List<String> textLines = new ArrayList<>();
                while (i < lines.size() && !StrUtil.isBlank(lines.get(i))) {
                    textLines.add(lines.get(i).trim());
                    i++;
                }
                subtitle.setTextLines(textLines);
                subtitles.add(subtitle);
            }
            // 移动到下一行，跳过块之间的空行
            i++;
        }
        return subtitles;
    }

    /**
     * 将多行字幕合并为单行，并按照 "序号-时间戳-文本-空行" 的格式化为字符串。
     *
     * @param subtitles SubtitleBlock对象的列表。
     * @return 准备写入文件的格式化字符串。
     */
    public static String mergeAndFormatSubtitles(List<SubtitleBlock> subtitles) {
        StrBuilder sb = new StrBuilder();
        for (SubtitleBlock subtitle : subtitles) {
            sb.append(subtitle.getIndex()).append(System.lineSeparator());
            sb.append(subtitle.getTimestamp()).append(System.lineSeparator());
            // 使用空格将多行文本合并为一行
            String mergedText = String.join(" ", subtitle.getTextLines());
            sb.append(mergedText).append(System.lineSeparator());
            // 添加一个空行作为块分隔符
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * 处理字幕文件：读取、合并多行文本并写入到新文件。
     *
     * @param inputPath  源字幕文件的路径。
     * @param outputPath 处理后输出文件的路径。
     */
    public static void processSubtitleFile(String inputPath, String outputPath) {
        List<SubtitleBlock> subtitles = parseSrtFile(inputPath);
        String formattedContent = mergeAndFormatSubtitles(subtitles);
        FileUtil.writeString(formattedContent, outputPath, StandardCharsets.UTF_8);
        System.out.println("字幕文件处理完成！");
        System.out.println("输入文件: " + inputPath);
        System.out.println("输出文件: " + outputPath);
    }
}
