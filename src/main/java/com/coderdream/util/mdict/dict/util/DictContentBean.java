package com.coderdream.util.mdict.dict.util;

import java.util.List;
import lombok.Data;

/**
 *  用于存储词典内容的 Bean
 */
@Data
public class DictContentBean {
  private String headword; // 词头
  private List<String> phonetics; // 音标
  private String pos;  // 词性
  private List<Meaning> meanings; // 词义列表
  private List<String> pronunciations; // 发音
  private List<String> synonyms; //同义词
  private List<String> antonyms; //反义词

  @Data
  public static class Meaning {
    private String number; // 序号
    private String definition; // 英文定义
    private String chineseDefinition; // 中文定义
    private List<Example> examples; // 例句列表
  }

  @Data
  public static class Example {
    private String example; // 例句
    private String translation; // 例句翻译
  }
}
