package com.coderdream.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StaticConfigUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    public static String getApiKey() {
        Environment env = applicationContext.getEnvironment();
        return env.getProperty("spring.ai.openai.api-key");
    }

    public static String getBaseUrl() {
        Environment env = applicationContext.getEnvironment();
        return env.getProperty("spring.ai.openai.base-url");
    }
}
