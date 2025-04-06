//package com.coderdream.util.mstts.demo05;
//
//import com.nillith.pinyin.Pinyin;
//
//public class PinyinUtil {
//
//  public static void main(String[] args) {
//    // 返回字符串
//    Pinyin.getPinyinString('你'); // nǐ
//    Pinyin.getPinyinString("你好，世界！"); // nǐ hǎo ， shì jiè ！
//
//// 返回Pinyin对象
//    Pinyin hao = Pinyin.getPinyin('好');
//    Pinyin[] hw = Pinyin.getPinyin("你好，世界！");
//
//// Pinyin对象的使用
//    hao.getInitial(); // h 声母
//    hao.getFinal(); // ǎo 韵母
//    hao.getFinalAscii(); // ao 韵母的ascii形式
//    hao.getTone(); // 3 声调
//    hao.toString(); // hǎo
//    hao.toStringAscii(); // hao3
//    hao.toStringAsciiNoTone(); // hao
//
//// 其他字符串方法
//    System.out.println(Pinyin.getPinyinStringAscii("你好，世界！"));
//    ; // ni3 hao3 ， shi4 jie4 ！
//    Pinyin.getPinyinStringAsciiNoTone("你好，世界！"); // ni hao ， shi jie ！
//    Pinyin.getPinyinString("你好，世界！", "-"/*自定义分隔符*/,
//      true/*忽略无查询结果的字符*/); // nǐ-hǎo-shì-jiè
//// 这个物体的重量是五公斤，请重复一遍。
//    System.out.println(
//      Pinyin.getPinyinStringAscii("这个物体的重量是五公斤，请重复一遍。"));
//    System.out.println(Pinyin.isHeteronym('好'));
//    ; // true  判断是否是多音字
//    Pinyin[] all = Pinyin.getPinyinAll('重'); // 获取多音字所有的拼音对象
//    for (Pinyin p : all) {
////      System.out.println(p.toStringAscii() + " " + p.getTone());
////      System.out.println(p.getFinalAscii() + " " + p.getTone());
//      System.out.println(p.toStringAsciiNoTone() + " " + p.getTone());
////      System.out.println(p.getFinal() + " " + p.getTone());
//    }
//  }
//}
