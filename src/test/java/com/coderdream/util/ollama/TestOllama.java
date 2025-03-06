package com.coderdream.util.ollama;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class TestOllama {

    private static final String OLLAMA_BASE_URL;
    private static final String OLLAMA_MODEL;
    private static final double OLLAMA_TEMPERATURE;

    static {
        // Load configuration from properties file.
        Properties prop = new Properties();
        try (InputStream input = TestOllama.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                throw new RuntimeException("Missing application.properties file for testing.");  // Crucial!
            }

            prop.load(input);

            OLLAMA_BASE_URL = prop.getProperty("spring.ai.ollama.base-url");
            OLLAMA_MODEL = prop.getProperty("spring.ai.ollama.chat.model");
            OLLAMA_TEMPERATURE = Double.parseDouble(prop.getProperty("spring.ai.ollama.chat.options.temperature"));


            log.info("OLLAMA_BASE_URL: {}", OLLAMA_BASE_URL);
            log.info("OLLAMA_MODEL: {}", OLLAMA_MODEL);
            log.info("OLLAMA_TEMPERATURE: {}", OLLAMA_TEMPERATURE);


        } catch (IOException ex) {
            log.error("Error loading application.properties", ex);
            throw new RuntimeException("Error loading application.properties", ex);
        } catch (NumberFormatException ex) {
            log.error("Error parsing numeric properties (temperature)", ex);
            throw new RuntimeException("Error parsing numeric properties", ex);
        } catch (NullPointerException ex) {
            log.error("Missing property in application.properties", ex);
            throw new RuntimeException("Missing property in application.properties", ex);
        }
    }

    @Test
    public void testOllamaChatModel() {
        String prompt = "你是一个精通中文和英文的翻译大师。如果我给你英文就翻译成中文，给你中文就翻译成英文。";
        String message = "Ollama now supports tool calling with popular models such as Llama 3.1.";

        // Construct the request body
        Map<String, Object> requestBodyMap = Map.of("model", OLLAMA_MODEL, "prompt", prompt + ":" + message, "stream", false); //Important to add stream=false

        String requestBody = JSONUtil.toJsonStr(requestBodyMap);

        log.info("Ollama API request body: {}", requestBody);

        // Make the API call using Hutool
        HttpResponse response = HttpRequest.post(OLLAMA_BASE_URL + "/api/generate")
                .header("Content-Type", "application/json")
                .body(requestBody)
                .execute();

        String responseBody = response.body();

        log.info("Ollama API response: {}", responseBody);

        // Assertions
        assertNotNull(responseBody, "Response body should not be null");
        assertTrue(response.isOk(), "Response should be successful (200 OK)");

        // You will need to parse the JSON response and extract the translated text.
        // The structure of the response depends on how Ollama formats its output.
        // This example assumes that the translated text is in a field called "response".
        try {
            Map<String, Object> responseMap = JSONUtil.parseObj(responseBody);
            String translatedText = (String) responseMap.get("response");

            log.info("Translated Text: {}",translatedText);
            assertNotNull(translatedText, "Translated text should not be null");
            assertFalse(translatedText.isEmpty(), "Translated text should not be empty");
        } catch (Exception e) {
            log.error("Error parsing JSON response: {}", responseBody, e);
            fail("Failed to parse JSON response: " + responseBody);
        }


        // Add more specific assertions based on your requirements
        // For example, you might check if the translated text contains certain keywords.
    }
}