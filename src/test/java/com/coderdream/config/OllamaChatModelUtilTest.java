package com.coderdream.config;


import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
@Slf4j
class OllamaChatModelUtilTest {

  @Test
  void getInstance() {

    OllamaChatModel ollamaChatModel = OllamaChatModelUtil.getInstance();
    log.info("instance: {}", ollamaChatModel);


    String message = "请使用中文简体回答：你好，世界！";

////      message = "请使用中文简体回答：" + message;
//    Prompt prompt = new Prompt(new UserMessage(message));
//    Flux<ChatResponse> stream = ollamaChatModel.stream(prompt);
//    stream.subscribe(chatResponse -> {
//      log.info("chatResponse: {}", chatResponse);
//    });

    Prompt prompt = new Prompt(new UserMessage(message));
    ChatResponse chatResponse = ollamaChatModel.call(prompt);
    String content = chatResponse.getResult().getOutput().getContent();
    System.out.println("content = " + content);
    //chatResponse.toString();
  }
}
