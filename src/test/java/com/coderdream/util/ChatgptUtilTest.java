package com.coderdream.util;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.entity.SentencePair;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ChatgptUtilTest {

  @Test
  void chatWithMessage() {
    String msg = "elixir of life 造句并翻译";
    String message = CdChatgptUtil.chatWithMessage(msg);
    assertNotNull(message);
    log.info("message: {}", message);
  }

  @Test
  void chatWithMessageList() {
    String msg = "elixir of life 造句并翻译";
    List<SentencePair> list = CdChatgptUtil.chatWithMessageList(msg);
    assertNotNull(list);
    for (SentencePair s : list) {
      log.info("s: {}", s);
    }
  }

  @Test
  void getSentenceFromChatgpt() {
    String msg = "down to luck";
    msg = "in the near future";
    List<SentencePair> list = CdChatgptUtil.getSentenceFromChatgpt(msg);
    assertNotNull(list);
    for (SentencePair s : list) {
      log.info("s: {}", s);
    }
  }

  @Test
  void getTranslate() {
    String msg = "a kind of scientist in the middle ages who tried to change metals into gold and find a cure for all illnesses";
    String translate = CdChatgptUtil.getTranslate(msg);
    assertNotNull(translate);
    log.info("translate: {}", translate);
  }

}
