package com.coderdream.entity;

import java.util.List;
import lombok.Data;

@Data
public class Book002SceneEntity {
  /** 场景标题，如：Scene 63　问候 */
  private String sceneTitle;

  /** 内容列表 */
  private List<Book002ContentEntity> contentEntityList;
}
