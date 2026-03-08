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
        String answer = ragService.generateAnswer(input.getContent());
        return ResponseEntity.ok(new ChatMessage(answer, "assistant"));
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingestData(@RequestParam String path) {
        ragService.ingestDocs(path);
        return ResponseEntity.ok("Ingestion started for path: " + path);
    }
}
