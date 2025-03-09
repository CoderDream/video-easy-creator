package com.coderdream.util.ollama;

import lombok.Data;

/**
 * Ollama 请求参数封装类
 */
@Data
public class OllamaReq {

  private String model;
  private String prompt;
  private boolean stream;
}
