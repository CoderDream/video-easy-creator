package com.coderdream.util.gemini;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdDateUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public class GeminiApiClient2 {

  private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
  private static final String API_KEY = CdConstants.GEMINI_API_KEY;//  从配置文件或环境变量读取
  private static final String PROXY_HOST = "127.0.0.1"; // 从配置文件或环境变量读取
  private static final int PROXY_PORT = 7890; // 从配置文件或环境变量读取


  /**
   * 调用 Gemini API 生成内容，支持代理
   *
   * @param prompt 用户输入的提示文本
   * @return Gemini API 返回的结果，包含时间信息，失败则返回错误信息
   */
  public static String generateContent(String prompt) {
    Instant startTime = Instant.now();
    HttpRequest request = HttpRequest.post(API_URL)
      .header("Content-Type", "application/json")
      .header("X-Google-API-Key", API_KEY)
      .body(buildRequestBody(prompt));

    // 配置代理，如果代理信息为空则不使用代理
    if (PROXY_HOST != null && !PROXY_HOST.isEmpty() && PROXY_PORT > 0) {
      request = request.setHttpProxy(PROXY_HOST, PROXY_PORT);
    }

    try (HttpResponse response = request.execute()) {
      int statusCode = response.getStatus();
      if (statusCode == 200) {
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        String result = extractResult(jsonObject);
        Instant endTime = Instant.now();
        long duration = Duration.between(startTime, endTime).toMillis();
        return String.format("API 返回结果: %s (耗时: %s)", result,
          CdDateUtil.formatDurationHMS(duration));
      } else {
        Instant endTime = Instant.now();
        long duration = Duration.between(startTime, endTime).toMillis();
        log.error("Gemini API 请求失败，状态码：{}, 响应体：{}, 耗时: {}",
          statusCode, response.body(), CdDateUtil.formatDurationHMS(duration));
        return String.format(
          "Gemini API 请求失败，状态码：%s, 响应体: %s (耗时: %s)", statusCode,
          response.body(), CdDateUtil.formatDurationHMS(duration));
      }
    }
  }

  /**
   * 构造请求体
   *
   * @param prompt 用户提示
   * @return 请求体JSON字符串
   */
  private static String buildRequestBody(String prompt) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.set("contents", new JSONObject[]{new JSONObject().set("parts",
      new JSONObject[]{new JSONObject().set("text", prompt)})});
    return jsonObject.toString();
  }

  /**
   * 从Gemini API的响应中提取结果文本
   *
   * @param jsonObject Gemini API的JSON响应
   * @return 提取的结果文本，如果提取失败，返回"Gemini API 返回了空结果"
   */
  private static String extractResult(JSONObject jsonObject) {
    try {
      return jsonObject.getJSONArray("contents").getJSONObject(0)
        .getJSONArray("parts").getJSONObject(0).getStr("text");
    } catch (Exception e) {
      log.warn("提取Gemini API返回结果失败: {}", e.getMessage());
      return "Gemini API 返回了空结果";
    }
  }

// ... other imports ...


  public static void main(String[] args) {
    String prompt = "Explain how AI works";
    String result = generateContent(prompt);
    System.out.println(result);
  }
}
