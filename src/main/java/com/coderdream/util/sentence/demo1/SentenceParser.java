package com.coderdream.util.sentence.demo1;

import com.coderdream.util.cd.CdStringUtil;
import com.coderdream.vo.SentenceVO;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SentenceParser {

  public static List<SentenceVO> parseSentences(String input) {
    List<SentenceVO> sentenceList = new ArrayList<>();
    if (input == null || input.isEmpty()) {
      return sentenceList;
    }
    // 先去掉  ``` 代码块
    input = input.replace("```\n", "");
    input = input.replace("```", "");

    String[] split = input.split("\n");
    SentenceVO sentenceVO;
    for (int i = 0; i < split.length; i++) {
      sentenceVO = new SentenceVO();
      // 过滤掉空行和不以 / 开头的行
      if (split[i].startsWith("/")) {
        sentenceVO.setPhonetics(CdStringUtil.processString(split[i])); // 去掉中间的空格
        sentenceList.add(sentenceVO);
      }
    }

    // 判断输入的句子是否为偶数行，如果不是则抛出异常
//    if (split.length % 2 == 0) {
//      SentenceVO sentenceVO;
//      for (int i = 0; i < split.length; i++) {
//        sentenceVO = new SentenceVO();
//        // 判断是否为偶数行
//        if (split[i].startsWith("/") && split[i + 1] != null && !split[i
//          + 1].startsWith("/")) {
//          sentenceVO.setPhonetics(split[i]);
//          sentenceVO.setEnglish(split[i + 1]);
//        } else if (!split[i].startsWith("/") && split[i + 1] != null && split[i
//          + 1].startsWith("/")) {
//          sentenceVO.setEnglish(split[i + 1]);
//          sentenceVO.setPhonetics(split[i]);
//        } else {
//          log.error("输入的句子格式不正确，{} {}", split[i], split[i + 1]);
//          break;
//        }
//        sentenceList.add(sentenceVO);
//        i++;
//      }
//    }

//    // 正则表达式匹配英文和音标，允许音标为空
//    Pattern pattern = Pattern.compile("(.+?)(?:\\s*/(.+?)/)?(?:\\n|$)",
//      Pattern.MULTILINE);
//    Matcher matcher = pattern.matcher(input);
//
//    while (matcher.find()) {
//      String english = matcher.group(1).trim(); // 捕获英文，并去除首尾空格
//      String phonetics = matcher.group(2) != null ? matcher.group(2).trim()
//        : null; // 捕获音标，如果为空则为 null
//      sentenceList.add(new SentenceVO(english, "/" + phonetics + "/",
//        null)); // 创建 SentenceVO 对象，中文翻译默认为 null
//    }
    return sentenceList;
  }

  public static List<SentenceVO> parseSentencesBackup(String input) {
    List<SentenceVO> sentenceList = new ArrayList<>();
    if (input == null || input.isEmpty()) {
      return sentenceList;
    }
    String[] split = input.split("\n");
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
      .filter(sentenceVO -> sentenceVO.getPhonetics() != null
        && !sentenceVO.getPhonetics().isEmpty()) // 过滤掉空音标
      .map(SentenceVO::getPhonetics)
      .collect(Collectors.toList());
  }

  public static void main(String[] args) {
    String input = "Scene 41 Checkup\n/siːn fɔːrti wʌn tʃɛkʌp/\nYou should keep an empty stomach before examination.\n/juː ʃʊd kiːp æn ɛmpti ˈstʌmək bɪˈfɔːr ɪɡˌzæmɪˈneɪʃən/\nI need to get my eyes checked.\n/aɪ niːd tuː get maɪ aɪz tʃɛkt/\nThen take your contacts out at first.\n/ðɛn teɪk jɔːr ˈkɑːntækts aʊt æt fɜːrst/\n";

    List<SentenceVO> sentences = SentenceParser.parseSentencesBackup(input);

    for (SentenceVO sentence : sentences) {
      System.out.println(sentence);
    }
  }
}
