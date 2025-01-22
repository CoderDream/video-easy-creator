package com.coderdream.util.txt.filter;

public class StringReplaceRegex {

    public static void main(String[] args) {
        String originalString1 = "这是一个测试字符串，包含 ^4^ 这样的字符。";
        String originalString2 = "这是一个测试字符串，包含 ^123^ 这样的字符。";
        String originalString3 = "这是一个测试字符串，包含 ^99999^ 这样的字符。";
        String originalString4 = "这是一个测试字符串，不包含 ^数字^ 这样的字符。";
        String regex = "\\^\\d+\\^";
        String replacement = "替换后的内容";

         System.out.println("原始字符串1: " + originalString1);
         String replacedString1 = originalString1.replaceAll(regex, replacement);
         System.out.println("替换后的字符串1: " + replacedString1);

        System.out.println("原始字符串2: " + originalString2);
         String replacedString2 = originalString2.replaceAll(regex, replacement);
        System.out.println("替换后的字符串2: " + replacedString2);

         System.out.println("原始字符串3: " + originalString3);
         String replacedString3 = originalString3.replaceAll(regex, replacement);
        System.out.println("替换后的字符串3: " + replacedString3);

         System.out.println("原始字符串4: " + originalString4);
         String replacedString4 = originalString4.replaceAll(regex, replacement);
        System.out.println("替换后的字符串4: " + replacedString4);
    }
}
