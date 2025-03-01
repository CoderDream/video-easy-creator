package com.coderdream.util.sentence.demo04;

import java.util.ArrayList;
import java.util.List;

public class PunctuationUtil {

    /**
     * 将超过maxLength字符的字符串按逗号分割，如果没有逗号，按长度切割
     *
     * @param input     输入字符串
     * @param maxLength 最大长度
     * @return 分割后的字符串列表
     */
    public static List<String> splitLongSentence(String input, int maxLength) {
        List<String> result = new ArrayList<>();

        // 空字符串或maxLength无效值直接返回
        if (input == null || input.isEmpty() || maxLength <= 0) {
            return result;
        }

        // 递归终止条件：长度不超过maxLength
        if (input.length() <= maxLength) {
            result.add(input);
            return result;
        }

        // 从中间向两侧找最近的逗号
        int mid = input.length() / 2;
        int leftComma = input.lastIndexOf("，", mid);
        int rightComma = input.indexOf("，", mid);

        int commaIndex;
        if (leftComma == -1 && rightComma == -1) {
            // 没有逗号时，按最大长度硬切
            int splitPoint = Math.min(maxLength, input.length());
            String firstPart = input.substring(0, splitPoint);
            result.add(firstPart);

            String remainingPart = input.substring(splitPoint);
            //避免空字符串递归, 并且maxLength需要大于0
            if (!remainingPart.isEmpty() && maxLength > 0) {
                result.addAll(splitLongSentence(remainingPart, maxLength));
            }

            return result;
        } else if (leftComma == -1) {
            commaIndex = rightComma;
        } else if (rightComma == -1) {
            commaIndex = leftComma;
        } else {
            // 选择离中点最近的逗号
            commaIndex = (mid - leftComma <= rightComma - mid) ? leftComma : rightComma;
        }

        // 按逗号位置切割并递归处理
        String firstPart = input.substring(0, commaIndex + 1);
        result.addAll(splitLongSentence(firstPart, maxLength));

        String remainingPart = input.substring(commaIndex + 1);
        if(!remainingPart.isEmpty()) {
            result.addAll(splitLongSentence(remainingPart, maxLength));
        }


        return result;
    }

    public static void main(String[] args) {
        // 测试分割长句子
        String longSentence = "作者自己经常凌晨两点才睡（偶尔还会因为看电影太入迷拖到三点半），但因为睡眠质量高，皮肤状态反而更好。";
        List<String> splitResult = splitLongSentence(longSentence, 1);
        for (String part : splitResult) {
            System.out.println(part);
        }
    }
}
