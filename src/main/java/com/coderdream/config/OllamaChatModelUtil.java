package com.coderdream.config;

import io.micrometer.observation.ObservationRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OllamaChatModelUtil {

  @Value("${spring.ai.ollama.base-url}")
  private static String ollamaBaseUrl;

  private static OllamaChatModel ollamaChatModel;

  public static synchronized OllamaChatModel getInstance() {
    if (ollamaChatModel == null) {
      OllamaApi ollamaApi = new OllamaApi(ollamaBaseUrl);

      OllamaOptions ollamaOptions = OllamaOptions.create()
          .withModel(OllamaModel.LLAMA3_1)
          .withTemperature(.9);

//      FunctionCallbackContext functionCallbackContext = new FunctionCallbackContext();
//      List<FunctionCallback> toolFunctionCallbacks = new ArrayList<>();
//      ObservationRegistry observationRegistry = new ObservationRegistry();
//      ModelManagementOptions modelManagementOptions = new ModelManagementOptions();

      // 假设OllamaChatModel需要一个OllamaApiClient实例来创建
      ollamaChatModel = new OllamaChatModel(
        ollamaApi,
        ollamaOptions,
        new FunctionCallbackContext(),
        Collections.emptyList(),
        ObservationRegistry.create(),
        ModelManagementOptions.builder().build());
    }
    return ollamaChatModel;
  }
}
