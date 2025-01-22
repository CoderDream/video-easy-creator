package com.coderdream.entity;

import com.coderdream.util.sentence.StanfordSentenceSplitter;
import java.util.List;
import lombok.Data;

@Data
public class DialogSingleEntityError {

  /**
   * 英文句子列表
   */
  private List<String> enSentences;


  /**
   * 中文句子列表
   */
  private List<String> cnSentences;

  /**
   * 错误信息
   */
  private String errorMessage;

}
