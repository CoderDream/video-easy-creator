package com.coderdream.util.gemini.api;

import com.coderdream.util.gemini.api.entity.Model;
import java.io.IOException;
import java.util.List;

public class Main03 {
    public static void main(String[] args) {
        try {
            // 1. 获取可用模型
            System.out.println("Fetching available models...");
            List<Model> models = GeminiApiUtil.listAvailableModels();
            for (Model model : models) {
                System.out.println("Model: " + model.getName() + " (" + model.getDisplayName() + ")");
            }

            System.out.println("\n----------------------------------\n");

            // 2. 计算Token数量
            // 【修改点】将过时的 'gemini-pro' 换成当前可用的模型
            String modelForTokenCount = "models/gemini-1.5-flash-latest";
            String textToCount = "Hello, world! This is a test.";
            System.out.println("正在使用模型计算Token: \"" + textToCount + "\"，模型: " + modelForTokenCount);
            int tokenCount = GeminiApiUtil.countTokens(modelForTokenCount, textToCount);
            System.out.println("Token 总数: " + tokenCount);

            System.out.println("\n----------------------------------\n");

            // 3. 【新增】生成内容
            // 我们使用 'gemini-1.5-flash-latest' 作为稳定可靠的替代。
            String modelForContent = "models/gemini-1.5-flash-latest";
            String prompt = "Explain how AI works in a few words";
            System.out.println("正在生成内容，提示词: \"" + prompt + "\"，模型: " + modelForContent);
            String generatedText = GeminiApiUtil.generateContent(modelForContent, prompt);
            System.out.println("生成的内容:\n" + generatedText);

        } catch (IOException | InterruptedException e) {
            System.err.println("调用 Gemini API 时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
