//package com.coderdream.util.mstts.demo03;
//
//import com.coderdream.util.python.PinyinToIpaConverter;
//import lombok.extern.slf4j.Slf4j; // 导入 Lombok 的 Slf4j 注解，用于自动生成日志对象
//import net.sourceforge.pinyin4j.PinyinHelper; // 导入 pinyin4j 库的 PinyinHelper 类，用于汉字转拼音
//import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType; // 导入 HanyuPinyinCaseType 类，用于设置拼音大小写
//import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat; // 导入 HanyuPinyinOutputFormat 类，用于设置拼音输出格式
//import net.sourceforge.pinyin4j.format.HanyuPinyinToneType; // 导入 HanyuPinyinToneType 类，用于设置拼音声调格式
//import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType; // 导入 HanyuPinyinVCharType 类，用于设置特殊字符的格式
//import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination; // 导入 BadHanyuPinyinOutputFormatCombination 异常类，用于处理拼音格式设置错误
//
//@Slf4j // 使用 Lombok 的 Slf4j 注解，自动生成 log 对象，用于日志记录
//public class PinyinConverter {
//
//  /**
//   * 获取汉字的所有拼音
//   *
//   * @param chineseCharacter 汉字字符
//   * @return 汉字的所有拼音，如果不是汉字则返回原字符
//   */
//  public static String[] getAllPinyin(char chineseCharacter) {
//    // 创建汉语拼音输出格式对象
//    HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
//    // 设置拼音大小写为小写
//    format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//    // 设置拼音声调格式为带数字
//    format.setToneType(
//      HanyuPinyinToneType.WITH_TONE_NUMBER); // 带声调数字，例如 zhong1, zhong2
//    // 设置特殊字符的格式为 unicode 格式
//    format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
//
//    // 判断字符是否为汉字
//    if (Character.toString(chineseCharacter).matches("[\\u4E00-\\u9FA5]+")) {
//      try {
//        // 将汉字转换为拼音数组
//        return PinyinHelper.toHanyuPinyinStringArray(chineseCharacter, format);
//      } catch (BadHanyuPinyinOutputFormatCombination e) {
//        // 记录错误日志
//        log.error("Error in converting character to pinyin, {}", e.getMessage(),
//          e);
//      }
//    } else {
//      // 如果不是汉字，则返回原字符
//      return new String[]{
//        Character.toString(chineseCharacter)};
//    }
//    return null; // Added to handle potential null return
//  }
//
//  /**
//   * @param string   汉字字符
//   * @param toneType 声调数字，例如1,2,3,4
//   * @return 汉字的所有拼音，如果不是汉字则返回原字符
//   */
//  public static String getPinyin(String string, Integer toneType) {
//    // 获取汉字的所有拼音
//    String[] pinyinArray = getAllPinyin(string.charAt(0));
//    if (pinyinArray != null) {
//      for (String pinyin : pinyinArray) {
//        if (pinyin.contains(toneType.toString())) {
//          return pinyin;
//        }
//      }
//    }
//    return null;
//  }
//
//  /**
//   * 获取汉字的所有拼音的IPA格式
//   *
//   * @param string   汉字字符
//   * @param toneType 声调数字，例如1,2,3,4
//   * @return 汉字的所有拼音，如果不是汉字则返回原字符
//   */
//  public static String getPinyinIpa(String string, Integer toneType) {
//    // 获取汉字的所有拼音
//    String[] pinyinArray = getAllPinyin(string.charAt(0));
//    if (pinyinArray != null) {
//      for (String pinyin : pinyinArray) {
//        if (pinyin.contains(toneType.toString())) {
//          return
//            pinyin.substring(0, pinyin.lastIndexOf(String.valueOf(toneType)))
//              + " "
//              + toneType;//PinyinToIpaConverter.convertPinyinToIpa(pinyin);
//        }
//      }
//    }
//    return null;
//  }
//
//  public static void main(String[] args) {
//
////    System.out.println(PinyinConverter.getPinyinIpa("重", 4));
//    System.out.println(PinyinConverter.getPinyinIpa("重", 2));
//
////    // 定义要转换的汉字
////    char chineseCharacter = '重';
////    // 获取汉字的所有拼音
////    String[] pinyinArray = getAllPinyin(chineseCharacter);
////
////    // 判断拼音数组是否为空
////    if (pinyinArray != null) {
////      // 打印汉字
////      System.out.println("Character: " + chineseCharacter);
////      // 打印所有可能的拼音
////      System.out.println("Possible Pinyin Pronunciations:");
////      // 循环打印拼音
////      for (String pinyin : pinyinArray) {
////        System.out.println(pinyin);
////      }
////    } else {
////      // 如果拼音数组为空，则打印未找到拼音的消息
////      System.out.println(
////        "Could not find pinyin for character: " + chineseCharacter);
////    }
//  }
//}
