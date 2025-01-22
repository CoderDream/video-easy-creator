package com.coderdream.util.gemini;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.callapi.HttpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Gemini API 通用工具类，提供静态方法调用 Gemini API。
 */
@Slf4j
public class GeminiApiClient3 {

  private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
  private static final String API_KEY = CdConstants.GEMINI_API_KEY;
  private static final String PROXY_HOST = CdConstants.PROXY_HOST;
  private static final int PROXY_PORT = CdConstants.PROXY_PORT;

  public static String URL =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
      + CdConstants.GEMINI_API_KEY;

  /**
   * 调用 Gemini API 生成内容
   *
   * @param prompt 用户输入的提示文本
   * @return Gemini API 返回的结果文本，或者错误信息
   */
  public static String generateContent(String prompt) {

    // 使用 Hutool 的 JSONObject 构造 JSON 对象
    JSONObject jsonObject = new JSONObject();

    // 构造"contents"数组
    JSONObject content = new JSONObject();
    content.set("parts", new Object[]{
      new JSONObject().set("text", prompt)  // 将动态参数填入 "text"
    });

    // 将"contents"加入到最终的 JSON 对象中
    jsonObject.set("contents", new Object[]{content});

    // 打印输出生成的 JSONObject
    System.out.println(jsonObject.toStringPretty());

    String result = HttpUtil.httpHutoolPost(URL, jsonObject.toString(),
      CdConstants.PROXY_HOST,
      CdConstants.PROXY_PORT);
    log.info("{}", result);

    // 使用 Hutool 将 JSON 字符串解析为对象
    JSONObject resultObject = JSONUtil.parseObj(result);

    // 将 JSON 数据转换为 GeminiApiResponse 实体类
    GeminiApiResponse response = resultObject.toBean(GeminiApiResponse.class);

    // 打印结果验证
    System.out.println("模型版本号: " + response.getModelVersion());
    System.out.println("候选结果数量: " + response.getCandidates().size());
    System.out.println(
      "第一条内容: " + response.getCandidates().get(0).getContent().getParts()
        .get(0).getText());
    result = response.getCandidates().get(0).getContent().getParts().get(0)
      .getText();

    return result;
  }

  /**
   * 构造 Gemini API 的请求体
   *
   * @param prompt 用户提示文本
   * @return 请求体 JSON 对象
   */
  private static JSONObject buildRequestBody(String prompt) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.set("contents", new JSONObject[]{new JSONObject().set("parts",
      new JSONObject[]{new JSONObject().set("text", prompt)})});
    return jsonObject;
  }

  /**
   * 从 Gemini API 的响应中提取结果文本
   *
   * @param jsonObject Gemini API 的 JSON 响应
   * @return 提取的结果文本，如果提取失败，返回空字符串
   */
  private static String extractResult(JSONObject jsonObject) {
    try {


      return jsonObject.getJSONArray("contents").getJSONObject(0)
        .getJSONArray("parts").getJSONObject(0).getStr("text");
    } catch (Exception e) {
      log.warn("提取 Gemini API 返回结果失败: {}", e.getMessage());
      return ""; // 返回空字符串而不是错误信息，让调用方自己处理
    }
  }

  public static void main(String[] args) {
    String prompt = "翻译成简体中文："
      + "Why are countryside walks no longer so popular?";// "Explain how AI works.";
    String result = generateContent(prompt);
    System.out.println(result);
  }
}
