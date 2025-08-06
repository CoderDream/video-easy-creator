package com.coderdream.util.gemini;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CallGeminiApiUtil {

  private static final String API_KEY = CdConstants.GEMINI_API_KEY; // 替换为你的 Gemini API 密钥
  private static final String MODEL_NAME = "gemini-2.0-flash"; // 选择合适的模型 "gemini-2.5-pro-exp-03-25"; gemini-2.0-flash gemini-1.5-pro-latest
  private static final int MAX_RETRIES = 10; // 最大重试次数
  private static final int RETRY_DELAY = 3000; // 重试延迟（毫秒）

  /**
   * 调用Gemini API 并获取生成的文本。
   *
   * @param prompt 提示词
   * @param modelName 模型名称
   * @return 生成的文本
   */
  public static String callApi(String prompt, String modelName) {
    log.info("提示词: {}", prompt);
    long startTime = System.currentTimeMillis();
    String text;
    int attempt;
    for (attempt = 0; attempt < MAX_RETRIES; attempt++) {
      try {
        // 设置请求体
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", new Object[]{part});
        requestBody.put("contents", new Object[]{content});
        requestBody.put("generationConfig",
          createGenerationConfig()); // 添加 generationConfig

        // 设置代理
        Proxy proxy = new Proxy(Proxy.Type.SOCKS,
          new InetSocketAddress("127.0.0.1", OperatingSystem.getProxyPort()));

        final String API_URL =
          "https://generativelanguage.googleapis.com/v1beta/models/" + modelName
            + ":generateContent?key=" + API_KEY;
        log.info("API_URL: {}", API_URL);
        log.info("requestBody: {}", JSONUtil.toJsonStr(requestBody));
        // 发送 POST 请求
        String response = HttpUtil.createPost(API_URL)
          .setProxy(proxy)
          .header("Content-Type", "application/json")
          .body(JSONUtil.toJsonStr(requestBody))
          .execute()
          .body();

        // 打印原始响应
        log.debug("Gemini API 响应: {}", response);

        // 解析响应
        JSONObject jsonResponse = JSONUtil.parseObj(response);
        if (jsonResponse.containsKey("candidates")) {
          JSONArray candidates = jsonResponse.getJSONArray("candidates");
          if (candidates != null && !candidates.isEmpty()) {
            JSONObject candidate = candidates.getJSONObject(0);
            if (candidate != null && candidate.containsKey("content")) {
              JSONObject contentResponse = candidate.getJSONObject("content");
              if (contentResponse != null && contentResponse.containsKey(
                "parts")) {
                JSONArray parts = contentResponse.getJSONArray("parts");
                if (parts != null && !parts.isEmpty()) {
                  JSONObject partResponse = parts.getJSONObject(0);
                  if (partResponse != null) {
                    text = partResponse.getStr("text", "")
                      .trim(); // 使用 getStr 并提供默认值

                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    String formattedTime = CdTimeUtil.formatDuration(duration);
                    log.info(
                      "调用Gemini API 任务完成，模型为：{}， 总耗时: {}，执行次数：{}",
                      MODEL_NAME,
                      formattedTime, attempt);
                    return text;
                  } else {
                    log.error("Gemini API 响应 'partResponse' 为空.");
                  }

                } else {
                  log.error("Gemini API 响应 'parts' 数组为空.");
                }
              } else {
                log.error("Gemini API 响应缺少 'content.parts' 字段.");
              }
            } else {
              log.error("Gemini API 响应缺少 'candidate.content' 字段.");
            }

          } else {
            log.error("Gemini API 响应 'candidates' 数组为空.");
          }
        } else {
          log.error("Gemini API 响应缺少 'candidates' 字段: {}", response);
        }
      } catch (Exception e) {
        log.error("任务执行出错 (尝试 {}/{}) : ", attempt + 1, MAX_RETRIES, e);
        log.error("异常详情:", e);  // 打印异常堆栈信息

        if (attempt < MAX_RETRIES - 1) {
          try {
            Thread.sleep(RETRY_DELAY); // 等待一段时间后重试
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("重试等待被中断！");
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            String formattedTime = CdTimeUtil.formatDuration(duration);
            log.info("任务失败, 总耗时: {}，执行次数：{}", formattedTime,
              attempt);
            return null; // 中断，返回 null
          }
        }
      }
    }

    log.error("达到最大重试次数，任务执行失败: {}", prompt);
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    String formattedTime = CdTimeUtil.formatDuration(duration);
    log.info("任务完成, 总耗时: {}，执行次数：{}", formattedTime,
      attempt + 1);
    return null; // 达到最大重试次数，返回 null
  }

  /**
   * 调用Gemini API 并获取生成的文本。
   *
   * @param prompt 提示词
   * @return 生成的文本
   */
  public static String callApi(String prompt) {
    return callApi(prompt, MODEL_NAME);
  }


  /**
   * 创建 GenerationConfig 详细信息请参考：
   * <a href="https://ai.google.dev/api/rest/v1beta/GenerationConfig">...</a>
   *
   * @return GenerationConfig 配置信息
   */
  private static Map<String, Object> createGenerationConfig() {
    Map<String, Object> generationConfig = new HashMap<>();
    generationConfig.put("temperature", 0.2);
    generationConfig.put("topK", 32);
    generationConfig.put("topP", 0.9);
    generationConfig.put("maxOutputTokens", 1024);
    return generationConfig;
  }

}

