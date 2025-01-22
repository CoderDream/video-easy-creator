package com.coderdream.util;

import com.coderdream.entity.SentencePair;
import com.coderdream.util.cd.CdStringUtil;
import java.util.ArrayList;
import java.util.List;

public class ChatProcessor {

    /**
     * 将输入字符串解析为对象列表，每个对象包含英文句子和对应的中文句子。
     *
     * @param input 输入的多行字符串，格式为：
     *              序号. 英文句子\n中文句子\n
     * @return 包含英文和中文句子的对象列表。
     */
    public static List<SentencePair> parseStringToObjects(String input) {
        List<SentencePair> sentencePairs = new ArrayList<>();

        if (input == null || input.trim().isEmpty()) {
            return sentencePairs;
        }

        // 按行分割输入字符串
        String[] lines = input.split("\n");

        for (int i = 0; i < lines.length; i += 2) {
            // 检查是否有对应的中英文句子对
            if (i + 1 < lines.length) {
                String englishLine = lines[i].trim();
                String chineseLine = lines[i + 1].trim();

                // 去掉英文句子中的序号部分
//                int dotIndex = englishLine.indexOf(".");
//                if (dotIndex != -1) {
//                    englishLine = englishLine.substring(dotIndex + 1).trim();
//                }

                // TODO
                // 判断englishSentence是否以中文字符开头
                if (englishLine.matches("^[\\u4e00-\\u9fa5].*")) {
                    // 如果是中文开头，则将englishSentence赋给chineseSentence
                    String temp = chineseLine;
                    chineseLine = englishLine;
                    englishLine = temp;
                }

                // 创建 SentencePair 对象并添加到列表中
                SentencePair pair = new SentencePair(CdStringUtil.removePrefix(englishLine), chineseLine);
                sentencePairs.add(pair);
            }
        }

        return sentencePairs;
    }

    public static void main(String[] args) {
        // 示例输入
        String input = "1. Winning the lottery is often down to luck rather than skill.\n" +
                       "中奖通常更多地取决于运气而不是技巧。\n" +
                       "2. Whether we succeed or fail in life can sometimes come down to luck.\n" +
                       "在生活中成功或失败有时取决于运气。\n" +
                       "3. The outcome of the game was down to luck, as both teams played equally well.\n" +
                       "比赛的结果取决于运气，因为两支队伍表现都很出色。";

        // 调用方法解析字符串
        List<SentencePair> sentencePairs = ChatProcessor.parseStringToObjects(input);

        // 打印结果
        for (SentencePair pair : sentencePairs) {
            System.out.println(pair);
        }
    }
}
