package com.coderdream.util.markdown;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LineRemover {


    /**
     * 删除以 | 开头的行和以 | [数字] 开头的行
     *
     * @param text 多行文本
     * @return 删除指定行后的文本
     */
    public static String removeUseLessLines(String text) {
        if (text == null || text.isEmpty()) {
            return ""; // 处理空或 null 的情况
        }

        // 定义匹配规则，匹配以 | 开头 或 以 | [数字] 开头的行
        String regex = "^\\|(\\s*\\[\\d+\\])?.*$"; // \\| 用于匹配 |，\\s* 匹配0个或多个空格， \\[ 和 \\] 用于匹配 [ ]

        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE); // MULTILINE 模式允许 ^ 和 $ 匹配每行的开头和结尾

        // 使用 stream 处理每一行，过滤掉匹配的行，并用换行符拼接
        return Arrays.stream(text.split("\\r?\\n")) // 将文本按行分割
                .filter(line -> !pattern.matcher(line).matches())  // 过滤掉匹配的行
                .collect(Collectors.joining(System.lineSeparator()));  // 使用换行符拼接剩余的行
    }



    public static void main(String[] args) {
        String text = "| 这是一行以 | 开头的行\n" +
                "| [123] 这是一行以 | [数字] 开头的行\n" +
                "这是一行普通的行\n" +
                "| [4] 这又是以 | [数字] 开头的行\n" +
                "|   这是一行以| 和多个空格开头的行\n" +
                "|  [678]   这也是以|和空格 [数字] 开头的行\n"+
                "|  [6789123123]   这也是以|和空格 [数字] 开头的行\n"+
               "这是另外一行\n" +
                "|这是最后一行以|开头的行";


        String result = removeUseLessLines(text);
        System.out.println("删除后的文本：\n" + result);
    }
}
