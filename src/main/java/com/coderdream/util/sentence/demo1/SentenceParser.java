package com.coderdream.util.sentence.demo1;

import com.coderdream.vo.SentenceVO;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SentenceParser {

  public static List<SentenceVO> parseSentences(String input) {
    List<SentenceVO> sentenceList = new ArrayList<>();
    if (input == null || input.isEmpty()) {
      return sentenceList;
    }
    // 正则表达式匹配英文和音标，允许音标为空
    Pattern pattern = Pattern.compile("(.+?)(?:\\s*/(.+?)/)?(?:\\n|$)",
      Pattern.MULTILINE);
    Matcher matcher = pattern.matcher(input);

    while (matcher.find()) {
      String english = matcher.group(1).trim(); // 捕获英文，并去除首尾空格
      String phonetics = matcher.group(2) != null ? matcher.group(2).trim()
        : null; // 捕获音标，如果为空则为 null
      sentenceList.add(new SentenceVO(english, "/" + phonetics + "/",
        null)); // 创建 SentenceVO 对象，中文翻译默认为 null
    }
    return sentenceList;
  }

  /**
   * 从 SentenceVO 列表中提取音标列表
   *
   * @param sentenceList SentenceVO 列表
   * @return 音标列表
   */
  public static List<String> getPhoneticsList(List<SentenceVO> sentenceList) {
    if (sentenceList == null || sentenceList.isEmpty()) {
      return new ArrayList<>();  // 返回一个空列表
    }
    return sentenceList.stream()
      .filter(sentenceVO -> sentenceVO.getPhonetics() != null && !sentenceVO.getPhonetics().isEmpty()) // 过滤掉空音标
      .map(SentenceVO::getPhonetics)
      .collect(Collectors.toList());
  }

  public static void main(String[] args) {
    String input = "Scene 41 Checkup\n/siːn fɔːrti wʌn tʃɛkʌp/\nYou should keep an empty stomach before examination.\n/juː ʃʊd kiːp æn ɛmpti ˈstʌmək bɪˈfɔːr ɪɡˌzæmɪˈneɪʃən/\nI need to get my eyes checked.\n/aɪ niːd tuː get maɪ aɪz tʃɛkt/\nThen take your contacts out at first.\n/ðɛn teɪk jɔːr ˈkɑːntækts aʊt æt fɜːrst/\n";

    List<SentenceVO> sentences = SentenceParser.parseSentences(input);

    for (SentenceVO sentence : sentences) {
      System.out.println(sentence);
    }
  }
}
