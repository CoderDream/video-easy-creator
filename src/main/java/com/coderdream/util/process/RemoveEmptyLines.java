package com.coderdream.util.process;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveEmptyLines {

    public static String removeEmptyLines(String text) {
        List<String> lines = Arrays.asList(text.split("\n"));
        List<String> nonBlankLines = lines.stream()
            .map(String::trim) // 去除每行首尾空格
            .filter(line -> !line.isEmpty()) // 过滤空行
            .collect(Collectors.toList());
        return String.join("\n", nonBlankLines);
    }

     public static void main(String[] args) {
        String textWithEmptyLines = "Line 1\n\n\nLine 2  \n  \nLine 3\n\n";
        String textWithoutEmptyLines = removeEmptyLines(textWithEmptyLines);
        System.out.println("Original:\n" + textWithEmptyLines);
        System.out.println("After removing empty lines:\n" + textWithoutEmptyLines);
    }
}
