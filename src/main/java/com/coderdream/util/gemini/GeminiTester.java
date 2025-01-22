package com.coderdream.util.gemini;

import com.coderdream.util.cd.CdConstants;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import swiss.ameri.gemini.api.*;
import swiss.ameri.gemini.gson.GsonJsonParser;
import swiss.ameri.gemini.spi.JsonParser;

/**
 * 测试 {@link GenAi} 功能的示例程序。
 */
@Slf4j
public class GeminiTester {

    private GeminiTester() {
        throw new AssertionError("禁止实例化");
    }


    /**
     * 程序入口点。以 Gemini API 密钥作为参数。请访问 <a href="https://aistudio.google.com/app/apikey">aistuio.google.com</a> 生成新的 API 密钥。
     *
     * @param args 接收 API 密钥作为参数
     * @throws Exception 如果出现错误
     */
    public static void main(String[] args) throws Exception {
        // 使用 GsonJsonParser 作为 JSON 解析器
        JsonParser parser = new GsonJsonParser();
        // 从命令行参数中获取 API 密钥
        String apiKey = CdConstants.GEMINI_API_KEY; //args[0];

        // 创建 HttpClient， 设置代理
        HttpClient proxyClient = HttpClient.newBuilder()
          .proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 7890)))
          .build();

        // 使用 try-with-resources 关闭 GenAi 资源
        try (var genAi = new GenAi(apiKey, parser, proxyClient)) {
            // 调用各个方法测试 GenAi 的功能
            listModels(genAi);          // 列出可用的模型
            getModel(genAi);            // 获取指定模型的信息
            countTokens(genAi);         // 统计文本标记数量
            generateContent(genAi);      // 生成内容（阻塞式）
            generateContentStream(genAi); // 生成内容（流式）
            generateWithResponseSchema(genAi); // 生成内容，指定响应模式（阻塞式）
            generateContentStreamWithResponseSchema(genAi); // 生成内容，指定响应模式（流式）
            multiChatTurn(genAi);       // 多轮对话
            textAndImage(genAi);        // 文本和图像
            embedContents(genAi);       // 内容嵌入
        }
    }

    private static void embedContents(GenAi genAi) {
        System.out.println("----- embed contents");
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
        List<GenAi.ContentEmbedding> embeddings = genAi.embedContents(model, null, null, null).join();
        System.out.println("Embedding count: " + embeddings.size());
        System.out.println("Values per embedding: " + embeddings.stream().map(GenAi.ContentEmbedding::values).map(List::size).toList());
    }

    private static void countTokens(GenAi genAi) {
        System.out.println("----- count tokens");
        // 创建 GenerativeModel 对象，用于统计标记数量
        var model = createStoryModel();
        // 调用 countTokens 方法统计标记数量
        Long result = genAi.countTokens(model).join();
        System.out.println("Tokens: " + result);
    }

    private static void multiChatTurn(GenAi genAi) {
        System.out.println("----- multi turn chat");
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
        genAi.generateContentStream(chatModel).forEach(System.out::println);
    }

    private static void printModel(GenerativeModel chatModel) {
        // 使用 GsonJsonParser 作为 JSON 解析器
        JsonParser parser = new GsonJsonParser();
        log.info("chatModel json {}",  parser.toJson(GenApiUtil.convert(
          chatModel))); // 打印格式化后的 JSON
    }

    private static void generateContentStream(GenAi genAi) {
        System.out.println("----- Generate content (streaming) -- with usage meta data");
        // 创建 GenerativeModel 对象，用于流式生成内容
        var model = createStoryModel();
        printModel(model);
        // 调用 generateContentStream 方法流式生成内容，并打印结果及使用元数据和安全评级
        genAi.generateContentStream(model).forEach(x -> {
            System.out.println(x);
            System.out.println(genAi.usageMetadata(x.id()));
            System.out.println(genAi.safetyRatings(x.id()));
        });
    }

    private static void generateContent(GenAi genAi) throws InterruptedException, ExecutionException, TimeoutException {
        // 创建 GenerativeModel 对象，用于生成内容
        var model = createStoryModel();
        System.out.println("----- Generate content (blocking)");
        // 调用 generateContent 方法生成内容（阻塞式），并打印结果及使用元数据和安全评级
        genAi.generateContent(model).thenAccept(gcr -> {
            System.out.println(gcr);
            System.out.println("----- Generate content (blocking) usage meta data & safety ratings");
            System.out.println(genAi.usageMetadata(gcr.id()));
            System.out.println(genAi.safetyRatings(gcr.id()).stream().map(GenAi.SafetyRating::toTypedSafetyRating).toList());
        }).get(20, TimeUnit.SECONDS); // 设置超时时间为 20 秒
    }

    private static void generateContentStreamWithResponseSchema(GenAi genAi) {
        System.out.println("----- Generate content (streaming) with response schema -- with usage meta data");
        // 创建 GenerativeModel 对象，指定响应模式，用于流式生成内容
        var model = createResponseSchemaModel();
        // 调用 generateContentStream 方法流式生成内容，并打印结果及使用元数据和安全评级
        genAi.generateContentStream(model).forEach(x -> {
            System.out.println(x);
            System.out.println(genAi.usageMetadata(x.id()));
            System.out.println(genAi.safetyRatings(x.id()));
        });
    }

    private static void generateWithResponseSchema(GenAi genAi) throws InterruptedException, ExecutionException, TimeoutException {
        // 创建 GenerativeModel 对象，指定响应模式，用于生成内容
        var model = createResponseSchemaModel();
        System.out.println("----- Generate with response schema (blocking)");
        // 调用 generateContent 方法生成内容（阻塞式），并打印结果及使用元数据和安全评级
        genAi.generateContent(model).thenAccept(gcr -> {
            System.out.println(gcr);
            System.out.println("----- Generate with response schema (blocking) usage meta data & safety ratings");
            System.out.println(genAi.usageMetadata(gcr.id()));
            System.out.println(genAi.safetyRatings(gcr.id()).stream().map(GenAi.SafetyRating::toTypedSafetyRating).toList());
        }).get(20, TimeUnit.SECONDS); // 设置超时时间为 20 秒
    }

    private static GenerativeModel createResponseSchemaModel() {
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

    private static GenerativeModel createStoryModel() {
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

    private static void getModel(GenAi genAi) {
        System.out.println("----- Get Model");
        // 调用 getModel 方法获取指定模型的信息
        System.out.println(genAi.getModel(ModelVariant.GEMINI_1_5_PRO));
    }

    private static void listModels(GenAi genAi) {
        System.out.println("----- List models");
        // 调用 listModels 方法列出可用的模型，并打印结果
        genAi.listModels().forEach(System.out::println);
    }

    private static void textAndImage(GenAi genAi) throws IOException {
        System.out.println("----- text and image");
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
        genAi.generateContent(model).thenAccept(System.out::println).join();
    }

    private static String loadSconesImage() throws IOException {
        // 加载 scones.png 图片，并将其转换为 Base64 字符串
        try (InputStream is = GeminiTester.class.getClassLoader().getResourceAsStream("scones.png")) {
            if (is == null) {
                throw new IllegalStateException("Image not found! ");
            }
            return Base64.getEncoder().encodeToString(is.readAllBytes());
        }
    }
}
