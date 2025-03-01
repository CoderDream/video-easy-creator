package com.coderdream.util.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringChecker {

    /**
     * 判断字符串是否包含英文字母（A-Z 和 a-z）和英文标点符号。
     *
     * @param str 要检查的字符串
     * @return 如果字符串包含英文字母或英文标点符号，则返回 true；否则返回 false。
     */
    public static boolean containsEnglishCharactersAndPunctuation(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        String regex = "[a-zA-Z,.?!;:'\"()\\[\\]{}<>/\\\\|`~\\-+=*&%$#@ ]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        return matcher.find(); // 直接返回是否找到匹配的字符
    }

    /**
     * 更严格的检查，检查字符串是否只包含英文字母和英文标点符号
     *
     * @param str 要检查的字符串
     * @return 如果字符串只包含英文字母和英文标点符号，则返回 true；否则返回 false。
     */
    public static boolean onlyContainsEnglishCharactersAndPunctuation(String str) {

        String regex = "^[a-zA-Z,.?!;:'\"()\\[\\]{}<>/\\\\|`~\\-+=*&%$#@ ]*$";
        return (str != null) && str.matches(regex);
    }

    /**
     * 判断字符串是否包含中文字符和中文标点符号。
     *
     * @param str 要检查的字符串
     * @return 如果字符串包含中文字符或中文标点符号，则返回 true；否则返回 false。
     */
    public static boolean containsChineseCharactersAndPunctuation(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        // 正则表达式：匹配中文字符和常见的中文标点符号
        String regex = "[\\u4e00-\\u9fa5。，、？！；：‘’“”《》【】…—～]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        return matcher.find();

    }

    /**
     * 更严格的检查，检查字符串是否只包含中文字符和中文标点符号。
     *
     * @param str 要检查的字符串
     * @return 如果字符串只包含中文字符和中文标点符号，则返回 true；否则返回 false。
     */
    public static boolean onlyContainsChineseCharactersAndPunctuation(String str) {

        String regex = "^[\\u4e00-\\u9fa5。，、？！；：‘’“”《》【】…—～]*$";
        return (str != null) && str.matches(regex);
    }

    public static void main(String[] args) {
        String str1 = "Hello, world!";
        String str2 = "你好世界";
        String str3 = "12345";
        String str4 = "Hello123!";
        String str5 = "";
        String str6 = null;
        String str7 = "你好，世界！";
        String str8 = "Hello，世界！";

        System.out.println("\"" + str1 + "\" contains English characters and punctuation: " + containsEnglishCharactersAndPunctuation(str1)); // true
        System.out.println("\"" + str2 + "\" contains English characters and punctuation: " + containsEnglishCharactersAndPunctuation(str2)); // false
        System.out.println("\"" + str3 + "\" contains English characters and punctuation: " + containsEnglishCharactersAndPunctuation(str3)); // false
        System.out.println("\"" + str4 + "\" contains English characters and punctuation: " + containsEnglishCharactersAndPunctuation(str4)); // true
        System.out.println("\"" + str5 + "\" contains English characters and punctuation: " + containsEnglishCharactersAndPunctuation(str5)); // false
        System.out.println("\"" + str6 + "\" contains English characters and punctuation: " + containsEnglishCharactersAndPunctuation(str6)); // false
        System.out.println("\"" + str7 + "\" contains English characters and punctuation: " + containsEnglishCharactersAndPunctuation(str7)); // false
        System.out.println("\"" + str8 + "\" contains English characters and punctuation: " + containsEnglishCharactersAndPunctuation(str8)); // true

        System.out.println("\"" + str1 + "\" only contains English characters and punctuation: " + onlyContainsEnglishCharactersAndPunctuation(str1)); // true
        System.out.println("\"" + str2 + "\" only contains English characters and punctuation: " + onlyContainsEnglishCharactersAndPunctuation(str2)); // false
        System.out.println("\"" + str3 + "\" only contains English characters and punctuation: " + onlyContainsEnglishCharactersAndPunctuation(str3)); // false
        System.out.println("\"" + str4 + "\" only contains English characters and punctuation: " + onlyContainsEnglishCharactersAndPunctuation(str4)); // false
        System.out.println("\"" + str5 + "\" only contains English characters and punctuation: " + onlyContainsEnglishCharactersAndPunctuation(str5)); // false
        System.out.println("\"" + str6 + "\" only contains English characters and punctuation: " + onlyContainsEnglishCharactersAndPunctuation(str6)); // false
        System.out.println("\"" + str7 + "\" only contains English characters and punctuation: " + onlyContainsEnglishCharactersAndPunctuation(str7)); // false
        System.out.println("\"" + str8 + "\" only contains English characters and punctuation: " + onlyContainsEnglishCharactersAndPunctuation(str8)); // false

        System.out.println("\"" + str1 + "\" contains Chinese characters and punctuation: " + containsChineseCharactersAndPunctuation(str1)); // false
        System.out.println("\"" + str2 + "\" contains Chinese characters and punctuation: " + containsChineseCharactersAndPunctuation(str2)); // true
        System.out.println("\"" + str3 + "\" contains Chinese characters and punctuation: " + containsChineseCharactersAndPunctuation(str3)); // false
        System.out.println("\"" + str4 + "\" contains Chinese characters and punctuation: " + containsChineseCharactersAndPunctuation(str4)); // false
        System.out.println("\"" + str5 + "\" contains Chinese characters and punctuation: " + containsChineseCharactersAndPunctuation(str5)); // false
        System.out.println("\"" + str6 + "\" contains Chinese characters and punctuation: " + containsChineseCharactersAndPunctuation(str6)); // false
        System.out.println("\"" + str7 + "\" contains Chinese characters and punctuation: " + containsChineseCharactersAndPunctuation(str7)); // true
        System.out.println("\"" + str8 + "\" contains Chinese characters and punctuation: " + containsChineseCharactersAndPunctuation(str8)); // false

        System.out.println("\"" + str1 + "\" only contains Chinese characters and punctuation: " + onlyContainsChineseCharactersAndPunctuation(str1)); // false
        System.out.println("\"" + str2 + "\" only contains Chinese characters and punctuation: " + onlyContainsChineseCharactersAndPunctuation(str2)); // true
        System.out.println("\"" + str3 + "\" only contains Chinese characters and punctuation: " + onlyContainsChineseCharactersAndPunctuation(str3)); // false
        System.out.println("\"" + str4 + "\" only contains Chinese characters and punctuation: " + onlyContainsChineseCharactersAndPunctuation(str4)); // false
        System.out.println("\"" + str5 + "\" only contains Chinese characters and punctuation: " + onlyContainsChineseCharactersAndPunctuation(str5)); // false
        System.out.println("\"" + str6 + "\" only contains Chinese characters and punctuation: " + onlyContainsChineseCharactersAndPunctuation(str6)); // false
        System.out.println("\"" + str7 + "\" only contains Chinese characters and punctuation: " + onlyContainsChineseCharactersAndPunctuation(str7)); // true
        System.out.println("\"" + str8 + "\" only contains Chinese characters and punctuation: " + onlyContainsChineseCharactersAndPunctuation(str8)); // false
    }
}
