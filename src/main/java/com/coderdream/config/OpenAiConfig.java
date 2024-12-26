package com.coderdream.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.ai.openai")
public class OpenAiConfig {

    private String apiKey;
    private String baseUrl;
    private ChatOptions chat;

    public static class ChatOptions {
        private String model;
        private float temperature;

        // getters 和 setters
        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public float getTemperature() {
            return temperature;
        }

        public void setTemperature(float temperature) {
            this.temperature = temperature;
        }
    }
}
