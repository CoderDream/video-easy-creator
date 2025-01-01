package com.coderdream.entity;


import java.util.List;
import lombok.Data;

/**
 * 用于存储 OALDPE 词典内容的 Bean
 */
@Data
public class DictOaldpeBean {

  private String headword; // 词头
  private String pos;  // 词性
  private String ukPronunciation; // 英式发音
  private String usPronunciation; // 美式发音
  private List<Sense> senses; // 词义列表

  @Data
  public static class Sense {

    private String definition; // 英文定义
    private String translation; // 中文翻译
    private List<Example> examples; // 例句列表
  }

  @Data
  public static class Example {

    private String example; // 例句
    private String translation; // 例句翻译
  }
}
