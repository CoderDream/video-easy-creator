package com.coderdream.util.gemini.api;

import com.coderdream.util.gemini.api.entity.Model;
import java.io.IOException;
import java.util.List;

public class Main02 {
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
            String modelToUse = "models/gemini-pro";
            String textToCount = "Hello, world! This is a test.";
            System.out.println("Counting tokens for text: \"" + textToCount + "\" using model: " + modelToUse);
            int tokenCount = GeminiApiUtil.countTokens(modelToUse, textToCount);
            System.out.println("Total tokens: " + tokenCount);

            System.out.println("\n----------------------------------\n");

            // 3. 【新增】生成内容
            // 注意：官方示例中的 'gemini-2.0-flash' 可能不是一个公开可用的模型名称。
            // 我们使用 'gemini-1.5-flash-latest' 作为稳定可靠的替代。
            String modelForContent = "models/gemini-1.5-flash-latest";
            String prompt = "Explain how AI works in a few words";
            System.out.println("Generating content for prompt: \"" + prompt + "\" using model: " + modelForContent);
            String generatedText = GeminiApiUtil.generateContent(modelForContent, prompt);
            System.out.println("Generated Text:\n" + generatedText);

        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred while calling Gemini API: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
