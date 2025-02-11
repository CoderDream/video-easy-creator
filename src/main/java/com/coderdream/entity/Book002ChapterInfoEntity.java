package com.coderdream.entity;

import java.util.List;
import lombok.Data;

@Data
public class Book002ChapterInfoEntity {
  /**
   * 章节标题；例如：Chapter 8　心情
   */
  private String chapterStr;
  /**
   * Section字符串
   */
  private List<Book002SceneEntity> sceneEntityList;
}


