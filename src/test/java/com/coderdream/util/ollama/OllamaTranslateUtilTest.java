package com.coderdream.util.ollama;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OllamaTranslateUtilTest {

  @Test
  void translateText() {
    OllamaTranslateUtil.translateText("Hello, world!");
  }
}
