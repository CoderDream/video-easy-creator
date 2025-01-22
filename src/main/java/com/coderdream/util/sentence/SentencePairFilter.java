package com.coderdream.util.sentence;

import com.coderdream.entity.SentencePair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public class SentencePairFilter {

  /**
   * 过滤掉句子对列表中相同的对象，保留原始顺序
   *
   * @param sentencePairs 待过滤的句子对列表
   * @return 过滤后的句子对列表
   */
  public static List<SentencePair> filterDuplicateSentencePairs(
    List<SentencePair> sentencePairs) {
    if (sentencePairs == null || sentencePairs.isEmpty()) {
      return new ArrayList<>(); // 返回空列表
    }
    // 使用 LinkedHashSet 来保证去重且保留插入顺序
    LinkedHashSet<SentencePair> set = new LinkedHashSet<>(sentencePairs);
    return new ArrayList<>(set);
  }


  public static void main(String[] args) {
    List<SentencePair> list1 = new ArrayList<>();
    list1.add(new SentencePair("hello", "你好"));
    list1.add(new SentencePair("world", "世界"));
    list1.add(new SentencePair("hello", "你好"));
    list1.add(new SentencePair("java", "爪哇"));
    list1.add(new SentencePair("world", "世界"));

    List<SentencePair> filteredList1 = filterDuplicateSentencePairs(list1);
    System.out.println("原始列表： " + list1);
    System.out.println("过滤重复后列表：" + filteredList1);

    List<SentencePair> list2 = new ArrayList<>();
    list2.add(new SentencePair("hello", "你好"));
    list2.add(new SentencePair("world", "世界"));

    List<SentencePair> filteredList2 = filterDuplicateSentencePairs(list2);
    System.out.println("原始列表： " + list2);
    System.out.println("过滤重复后列表：" + filteredList2);

    List<SentencePair> list3 = new ArrayList<>();

    List<SentencePair> filteredList3 = filterDuplicateSentencePairs(list3);
    System.out.println("原始列表： " + list3);
    System.out.println("过滤重复后列表：" + filteredList3);

  }
}
