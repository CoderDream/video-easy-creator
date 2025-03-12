package com.coderdream.util.sentence.demo05;

import java.util.ArrayList;
import java.util.List;

public class SentenceSplitWithComma {

    private static final int MAX_LENGTH = 100; // 最大句子长度

    /**
     * 分割句子，确保每个子句的长度不超过 MAX_LENGTH。
     * 如果找不到逗号分割，则返回原始长句子。
     *
     * @param sentence 要分割的句子
     * @return 分割后的句子列表
     */
    public static List<String> splitSentence(String sentence) {
        List<String> result = new ArrayList<>();
        splitRecursive(sentence, result);
        return result;
    }

    private static void splitRecursive(String sentence, List<String> result) {
        if (sentence.length() <= MAX_LENGTH) {
            result.add(sentence);
            return;
        }

        int middle = sentence.length() / 2;
        int splitIndex = -1;

        // 从中间往左边找逗号
        for (int i = middle; i >= 0; i--) {
            if (sentence.charAt(i) == ',') {
                splitIndex = i;
                break;
            }
        }

        // 如果没找到逗号，则返回原始长句子，不再强制分割
        if (splitIndex == -1) {
            result.add(sentence);
            return;
        }

        // 分割句子
        String left = sentence.substring(0, splitIndex + 1); // 包含逗号
        String right = sentence.substring(splitIndex + 1);

        // 递归调用
        splitRecursive(left.trim(), result);
        splitRecursive(right.trim(), result);
    }

    public static void main(String[] args) {
        String longSentence = "This is a very long sentence that needs to be split because it exceeds the maximum length allowed which is 100 characters. " +
                "It's important to have shorter sentences for better readability and to avoid overwhelming the reader with too much information at once. " +
                "Therefore this tool will help us break down the sentence into smaller more manageable chunks."; // 没有逗号的长句子

        List<String> splitSentences = SentenceSplitWithComma.splitSentence(longSentence);

        for (int i = 0; i < splitSentences.size(); i++) {
            System.out.println("Sentence " + (i + 1) + ": " + splitSentences.get(i));
        }

        String longSentenceWithComma = "This is a very long sentence, that needs to be split, because it exceeds the maximum length allowed, which is 100 characters.";
        List<String> splitSentencesWithComma = splitSentence(longSentenceWithComma);

        System.out.println("\nWith comma:");
        for (int i = 0; i < splitSentencesWithComma.size(); i++) {
            System.out.println("Sentence " + (i + 1) + ": " + splitSentencesWithComma.get(i));
        }
    }
}
