package com.coderdream.util.txt.doubao;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DTextFileProcessor {

    /**
     * 该方法用于处理文本文件，将 ch003_temp.txt 的内容处理后写入 ch003.txt 中
     * 处理规则如下：
     * 1. 若行末尾不是句子结束标点符号（中英文句号、问号、感叹号等），将下一行或下下一行合并到当前行，但后面一行包含中英文冒号则不合并，而是保留该行
     * 2. 移除以 ☺ 开头的行
     * 3. 移除以 ◎ 开头的行
     * 4. 去掉 Mr. 后面的空格
     * 5. 去掉中英文冒号后面的空格
     * 6. 移除以 注释1. 开头的行，移除以 xx. 开头的行（xx 代表 n 位数字）
     * 7. 移除以 [ 开头的行
     * 8. 移除以 ( 开头的行
     * 9. 移除以中英文左括号开头的行
     * 10. 移除以“英语通：”开头的行
     * 11. 移除以“摘自”开头的行
     * 12. 移除以“注释”开头的行
     * 13. 移除以“单词特搜”开头的行
     * 14. 移除以“■”开头的行
     * 15. 移除以“《”开头的行
     * 16. 移除以中英文双引号开头的行
     * 17. 移除所有标点符号之前的空格
     * 18. 替换所有的多空格为单空格
     * 19. 若全文只有一个“精彩点拨”，则删除“精彩点拨 ”开头及后面所有的行
     * 20. 合并时考虑不同的行尾符号（LF，CRLF）
     * 同时记录方法调用的耗时，返回时分秒毫秒
     *
     * @param inputFilePath  输入文件的路径，如 D:\0000\EnBook001\900\ch003\ch003_t.txt
     * @param outputFilePath 输出文件的路径，如 D:\0000\EnBook001\900\ch003\ch003.txt
     * @return 方法调用的耗时，格式为时分秒毫秒
     */
    public static String processTextFile(String inputFilePath, String outputFilePath) {
        Instant start = Instant.now();
        StringBuilder result = new StringBuilder();
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            String line = reader.readLine();
            while (line!= null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            log.error("读取文件时发生异常", e);
        }
        boolean hasHighlight = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            line = preprocessLine(line);
            if (!line.isEmpty()) {
                if (line.startsWith("精彩点拨 ") &&!hasHighlight) {
                    hasHighlight = true;
                    continue;
                } else if (line.startsWith("精彩点拨 ")) {
                    continue;
                }
                result.append(line);
                if (!isSentenceEnd(line)) {
                    if (i + 1 < lines.size()) {
                        String nextLine = lines.get(i + 1);
                        nextLine = preprocessLine(nextLine);
                        if (!nextLine.contains(":") &&!nextLine.contains("：")) {
                            result.append(removeAllExtraSpaces(nextLine));
                        } else {
                            result.append("\n").append(removeAllExtraSpaces(nextLine));
                        }
                        if (!isSentenceEnd(nextLine)) {
                            if (i + 2 < lines.size()) {
                                String nextNextLine = lines.get(i + 2);
                                nextNextLine = preprocessLine(nextNextLine);
                                if (!nextNextLine.contains(":") &&!nextNextLine.contains("：")) {
                                    result.append(removeAllExtraSpaces(nextNextLine));
                                } else {
                                    result.append("\n").append(removeAllExtraSpaces(nextNextLine));
                                }
                            }
                        }
                    }
                }
                result.append("\n");
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write(result.toString());
        } catch (IOException e) {
            log.error("写入文件时发生异常", e);
        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        long millis = duration.toMillis() % 1000;
        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis);
    }

    /**
     * 对一行进行预处理，包括移除特定开头的行，去掉 Mr. 后的空格，去掉中英文冒号后的空格，移除标点符号前的空格，替换多空格为单空格
     *
     * @param line 输入的一行文本
     * @return 处理后的一行文本，如果需要移除则返回空字符串
     */
    private static String preprocessLine(String line) {
        if (line.startsWith("☺") || line.startsWith("◎") || line.startsWith("[") || line.startsWith("(") || line.startsWith("（") ||
                line.startsWith("注释1.") || line.matches("\\d+\\..*") || line.startsWith("英语通：") || line.startsWith("摘自") ||
                line.startsWith("注释") || line.startsWith("单词特搜") || line.startsWith("■") || line.startsWith("《") ||
                line.startsWith("\"") || line.startsWith("“")) {
            return "";
        }
        line = line.replaceAll("Mr\\.\\s+", "Mr.");
        line = line.replaceAll(":\\s+", ":");
        line = line.replaceAll("：\\s+", "：");
        line = line.replaceAll("\\s+(\\p{Punct})", "$1"); // 移除标点符号前的空格
        line = line.replaceAll("\\s+", " "); // 替换多空格为单空格
        return line;
    }

    /**
     * 判断一行是否以句子结束标点符号结尾
     *
     * @param line 输入的一行文本
     * @return 如果以句子结束标点结尾返回 true，否则返回 false
     */
    private static boolean isSentenceEnd(String line) {
        if (line.endsWith(".") || line.endsWith("!") || line.endsWith("?") || line.endsWith("。") || line.endsWith("！") || line.endsWith("？")) {
            return true;
        }
        return false;
    }

    /**
     * 去除字符串中所有多余的空格，包括行内的多个连续空格和行尾的空格
     *
     * @param line 输入的字符串
     * @return 去除多余空格后的字符串
     */
    private static String removeAllExtraSpaces(String line) {
        return line.replaceAll("\\s+", " ").trim();
    }

    public static void main(String[] args) {
        String folderPath = "D:\\0000\\EnBook001\\900\\";
        String subFolder = "ch004";

        String inputFilePath = folderPath +subFolder+ File.separator + subFolder +"_t.txt";
        String outputFilePath = folderPath +subFolder+ File.separator + subFolder +".txt";
        String timeTaken = DTextFileProcessor.processTextFile(inputFilePath, outputFilePath);
        System.out.println("方法调用耗时: " + timeTaken);
    }
}
