package com.bank.rag.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RagServiceTest {

    private ChatClient chatClient;
    private MockVectorStore vectorStore;
    private RagService ragService;

    @BeforeEach
    void setUp() {
        vectorStore = new MockVectorStore();

        chatClient = new ChatClient() {
            @Override
            public ChatResponse call(Prompt prompt) {
                Generation generation = new Generation("The interest rate is 5%.");
                return new ChatResponse(List.of(generation));
            }
        };

        ragService = new RagService(chatClient, vectorStore);
        ReflectionTestUtils.setField(ragService, "temperature", 0.0);
    }

    @Test
    void testGenerateAnswer() {
        String answer = ragService.generateAnswer("What is the rate?");

        assertEquals("The interest rate is 5%.", answer);
        assertTrue(vectorStore.searchCalled);
    }

    // Manual Stub for VectorStore
    static class MockVectorStore implements VectorStore {
        boolean searchCalled = false;

        @Override
        public void add(List<Document> documents) {
        }

        @Override
        public Optional<Boolean> delete(List<String> idList) {
            return Optional.of(true);
        }

        @Override
        public List<Document> similaritySearch(String query) {
            searchCalled = true;
            return List.of(new Document("Bank interest rate is 5%"));
        }

        @Override
        public List<Document> similaritySearch(org.springframework.ai.vectorstore.SearchRequest request) {
            searchCalled = true;
            return List.of(new Document("Bank interest rate is 5%"));
        }
    }
}
