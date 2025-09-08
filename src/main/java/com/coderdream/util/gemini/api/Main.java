// 文件: com.coderdream.util.gemini.api.Main.java

package com.coderdream.util.gemini.api;

import com.coderdream.util.gemini.api.entity.Model;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // ... (前面的代码保持不变) ...

            // 3. 【新增】生成内容
            String modelForContent = "models/gemini-1.5-flash-latest";
            String prompt = "Explain how AI works in a few words";
            System.out.println("正在生成内容，提示词: \"" + prompt + "\"，模型: " + modelForContent);
            String generatedText = GeminiApiUtil.generateContent(modelForContent, prompt);
            System.out.println("生成的内容:\n" + generatedText);

        } catch (IOException e) {
            // 【优化】更智能地处理错误信息
            if (e.getMessage() != null && e.getMessage().contains("\"code\": 429")) {
                System.err.println("调用 Gemini API 失败：超出了配额限制 (HTTP 429)。");
                System.err.println("这通常意味着您今天的免费调用次数已用完。请检查您的Google Cloud项目配额或升级到随用随付计划。");
            } else {
                System.err.println("调用 Gemini API 时发生了一个未知的IO错误: " + e.getMessage());
            }
            // 打印完整的堆栈信息以供调试
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("API 调用线程被中断。");
            Thread.currentThread().interrupt(); // 重新设置中断状态
            e.printStackTrace();
        }
    }
}
