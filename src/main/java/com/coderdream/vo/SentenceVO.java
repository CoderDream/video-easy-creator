package com.coderdream.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SentenceVO {


  private String english; // 英文句子
  private String phonetics; // 音标
  private String chinese; // 中文翻译
}
