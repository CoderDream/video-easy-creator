package com.coderdream.util.gemini;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import swiss.ameri.gemini.api.GenAi;
import swiss.ameri.gemini.api.GenAi.GeneratedContent;
import swiss.ameri.gemini.api.GenAi.Model;
import swiss.ameri.gemini.api.ModelVariant;
import swiss.ameri.gemini.api.Schema;

/**
 * 使用 JUnit5 测试 GeminiApiUtil 工具类
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GeminiApiUtilTest {

  @Test
  @Order(1)
  void testListModels() {
    log.info("----- 测试 listModels 方法开始");
    // 列出所有模型
    List<Model> models = GeminiApiUtil.listModels();
    log.info("Models: {}", models);
    log.info("----- 测试 listModels 方法结束");
  }

  @Test
  @Order(2)
  void testGetModel() {
    log.info("----- 测试 getModel 方法开始");
    // 列出所有模型
    Model model = GeminiApiUtil.getModel(ModelVariant.GEMINI_1_5_PRO);
    log.info("Model: {}", model);
    log.info("----- 测试 getModel 方法结束");
  }

  @Test
  @Order(301)
  void testCountTokens_301() {
    log.info("----- 测试 countTokens 方法开始");
    // 列出所有模型
    String content = "Write a short poem about nature.";
    Long totalTokens = GeminiApiUtil.countTokens(content);
    log.info("1.统计文本标记的数量: {}", totalTokens);
    log.info("----- 测试 countTokens 方法结束");
  }

  @Test
  @Order(302)
  void testCountTokens_302() {
    log.info("----- 测试 countTokens 302 方法开始");
    // 列出所有模型
    String content = "Write a 50 word story about a magic backpack.";
    Long totalTokens = GeminiApiUtil.countTokens(content);
    log.info("2.统计文本标记的数量: {}", totalTokens);
    log.info("----- 测试 countTokens 302 方法结束");
  }

  @Test
  @Order(401)
  void testGenerateContent_401() throws Exception {
    log.info("----- 测试 generateContent 方法开始");
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent = GeminiApiUtil.generateContent(
      "Write a short poem about nature.");
    log.info("Generated content: {}", generatedContent);
    log.info("----- 测试 generateContent 方法结束");
  }

  @Test
  @Order(402)
  void testGenerateContent_402() throws Exception {
    log.info("----- 2.测试 generateContent 方法开始");
    String prompt = "给我讲个Java程序员的笑话！";
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);
    log.info("2. Generated content: {}", generatedContent);
    log.info("----- 2.测试 generateContent 方法结束");
  }

  @Test
  @Order(501)
  void testGenerateContentStream() {
    log.info("----- 测试 generateContentStream 方法开始");
    // 生成文本内容（流式）
    List<GeneratedContent> streamResponse = GeminiApiUtil.generateContentStream(
      "Write a short poem about nature.");
    log.info("Generated stream content: {}", streamResponse);
    log.info("----- 测试 generateContentStream 方法结束");
  }

  @Test
  @Order(601)
  void testGenerateContentWithResponseSchema() throws Exception {
    log.info("----- 测试 generateContentWithResponseSchema 方法开始");
    // 生成内容，指定响应模式
    Schema schema = Schema.builder()
      .type(Schema.Type.ARRAY)
      .items(Schema.builder()
        .type(Schema.Type.OBJECT)
        .properties(Map.of(
          "recipe_name", Schema.builder()
            .type(Schema.Type.STRING)
            .build()
        ))
        .build())
      .build();
    GeneratedContent schemaResponse = GeminiApiUtil.generateContentWithResponseSchema(
      "List 3 popular cookie recipes.", schema);
    log.info("Generated content with schema: {}", schemaResponse);
    log.info("----- 测试 generateContentWithResponseSchema 方法结束");
  }

  @Test
  @Order(701)
  void testGenerateContentStreamWithResponseSchema() {
    log.info("----- 测试 generateContentStreamWithResponseSchema 方法开始");
    // 生成内容，指定响应模式
    Schema schema = Schema.builder()
      .type(Schema.Type.ARRAY)
      .items(Schema.builder()
        .type(Schema.Type.OBJECT)
        .properties(Map.of(
          "recipe_name", Schema.builder()
            .type(Schema.Type.STRING)
            .build()
        ))
        .build())
      .build();
    List<GeneratedContent> schemaResponse = GeminiApiUtil.generateContentStreamWithResponseSchema(
      "List 3 popular cookie recipes.", schema);
    log.info("Generated content stream with schema: {}", schemaResponse);
    log.info("----- 测试 generateContentStreamWithResponseSchema 方法结束");
  }

  @Test
  @Order(801)
  void testMultiChatTurn() {
    log.info("----- 测试 multiChatTurn 方法开始");
    // 多轮对话
    List<String> contents = List.of(
      "Write the first line of a story about a magic backpack.",
      "In the bustling city of Meadow brook, lived a young girl named Sophie. She was a bright and curious soul with an imaginative mind.",
      "Can you set it in a quiet village in 1600s France? Max 30 words"
    );
    List<GenAi.GeneratedContent> multiChatResponse = GeminiApiUtil.multiChatTurn(
      contents);
    log.info("Multi chat response: {}", multiChatResponse);
    log.info("----- 测试 multiChatTurn 方法结束");
  }

  @Test
  @Order(901)
  void testTextAndImage() throws Exception {
    log.info("----- 测试 textAndImage 方法开始");
    // 文本和图像内容
    GeneratedContent textImageResponse = GeminiApiUtil.textAndImage(
      "这张图片里是什么？", "scones.png");
    log.info("Text and image response: {}", textImageResponse);
    log.info("----- 测试 textAndImage 方法结束");
  }

  @Test
  @Order(1001)
  void testEmbedContents() {
    log.info("----- 测试 embedContents 方法开始");
    // 内容嵌入
    List<GenAi.ContentEmbedding> embeddings = GeminiApiUtil.embedContents(
      "Write a 50 word story about a magic backpack.");
    log.info("Embeddings: {}", embeddings);
    log.info("----- 测试 embedContents 方法结束");
  }
}
