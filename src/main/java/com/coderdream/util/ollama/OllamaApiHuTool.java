package com.coderdream.util.ollama;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OllamaApiHuTool {

  private static final String OLLAMA_URL = "http://192.168.3.165:11434/api/generate";
  private static final int MAX_RETRIES = 3;
  private static final int RETRY_DELAY_MS = 1000; // 1 second
  private static final int TIMEOUT = 60 * 1000; // 60 seconds

  /**
   * 调用Ollama API生成文本
   *
   * @param ollamaReq Ollama请求参数对象
   * @return API响应，如果失败则返回null
   */
  public OllamaResponse generate(OllamaReq ollamaReq) {

    JSONObject requestBody = new JSONObject();
    requestBody.set("model", ollamaReq.getModel());
    requestBody.set("prompt", ollamaReq.getPrompt());
    requestBody.set("stream", ollamaReq.isStream());

    OllamaResponse response = null;
    for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
      try {
        response = executeRequest(requestBody);
        if (response != null) {
          return response; // 成功，退出循环
        } else {
          log.warn("Attempt {} failed, retrying...", attempt);
        }
      } catch (Exception e) {
        log.error("Attempt {} failed with exception: {}", attempt,
          e.getMessage(), e);
      }

      if (attempt < MAX_RETRIES) {
        try {
          Thread.sleep(RETRY_DELAY_MS); // 休眠后重试
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt(); // Restore interrupted state.
          log.error("Retry interrupted.", e);
          return null;
        }
      }
    }

    log.error("Failed to generate text after {} retries.", MAX_RETRIES);
    return null;
  }

  /**
   * 执行HTTP请求并解析响应(使用HuTool)
   *
   * @param requestBody 请求体
   * @return OllamaResponse对象，如果发生错误则返回null
   */
  private OllamaResponse executeRequest(JSONObject requestBody) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    try {
      String jsonRequest = requestBody.toString();

      HttpResponse httpResponse = HttpRequest.post(OLLAMA_URL)
        .header("Content-Type", "application/json")
        .body(jsonRequest)
        .timeout(TIMEOUT)
        .execute();

      int statusCode = httpResponse.getStatus();
      String responseBody = httpResponse.body();

      if (statusCode >= 200 && statusCode < 300) {
        OllamaResponse ollamaResponse = JSONUtil.toBean(responseBody,
          OllamaResponse.class);
        stopWatch.stop();
        log.info("Ollama API call successful. Time taken: {}",
          DateUtil.formatBetween(stopWatch.getTotalTimeMillis()));
        return ollamaResponse;
      } else {
        log.error(
          "Ollama API call failed with status code: {}. Response body: {}",
          statusCode, responseBody);
        return null;
      }

    } catch (Exception e) {
      log.error("Error calling Ollama API: {}",
        ExceptionUtil.stacktraceToString(e));
      return null; // 返回null而不是抛出异常，以便重试机制可以工作
    }
  }

  public static String generate(String prompt) {
    OllamaApiHuTool ollamaApi = new OllamaApiHuTool();
    OllamaReq ollamaReq = new OllamaReq();
    ollamaReq.setModel("SiliconBasedWorld/Qwen2.5-7B-Instruct-1M:latest");
//    ollamaReq.setPrompt(
//      "将英文句子翻译为中文，只给我文本的翻译，别添加其他的内容。不要返回英文原文，不要重复翻译。英文为："
//        + prompt);

    ollamaReq.setPrompt(prompt);
    ollamaReq.setStream(false);
    log.info("Calling Ollama API with prompt: {}", ollamaReq.getPrompt());
    OllamaResponse response = ollamaApi.generate(ollamaReq);

    if (response != null) {
      log.trace("Response: {}", response);
    } else {
      log.error("Failed to get response from Ollama API. {} ",
        ollamaReq.getPrompt());
    }

//    assert response != null;
//    return response.getResponse();
    if (response != null) {
      return response.getResponse();
    }
    return null;
  }

  public static void main(String[] args) {

    String response = OllamaApiHuTool.generate(
      "Create a set of math, worksheets for parents.");
    if (response != null) {
      System.out.println("Response: " + response);
    } else {
      System.out.println("Failed to get response from Ollama API.");
    }
  }
}
