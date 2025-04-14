package com.example.chat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {
    private final RestTemplate restTemplate;

    @Value("${ollama.url:http://localhost:11434}") // Default value if not configured
    private String ollamaUrl;

    public OllamaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generate(String model, String prompt) {
        try {
            String url = ensureValidUrl(ollamaUrl) + "/api/generate";

            Map<String, Object> request = new HashMap<>();
            request.put("model", model);
            request.put("prompt", prompt);
            request.put("stream", false);

            return restTemplate.postForObject(url, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate response from Ollama: " + e.getMessage(), e);
        }
    }

    private String ensureValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Ollama URL cannot be null or empty");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }
        return url;
    }
}