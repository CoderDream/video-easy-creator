package com.coderdream.util;

import com.coderdream.config.OpenAiChatModelUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.SentencePair;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import reactor.core.publisher.Flux;

@Slf4j
public class CdChatgptUtil {

  public static String chatWithMessage(String msg) {
    //可选参数在配置文件中配置了，在代码中也配置了，那么以代码的配置为准，也就是代码的配置会覆盖掉配置文件中的配置
    Flux<ChatResponse> flux = OpenAiChatModelUtil.getInstance()
      .stream(new Prompt(msg, OpenAiChatOptions.builder()
        .withModel("gpt-3.5-turbo") ////.withModel("gpt-4-32k") //gpt的版本，32k是参数量
        .withTemperature(0.4) //温度越高，回答得比较有创新性，但是准确率会下降，温度越低，回答的准确率会更好
        .build()));

    StringBuilder stringBuilder = new StringBuilder();
    flux.toStream().forEach(chatResponse -> {
      String content = chatResponse.getResult().getOutput().getContent();
      if (StrUtil.isNotBlank(content)) {
        stringBuilder.append(content);
      }
    });
    log.info("StringBuilder：{}", stringBuilder);
//    Mono<List<ChatResponse>> listMono = flux.collectList();
//    listMono.subscribe(chatResponses -> {
////            System.out.println("总共回答了" + chatResponses.size() + "次");
//    });

    String rawString = stringBuilder.toString();

    return CdStringUtil.removeNewLinesAndBlankLines(rawString);
  }

  /**
   * 将消息字符串解析为包含英文句子和对应中文翻译的列表。
   *
   * @param msg 输入的消息字符串。
   * @return 一个列表，第一元素是英文句子，第二元素是对应的中文翻译。 如果输入不符合要求，则返回空列表。
   */
  public static List<SentencePair> chatWithMessageList(String msg) {
    List<SentencePair> resultList = new ArrayList<>();

    // 调用 chatWithMessage 方法获取处理后的消息。
    String message = chatWithMessage(msg);
    SentencePair sentencePair = new SentencePair();
    // 检查消息中是否恰好包含一个左括号。
    int leftParenthesisCount = CdStringUtil.countLeftParentheses(message);
    int rightParenthesisCount = CdStringUtil.countRightParentheses(message);
    if (leftParenthesisCount > 0 && rightParenthesisCount > 0) {
      sentencePair = new SentencePair();
      // 查找第一个 '（' 和 '）' 的位置。
      int leftParenthesisIndex = message.indexOf("（");
      int rightParenthesisIndex = message.indexOf("）");

      // 确保左右括号都存在且顺序正确。
      if (leftParenthesisIndex != -1 && rightParenthesisIndex != -1
        && leftParenthesisIndex < rightParenthesisIndex) {
        // 根据括号的位置提取英文和中文句子。
        String englishSentence = message.substring(0, leftParenthesisIndex)
          .trim();
        String chineseSentence = message.substring(leftParenthesisIndex + 1,
          rightParenthesisIndex).trim();

        // 判断englishSentence是否以中文字符开头
        if (englishSentence.matches("^[\\u4e00-\\u9fa5].*")) {
          // 如果是中文开头，则将englishSentence赋给chineseSentence
          String temp = chineseSentence;
          chineseSentence = englishSentence;
          englishSentence = temp;
        }

        // 将提取的句子添加到结果列表中。
        sentencePair.setEnglishSentence(CdStringUtil.removePrefix(englishSentence));
        sentencePair.setChineseSentence(chineseSentence);
        resultList.add(sentencePair);
      }

      return resultList;
    }

    // 如果输入不符合要求，则返回空列表。
    if (StrUtil.isNotEmpty(message)) {

      // 调用方法解析字符串
      resultList = ChatProcessor.parseStringToObjects(message);

      // 打印结果
      for (SentencePair pair : resultList) {
        System.out.println(pair);
      }

      return resultList;
    }

    return resultList;
  }

  /**
   * 将消息字符串解析为包含英文句子和对应中文翻译的列表。
   *
   * @param msg 输入的消息字符串。
   * @return 一个列表，第一元素是英文句子，第二元素是对应的中文翻译。 如果输入不符合要求，则返回空列表。
   */
  public static List<SentencePair> getSentenceFromChatgpt(String msg) {
    // 调用 chatWithMessageList 方法获取处理后的消息。
    return chatWithMessageList(msg + " 造句并翻译");
  }

  /**
   * 翻译：a kind of scientist in the middle ages who tried to change metals into
   * gold and find a cure for all illnesses
   */
  public static String getTranslate(String msg) {
    // 调用 chatWithMessageList 方法获取处理后的消息。
    String message = chatWithMessage(msg + " 翻译");
//    log.info("message:{}", message);
    return message;
  }

}
