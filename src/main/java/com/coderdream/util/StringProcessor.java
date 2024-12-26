package com.coderdream.util;

public class StringProcessor {
    public static void main(String[] args) {
        String input = "The alchemist believed he had finally discovered the elixir of life a potion that could grant eternal youth and immortality.（炼金术士相信他终于发现了长生不老的灵丹妙药（——长生不老药。）";

        String[] result = processString(input);

        // 输出结果
        for (String sentence : result) {
            System.out.println(sentence);
        }
    }

    public static String[] processString(String input) {
        // 去除括号及其内容（包括中文和英文括号）
        String noBrackets = input.replaceAll("[（）()]", "");

        // 分割中英文句子，这里我们假设英文句子以空格分隔，中文句子以中文句号或逗号分隔
        // 但由于英文句子可能缺少句号，我们需要进一步处理
        String[] potentialSentences = noBrackets.split("[，。]");

        // 构建结果数组
        String[] result = new String[2];
        boolean englishFound = false;
        StringBuilder englishSentence = new StringBuilder();
        StringBuilder chineseSentence = new StringBuilder();

        for (String part : potentialSentences) {
            part = part.trim();
            if (!part.isEmpty()) {
                if (containsEnglish(part)) {
                    if (englishFound) {
                        // 如果已经找到了英文句子，那么当前部分应该与之前的英文句子合并
                        englishSentence.append(" ").append(part);
                    } else {
                        // 否则，这是第一个英文句子（或部分）
                        englishSentence.append(part);
                        englishFound = true;
                    }
                } else {
                    // 这是中文部分
                    if (chineseSentence.length() == 0) {
                        // 如果这是第一个中文部分，直接添加到chineseSentence中
                        chineseSentence.append(part);
                    } else {
                        // 如果已经有了中文句子，检查是否需要合并（这里假设不需要，因为中文句子通常是一个完整的表达）
                        // 但如果需要合并，可以在这里添加逻辑
                    }
                }
            }
        }

        // 将StringBuilder转换为String并放入结果数组
        result[0] = englishFound ? englishSentence.toString() : "";
        result[1] = chineseSentence.toString();

        return result;
    }

    // 辅助方法：检查字符串是否包含英文字母
    private static boolean containsEnglish(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c) && (Character.isUpperCase(c) || Character.isLowerCase(c))) {
                return true;
            }
        }
        return false;
    }
}
