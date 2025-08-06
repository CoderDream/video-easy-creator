package com.coderdream.util.gemini;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class CallGeminiApiUtilTest {

  @Test
  void callApi() {
    String prompt = "Write a story about a magic backpack.";
    String s = CallGeminiApiUtil.callApi(prompt);
    log.info("{}", s);
  }

  @Test
  void testCallApi() {
    fail("Not yet implemented");
  }
}
