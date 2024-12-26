package com.coderdream.util;

public class RemoveNewLinesAndBlankLines {
    public static void main(String[] args) {
        // 示例输入字符串，包含回车、换行和空行
        String input = "This is a line.\r\n\r\nThis is another line.\n\n\nAnd here is an empty line.\n\rAnother line with CR.";

        // 使用 StringBuilder 移除回车换行和空行
        StringBuilder result = removeNewLinesAndBlankLines(input);

        // 输出结果
        System.out.println(result.toString());
    }

    public static StringBuilder removeNewLinesAndBlankLines(String input) {
        StringBuilder result = new StringBuilder();
        boolean isNewLine = false; // 标记当前是否处于新行状态

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (currentChar == '\r' || currentChar == '\n') {
                // 如果是回车或换行，则设置 isNewLine 为 true
                isNewLine = true;
            } else {
                // 如果不是回车或换行，则检查是否处于新行状态
                if (isNewLine && !result.isEmpty()
                  && result.charAt(result.length() - 1) != '\n') {
                    // 如果之前是新行且 result 末尾不是换行符，则添加一个换行符以保持格式
                    result.append('\n');
                }
                // 添加当前字符到 result 中
                result.append(currentChar);
                // 重置 isNewLine 为 false
                isNewLine = false;
            }
        }

        // 移除末尾可能多余的换行符
        while (!result.isEmpty() && result.charAt(result.length() - 1) == '\n') {
            result.deleteCharAt(result.length() - 1);
        }

        return result;
    }
}
