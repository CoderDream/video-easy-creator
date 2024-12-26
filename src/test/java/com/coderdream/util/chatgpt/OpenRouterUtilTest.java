package com.coderdream.util.chatgpt;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.entity.Model;
import com.coderdream.entity.SentencePair;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class OpenRouterUtilTest {

  @Test
  void genSentencePair03() {
    String input = "用下面的词组造英文句子并翻译成中文，只需要返回一个用例：to venture out somewhere";
    SentencePair sentencePair =
      OpenRouterUtil.genSentence(
        ModelConstants.GOOGLE_GEMINI_FLASH_1_5_8B_EXP, input);
    log.info("{}", sentencePair);
  }

  @Test
  void genSentencePair04() {
    String input = "用下面的词组造英文句子并翻译成中文，只需要返回一个用例：to venture out somewhere";
    SentencePair sentencePair =
      OpenRouterUtil.genSentence(
        ModelConstants.GOOGLE_GEMINI_FLASH_1_5_8B_EXP, input);
    log.info("{}", sentencePair);
  }

  @Test
  void translate0103() {
    String input = "to venture out somewhere";
    String chinese = OpenRouterUtil.translate(input);
    log.info("{}", chinese);
  }

  @Test
  void translate0104() {
    String input = "chauvinist";
    String chinese = OpenRouterUtil.translate(input);
    log.info("{}", chinese);
  }

  /**
   * 尝试所有模型
   */
  @Test
  void translate0201() {
    String input = "chauvinist";
    input = "Generation X";

    List<Model> models = ModelParser.parseModelsFromFile();
    for (Model model : models) {
      String chinese = OpenRouterUtil.translate(model.getModelId(), input);
      log.info("{} sentencePair: {}", model.getModelName(), chinese);
    }
  }


  @Test
  void translate0203() {
    String input = "翻译：to venture out somewhere";
    String chinese =
      OpenRouterUtil.translate(
        ModelConstants.GOOGLE_GEMINI_FLASH_1_5_8B_EXP, input);
    log.info("{}", chinese);
  }

  @Test
  void translate0204() {
    String input = "翻译：to venture out somewhere";
    String chinese =
      OpenRouterUtil.translate(
        ModelConstants.GOOGLE_GEMINI_FLASH_1_5_EXP, input);
    log.info("{}", chinese);
  }


}
