package com.coderdream.util.txt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParagraphUtil {

  /**
   * 用于匹配简单句的正则表达式，包含中英文句号、问号、感叹号或省略号
   */
  private static final Pattern SENTENCE_PATTERN = Pattern.compile(
    "([^。.?？！!…......]+[。.?？！!…......])");// Pattern.compile("([^。，,?!…]+[。，,?!…])");

  /**
   * 将一个段落按照中英文句号、问号、感叹号和省略号分割成句子列表
   *
   * @param paragraph 需要分割的段落文本
   * @return 包含分割后句子的列表
   */
  public static List<String> splitSentences(String paragraph) {
    List<String> sentences = new ArrayList<>();
    if (paragraph == null || paragraph.isEmpty()) {
      return sentences; // 如果段落为空或null，返回空列表
    }

    Matcher matcher = SENTENCE_PATTERN.matcher(paragraph);
    while (matcher.find()) {
      sentences.add(matcher.group(1).trim()); // 将匹配到的句子添加到列表，并去除前后空格
    }

    return sentences;
  }

  public static void main(String[] args) {
    String paragraph = "Let me see. \"Wanted：manager for upand-coming firm. Must have good organizational skills. Experience a plus. Please contact Susan Lee. Oh, I don't know... 让我瞧瞧。 招聘：极富发展潜力的公司招聘经理。需良好的组织才能。需有经验。有意者请与苏珊·李联系。哦，我不知道......";

    paragraph = "记住，他们想找可以和别人共同工作的人。你要向他们展示你是多么得好相处，多么地有个人魅力！";

    List<String> sentences = ParagraphUtil.splitSentences(paragraph);
    for (String sentence : sentences) {
      System.out.println(sentence);
    }
    // 输出结果：
    // Let me see.
    // "Wanted：manager for upand-coming firm.
    // Must have good organizational skills.
    // Experience a plus.
    // Please contact Susan Lee."
    // Oh, I don't know...
    // 让我瞧瞧。
    // "招聘：极富发展潜力的公司招聘经理。
    // 需良好的组织才能。
    // 需有经验。
    // 有意者请与苏珊·李联系"。
    // 哦，我不知道......
  }
}
