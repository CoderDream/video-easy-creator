package com.coderdream.util.bbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringSplitter4 {

    /**
     * 分割字符串，当字符串长度超过 maxLength 时，使用指定的分隔符进行分割。
     *
     * @param input     要分割的字符串
     * @param maxLength 最大长度
     * @return 分割后的字符串列表
     */
    public static List<String> splitString(String input, int maxLength) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }

        if (input.length() <= maxLength) {
            return Arrays.asList(input); // 如果小于等于maxLength，则不需要分割
        }

        List<String> result = new ArrayList<>();
        String[] commaSeparators = {": ", "; ", ":", ";"};
        String[] commonSeparators = {" and ", " that ", " when ", " but ", " for ", " because ", " with ", " where ", " which ", " how ",
            " why ", " by ", " from ", " is ", " to ", " not ", " who ", " or ", " And ", " That ", " When ", " But ",
            " For ", " Than ", " Because ", " With ", " Where ", " Which ", " How ", " Why ", " By ", " From ", " Is ",
            " To ", " Not ", " Who ", " Or "};

        // 先按逗号分割
        List<String> commaSplittedList = StringSplitUtil2.splitStringWithComma(input);
        for (String s : commaSplittedList) {
            splitAndAdd(s, maxLength, commaSeparators, false, result);  // 添加冒号分隔符分割后的子句
        }
        List<String> result2 = new ArrayList<>();
        for (String s : result) {
            splitAndAdd(s, maxLength, commonSeparators, true, result2); // 添加常用分隔符分割后的子句
        }

        return result2;
    }

    /**
     * 拆分字符串，并根据prependSeparator决定是否保留分隔符。
     *
     * @param input            输入字符串
     * @param maxLength        最大长度
     * @param separators       分隔符数组
     * @param prependSeparator 是否保留分隔符
     * @param result           结果列表
     */
    private static void splitAndAdd(String input, int maxLength, String[] separators, boolean prependSeparator, List<String> result) {

        if (input.length() <= maxLength) {
            result.add(input);
            return;
        }
        List<String> currentList = new ArrayList<>(List.of(input));
        for (String separator : separators) {
            List<String> tempResult = new ArrayList<>();
            for (String current : currentList) {
                if (current.length() <= maxLength) {
                    tempResult.add(current);
                } else {
                    List<String> splittedStrings = prependSeparator ? StringSplitter6.splitAndPrependSeparator(
                        current, separator) : StringSplitter6.splitAndSeparator(current, separator);
                    tempResult.addAll(splittedStrings);
                }
            }
            currentList = tempResult;
        }
        result.addAll(currentList); // 把所有分割后的句子都添加到结果集中。

    }

    public static List<String> splitString(String input) {
        int maxLength = 65;
        return splitString(input, maxLength);
    }

    public static List<String> splitStringList(List<String> strings) {
        List<String> result = new ArrayList<>();
        for (String string : strings) {
            result.addAll(StringSplitter4.splitString(string));
        }
        return result;
    }

    public static List<String> splitStringList(List<String> strings, int maxLength) {
        List<String> result = new ArrayList<>();
        for (String string : strings) {
            result.addAll(StringSplitter4.splitString(string, maxLength));
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("列表:");
        String input = "If you are living happily in isolation in The Scottish Highlands, for example, I'm sure you could feel lonely if you came here to London.";
        input = "That is my job. And I will say it’s very easy to speak truth from this podium when you have a president who is implementing policies that are wildly popular with the American people,";

        int maxLength = 65;
        List<String> sentenceList = StringSplitter4.splitString(input);
        for (String string : sentenceList) {
            System.out.println(string);
        }

        sentenceList = StringSplitter4.splitStringList(sentenceList);
    }
}
