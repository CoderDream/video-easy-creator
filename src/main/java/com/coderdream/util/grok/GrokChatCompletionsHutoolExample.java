package com.coderdream.util.grok;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.youtube.YouTubeApiUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Grok API示例类 用于展示如何使用Hutool调用xAI的Grok API，包括聊天完成和模型查询功能
 */
@Slf4j
public class GrokChatCompletionsHutoolExample {

  // API Key常量，需替换为实际值
  private static final String API_KEY = CdConstants.GROK_API_KEY;//"sk-123456";
  // API端点常量
  private static final String BASE_URL = "https://api.x.ai";// "http://192.168.3.165:3000";//"https://api.x.ai";
  private static final String CHAT_COMPLETIONS_URL =
    BASE_URL + "/v1/chat/completions";// "https://api.x.ai/v1/chat/completions";
  private static final String MODELS_URL =
    BASE_URL + "/v1/models";//"https://api.x.ai/v1/models";

  public static void main(String[] args) {
    YouTubeApiUtil.enableProxy();
    // 调用模型查询接口
    String modelsResponse = GrokChatCompletionsHutoolExample.listAvailableModels();
    log.info("模型查询响应结果: {}", modelsResponse);

    // 调用聊天完成接口
    String chatResponse = GrokChatCompletionsHutoolExample.callChatCompletions(
      "你好，Grok！请告诉我今天的天气怎么样？");
    log.info("聊天完成响应结果: {}", chatResponse);
  }

  /**
   * 调用Grok API的聊天完成接口
   *
   * @param userMessage 用户输入的消息内容
   * @return API返回的响应内容（JSON字符串）
   */
  public static String callChatCompletions(String userMessage) {
    // 记录方法开始时间
    TimeInterval timer = DateUtil.timer();
    log.info("开始调用Grok API聊天完成接口，输入消息: {}", userMessage);

    try {
      // 构造请求体JSON
      JSONObject requestBody = new JSONObject();
      requestBody.set("model", "grok-beta"); // 模型名称，需参考官方文档
      requestBody.set("messages", JSONUtil.createArray()
        .put(new JSONObject().set("role", "user").set("content", userMessage)));
      requestBody.set("max_tokens", 150); // 最大token数
      requestBody.set("temperature", 0.7); // 输出随机性

      // 发送POST请求
      HttpResponse response = HttpRequest.post(CHAT_COMPLETIONS_URL)
        .header("Authorization", "Bearer " + API_KEY) // 添加认证头
        .header("Content-Type", "application/json") // 设置内容类型
        .body(requestBody.toString()) // 设置请求体
        .timeout(20000) // 设置超时时间20秒
        .execute();

      // 检查响应状态
      if (response.isOk()) {
        String responseBody = response.body();
        log.info("聊天完成接口调用成功，响应内容: {}", responseBody);
        return responseBody;
      } else {
        log.error("聊天完成接口调用失败，状态码: {}, 错误信息: {}",
          response.getStatus(), response.body());
        return "调用失败，状态码: " + response.getStatus();
      }
    } catch (Exception e) {
      log.error("调用聊天完成接口时发生异常: ", e);
      return "调用异常: " + e.getMessage();
    } finally {
      // 计算并记录耗时
      long costTimeMs = timer.interval(); // 获取毫秒数
      String costTimeFormatted = DateUtil.formatBetween(costTimeMs); // 转换为时分秒
      log.info("聊天完成接口调用结束，总耗时: {}", costTimeFormatted);
    }
  }

  /**
   * 调用Grok API的模型查询接口
   *
   * @return API返回的可用模型列表（JSON字符串）
   */
  public static String listAvailableModels() {
    // 记录方法开始时间
    TimeInterval timer = DateUtil.timer();
    log.info("开始调用Grok API模型查询接口");

    try {
      // 发送GET请求
      HttpResponse response = HttpRequest.get(MODELS_URL)
        .header("Authorization", "Bearer " + API_KEY) // 添加认证头
        .header("Content-Type", "application/json") // 设置内容类型
        .timeout(20000) // 设置超时时间20秒
        .execute();

      // 检查响应状态
      if (response.isOk()) {
        String responseBody = response.body();
        log.info("模型查询接口调用成功，响应内容: {}", responseBody);
        return responseBody;
      } else {
        log.error("模型查询接口调用失败，状态码: {}, 错误信息: {}",
          response.getStatus(), response.body());
        return "调用失败，状态码: " + response.getStatus();
      }
    } catch (Exception e) {
      log.error("调用模型查询接口时发生异常: ", e);
      return "调用异常: " + e.getMessage();
    } finally {
      // 计算并记录耗时
      long costTimeMs = timer.interval(); // 获取毫秒数
      String costTimeFormatted = DateUtil.formatBetween(costTimeMs); // 转换为时分秒
      log.info("模型查询接口调用结束，总耗时: {}", costTimeFormatted);
    }
  }
}
