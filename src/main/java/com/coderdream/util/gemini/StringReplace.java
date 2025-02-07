package com.coderdream.util.gemini;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringReplace {
    public static void main(String[] args) {
         String inputString = "Yes. /jes/\n" +
                "It's been a problem for about five months. /ɪts bin ə ˈprɑːbləm fɔr əˈbaʊt faɪv mʌnθs/\n" +
                "What's wrong? /wʌts rɒŋ/\n" +
                "What's the matter? /wʌts ðə ˈmætər/";

        String replacedString = StringReplace.replaceLineEndings(inputString);

        System.out.println("原始字符串：");
        System.out.println(inputString);
        System.out.println("\n替换后的字符串：");
        System.out.println(replacedString);
    }


    public static String replaceLineEndings(String input) {
         Pattern pattern = Pattern.compile("(\\s+)/(\\n)?", Pattern.MULTILINE);  // 匹配行末的空格斜线和可选的换行符
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll("$1\n/"); // 保留空格, 添加换行和斜线, 并添加换行符
    }
}
