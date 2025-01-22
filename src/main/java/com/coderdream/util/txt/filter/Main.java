package com.coderdream.util.txt.filter;

public class Main {
    public static void main(String[] args) {
        String original = "这是一段包含 ^12345^ 的测试字符串。";
        String replacement = "新的内容";
        String result = StringUtil2.replaceNumberPattern(original, replacement);
        System.out.println(result);  // 输出：这是一段包含 新的内容 的测试字符串。
    }
}
