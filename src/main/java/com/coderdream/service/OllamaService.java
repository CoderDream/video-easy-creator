//package com.coderdream.service;
//
//import org.springframework.ai.ollama.OllamaChatModel;
//import org.springframework.ai.ollama.OllamaChatRequest;
//
//public class OllamaService {
//
//    private OllamaChatModel ollamaChatModel;
//
//    public OllamaService(String ollamaApiUrl, String modelName) {
//        // 使用工厂方法直接创建模型
//        this.ollamaChatModel = OllamaChatModel.builder()
//                .apiUrl(ollamaApiUrl)  // 设置 Ollama API 地址
//                .modelName(modelName)  // 设置模型名称
//                .build();
//    }
//
//    public String chatWithLlama(String userMessage) {
//        OllamaChatRequest chatRequest = OllamaChatRequest.builder()
//                .model(ollamaChatModel.getModelName())
//                .messages(new String[]{userMessage})
//                .build();
//
//        // 调用模型生成聊天响应
//        String response = ollamaChatModel.chat(chatRequest);
//        return response;
//    }
//}
