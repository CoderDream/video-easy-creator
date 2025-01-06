package com.coderdream.entity;

import java.util.List;
import lombok.Data;

@Data
public class DialogSingleSentenceEntity {

  /**
   * 主持人英文名
   */
  private String hostEn;
  /**
   * 主持人中文名
   */
  private String hostCn;
  /**
   * 脚本英文
   */
  private List<String> contentEn;
  /**
   * 脚本中文
   */
  private List<String> contentCn;

}
