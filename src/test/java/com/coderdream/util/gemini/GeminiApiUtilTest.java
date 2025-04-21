package com.coderdream.util.gemini;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.sentence.JsonToBook01BeanListUtil;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
  @Order(405)
  void testGenerateContent_405() throws Exception {
    log.info("----- 405.测试 generateContent 方法开始");
    String prompt = "帮我把下面的段落分割成句子：";
    prompt += "\"I'm going to the store,\" she said.He exclaimed, \"That's amazing!\"Have you read \"The Lord of the Rings\"?My favorite song is \"Bohemian Rhapsody.\"She called him a \"genius,\" but I think he's just lucky.The \"expert\" didn't know what he was talking about.He said it was \"totally awesome.\"Let's go \"hang out\" later.The string variable was set to \"Hello World!\".The command is \"ls -l\".";
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);
    log.info("405. Generated content: {}", generatedContent);
    log.info("----- 405.测试 generateContent 方法结束");
  }

  @Test
  @Order(406)
  void testGenerateContent_406() throws Exception {
    log.info("----- 406.测试 generateContent 方法开始");
    String prompt = "帮我把下面的段落分割成句子，用json返回给我：";
    prompt += "Let me see. \"Wanted: manager for upand-coming firm. Must have good organizational skills. Experience a plus. Please contact Susan Lee.\" Oh, I don't know... 让我瞧瞧。 \"招聘：极富发展潜力的公司招聘经理。需良好的组织才能。需有经验。有意者请与苏珊·李联系\"。哦，我不知道......";
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);
    log.info("406. Generated content: {}", generatedContent);
    log.info("----- 406.测试 generateContent 方法结束");
//    String text = generatedContent.text();
//    // 先移除```json
//    text = text.replace("```json", "");
//    // 再移除 ```
//    text = text.replace("```", "");
//    text = text.replace("\n", "");
//    // 再转换成JSON对象
//    JSONObject jsonObject = JSONUtil.parseObj(text);
//    JSONArray jsonArray = jsonObject.getJSONArray("sentences");
//    for (int i = 0; i < jsonArray.size(); i++) {
//      String sentence = (String)jsonArray.get(i);
////      String sentence = (String) sentenceObject.get("text");
//      log.info("句子：{}", sentence);
//    }
  }

  @Test
  @Order(407)
  void testGenerateContent_407() throws Exception {
    log.info("----- 407.测试 generateContent 方法开始");
    String prompt = "不要管以前的对话记录。帮我把下面的段落分割成句子，用json返回给我，json的格式如下：[{\"speaker\":\"A\",\"sentences\":[\"I'm looking for a job which offers lodging.\"]},{\"speaker\":\"B\",\"sentences\":[\"It's hard.\"]}]；段落内容如下：";
    File txtFile = new File(
      "D:\\0000\\EnBook001\\900\\900V1_ch01_v3.txt");
    String text1 = FileUtils.readFileToString(txtFile, "UTF-8");
    prompt += text1;
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);
    log.info("407. Generated content: {}", generatedContent);
    log.info("----- 407.测试 generateContent 方法结束");
    String text = generatedContent.text();
    // 先移除```json
    text = text.replace("```json", "");
    // 再移除 ```
    text = text.replace("```", "");
    text = text.replace("\n", "");
//    // 写入文本文件
//    File bookFile = new File(txtFile.getParent() + "\\" + txtFile.getName()
//      + "_逐字稿V_407.txt");
//    FileUtils.writeStringToFile(bookFile, text, "UTF-8");

    // TODO
    JsonToBook01BeanListUtil.processBackup("", text,
      new Integer[]{0, 12, 16, 18, 12, 20, 8}, "S101");

