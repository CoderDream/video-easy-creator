package com.coderdream.util.mdict.dict.parser;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class WordUtilTest {

  @Test
  void getWordPhonetics() {

    String wordPhonetics = WordUtil.getWordPhonetics("exclusive");
    log.info("wordPhonetics: {}", wordPhonetics);
    Assertions.assertNotSame(null, wordPhonetics);

  }
}
