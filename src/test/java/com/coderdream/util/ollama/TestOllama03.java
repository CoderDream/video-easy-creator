package com.coderdream.util.ollama;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Slf4j
public class TestOllama03 {


  private static final String OLLAMA_BASE_URL;
  private static final String OLLAMA_MODEL;
  private static final double OLLAMA_TEMPERATURE;
  private static final String OPENAI_MODEL;
  private static final double OPENAI_TEMPERATURE;

  static {
    // Load configuration from properties file.  Alternatively, use a Constants class
    Properties prop = new Properties();
    try (InputStream input = TestOllama03.class.getClassLoader()
      .getResourceAsStream("application.properties")) {
      if (input == null) {
        System.out.println("Sorry, unable to find application.properties");
        throw new RuntimeException(
          "Missing application.properties file for testing.");  // Crucial!
      }

      prop.load(input);

      OLLAMA_BASE_URL = prop.getProperty("spring.ai.ollama.base-url");
      OLLAMA_MODEL = prop.getProperty("spring.ai.ollama.chat.model");
      OLLAMA_TEMPERATURE = Double.parseDouble(
        prop.getProperty("spring.ai.ollama.chat.options.temperature"));
      OPENAI_MODEL = prop.getProperty("spring.ai.openai.chat.options.model");
      OPENAI_TEMPERATURE = Double.parseDouble(
        prop.getProperty("spring.ai.openai.chat.options.temperature"));


    } catch (IOException ex) {
      ex.printStackTrace();
      throw new RuntimeException("Error loading application.properties",
        ex); // Crucial!
    } catch (NumberFormatException ex) {
      System.err.println(
        "Error parsing numeric properties (temperature): " + ex.getMessage());
      throw new RuntimeException("Error parsing numeric properties", ex);
    } catch (NullPointerException ex) {
      System.err.println(
        "Missing Env Variable OPENAI_API_KEY: " + ex.getMessage());
      throw new RuntimeException("Error loading Env variable OPENAI_API_KEY",
        ex);
    }
  }


  @Test
  public void testOllamaChatModel() {
    // Assert that the Ollama service is running.  This is basic validation that is required since we don't use Mock.
    //  It can be a simple HTTP check.
    //  Here is an example, replace it with your actual implementation for real scenarios.
       /*
       try {
           URL url = new URL(OLLAMA_BASE_URL);
           HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           connection.setRequestMethod("GET");
           int responseCode = connection.getResponseCode();
           assertEquals(200, responseCode, "Ollama service is not running at " + OLLAMA_BASE_URL);
       } catch (IOException e) {
           fail("Failed to connect to Ollama service: " + e.getMessage());
       }
       */

    // Here is an example of using OpenAI SDK (com.unfbx.chatgpt) with properties.
    OpenAiClient openAiClient = new OpenAiClient("OPENAI_API_KEY");
    String prompt = """
      你是一个精通中文和英文的翻译大师。如果我给你英文就翻译成中文，给你中文就翻译成英文。
      """;
    String message = """
      Ollama now supports tool calling with popular models such as Llama 3.1.
      This enables a model to answer a given prompt using tool(s) it knows about,
      making it possible for models to perform more complex tasks or interact with the outside world.
      """;
    Message system = Message.builder().role(Message.Role.SYSTEM).content(prompt)
      .build();
    Message user = Message.builder().role(Message.Role.USER).content(message)
      .build();
    List<Message> messages = Arrays.asList(system, user);
    ChatCompletion chatCompletion = ChatCompletion.builder().messages(messages)
      .model(OPENAI_MODEL).temperature(OPENAI_TEMPERATURE).build();
    ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(
      chatCompletion);
    // Log the raw API response
    log.debug("Raw OpenAI API response: {}", chatCompletionResponse);
    chatCompletionResponse.getChoices().forEach(e -> {
      System.out.println(e.getMessage().getContent());
    });
    // String result = ollamaChatModel.call(prompt + ":" + message); // This line will cause an error since there is no ollamaChatModel injected.
    // Use OpenAI client library or make direct API calls to Ollama server.
    // Assertions depending on the result you get from Ollama.

    // assertNotNull(result);
    // assertTrue(result.contains("specific keyword"), "The translation should contain specific keyword.");
  }
}
