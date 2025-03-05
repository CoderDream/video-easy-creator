package com.coderdream.util.gemini;

import com.coderdream.util.cd.CdConstants;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import swiss.ameri.gemini.api.*;
import swiss.ameri.gemini.gson.GsonJsonParser;
import swiss.ameri.gemini.spi.JsonParser;

/**
 * 使用 JUnit 测试 {@link GenAi} 功能的示例程序。
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GeminiTest {

  private static JsonParser parser;
  private static GenAi genAi;

  @BeforeAll
  static void setup() {
    // 使用 GsonJsonParser 作为 JSON 解析器
    parser = new GsonJsonParser();
    // 从常量中获取 API 密钥
    String apiKey = CdConstants.GEMINI_API_KEY;

    // 创建 HttpClient， 设置代理
    HttpClient proxyClient = HttpClient.newBuilder()
      .proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 7890)))
      .build();

    // 创建 GenAi 实例
    genAi = new GenAi(apiKey, parser, proxyClient);
  }

  @AfterAll
  static void cleanup() {
    // 关闭 GenAi 资源
    if (genAi != null) {
      genAi.close();
    }
  }

  /**
   * 测试列出可用模型的功能。
   */
  @Test
  @Order(1)
  void testListModels() {
    log.info("----- 测试 listModels 方法开始");
    LocalDateTime startTime = LocalDateTime.now();
    // 调用 listModels 方法列出可用的模型，并打印结果
    genAi.listModels().forEach(model -> log.info(model.toString()));
    LocalDateTime endTime = LocalDateTime.now();
    log.info("----- 测试 listModels 方法结束, 耗时: {}",
      getDuration(startTime, endTime));
  }

  /**
   * 测试获取指定模型信息的功能。
   */
  @Test
  @Order(2)
  void testGetModel() {
    log.info("----- 测试 getModel 方法开始");
    LocalDateTime startTime = LocalDateTime.now();
    // 调用 getModel 方法获取指定模型的信息
    log.info(genAi.getModel(ModelVariant.GEMINI_1_5_PRO).toString());
    LocalDateTime endTime = LocalDateTime.now();
    log.info("----- 测试 getModel 方法结束, 耗时: {}",
      getDuration(startTime, endTime));
  }

  @Test
  @Order(21)
  void testGetModel_02() {
    log.info("----- 测试 getModel 方法开始");
    LocalDateTime startTime = LocalDateTime.now();
    // 调用 getModel 方法获取指定模型的信息
    log.info(genAi.getModel("gemini-2.0-flash").toString());
    LocalDateTime endTime = LocalDateTime.now();
    log.info("----- 测试 getModel 方法结束, 耗时: {}",
      getDuration(startTime, endTime));
  }

  /**
   * 测试统计文本标记数量的功能。
   */
  @Test
  @Order(3)
  void testCountTokens() {
    log.info("----- 测试 countTokens 方法开始");
    LocalDateTime startTime = LocalDateTime.now();
    // 创建 GenerativeModel 对象，用于统计标记数量
    var model = createStoryModel();
    // 调用 countTokens 方法统计标记数量
    Long result = genAi.countTokens(model).join();
    log.info("Tokens: {}", result);
    LocalDateTime endTime = LocalDateTime.now();
    log.info("----- 测试 countTokens 方法结束, 耗时: {}",
      getDuration(startTime, endTime));
  }


  /**
   * 测试生成内容（阻塞式）的功能。
   */
  @Test
  @Order(4)
  void testGenerateContent()
    throws InterruptedException, ExecutionException, TimeoutException {
    log.info("----- 测试 generateContent 方法开始");
    LocalDateTime startTime = LocalDateTime.now();
    // 创建 GenerativeModel 对象，用于生成内容
    var model = createStoryModel();
    // 调用 generateContent 方法生成内容（阻塞式），并打印结果及使用元数据和安全评级
    genAi.generateContent(model).thenAccept(gcr -> {
      log.info(gcr.toString());
      log.info(
        "----- 测试 generateContent 方法 (blocking) usage meta data & safety ratings");
      log.info(genAi.usageMetadata(gcr.id()).toString());
      log.info(genAi.safetyRatings(gcr.id()).stream()
        .map(GenAi.SafetyRating::toTypedSafetyRating).toList().toString());
    }).get(20, TimeUnit.SECONDS); // 设置超时时间为 20 秒
    LocalDateTime endTime = LocalDateTime.now();
    log.info("----- 测试 generateContent 方法结束, 耗时: {}",
      getDuration(startTime, endTime));
  }


  /**
   * 测试生成内容（流式）的功能。
   */
  @Test
  @Order(5)
  void testGenerateContentStream() {
    log.info("----- 测试 generateContentStream 方法开始");
    LocalDateTime startTime = LocalDateTime.now();
    // 创建 GenerativeModel 对象，用于流式生成内容
    var model = createStoryModel();
    printModel(model);
    // 调用 generateContentStream 方法流式生成内容，并打印结果及使用元数据和安全评级
    genAi.generateContentStream(model).forEach(x -> {
      log.info(x.toString());
      log.info(genAi.usageMetadata(x.id()).toString());
      log.info(genAi.safetyRatings(x.id()).toString());
    });
    LocalDateTime endTime = LocalDateTime.now();
    log.info("----- 测试 generateContentStream 方法结束, 耗时: {}",
      getDuration(startTime, endTime));
  }

  /**
   * 测试生成内容，指定响应模式（阻塞式）的功能。
   */
  @Test
  @Order(6)
  void testGenerateWithResponseSchema()
    throws InterruptedException, ExecutionException, TimeoutException {
    log.info("----- 测试 generateWithResponseSchema 方法开始");
    LocalDateTime startTime = LocalDateTime.now();
    // 创建 GenerativeModel 对象，指定响应模式，用于生成内容
    var model = createResponseSchemaModel();
    // 调用 generateContent 方法生成内容（阻塞式），并打印结果及使用元数据和安全评级
    genAi.generateContent(model).thenAccept(gcr -> {
      log.info(gcr.toString());
      log.info(
        "----- 测试 generateWithResponseSchema 方法 (blocking) usage meta data & safety ratings");
      log.info(genAi.usageMetadata(gcr.id()).toString());
      log.info(genAi.safetyRatings(gcr.id()).stream()
        .map(GenAi.SafetyRating::toTypedSafetyRating).toList().toString());
    }).get(20, TimeUnit.SECONDS); // 设置超时时间为 20 秒
    LocalDateTime endTime = LocalDateTime.now();
    log.info("----- 测试 generateWithResponseSchema 方法结束, 耗时: {}",
      getDuration(startTime, endTime));
  }


  /**
   * 测试生成内容，指定响应模式（流式）的功能。
   */
  @Test
  @Order(7)
  void testGenerateContentStreamWithResponseSchema() {
    log.info("----- 测试 generateContentStreamWithResponseSchema 方法开始");
    LocalDateTime startTime = LocalDateTime.now();
    // 创建 GenerativeModel 对象，指定响应模式，用于流式生成内容
    var model = createResponseSchemaModel();
    // 调用 generateContentStream 方法流式生成内容，并打印结果及使用元数据和安全评级
    genAi.generateContentStream(model).forEach(x -> {
      log.info(x.toString());
      log.info(genAi.usageMetadata(x.id()).toString());
      log.info(genAi.safetyRatings(x.id()).toString());
    });
    LocalDateTime endTime = LocalDateTime.now();
    log.info(
      "----- 测试 generateContentStreamWithResponseSchema 方法结束, 耗时: {}",
      getDuration(startTime, endTime));
  }

  /**
   * 测试多轮对话的功能。
   */
  @Test
  @Order(8)
  void testMultiChatTurn() {
    log.info("----- 测试 multiChatTurn 方法开始");
    LocalDateTime startTime = LocalDateTime.now();
    // 创建 GenerativeModel 对象，用于多轮对话
    GenerativeModel chatModel = GenerativeModel.builder()
      .modelName(ModelVariant.GEMINI_1_5_PRO) // 指定模型
      .addContent(new Content.TextContent( // 添加用户内容
        Content.Role.USER.roleName(),
        "Write the first line of a story about a magic backpack."
      ))
      .addContent(new Content.TextContent( // 添加模型内容
        Content.Role.MODEL.roleName(),
        "In the bustling city of Meadow brook, lived a young girl named Sophie. She was a bright and curious soul with an imaginative mind."
      ))
      .addContent(new Content.TextContent( // 添加用户内容
        Content.Role.USER.roleName(),
        "Can you set it in a quiet village in 1600s France? Max 30 words"
      ))
      .build();
    printModel(chatModel);
    // 调用 generateContentStream 方法进行多轮对话，并打印结果
    genAi.generateContentStream(chatModel).forEach(x -> log.info("{}", x));
    LocalDateTime endTime = LocalDateTime.now();
    log.info("----- 测试 multiChatTurn 方法结束, 耗时: {}",
      getDuration(startTime, endTime));
  }


  /**
   * 测试文本和图像内容的功能。
   */
  @Test
  @Order(9)
  void testTextAndImage() throws IOException {
    log.info("----- 测试 textAndImage 方法开始");
    LocalDateTime startTime = LocalDateTime.now();
    // 创建 GenerativeModel 对象，包含文本和图像内容
    var model = GenerativeModel.builder()
      .modelName(ModelVariant.GEMINI_1_5_FLASH) // 指定模型
      .addContent(
        Content.textAndMediaContentBuilder()
          .role(Content.Role.USER) // 指定角色为用户
          .text("这张图片里是什么？")// .text("What is in this image?") // 添加文本内容
          .addMedia(new Content.MediaData( // 添加媒体内容
            "image/png",
            loadSconesImage()
          ))
          .build()
      ).build();

    printModel(model);
    // 调用 generateContent 方法生成内容（阻塞式），并打印结果
    genAi.generateContent(model).thenAccept(gcr -> log.info(gcr.toString()))
      .join();
    LocalDateTime endTime = LocalDateTime.now();
    log.info("----- 测试 textAndImage 方法结束, 耗时: {}",
      getDuration(startTime, endTime));
  }


  /**
   * 测试内容嵌入的功能。
   */
  @Test
  @Order(10)
  void testEmbedContents() {
    log.info("----- 测试 embedContents 方法开始");
    LocalDateTime startTime = LocalDateTime.now();
    // 创建 GenerativeModel 对象，用于文本嵌入
    var model = GenerativeModel.builder()
      .modelName(ModelVariant.TEXT_EMBEDDING_004) // 指定模型
      .addContent(Content.textContent( // 添加用户内容
        Content.Role.USER,
        "Write a 50 word story about a magic backpack."
      ))
      .addContent(Content.textContent( // 添加模型内容
        Content.Role.MODEL,
        "bla bla bla bla"
      ))
      .addSafetySetting(SafetySetting.of( // 添加安全设置
        SafetySetting.HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT,
        SafetySetting.HarmBlockThreshold.BLOCK_ONLY_HIGH
      ))
      .generationConfig(new GenerationConfig( // 配置生成参数
        null,
        null,
        null,
        null,
        null,
        null,
        null
      ))
      .build();
    printModel(model);
    // 调用 embedContents 方法进行内容嵌入，并获取结果
    List<GenAi.ContentEmbedding> embeddings = genAi.embedContents(model, null,
      null, null).join();
    log.info("Embedding count: {}", embeddings.size());
    log.info("Values per embedding: {}",
      embeddings.stream().map(GenAi.ContentEmbedding::values).map(List::size)
        .toList());
    LocalDateTime endTime = LocalDateTime.now();
    log.info("----- 测试 embedContents 方法结束, 耗时: {}",
      getDuration(startTime, endTime));
  }

  /**
   * 将GenerativeModel 转换为 JSON并打印。
   *
   * @param chatModel GenerativeModel 对象
   */
  private void printModel(GenerativeModel chatModel) {
    log.info("chatModel json {}", parser.toJson(GenApiUtil.convert(
      chatModel))); // 打印格式化后的 JSON
  }

  /**
   * 创建用于生成故事的 GenerativeModel 对象。
   *
   * @return GenerativeModel 对象
   */
  private GenerativeModel createStoryModel() {
    // 创建 GenerativeModel 对象，用于生成故事
    return GenerativeModel.builder()
      .modelName(ModelVariant.GEMINI_2_0_FLASH_EXP) // 指定模型
      .addContent(Content.textContent( // 添加用户内容
        Content.Role.USER,
        "Write a 50 word story about a magic backpack."
      ))
      .addSafetySetting(SafetySetting.of( // 添加安全设置
        SafetySetting.HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT,
        SafetySetting.HarmBlockThreshold.BLOCK_ONLY_HIGH
      ))
      .generationConfig(new GenerationConfig( // 配置生成参数
        null,
        null,
        null,
        null,
        null,
        null,
        null
      ))
      .build();
  }

  /**
   * 创建指定响应模式的 GenerativeModel 对象。
   *
   * @return GenerativeModel 对象
   */
  private GenerativeModel createResponseSchemaModel() {
    // 创建 GenerativeModel 对象，指定响应模式
    return GenerativeModel.builder()
      .modelName(ModelVariant.GEMINI_1_5_FLASH) // 指定模型
      .addContent(Content.textContent( // 添加用户内容
        Content.Role.USER,
        "List 3 popular cookie recipes."
      ))
      .addSafetySetting(SafetySetting.of( // 添加安全设置
        SafetySetting.HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT,
        SafetySetting.HarmBlockThreshold.BLOCK_ONLY_HIGH
      ))
      .generationConfig(new GenerationConfig( // 配置生成参数，指定响应模式为 JSON
        null,
        "application/json",
        Schema.builder()
          .type(Schema.Type.ARRAY)
          .items(Schema.builder()
            .type(Schema.Type.OBJECT)
            .properties(Map.of(
              "recipe_name", Schema.builder()
                .type(Schema.Type.STRING)
                .build()
            ))
            .build())
          .build(),
        null,
        null,
        null,
        null
      ))
      .build();
  }

  /**
   * 加载 scones.png 图片，并将其转换为 Base64 字符串。
   *
   * @return Base64 字符串
   * @throws IOException 如果加载图片失败
   */
  private String loadSconesImage() throws IOException {
    // 加载 scones.png 图片，并将其转换为 Base64 字符串
    try (InputStream is = GeminiTest.class.getClassLoader()
      .getResourceAsStream("scones.png")) {
      if (is == null) {
        throw new IllegalStateException("Image not found! ");
      }
      return Base64.getEncoder().encodeToString(is.readAllBytes());
    }
  }

  /**
   * 计算方法耗时，并返回时分秒格式。
   *
   * @param startTime 方法开始时间
   * @param endTime   方法结束时间
   * @return 时分秒格式的耗时
   */
  private String getDuration(LocalDateTime startTime, LocalDateTime endTime) {
    Duration duration = Duration.between(startTime, endTime);
    long hours = duration.toHours();
    long minutes = duration.toMinutesPart();
    long seconds = duration.toSecondsPart();
    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
  }
}
