package com.coderdream.util.txt.filter;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class StringUtil {

    private static String replaceString(String line) {
        // 定义一个正则表达式，匹配所有中英文标点符号前面的空格
        String regex = "\\s+([\\p{Punct}。，、？！；：‘’“”【】《》「」『』…·（）—])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        // 使用replaceAll方法替换所有匹配到的空格
        line = matcher.replaceAll("$1");

        // 去除多余的空格（原代码保留）
        line = line.replace("  ", " ");
         //  去除数字（原代码保留）
        line = replaceNumberPattern(line, "");
        return line;
    }


    /**
    *  去除字符串中的数字
    */
    private static String replaceNumberPattern(String line, String replacement) {
    	 if (line == null || line.length() == 0) {
    	      return line;
    	    }
        return line.replaceAll("[\\d]+", replacement);
      }

    public static void main(String[] args) {
//        String testString = "这是一句测试，  包含  ， 多个标点，   ： ；  ，  。“”『』【】（）  。 还有数字 1234567。";
//        String result = replaceString(testString);
//        System.out.println("原始字符串： " + testString);
//        System.out.println("处理后字符串：" + result);

        String testString= "5. 这是测试文本，包含数字和特殊字符。";
        System.out.println(testString.trim().startsWith("5."));
    }
}
