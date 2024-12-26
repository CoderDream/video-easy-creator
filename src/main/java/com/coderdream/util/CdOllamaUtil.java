package com.coderdream.util;

import cn.hutool.core.util.StrUtil;
import com.coderdream.config.OpenAiChatModelUtil;
import com.coderdream.entity.SentencePair;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import reactor.core.publisher.Flux;

@Slf4j
public class CdOllamaUtil {

//  public static String chatWithMessage(String msg) {
//
//    OllamaChatModel ollamaChatModel = "";
//    msg = "请使用中文简体回答：" + msg;
//    Prompt prompt = new Prompt(new UserMessage(msg));
//    ChatResponse chatResponse = ollamaChatModel.call(prompt);
//    String content = chatResponse.getResult().getOutput().getContent();
//    System.out.println("content = " + content);
//    return chatResponse.toString();
//  }
// 用下面的词组造句并翻译成中文，只需要返回一条可以了，格式为englishSententc:xxx,chineseSentence:xxx：to venture out somewhere

}
