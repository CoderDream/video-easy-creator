package com.coderdream.entity;

import lombok.Data;

@Data
public class WordPronunciation {

  /**
   * 英式发音音标
   */
  private String britishPronunciation;
  /**
   * 美式发音音标
   */
  private String americanPronunciation;

}
