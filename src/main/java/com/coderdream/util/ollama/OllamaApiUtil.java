package com.coderdream.util.ollama;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;

/**
 * OllamaApiUtil 类：封装了与 Ollama API 交互的工具方法
 */
@Slf4j
public class OllamaApiUtil {

    private static final String OLLAMA_BASE_URL;  // Ollama API 的基本 URL
    private static final String OLLAMA_MODEL;    // 要使用的模型名称
    private static final double OLLAMA_TEMPERATURE; // 温度参数，影响生成文本的随机性

    static {
        // Load configuration from properties file.  加载配置文件
        Properties prop = new Properties();
        try (InputStream input = OllamaApiUtil.class.getClassLoader().getResourceAsStream("application.properties")) { // try-with-resources statement
            if (input == null) {
                System.out.println("Sorry, 无法找到 application.properties 文件");
                throw new RuntimeException("缺少 application.properties 文件，请检查是否存在.");  // 关键！找不到配置文件就抛出异常
            }

            prop.load(input);

            OLLAMA_BASE_URL = prop.getProperty("spring.ai.ollama.base-url");
            OLLAMA_MODEL = prop.getProperty("spring.ai.ollama.chat.model");
            OLLAMA_TEMPERATURE = Double.parseDouble(prop.getProperty("spring.ai.ollama.chat.options.temperature"));

            log.info("OLLAMA_BASE_URL: {}", OLLAMA_BASE_URL);
            log.info("OLLAMA_MODEL: {}", OLLAMA_MODEL);
            log.info("OLLAMA_TEMPERATURE: {}", OLLAMA_TEMPERATURE);

        } catch (IOException ex) {
            log.error("加载 application.properties 文件时发生错误", ex);
            throw new RuntimeException("加载 application.properties 文件时发生错误", ex);
        } catch (NumberFormatException ex) {
            log.error("解析数值类型的属性 (temperature) 时发生错误", ex);
            throw new RuntimeException("解析数值类型的属性时发生错误", ex);
        } catch (NullPointerException ex) {
            log.error("在 application.properties 文件中缺少必要的属性", ex);
            throw new RuntimeException("缺少必要的属性，请检查 application.properties 文件", ex);
        }
    }


    /**
     * 调用 Ollama API 生成文本
     *
     * @param request   Ollama 请求对象，包含 prompt 和 message
     * @return Ollama 响应对象，包含结果、错误信息和请求耗时
     */
    public static OllamaResponse generate(OllamaRequest request) {
        return generate(request, OLLAMA_BASE_URL, OLLAMA_MODEL);
    }

    /**
     * 调用 Ollama API 生成文本
     *
     * @param request   Ollama 请求对象，包含 prompt 和 message
     * @param baseUrl   Ollama API 的基本 URL
     * @param model     模型名称
     * @return Ollama 响应对象，包含结果、错误信息和请求耗时
     */
    public static OllamaResponse generate(OllamaRequest request, String baseUrl, String model) {
        OllamaResponse ollamaResponse = new OllamaResponse();
        Instant start = Instant.now();  // 记录开始时间

        try {
            String prompt = request.getPrompt();
            String message = request.getMessage();

            // Construct the request body 构造请求体
            Map<String, Object> requestBodyMap = Map.of("model", model, "prompt", prompt + ":" + message, "stream", false);

            String requestBody = JSONUtil.toJsonStr(requestBodyMap);

            log.info("Ollama API 请求体: {}", requestBody);

            // Make the API call using Hutool 使用 Hutool 发起 API 调用
            HttpResponse response = HttpRequest.post(baseUrl + "/api/generate")
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .execute();

            String responseBody = response.body();

            log.info("Ollama API 响应: {}", responseBody);

            if (response.isOk()) { // 检查 HTTP 状态码是否为 200 OK
                try {
                    Map<String, Object> responseMap = JSONUtil.parseObj(responseBody);
                    String translatedText = (String) responseMap.get("response");
                    ollamaResponse.setSuccess(true);
                    ollamaResponse.setResponse(translatedText);
                } catch (Exception e) {
                    log.error("解析 JSON 响应时发生错误: {}", responseBody, e);
                    ollamaResponse.setSuccess(false);
                    ollamaResponse.setError("解析 JSON 响应时发生错误: " + e.getMessage() + ", 响应内容: " + responseBody);
                }
            } else {
                log.error("Ollama API 请求失败，状态码: {}", response.getStatus());
                ollamaResponse.setSuccess(false);
                ollamaResponse.setError("Ollama API 请求失败，状态码: " + response.getStatus() + ", 响应内容: " + responseBody);
            }

        } catch (Exception e) {
            log.error("Ollama API 调用过程中发生错误", e);
            ollamaResponse.setSuccess(false);
            ollamaResponse.setError("Ollama API 调用过程中发生错误: " + e.getMessage());

        } finally {
            Instant finish = Instant.now();  // 记录结束时间
            long timeElapsed = Duration.between(start, finish).toMillis();
            LocalTime time = LocalTime.ofSecondOfDay(timeElapsed / 1000);
            int milliseconds = (int) (timeElapsed % 1000);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
            String formattedTime = time.format(formatter).substring(0, 8) + "." + String.format("%03d", milliseconds);  //format to HH:mm:ss.SSS

            ollamaResponse.setDuration(formattedTime);
        }

        return ollamaResponse;
    }

    /**
     * Ollama 请求对象
     */
    @Data
    public static class OllamaRequest {
        private String prompt; // prompt：描述模型的任务
        private String message; // 消息内容：要传递给模型的文本
    }

    /**
     * Ollama 响应对象
     */
    @Data
    public static class OllamaResponse {
        private boolean success;    // 是否成功
        private String response;   // 响应结果
        private String error;      // 错误信息
        private String duration;   // 请求耗时，格式为 时:分:秒.毫秒
    }
}