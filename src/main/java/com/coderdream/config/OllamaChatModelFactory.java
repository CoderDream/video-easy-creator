//package com.coderdream.config;
//
//import org.springframework.ai.ollama.OllamaChatModel;
//import org.springframework.ai.ollama.OllamaChatOptions;
//
//public class OllamaChatModelFactory {
//
//    // 使用新的 OllamaChatConfig 来配置
//    public static OllamaChatModel createOllamaChatModel(String ollamaApiUrl, String modelName) {
//        OllamaChatConfig config = OllamaChatConfig.builder()
//          .apiUrl(ollamaApiUrl)    // 设置 Ollama API 地址
//          .modelName(modelName)    // 设置模型名称
//          .build();
//
//        return new OllamaChatModel(config);  // 使用 config 创建 OllamaChatModel
//    }
//}
