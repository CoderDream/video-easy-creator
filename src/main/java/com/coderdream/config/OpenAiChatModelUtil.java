package com.coderdream.config;

import org.apache.logging.log4j.util.PropertiesUtil;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;

public class OpenAiChatModelUtil {

    public static OpenAiChatModel getInstance() {
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7890");
        // 读取yaml配置文件中的OpenAI API密钥
        PropertiesUtil propertiesUtil = new PropertiesUtil("");
//        OpenAiConfig openAiConfig = new OpenAiConfig();

        String baseUrl =System.getenv("OPENROUTER_BASE_URL");
        String apiKey = System.getenv("OPENROUTER_API_KEY"); // 从环境变量获取API Key


        // 通过 Environment 直接获取配置属性
//        String baseUrl = StaticConfigUtil.getBaseUrl();
//        System.out.println("Base Url: " + baseUrl);
//        // 通过 Environment 直接获取配置属性
//        String apiKey = StaticConfigUtil.getApiKey();
//        System.out.println("API Key: " + apiKey);
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, apiKey);

        return new OpenAiChatModel(openAiApi);
    }

}
