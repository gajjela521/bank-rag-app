package com.bank.rag.controller;

import com.bank.rag.model.ChatMessage;
import com.bank.rag.service.RagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RagService ragService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAskQuestion() throws Exception {
        ChatMessage input = new ChatMessage("Hello", "user");
        given(ragService.generateAnswer("Hello")).willReturn("Hi there!");

        mockMvc.perform(post("/api/chat/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hi there!"))
                .andExpect(jsonPath("$.role").value("assistant"));
    }
}