//    JSONObject jsonObject = JSONUtil.parseObj(text);
//    JSONArray jsonArray = jsonObject.getJSONArray("sentences");
//    for (int i = 0; i < jsonArray.size(); i++) {
//      String sentence = (String)jsonArray.get(i);
////      String sentence = (String) sentenceObject.get("text");
//      log.info("句子：{}", sentence);
//    }
  }

  @Test
  @Order(409)
  void testGenerateContent_409() throws Exception {
    log.info("----- 409.测试 generateContent 方法开始");
    String prompt = "不要管以前的对话记录。帮我把下面的段落分割成句子，用json返回给我，中英文句子要分开，"
      + "你返回给我的json里的对象数应该是段落有效行数的两倍，json的格式如下："
      + "[{\"speaker\":\"A\",\"sentences\":[\"I'm looking for a job which offers lodging.\"]},"
      + "{\"speaker\":\"B\",\"sentences\":[\"It's hard.\"]},"
      + "{\"speaker\":\"A\",\"sentences\":[\"Is this vacancy still available?\"]},"
      + "{\"speaker\":\"B\",\"sentences\":[\"Yes, we still need satisfying people.\"]},"
      + "{\"speaker\":\"A\",\"sentences\":[\"What are the requirements to apply for the position?\"]},"
      + "{\"speaker\":\"B\",\"sentences\":[\"You must have over two years' experience first.\"]},"
      + "\"speaker\":\"A\",\"sentences\":[\"我正在找一份能提供住宿的工作。\"]},"
      + "{\"speaker\":\"B\",\"sentences\":[\"这挺难的。\"]},"
      + "{\"speaker\":\"A\",\"sentences\":[\"这个工作还招人吗？\"]},"
      + "{\"speaker\":\"B\",\"sentences\":[\"是的，我们一直需要合适的人选。\"]},"
      + "{\"speaker\":\"A\",\"sentences\":[\"应聘这个职务的条件是什么？\"]},"
      + "{\"speaker\":\"B\",\"sentences\":[\"首先你必须有两年以上的经验。\"]}]；段落内容如下：";

    File txtFile = new File(
      "D:\\0000\\EnBook001\\900\\900V1_ch02.txt");
    String text1 = FileUtils.readFileToString(txtFile, "UTF-8");
    prompt += text1;
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);
    log.info("409. Generated content: {}", generatedContent);
    log.info("----- 409.测试 generateContent 方法结束");
    String text = generatedContent.text();
    // 先移除```json
    text = text.replace("```json", "");
    // 再移除 ```
    text = text.replace("```", "");
    text = text.replace("\n", "");
    // 写入文本文件
    File bookFile = new File(txtFile.getParent() + "\\" + txtFile.getName()
      + "_逐字稿V_4093.txt");
    FileUtils.writeStringToFile(bookFile, text, "UTF-8");

//    Integer[] rangeIndexes = new Integer[]{0, 12, 16, 18, 12, 20, 8};

//    Integer[] rangeIndexes = ParagraphCounter.countLinesPerParagraph(text1);
//    String chapterInfoTag = "S102";
//    JsonToBook01BeanListUtil.processBackup(txtFile.getAbsolutePath(), text,
//      rangeIndexes, chapterInfoTag);

//    JSONObject jsonObject = JSONUtil.parseObj(text);
//    JSONArray jsonArray = jsonObject.getJSONArray("sentences");
//    for (int i = 0; i < jsonArray.size(); i++) {
//      String sentence = (String)jsonArray.get(i);
////      String sentence = (String) sentenceObject.get("text");
//      log.info("句子：{}", sentence);
//    }
  }


  @Test
  @Order(408)
  void testGenerateContent_408() throws Exception {
    log.info("----- 408.测试 generateContent 方法开始");
    String prompt = "帮我把下面的段落分割成句子，用字符串列表返回给我，不要添加任何序号：";
    File txtFile = new File(
      "D:\\0000\\EnBook001\\900\\900V1_ch01_v3.txt");
    String text1 = FileUtils.readFileToString(txtFile, "UTF-8");
    prompt += text1;
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);
    log.info("408. Generated content: {}", generatedContent);
    log.info("----- 408.测试 generateContent 方法结束");
    String text = generatedContent.text();
    // 先移除```json
    text = text.replace("```", "");
    // 再移除 ```
//    text = text.replace("```", "");
    text = text.replace("\n", "");
    List<String> sentences = JSONUtil.toList(text, String.class);
    // 写入文本文件
    File bookFile = new File(txtFile.getParent() + "\\" + txtFile.getName()
      + "_逐字稿V2.txt");
//    FileUtils.writeStringToFile(bookFile, text, "UTF-8");
    CdFileUtil.writeToFile(bookFile.getAbsolutePath(), sentences);

//    JsonToBook01BeanListUtil.process(text);

