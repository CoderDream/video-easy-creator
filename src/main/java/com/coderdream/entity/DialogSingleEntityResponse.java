package com.coderdream.entity;

import java.util.List;
import lombok.Data;

@Data
public class DialogSingleEntityResponse {

  /**
   * 对象列表
   */
  private List<DialogSingleEntity> dialogSingleEntityList;

  /**
   * 错误对象列表
   */
  private List<DialogSingleEntityError> dialogSingleEntityErrorList;
}
