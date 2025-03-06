package com.coderdream.util.ollama;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OllamaApiUtilTest 类：用于测试 OllamaApiUtil 类的功能
 */
@Slf4j
public class OllamaApiUtilTest {

    /**
     * 测试 Ollama Chat 模型
     */
    @Test
    public void testOllamaChatModel() {
        String prompt = "你是一个精通中文和英文的翻译大师。如果我给你英文就翻译成中文，给你中文就翻译成英文。";  // prompt：描述模型的任务
        String message = "Ollama now supports tool calling with popular models such as Llama 3.1."; // 消息内容：要翻译的文本

        OllamaApiUtil.OllamaRequest ollamaRequest = new OllamaApiUtil.OllamaRequest();
        ollamaRequest.setPrompt(prompt);
        ollamaRequest.setMessage(message);

        OllamaApiUtil.OllamaResponse ollamaResponse = OllamaApiUtil.generate(ollamaRequest);

        assertNotNull(ollamaResponse, "OllamaResponse 对象不应为 null");
        assertTrue(ollamaResponse.isSuccess(), "Ollama API 调用应该成功");
        assertNotNull(ollamaResponse.getResponse(), "翻译后的文本不应为 null");
        assertFalse(ollamaResponse.getResponse().isEmpty(), "翻译后的文本不应为空");

        log.info("翻译后的文本: {}", ollamaResponse.getResponse());
        log.info("API 调用耗时: {}", ollamaResponse.getDuration());
    }
}