//    JSONObject jsonObject = JSONUtil.parseObj(text);
//    JSONArray jsonArray = jsonObject.getJSONArray("sentences");
//    for (int i = 0; i < jsonArray.size(); i++) {
//      String sentence = (String)jsonArray.get(i);
////      String sentence = (String) sentenceObject.get("text");
//      log.info("句子：{}", sentence);
//    }
  }


  @Test
  @Order(403)
  void testGenerateContent_403() throws Exception {
    log.info("----- 3.测试 generateContent 方法开始");
    String prompt = "我是一个故事讲述者，请帮我根据书籍文本准备一份10000个中文字符的逐字稿，分三部分，第一部分：作者简介（100字左右）；第二部分：全书核心内容（8000字左右）；第三部分：总结（100字左右）；书籍文本如下：";
    prompt =
      "我是一个故事讲述者，请帮我根据附件文本准备一份5000个中文字符的逐字稿，分三部分，第一部分：作者简介（200字左右）；第二部分：全书核心内容（4500字左右）；第三部分：总结（200字左右），记得是逐字稿，故事要有趣味性，我要用来作为播客的脚本，"
        + "给我的文本不要任何格式，字数4800~5000字，不要少也不要多，返回前先告诉我生成的逐字稿有多少个字，不达标要重新生成；";
    String bookFileName = "D:\\0000\\01_BookStore\\0001_LuoXiang\\刑法学讲义.txt";
    prompt += "书籍文本如下：";
    prompt += FileUtils.readFileToString(new File(bookFileName), "UTF-8");
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);
    log.info("3. Generated content: {}", generatedContent);
    log.info("----- 3.测试 generateContent 方法结束");
  }


  @Test
  @Order(404)
  void testGenerateContent_404() throws Exception {
    log.info("----- 4.测试 generateContent 方法开始");
    String prompt = "我是一个故事讲述者，请帮我根据书籍文本准备一份10000个中文字符的逐字稿，分三部分，第一部分：作者简介（100字左右）；第二部分：全书核心内容（8000字左右）；第三部分：总结（100字左右）；书籍文本如下：";
    prompt =
      "我是一个故事讲述者，请帮我根据附件文本准备一份5000个中文字符的逐字稿，分三部分，第一部分：作者简介（400字左右）；第二部分：全书核心内容（9000字左右）；第三部分：总结（400字左右），记得是逐字稿，故事要有趣味性，我要用来作为播客的脚本，"
        + "给我的文本不要任何格式，字数9800~10000字，不要少也不要多，返回前先告诉我生成的逐字稿有多少个字，不达标要重新生成；";
    String bookFileName = "D:\\0000\\01_BookStore\\0001_LuoXiang\\刑法学讲义.txt";
    prompt += "书籍文本如下：";
    prompt += FileUtils.readFileToString(new File(bookFileName), "UTF-8");
    for (int i = 0; i < 10; i++) {
      Thread.sleep(1000);
      // 生成文本内容（阻塞式）
      GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);
      File bookFile = new File(bookFileName);
      int index = i + 1;
      String scriptFileName =
        bookFile.getParent() + File.separator + bookFile.getName() + "_逐字稿V"
          + index + ".txt";
      FileUtils.writeStringToFile(new File(scriptFileName),
        generatedContent.text(), "UTF-8");
      log.info("4. Generated content: {}", generatedContent);
    }

    log.info("----- 4.测试 generateContent 方法结束");
  }

  @Test
  @Order(4010)
  void testGenerateContent_4010() throws Exception {
    log.info("----- 4.测试 generateContent 方法开始");
    String prompt = FileUtil.readString(
      CdFileUtil.getResourceRealPath() + "\\youtube\\description_prompt.txt",
      StandardCharsets.UTF_8);
    ;
    prompt += "字幕如下：";
    prompt += FileUtil.readString(
      "D:\\0000\\EnBook001\\900\\ch003\\ch003_total.srt",
      StandardCharsets.UTF_8);
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);

    String scriptFileName = "";
    FileUtils.writeStringToFile(
      new File("D:\\0000\\EnBook001\\900\\ch003\\ch003_description.txt"),
      generatedContent.text(), "UTF-8");
    log.info("4. Generated content: {}", generatedContent);

    log.info("----- 4.测试 generateContent 方法结束");
  }

  @Test
  @Order(4011)
  void testGenerateContent_4011() throws Exception {
    log.info("----- 4.测试 generateContent 方法开始");
    String prompt = FileUtil.readString(
      CdFileUtil.getResourceRealPath() + "\\youtube\\gemini_prompt.txt",
      StandardCharsets.UTF_8);
    prompt += FileUtil.readString(
      "D:\\0000\\0003_PressBriefings\\20250416\\20250416_script_pure.txt",
      StandardCharsets.UTF_8);
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);

    FileUtils.writeStringToFile(
      new File("D:\\0000\\0003_PressBriefings\\20250416\\20250416_script_pure_gemini.txt"),
      generatedContent.text(), "UTF-8");
    log.info("4. Generated content: {}", generatedContent);

    log.info("----- 4.测试 generateContent 方法结束");
  }

  //

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
