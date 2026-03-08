package com.bank.rag.controller;

import com.bank.rag.model.ChatMessage;
import com.bank.rag.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // For demo purposes allow all CORS
public class ChatController {

    @Autowired
    private RagService ragService;

    @PostMapping("/ask")
    public ResponseEntity<ChatMessage> askQuestion(@RequestBody ChatMessage input) {
        try {
            String answer = ragService.generateAnswer(input.getContent());
            return ResponseEntity.ok(new ChatMessage(answer, "assistant"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ChatMessage("I'm sorry, I'm experiencing an issue with the AI Service. Please check if the OPENAI_API_KEY environment variable is configured correctly on Render.", "assistant"));
        }
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingestData(@RequestParam String path) {
        ragService.ingestDocs(path);
        return ResponseEntity.ok("Ingestion started for path: " + path);
    }
}
