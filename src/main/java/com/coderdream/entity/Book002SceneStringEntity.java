package com.coderdream.entity;

import java.util.List;
import lombok.Data;

@Data
public class Book002SceneStringEntity {
  /** 场景标题，如：Scene 63　问候 */
  private String sceneTitle;

  /** 内容列表 */
  private List<List<String>> contentStringList;
}
