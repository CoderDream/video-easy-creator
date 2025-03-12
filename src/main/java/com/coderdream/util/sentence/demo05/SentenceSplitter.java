package com.coderdream.util.sentence.demo05;

import java.util.ArrayList;
import java.util.List;

public class SentenceSplitter {

    private static final int MAX_LENGTH = 100; // 最大句子长度

    /**
     * 分割句子，确保每个子句的长度不超过 MAX_LENGTH。
     * 使用用户提供的分隔符数组按顺序分割。
     * 如果找不到任何分隔符分割，则返回原始长句子。
     *
     * @param sentence    要分割的句子
     * @param delimiters 分隔符字符串数组 (例如：{" when ", ", ", ". "})
     * @return 分割后的句子列表
     */
    public static List<String> splitSentence(String sentence, String[] delimiters) {
        List<String> result = new ArrayList<>();
        splitRecursive(sentence, delimiters, 0, result); //  从分隔符数组的第一个元素开始
        return result;
    }

    private static void splitRecursive(String sentence, String[] delimiters, int delimiterIndex, List<String> result) {
        if (sentence.length() <= MAX_LENGTH) {
            result.add(sentence);
            return;
        }

        if (delimiterIndex >= delimiters.length) {
            // 没有更多的分隔符可用，停止分割
            result.add(sentence);
            return;
        }

        String delimiter = delimiters[delimiterIndex];
        int splitIndex = sentence.length() / 2;

        int foundIndex = -1; // 分隔符的位置

        // 从中间往左边找分隔符
        for (int i = splitIndex; i >= 0; i--) {
            int index = sentence.substring(0, i).lastIndexOf(delimiter); // 查找分隔符
            if (index != -1) {
                foundIndex = index + delimiter.length() - 1; //  找到分隔符在句子中的位置
                break; // 找到后就退出循环
            }
        }

        if (foundIndex == -1) {
            // 当前分隔符没有找到，尝试使用下一个分隔符
            splitRecursive(sentence, delimiters, delimiterIndex + 1, result);
            return;
        }

        // 分割句子
        String left = sentence.substring(0, foundIndex + 1); // 包含分隔符
        String right = sentence.substring(foundIndex + 1);

        // 递归调用
        splitRecursive(left.trim(), delimiters, delimiterIndex, result); //  使用相同的分隔符进行递归
        splitRecursive(right.trim(), delimiters, delimiterIndex, result);
    }

    public static void main(String[] args) {
        String longSentence = "So this executive order will direct your Department of Education and Department of the Treasury to basically bring about modifications to the public service loan forgiveness program in order to ensure that people who are engaged in these sorts of activities can't benefit from a program that's really not intended to support those sorts of things.";
        String[] delimiters = {" that ", " to "};  // 用户提供的分隔符

        List<String> splitSentences = splitSentence(longSentence, delimiters);

        for (int i = 0; i < splitSentences.size(); i++) {
            System.out.println("Sentence " + (i + 1) + ": " + splitSentences.get(i));
        }

        String longSentence2 = "This is a long sentence. It needs to be split. Because it is too long.";
        String[] delimiters2 = {". ", ", "};

        List<String> splitSentences2 = splitSentence(longSentence2, delimiters2);
        System.out.println("\nSecond Example:");
        for (int i = 0; i < splitSentences2.size(); i++) {
            System.out.println("Sentence " + (i + 1) + ": " + splitSentences2.get(i));
        }
    }
}
