package com.example.demo.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final String geminiApiKey;

    // FIX 1: Pass the properties from application.properties directly into the constructor.
    // Spring will ensure these values are loaded BEFORE the constructor is called.
    public GeminiService(WebClient.Builder webClientBuilder,
                         @Value("${gemini.api.url}") String geminiApiUrl,
                         @Value("${gemini.api.key}") String geminiApiKey) {

        // Now, geminiApiUrl is guaranteed to have a value here.
        this.webClient = webClientBuilder.baseUrl(geminiApiUrl).build();
        this.geminiApiKey = geminiApiKey;
    }

    public Mono<String> generateContent(String textPrompt) {
        // Build the Gemini API request body
        JSONObject content = new JSONObject()
                .put("parts", new JSONArray().put(new JSONObject().put("text", textPrompt)));

        JSONObject requestBody = new JSONObject()
                .put("contents", new JSONArray().put(content));

        // Make an API call
        return webClient.post()
                // FIX 2: A cleaner way to add the query parameter to the base URL
                .uri("?key={apiKey}", this.geminiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody.toString())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseResponse);
    }

    // Your parseResponse method is good, but I've made it slightly more robust
    private String parseResponse(String responseBody) {
        try {
            JSONObject response = new JSONObject(responseBody);

            // Check for an error field from the API first
            if (response.has("error")) {
                return "API Error: " + response.getJSONObject("error").getString("message");
            }

            JSONArray candidates = response.getJSONArray("candidates");
            JSONObject firstCandidate = candidates.getJSONObject(0);
            JSONObject content = firstCandidate.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            JSONObject firstPart = parts.getJSONObject(0);
            return firstPart.getString("text");
        } catch (Exception e) {
            // This can help you see the raw response if parsing fails
            return "Error parsing AI response: " + e.getMessage() + ". Raw Response: " + responseBody;
        }
    }
}