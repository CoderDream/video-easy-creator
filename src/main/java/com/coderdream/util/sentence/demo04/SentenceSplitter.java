package com.coderdream.util.sentence.demo04;

import java.util.ArrayList;
import java.util.List;

public class SentenceSplitter {

    private static final int MAX_LENGTH = 80;
    private static final String CHINESE_COMMA = "，";
    private static final String SPLIT_MARKER = "；";

    /**
     * 分割句子，超过最大长度则从中文逗号处分割
     *
     * @param sentence  原始句子
     * @return 分割后的句子列表
     */
    public static List<String> splitSentence(String sentence) {
        List<String> result = new ArrayList<>();
        splitSentenceInternal(sentence, result);
        return result;
    }

    private static void splitSentenceInternal(String sentence, List<String> result) {
        if (sentence.length() <= MAX_LENGTH) {
            result.add(sentence);
            return;
        }

        int commaIndex = sentence.indexOf(CHINESE_COMMA);

        if (commaIndex == -1) {
            // 没有找到逗号，直接添加，不再分割
            result.add(sentence);
            return;
        }

        // 找到中间的逗号
        int middle = sentence.length() / 2;
        int bestSplitIndex = -1;
        int minDistance = Integer.MAX_VALUE;

        for (int i = 0; i < sentence.length(); i++) {
            if (sentence.charAt(i) == CHINESE_COMMA.charAt(0)) {
                int distance = Math.abs(i - middle);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestSplitIndex = i;
                }
            }
        }


        if (bestSplitIndex != -1) {
            String firstPart = sentence.substring(0, bestSplitIndex + 1);
            String secondPart = sentence.substring(bestSplitIndex + 1);

            if (firstPart.length() > 0) {
                result.add(firstPart);
            }
            if (secondPart.length() > 0) {
                splitSentenceInternal(secondPart, result);
            }

        } else {
            // 找不到合适的逗号，直接添加，不再分割
            result.add(sentence);
        }

    }


    public static void main(String[] args) {
        String sentence = "作者自己经常凌晨两点才睡（偶尔还会因为看电影太入迷拖到三点半），但因为睡眠质量高，皮肤状态反而更好。";
        List<String> splittedSentences = splitSentence(sentence);

        for (int i = 0; i < splittedSentences.size(); i++) {
            String s = splittedSentences.get(i);
            System.out.print(s);
            if (i < splittedSentences.size() - 1) {
                System.out.print(SPLIT_MARKER);
            }

        }

        System.out.println("\n--- Test Case 2 ---");
        String sentence2 = "这是一个很短的句子，不需要分割。";
        List<String> splittedSentences2 = splitSentence(sentence2);

        for (int i = 0; i < splittedSentences2.size(); i++) {
            String s = splittedSentences2.get(i);
            System.out.print(s);
            if (i < splittedSentences2.size() - 1) {
                System.out.print(SPLIT_MARKER);
            }

        }

        System.out.println("\n--- Test Case 3 ---");
        String sentence3 = "这是一个超过长度但是没有逗号的句子，abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz。";
        List<String> splittedSentences3 = splitSentence(sentence3);

        for (int i = 0; i < splittedSentences3.size(); i++) {
            String s = splittedSentences3.get(i);
            System.out.print(s);
            if (i < splittedSentences3.size() - 1) {
                System.out.print(SPLIT_MARKER);
            }

        }

        System.out.println("\n--- Test Case 4 ---");
        String sentence4 = "这是一个很长，逗号很多的句子，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，，。";
        List<String> splittedSentences4 = splitSentence(sentence4);

        for (int i = 0; i < splittedSentences4.size(); i++) {
            String s = splittedSentences4.get(i);
            System.out.print(s);
            if (i < splittedSentences4.size() - 1) {
                System.out.print(SPLIT_MARKER);
            }

        }


        System.out.println("\n--- Test Case 5 ---");
        String sentence5 = "这是一个测试句子，超过了80个字符，包含多个逗号。这个句子的目的是测试分割逻辑。";
        List<String> splittedSentences5 = splitSentence(sentence5);

        for (int i = 0; i < splittedSentences5.size(); i++) {
            String s = splittedSentences5.get(i);
            System.out.print(s);
            if (i < splittedSentences5.size() - 1) {
                System.out.print(SPLIT_MARKER);
            }

        }

    }
}
