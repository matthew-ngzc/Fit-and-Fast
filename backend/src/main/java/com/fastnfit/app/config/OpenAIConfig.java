package com.fastnfit.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenAIConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    public String getOpenaiApiKey() {
        return openaiApiKey;
    }
}