package com.coderdream.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SentenceDurationVO {

  private Integer id; // 句子ID
  private String phonetics; // 音标
  private String chinese; // 中文
  private double chineseDuration; // 中文时长
  private String english; // 英文
  private double englishDuration; // 英文时长
  private double totalDuration; // 总时长
}
