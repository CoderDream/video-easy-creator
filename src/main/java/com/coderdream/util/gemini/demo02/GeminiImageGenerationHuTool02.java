package com.coderdream.util.gemini.demo02;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdTimeUtil;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

@Slf4j
public class GeminiImageGenerationHuTool02 {

  private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp-image-generation:generateContent";
  private static final String API_KEY = CdConstants.GEMINI_API_KEY; // 替换为你的 Gemini API Key
  private static final String IMAGE_SAVE_PATH = "gemini-native-image.png";

  public static void main(String[] args) {
    String contents = "Hi, can you create a 3d rendered image of a pig " +
      "with wings and a top hat flying over a happy " +
      "futuristic scifi city with lots of greenery?";

    log.info("Starting Gemini image generation...");
    generateContent(contents);
    log.info("Image saved to: {}", IMAGE_SAVE_PATH);
  }

  /**
   * 调用 Gemini API 生成内容，包括文本和图像，并记录耗时和处理异常。
   *
   * @param contents 要发送给 Gemini API 的提示内容。
   */
  public static void generateContent(String contents) {
    long startTime = System.currentTimeMillis();
    log.debug("Starting generateContent...");
    HttpResponse response = null;
    try {
      // 构建请求体 (JSON)
      JSONObject requestBody = JSONUtil.createObj();
      JSONArray contentsArray = JSONUtil.createArray();
      contentsArray.add(JSONUtil.createObj().set("text", contents));
      requestBody.set("contents", contentsArray);

      JSONObject generationConfig = JSONUtil.createObj();
      JSONArray responseModalities = JSONUtil.createArray();
      responseModalities.add("TEXT");
      responseModalities.add("IMAGE");
      generationConfig.set("responseModalities", responseModalities);
      requestBody.set("generationConfig", generationConfig);

      String jsonRequestBody = requestBody.toString();
      log.debug("Gemini API request body: {}", jsonRequestBody);

      // 发送 HTTP POST 请求
      long requestStartTime = System.currentTimeMillis();
      response = HttpRequest.post(GEMINI_API_URL + "?key=" + API_KEY)
        .header("Content-Type", "application/json")
        .body(jsonRequestBody)
        .execute();
      long requestEndTime = System.currentTimeMillis();
      log.debug("Gemini API request finished in: {}",
        CdTimeUtil.formatDuration(requestEndTime - requestStartTime));

      String responseBody = response.body();
      log.debug("Gemini API response body: {}", responseBody);

      // 解析响应 (JSON)
      JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
      log.debug("Parsed JSON response: {}", jsonResponse);

      if (jsonResponse != null && jsonResponse.containsKey("candidates")) {
        JSONArray candidates = jsonResponse.getJSONArray("candidates");
        if (candidates != null && !candidates.isEmpty()) {
          JSONObject candidate = candidates.getJSONObject(0);
          if (candidate != null && candidate.containsKey("content")) {
            JSONObject content = candidate.getJSONObject("content");
            if (content != null && content.containsKey("parts")) {
              JSONArray parts = content.getJSONArray("parts");
              if (parts != null) {
                for (int i = 0; i < parts.size(); i++) {
                  JSONObject part = parts.getJSONObject(i);
                  log.debug("Processing part: {}", part);
                  if (part != null && part.containsKey("text")) {
                    String textPart = part.getStr(
                      "text"); // 使用 HuTool 的 getStr 方法
                    log.info("Text Part: {}", textPart);
                  } else if (part != null && part.containsKey("inlineData")) {
                    JSONObject inlineData = part.getJSONObject("inlineData");
                    log.debug("Processing inlineData: {}", inlineData);
                    if (inlineData != null && inlineData.containsKey("mimeType")
                      && inlineData.containsKey("data")) {
                      String mimeType = inlineData.getStr(
                        "mimeType"); // 使用 HuTool 的 getStr 方法
                      String base64Data = inlineData.getStr(
                        "data"); // 使用 HuTool 的 getStr 方法
                      if (mimeType != null && mimeType.startsWith("image/")
                        && base64Data != null) {
                        byte[] imageData = Base64.decodeBase64(base64Data);
                        try (FileOutputStream fos = new FileOutputStream(
                          IMAGE_SAVE_PATH)) {
                          fos.write(imageData);
                          log.info("Image part saved to: {}", IMAGE_SAVE_PATH);
                        } catch (IOException e) {
                          log.error("Error saving image part: {}",
                            e.getMessage(), e);
                          throw e; // 重新抛出 IO 异常
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      } else if (jsonResponse != null && jsonResponse.containsKey("error")) {
        JSONObject error = jsonResponse.getJSONObject("error");
        if (error != null && error.containsKey("status")
          && error.getJSONObject("status") != null && error.getJSONObject(
          "status").containsKey("message")) {
          log.error("Error from Gemini API: {}", error.getJSONObject("status")
            .getStr("message")); // 使用 HuTool 的 getStr 方法
        } else if (error != null && error.containsKey("message")) {
          log.error("Error from Gemini API: {}",
            error.getStr("message")); // 使用 HuTool 的 getStr 方法
        } else {
          log.error("Error from Gemini API: {}", jsonResponse);
        }
      } else {
        log.warn("No 'candidates' or 'error' found in Gemini API response.");
      }
    } catch (cn.hutool.http.HttpException e) {
      log.error("HTTP error during Gemini API call: {}", e.getMessage(), e);
    } catch (IOException e) {
      log.error("IO error during Gemini API call or image saving: {}",
        e.getMessage(), e);
    } finally {
      long endTime = System.currentTimeMillis();
      log.debug("generateContent finished in: {}",
        CdTimeUtil.formatDuration(endTime - startTime));
      if (response != null) {
        response.close();
      }
    }
  }
}
