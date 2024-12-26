package com.coderdream.util.chatgpt;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.coderdream.entity.Model;
import com.coderdream.entity.SentencePair;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenRouterUtil {

  public static void main(String[] args) {

//    String prompt = "你是谁";
//    prompt = "翻译：to venture out somewhere";
//    prompt = "用下面的词组造英文句子并翻译成中文，只需要返回一个用例：to venture out somewhere";
////    SentencePair sentencePair = OpenRouterUtil.genSentence(prompt);
////    log.info("sentencePair: {}", sentencePair);
//
//    List<Model> models = ModelParser.parseModelsFromFile();
//    for (Model model : models) {
////      log.info("model: {}", model);
//      SentencePair sentencePair = OpenRouterUtil.genSentence(
//        model.getModelId(), prompt);
//      log.info("{} sentencePair: {}", model.getModelName(), sentencePair);
//    }
  }

  /**
   * 根据模型ID和输入生成句子对
   *
   * @param model 模型ID
   * @param input 输入
   * @return 句子对
   */
  public static String genChinese(String model, String input) {

//    SentencePair sentencePair = new SentencePair();
//    switch (model.toLowerCase()) {
//      case ModelConstants.GOOGLE_GEMINI_EXP_1114_FREE:
//        System.out.println("匹配到Google Gemini Experiment 1114 Free");
//        break;
//      case ModelConstants.GOOGLE_GEMINI_EXP_1121_FREE:
//        System.out.println("匹配到Google Gemini Experiment 1121 Free");
//        break;
//      case ModelConstants.GOOGLE_GEMINI_EXP_1206_FREE:
//        System.out.println("匹配到Google Gemini Experiment 1206 Free");
//        break;
//      case ModelConstants.GOOGLE_GEMINI_FLASH_1_5_8B_EXP:
//        sentencePair = parseSentenceParentheses(input);
////        System.out.println("匹配到Google Gemini Flash 1.5 8b Experiment");
//        break;
//      case ModelConstants.GOOGLE_GEMINI_FLASH_1_5_EXP:
//        sentencePair = parseSentenceGoogleGeminiFlash15Experiment(input);
//        System.out.println("匹配到Google Gemini Flash 1.5 Experiment");
//        break;
//      case ModelConstants.GOOGLE_GEMINI_2_0_FLASH_EXP_FREE:
//        System.out.println("匹配到Google Gemini 2.0 Flash Experiment Free");
//        break;
//      case ModelConstants.GOOGLE_GEMINI_PRO_1_5_EXP:
//        System.out.println("匹配到Google Gemini Pro 1.5 Experiment");
//        break;
//      case ModelConstants.GOOGLE_GEMMA_2_9B_IT_FREE:
//        System.out.println("匹配到Google Gemma 2 9b IT Free");
//        break;
//      case ModelConstants.GOOGLE_LEARNLM_1_5_PRO_EXPERIMENTAL_FREE:
//        System.out.println("匹配到Google LearnLM 1.5 Pro Experimental Free");
//        break;
//      case ModelConstants.HUGGINGFACEH4_ZEPHYR_7B_BETA_FREE:
//        System.out.println("匹配到HuggingFace Zephyr 7b Beta Free");
//        break;
//      case ModelConstants.META_LLAMA_3_8B_INSTRUCT_FREE:
//        System.out.println("匹配到Meta LLaMA 3 8b Instruct Free");
//        break;
//      case ModelConstants.META_LLAMA_3_1_405B_INSTRUCT_FREE:
//        System.out.println("匹配到Meta LLaMA 3.1 405b Instruct Free");
//        break;
//      case ModelConstants.META_LLAMA_3_1_70B_INSTRUCT_FREE:
//        System.out.println("匹配到Meta LLaMA 3.1 70b Instruct Free");
//        break;
//      case ModelConstants.META_LLAMA_3_1_8B_INSTRUCT_FREE:
//        System.out.println("匹配到Meta LLaMA 3.1 8b Instruct Free");
//        break;
//      case ModelConstants.META_LLAMA_3_2_11B_VISION_INSTRUCT_FREE:
//        System.out.println("匹配到Meta LLaMA 3.2 11b Vision Instruct Free");
//        break;
//      case ModelConstants.META_LLAMA_3_2_1B_INSTRUCT_FREE:
//        System.out.println("匹配到Meta LLaMA 3.2 1b Instruct Free");
//        break;
//      case ModelConstants.META_LLAMA_3_2_3B_INSTRUCT_FREE:
//        System.out.println("匹配到Meta LLaMA 3.2 3b Instruct Free");
//        break;
//      case ModelConstants.META_LLAMA_3_2_90B_VISION_INSTRUCT_FREE:
//        System.out.println("匹配到Meta LLaMA 3.2 90b Vision Instruct Free");
//        break;
//      case ModelConstants.MICROSOFT_PHI_3_MEDIUM_128K_INSTRUCT_FREE:
//        System.out.println("匹配到Microsoft Phi-3 Medium 128k Instruct Free");
//        break;
//      case ModelConstants.MICROSOFT_PHI_3_MINI_128K_INSTRUCT_FREE:
//        System.out.println("匹配到Microsoft Phi-3 Mini 128k Instruct Free");
//        break;
//      case ModelConstants.MISTRALAI_MISTRAL_7B_INSTRUCT_FREE:
//        System.out.println("匹配到Mistral AI 7b Instruct Free");
//        break;
//      case ModelConstants.GRYPHE_MYTHOMAX_L2_13B_FREE:
//        System.out.println("匹配到Gryphe Mythomax L2 13b Free");
//        break;
//      case ModelConstants.OPENCHAT_OPENCHAT_7B_FREE:
//        System.out.println("匹配到OpenChat 7b Free");
//        break;
//      case ModelConstants.QWEN_QWEN_2_7B_INSTRUCT_FREE:
//        System.out.println("匹配到Qwen 2 7b Instruct Free");
//        break;
//      case ModelConstants.UNDI95_TOPPY_M_7B_FREE:
//        System.out.println("匹配到Undi95 Toppy M 7b Free");
//        break;
//      default:
//        System.out.println("未匹配到任何已知的模型常量");
//    }

    return input;

//    return sentencePair;
  }

  /**
   * 根据模型ID和输入生成句子对
   *
   * @param model 模型ID
   * @param input 输入
   * @return 句子对
   */
  public static SentencePair genSentencePair(String model, String input) {
    SentencePair sentencePair = new SentencePair();
    switch (model.toLowerCase()) {
      case ModelConstants.GOOGLE_GEMINI_EXP_1114_FREE:
        System.out.println("匹配到Google Gemini Experiment 1114 Free");
        break;
      case ModelConstants.GOOGLE_GEMINI_EXP_1121_FREE:
        System.out.println("匹配到Google Gemini Experiment 1121 Free");
        break;
      case ModelConstants.GOOGLE_GEMINI_EXP_1206_FREE:
        System.out.println("匹配到Google Gemini Experiment 1206 Free");
        break;
      case ModelConstants.GOOGLE_GEMINI_FLASH_1_5_8B_EXP:
        sentencePair = parseSentenceParentheses(input);
//        System.out.println("匹配到Google Gemini Flash 1.5 8b Experiment");
        break;
      case ModelConstants.GOOGLE_GEMINI_FLASH_1_5_EXP:
        sentencePair = parseSentenceGoogleGeminiFlash15Experiment(input);
        System.out.println("匹配到Google Gemini Flash 1.5 Experiment");
        break;
      case ModelConstants.GOOGLE_GEMINI_2_0_FLASH_EXP_FREE:
        parseSentenceGoogleGemini20FlashExpFree(input);
//        System.out.println("匹配到Google Gemini 2.0 Flash Experiment Free");
        break;
      case ModelConstants.GOOGLE_GEMINI_PRO_1_5_EXP:
        System.out.println("匹配到Google Gemini Pro 1.5 Experiment");
        break;
      case ModelConstants.GOOGLE_GEMMA_2_9B_IT_FREE:
        System.out.println("匹配到Google Gemma 2 9b IT Free");
        break;
      case ModelConstants.GOOGLE_LEARNLM_1_5_PRO_EXPERIMENTAL_FREE:
        System.out.println("匹配到Google LearnLM 1.5 Pro Experimental Free");
        break;
      case ModelConstants.HUGGINGFACEH4_ZEPHYR_7B_BETA_FREE:
        System.out.println("匹配到HuggingFace Zephyr 7b Beta Free");
        break;
      case ModelConstants.META_LLAMA_3_8B_INSTRUCT_FREE:
        System.out.println("匹配到Meta LLaMA 3 8b Instruct Free");
        break;
      case ModelConstants.META_LLAMA_3_1_405B_INSTRUCT_FREE:
        System.out.println("匹配到Meta LLaMA 3.1 405b Instruct Free");
        break;
      case ModelConstants.META_LLAMA_3_1_70B_INSTRUCT_FREE:
        System.out.println("匹配到Meta LLaMA 3.1 70b Instruct Free");
        break;
      case ModelConstants.META_LLAMA_3_1_8B_INSTRUCT_FREE:
        System.out.println("匹配到Meta LLaMA 3.1 8b Instruct Free");
        break;
      case ModelConstants.META_LLAMA_3_2_11B_VISION_INSTRUCT_FREE:
        System.out.println("匹配到Meta LLaMA 3.2 11b Vision Instruct Free");
        break;
      case ModelConstants.META_LLAMA_3_2_1B_INSTRUCT_FREE:
        System.out.println("匹配到Meta LLaMA 3.2 1b Instruct Free");
        break;
      case ModelConstants.META_LLAMA_3_2_3B_INSTRUCT_FREE:
        System.out.println("匹配到Meta LLaMA 3.2 3b Instruct Free");
        break;
      case ModelConstants.META_LLAMA_3_2_90B_VISION_INSTRUCT_FREE:
        System.out.println("匹配到Meta LLaMA 3.2 90b Vision Instruct Free");
        break;
      case ModelConstants.MICROSOFT_PHI_3_MEDIUM_128K_INSTRUCT_FREE:
        System.out.println("匹配到Microsoft Phi-3 Medium 128k Instruct Free");
        break;
      case ModelConstants.MICROSOFT_PHI_3_MINI_128K_INSTRUCT_FREE:
        System.out.println("匹配到Microsoft Phi-3 Mini 128k Instruct Free");
        break;
      case ModelConstants.MISTRALAI_MISTRAL_7B_INSTRUCT_FREE:
        System.out.println("匹配到Mistral AI 7b Instruct Free");
        break;
      case ModelConstants.GRYPHE_MYTHOMAX_L2_13B_FREE:
        System.out.println("匹配到Gryphe Mythomax L2 13b Free");
        break;
      case ModelConstants.OPENCHAT_OPENCHAT_7B_FREE:
        System.out.println("匹配到OpenChat 7b Free");
        break;
      case ModelConstants.QWEN_QWEN_2_7B_INSTRUCT_FREE:
        System.out.println("匹配到Qwen 2 7b Instruct Free");
        break;
      case ModelConstants.UNDI95_TOPPY_M_7B_FREE:
        System.out.println("匹配到Undi95 Toppy M 7b Free");
        break;
      default:
        System.out.println("未匹配到任何已知的模型常量");
    }

    return sentencePair;
  }

  // 解析字符串并返回 SentencePair 对象
  public static SentencePair genSentence(String prompt) {
    return genSentence("", prompt);
  }

  // 解析字符串并返回 SentencePair 对象
  public static SentencePair genSentence(String model, String prompt) {
    String response = callApiThreeTimes(model, prompt);
    log.info("Response: {}", response);
    if (StrUtil.isNotBlank(response)) {
      return genSentencePair(model, response);
    }

    return null;
  }

  /**
   * 翻译，使用默认模型进行翻译（Google: Gemini Experimental 1206
   * (free)	google/gemini-exp-1206:free	2097152）
   */
  public static String translate(String prompt) {
    return translate("", prompt);
  }

  /**
   * 翻译，使用指定模型进行翻译
   */
  public static String translate(String model, String prompt) {
    // 尝试调用API进行翻译
    String response = callApiThreeTimes(model, "简明翻译：" + prompt);
    // 如果响应不为空，处理翻译结果
    return genChinese(model, response);
  }


  /**
   * 尝试三次调用API，如果失败则返回null
   */
  public static String callApiThreeTimes(String model, String prompt) {
    // 尝试调用API
    String response = callApi(model, prompt);

    // 如果返回为空或包含特定错误信息，则进行重试
    if (response == null || response.contains(
      "No choices found in the response.")) {
      response = callApi(model, prompt);  // 再次调用API
      if (response == null || response.contains(
        "No choices found in the response.")) {
        response = callApi(model, prompt);  // 第三次调用
      }
    }

    // 如果仍然返回空值或者翻译失败，返回null
    if (StrUtil.isBlank(response)) {
      log.warn("Translation failed for model: {} with prompt: {}", model,
        prompt);
      return null;
    }

    // 打印API返回的响应
    log.info("Response: {}", response);

    // 如果响应不为空，处理翻译结果
    return response;
  }

  /**
   * 解析输入字符串，返回包含中英文句子的 SentencePair 对象
   * <pre>
   *   He decided to venture out somewhere to find a better coffee shop.  (他决定出去找一家更好的咖啡店。)
   * </pre>
   *
   * @param input 输入的字符串
   * @return SentencePair 对象，如果输入无效则返回 null
   */
  public static SentencePair parseSentenceParentheses(String input) {

    // 创建 SentencePair 对象
    SentencePair sentencePair = new SentencePair();
    // 输入为空或仅包含空白字符，直接返回 null
    if (input == null || input.trim().isEmpty()) {
      return sentencePair;
    }

    // 查找中文括号的位置
    int startIdx = input.indexOf('（');
    int endIdx = input.indexOf('）');

    // 如果没有中文括号，返回 null
    if (startIdx == -1 || endIdx == -1 || endIdx <= startIdx) {
      log.warn("未找到中文括号，输入无效: {}", input);
      //return sentencePair;
    } else {
      // 提取中文和英文句子
      String englishSentence = input.substring(0, startIdx).trim(); // 中文部分
      String chineseSentence = input.substring(startIdx + 1, endIdx)
        .trim(); // 英文部分

      sentencePair.setEnglishSentence(englishSentence);
      sentencePair.setChineseSentence(chineseSentence);
    }

    // 查找\n\n
    int nextIdx = input.indexOf("\n\n");
    if (nextIdx == -1 || nextIdx <= startIdx) {
      log.warn("未找到\n\n，输入无效: {}", input);
    } else {
      String englishSentence = input.substring(0, nextIdx).trim(); // 中文部分
      String chineseSentence = input.substring(nextIdx + 2)
        .trim(); // 英文部分

      sentencePair.setEnglishSentence(englishSentence);
      sentencePair.setChineseSentence(chineseSentence);
    }

    return sentencePair;
  }

  // Google Gemini Flash 1.5 8b Experiment

  /**
   * 解析输入字符串，返回包含中英文句子的 SentencePair 对象
   * <pre>
   *   **Sentence:**  After days of heavy rain, we finally ventured out to the park for a walk.
   *
   *
   * **Translation:**  经过几天的暴雨，我们终于冒险去公园散步了。
   * </pre>
   *
   * @param input 输入的字符串
   * @return SentencePair 对象，如果输入无效则返回 null
   */
  public static SentencePair parseSentenceGoogleGeminiFlash15Experiment(
    String input) {

//    String inputText = "**Sentence:**  After days of heavy rain, we finally ventured out to the park for a walk.\n" +
//      "\n" +
//      "**Translation:**  经过几天的暴雨，我们终于冒险去公园散步了。";
    String englishSentence = extractEnglishSentence(input);
    String chineseSentence = extractChineseSentence(input);

    SentencePair sentencePair = new SentencePair(englishSentence,
      chineseSentence);

    System.out.println("英文句子: " + sentencePair.getEnglishSentence());
    System.out.println("中文句子: " + sentencePair.getChineseSentence());

    return sentencePair;
  }

  private static String extractEnglishSentence(String text) {
    int startIndex = text.indexOf("**Sentence:**") + "**Sentence:**".length();
    int endIndex = text.indexOf("**Translation:**");
    if (startIndex >= 0 && endIndex > startIndex) {
      return text.substring(startIndex, endIndex).trim();
    }
    return "";
  }

  private static String extractChineseSentence(String text) {
    int startIndex =
      text.indexOf("**Translation:**") + "**Translation:**".length();
    return text.substring(startIndex).trim();
  }

  // GOOGLE_GEMINI_2_0_FLASH_EXP_FREE
  /**
   * 解析输入字符串，返回包含中英文句子的 SentencePair 对象
   * <pre>
   *   **Sentence:**  After days of heavy rain, we finally ventured out to the park for a walk.
   *
   *
   * **Translation:**  经过几天的暴雨，我们终于冒险去公园散步了。
   * </pre>
   *
   * @param input 输入的字符串
   * @return SentencePair 对象，如果输入无效则返回 null
   */
  public static SentencePair parseSentenceGoogleGemini20FlashExpFree(
    String input) {

//    String inputText = "**Sentence:**  After days of heavy rain, we finally ventured out to the park for a walk.\n" +
//      "\n" +
//      "**Translation:**  经过几天的暴雨，我们终于冒险去公园散步了。";
    String englishSentence = extractEnglishSentenceGoogleGemini20FlashExpFree(input);
    String chineseSentence = extractChineseSentenceGoogleGemini20FlashExpFree(input);

    SentencePair sentencePair = new SentencePair(englishSentence,
      chineseSentence);

    System.out.println("英文句子: " + sentencePair.getEnglishSentence());
    System.out.println("中文句子: " + sentencePair.getChineseSentence());

    return sentencePair;
  }


  private static String extractEnglishSentenceGoogleGemini20FlashExpFree(String text) {
    int startIndex = text.indexOf("**English:**") + "**English:**".length();
    int endIndex = text.indexOf("**Translation:**");
    if (startIndex >= 0 && endIndex > startIndex) {
      return text.substring(startIndex, endIndex).trim();
    }
    return "";
  }

  private static String extractChineseSentenceGoogleGemini20FlashExpFree(String text) {
    int startIndex =
      text.indexOf("**Chinese:**") + "**Chinese:**".length();
    return text.substring(startIndex).trim();
  }

  /**
   * 解析输入字符串，返回包含中英文句子的 SentencePair 对象
   *
   * @param input 输入的字符串
   * @return SentencePair 对象，如果输入无效则返回 null
   */
  public static SentencePair parseSentenceV3(String input) {
    // 输入为空或仅包含空白字符，直接返回 null
    if (input == null || input.trim().isEmpty()) {
      return null;
    }

    // 去掉星号及其包含的内容，并清理多余空格
    String cleanedInput = input.replaceAll("\\*\\*.*?\\*\\*", "").trim();

    // 查找英文和中文部分的位置
    int englishStartIdx =
      cleanedInput.indexOf("English:") + "English:".length();
    int chineseStartIdx =
      cleanedInput.indexOf("Chinese:") + "Chinese:".length();

    // 如果找不到英文或中文部分，返回 null
    if (englishStartIdx == -1 || chineseStartIdx == -1) {
      return null;
    }

    // 提取英文和中文句子
    String englishSentence = cleanedInput.substring(englishStartIdx,
      cleanedInput.indexOf("\n", englishStartIdx)).trim();
    String chineseSentence = cleanedInput.substring(chineseStartIdx).trim();

    // 创建 SentencePair 对象
    SentencePair sentencePair = new SentencePair();
    sentencePair.setEnglishSentence(englishSentence);
    sentencePair.setChineseSentence(chineseSentence);

    return sentencePair;
  }

  private static String callApi(String model, String prompt) {

    if (StrUtil.isBlank(prompt)) {
      return null;
    }

    if (StrUtil.isBlank(model)) {
      model = ModelConstants.GOOGLE_GEMINI_2_0_FLASH_EXP_FREE;
    }

    // 设置请求URL和API Key
    String baseUrl =System.getenv("OPENROUTER_BASE_URL");
    String apiKey = System.getenv("OPENROUTER_API_KEY"); // 从环境变量获取API Key
    String yourSiteUrl = "https://your-site-url.com";    // 替换为你的站点URL
    String yourAppName = "YourAppName";                 // 替换为你的应用名称

    // 构造请求的JSON数据
    JSONObject requestBody = new JSONObject();
//        requestBody.set("model", "openai/gpt-3.5-turbo"); // mistralai/mixtral-8x7b-instruct

//    String    model = "meta-llama/llama-3.1-70b-instruct:free";
    requestBody.set("model",
      model);// "mistralai/mixtral-8x7b-instruct"); // mistralai/mixtral-8x7b-instruct "mistralai/mistral-7b-instruct"
    JSONArray messages = new JSONArray();
    JSONObject userMessage = new JSONObject();
    userMessage.set("role", "user");
    userMessage.set("content", prompt);
    messages.add(userMessage);
    requestBody.set("messages", messages);

    // 发送POST请求
    HttpResponse response = HttpRequest.post(baseUrl)
      .header("Authorization", "Bearer " + apiKey)      // 设置API Key
//            .header("HTTP-Referer", yourSiteUrl)              // 可选，排名所需
//            .header("X-Title", yourAppName)                   // 可选，排名所需
      .header("Content-Type", "application/json")       // JSON内容类型
      .body(requestBody.toString())                     // 设置请求体
      .execute();

    // 解析响应
    if (response.getStatus() == 200) {
      JSONObject responseBody = new JSONObject(response.body());
      JSONArray choices = responseBody.getJSONArray("choices");
      if (choices != null && !choices.isEmpty()) {
        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject message = firstChoice.getJSONObject("message");
        String content = message.getStr("content");
        System.out.println("Response: " + model + " : " + content);
        return content;
      } else {
        System.out.println(
          "Response: " + model + " : " + " No choices found in the response.");
      }
    } else {
      System.out.println(
        "Error: " + response.getStatus() + ", " + response.body());
    }
    return null;
  }
}
