package com.bank.rag.model;

public class ChatMessage {
    private String content;
    private String role; // "user" or "assistant"

    public ChatMessage() {
    }

    public ChatMessage(String content, String role) {
        this.content = content;
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
