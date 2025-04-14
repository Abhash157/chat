package com.example.chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ollama")
public class OllamaController {

    private final OllamaService ollamaService;

    public OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generate(@RequestBody GenerateRequest request) {
        if (request.model() == null || request.model().isBlank()) {
            return ResponseEntity.badRequest().body("Model cannot be empty");
        }
        if (request.prompt() == null || request.prompt().isBlank()) {
            return ResponseEntity.badRequest().body("Prompt cannot be empty");
        }

        try {
            StringBuilder responseText = new StringBuilder();
            URL url = new URL("http://localhost:11434/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            // JSON payload
            String body = String.format("{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": true}",
                    request.model(), request.prompt());
            conn.getOutputStream().write(body.getBytes());

            // Reading the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        JSONObject json = new JSONObject(line);
                        if (json.has("response")) {
                            responseText.append(json.getString("response"));
                        }
                    } catch (Exception e) {
                        System.err.println("Skipping invalid JSON line: " + line);
                    }
                }
            }
            reader.close();

            return ResponseEntity.ok(responseText.toString().trim());

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error processing request: " + e.getMessage());
        }
    }

    public record GenerateRequest(String model, String prompt) {}
}
