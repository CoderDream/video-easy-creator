package com.coderdream.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.ollama.OllamaChatModel;
import io.micrometer.observation.ObservationRegistry;
import java.util.Collections;

/**
 * Configuration class for Ollama integration.
 * Sets up the local LLM with specific parameters and functions.
 */
@Configuration
public class OllamaConfig {

    /**
     * Creates the Ollama API client configured for local access.
     *
     * @return OllamaApi instance configured for localhost
     */
    @Bean
    public OllamaApi ollamaApi() {
        return new OllamaApi("http://localhost:11434");
    }

    /**
     * Configures Ollama model options with specific model and temperature settings.
     *
     * @return OllamaOptions configured for llama3.2-vision
     */
    @Bean
    public OllamaOptions ollamaOptions() {
        return OllamaOptions.create()
                .withModel(OllamaModel.LLAMA3_1)
                .withTemperature(.9);
    }

    /**
     * Creates the Ollama chat model with all required dependencies.
     *
     * @param ollamaApi API client for Ollama
     * @param ollamaOptions Model configuration options
     * @return ChatModel instance for Ollama interaction
     */
    @Bean("Ollama")
    public ChatModel ollamaChatModel(OllamaApi ollamaApi,
                                     OllamaOptions ollamaOptions) {
        return new OllamaChatModel(
                ollamaApi,
                ollamaOptions,
                new FunctionCallbackContext(),
                Collections.emptyList(),
                ObservationRegistry.create(),
                ModelManagementOptions.builder().build());
    }
}
