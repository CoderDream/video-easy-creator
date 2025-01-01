package com.coderdream.entity;

import java.util.List;
import lombok.Data;

/**
 * 用于存储 Collins 词典内容的 Bean
 */
@Data
public class DictCollinsBean {

  private List<Sense> senses; // 词义列表

  @Data
  public static class Sense {

    private String number; // 序号
    private String pos;  // 词性
    private String definition; // 英文定义
    private String caption; // 标题，包含 序号，词性，英文定义
    private List<Example> examples; // 例句列表
  }

  @Data
  public static class Example {

    private String example; // 例句
    private String exampleWithHighlight; // 高亮后的例句
  }
}
