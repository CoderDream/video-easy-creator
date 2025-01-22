package com.coderdream.util.cd;

public class ChineseCharacterUtil {

    /**
     * 判断字符是否为中文字符（包括中文标点符号）
     *
     * @param c 待判断的字符
     * @return true 如果是中文字符，false 否则
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否全部由中文字符组成（包括中文标点符号）
     *  优化，使用Character.codePointAt 和 Character.isIdeographic 解决unicode的增补字符问题
     * @param s 待判断的字符串
     * @return true 如果全部由中文字符组成，false 否则
     */

    public static boolean isChinese(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        int len = s.length();
        for(int i = 0; i < len;) {
            int codePoint = s.codePointAt(i);
            if (!Character.isIdeographic(codePoint)){
                return false;
            }
            i+= Character.charCount(codePoint);
        }
        return true;
    }


    /**
     * 判断字符串是否包含中文字符（包括中文标点符号）
     *
     * @param str 待判断的字符串
     * @return true 如果包含中文字符，false 否则
     */
    public static boolean containsChinese(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断字符串是否全部由中文字符组成（包括中文标点符号）
     *
     * @param str 待判断的字符串
     * @return true 如果全部由中文字符组成，false 否则
     */
    public static boolean isAllChinese(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!isChinese(c)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println("isChinese('中'): " + isChinese('中'));   // true
        System.out.println("isChinese('，'): " + isChinese('，'));   // true
        System.out.println("isChinese('a'): " + isChinese('a'));    // false
        System.out.println("isChinese('1'): " + isChinese('1'));    // false

        System.out.println("containsChinese(\"hello, world!\"): " + containsChinese("hello, world!"));   // false
        System.out.println("containsChinese(\"你好，世界！\"): " + containsChinese("你好，世界！"));       // true
        System.out.println("containsChinese(\"hello,你好\"): " + containsChinese("hello,你好"));         // true
        System.out.println("containsChinese(\"hello,，\"): " + containsChinese("hello,，"));          // true

        System.out.println("isAllChinese(\"你好，世界！\"): " + isAllChinese("你好，世界！"));       // true
        System.out.println("isAllChinese(\"hello,你好\"): " + isAllChinese("hello,你好"));         // false
        System.out.println("isAllChinese(\"，，。\"): " + isAllChinese("，，。"));       // true
        System.out.println("isAllChinese(\"hello\"): " + isAllChinese("hello"));            // false


    }
}
