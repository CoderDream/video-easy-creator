package com.coderdream.util.bbc;

import java.util.ArrayList;
import java.util.List;

public class SentenceSplitter {

    /**
     * 根据分号和短横线分割句子，分号和短横线都保留。
     *
     * @param input 输入的句子字符串
     * @return 包含前半句和后半句的字符串列表，如果只有前半句则后半句为空
     */
     public static List<String> splitSentence(String input) {
        List<String> result = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return result;
        }

        // 先替换多空格为单空格
        input = input.replaceAll("\\s+", " ");

        int semicolonIndex = input.indexOf(';');
        int hyphenIndex = input.indexOf("- ");

        if (semicolonIndex != - 1 && hyphenIndex != - 1) {
            // 同时存在分号和短横线
            if (semicolonIndex < hyphenIndex) { // 分号在前
                 result.add(input.substring(0, semicolonIndex).trim() + ";");
                 result.add("- " + input.substring(hyphenIndex).trim());
            } else { // 短横线在前
               result.add(input.substring(0, hyphenIndex).trim() + ";");
               result.add("- " + input.substring(hyphenIndex).trim());
            }
        } else if (semicolonIndex != - 1) {
            // 只有分号
            result.add(input.substring(0, semicolonIndex).trim() + ";");
        }
        else if (hyphenIndex != - 1) {
            // 只有短横线
            result.add(input.substring(0, hyphenIndex).trim());
            result.add("- " + input.substring(hyphenIndex).trim());
        } else {
            // 没有分号和短横线
            result.add(input.trim());
        }

        return result;
    }


    public static void main(String[] args) {
        String input1 = "This is the first part; and this is the second- part";
        List<String> result1 = SentenceSplitter.splitSentence(input1);
        System.out.println("Input 1: " + input1);
        System.out.println("Result 1: " + result1);

        String input2 = "This is the first part- and this is the second part";
        List<String> result2 = splitSentence(input2);
        System.out.println("Input 2: " + input2);
        System.out.println("Result 2: " + result2);

        String input3 = "This is a sentence with no separator";
        List<String> result3 = splitSentence(input3);
        System.out.println("Input 3: " + input3);
        System.out.println("Result 3: " + result3);

        String input4 = "This is the first part; and this is the second part";
        List<String> result4 = splitSentence(input4);
        System.out.println("Input 4: " + input4);
        System.out.println("Result 4: " + result4);

         String input5 = "This is the first part- ; and this is the second part";
        List<String> result5 = splitSentence(input5);
        System.out.println("Input 5: " + input5);
        System.out.println("Result 5: " + result5);

        String input6 = "This is the first part- ; and this- is the second part";
        List<String> result6 = splitSentence(input6);
        System.out.println("Input 6: " + input6);
        System.out.println("Result 6: " + result6);

    }
}
