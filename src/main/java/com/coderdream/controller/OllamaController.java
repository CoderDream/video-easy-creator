package com.coderdream.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/ollama")
public class OllamaController {

  @Resource
  private OllamaChatModel ollamaChatModel;

  /**
   * 流式对话
   *
   * @param message 用户指令
   * @return 对话结果流
   */
  @GetMapping("/streamChat")
  public Flux<ChatResponse> generateStream(
    @RequestParam("message") String message) {
    message = "请使用中文简体回答：" + message;
    Prompt prompt = new Prompt(new UserMessage(message));
    return ollamaChatModel.stream(prompt);
  }

  /**
   * 普通对话
   *
   * @param message 用户指令
   * @return 对话结果
   */
  @GetMapping("/chat")
  public String generate(@RequestParam("message") String message) {
    message = "请使用中文简体回答：" + message;
    Prompt prompt = new Prompt(new UserMessage(message));
    ChatResponse chatResponse = ollamaChatModel.call(prompt);
    String content = chatResponse.getResult().getOutput().getContent();
    System.out.println("content = " + content);
    return chatResponse.toString();
  }
}
