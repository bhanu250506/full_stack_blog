package com.example.demo.controller;


import com.example.demo.dto.AiRequest;
import com.example.demo.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final GeminiService geminiService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateContent(@RequestBody AiRequest aiRequest) {
        // We add .block() to wait for the response
        String generatedText = geminiService.generateContent(aiRequest.getPrompt()).block();
        return ResponseEntity.ok(Map.of("response", generatedText));
    }


    @PostMapping("/correct-grammar")
    public ResponseEntity<Map<String, String>> correctGrammar(@RequestBody AiRequest aiRequest) {
        String grammarPrompt = "Correct the grammar of the following text. Only return the corrected text, with no other explanation: "
                + aiRequest.getPrompt();

        // We add .block() to wait for the response
        String generatedText = geminiService.generateContent(grammarPrompt).block();
        return ResponseEntity.ok(Map.of("response", generatedText));
    }


    @PostMapping("/expand-summary")
    public ResponseEntity<Map<String, String>> expandSummary(@RequestBody AiRequest aiRequest) {
        String expandPrompt = "Expand the following summary points into a full, engaging blog post. Format it nicely: "
                + aiRequest.getPrompt();

        // We add .block() to wait for the response
        String generatedText = geminiService.generateContent(expandPrompt).block();
        return ResponseEntity.ok(Map.of("response", generatedText));
    }
    @PostMapping("/write-blog")
    public ResponseEntity<Map<String, String>> writeBlogFromTopic(@RequestBody AiRequest aiRequest) {
        String blogPrompt = "Write a high-quality, engaging, and well-structured blog post on the following topic. " +
                "Enhance the content, use clear headings, and provide a compelling introduction and conclusion. " +
                "The topic is: " + aiRequest.getPrompt();

        // We add .block() to wait for the response
        String generatedText = geminiService.generateContent(blogPrompt).block();
        return ResponseEntity.ok(Map.of("response", generatedText));
    }
}
