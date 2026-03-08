package com.bank.rag.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("${spring.ai.openai.chat.options.temperature}")
    private double temperature;

    @Autowired
    public RagService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @jakarta.annotation.PostConstruct
    public void autoIngest() {
        try {
            org.springframework.core.io.support.PathMatchingResourcePatternResolver resolver = new org.springframework.core.io.support.PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:data/*.md");
            for (Resource resource : resources) {
                org.springframework.ai.reader.TextReader reader = new org.springframework.ai.reader.TextReader(resource);
                reader.getCustomMetadata().put("filename", resource.getFilename());
                List<Document> documents = reader.get();
                TokenTextSplitter splitter = new TokenTextSplitter();
                List<Document> splittedDocs = splitter.apply(documents);
                vectorStore.add(splittedDocs);
                System.out.println("Auto-Ingested: " + resource.getFilename());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Manual Ingestion Logic (Legacy / CLI)
    public void ingestDocs(String folderPath) {
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            List<File> files = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".md"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            System.out.println("Found " + files.size() + " markdown files.");

            for (File file : files) {
                Resource resource = new FileSystemResource(file);
                // Use TextReader for markdown/text files
                org.springframework.ai.reader.TextReader reader = new org.springframework.ai.reader.TextReader(
                        resource);
                reader.getCustomMetadata().put("filename", file.getName());

                List<Document> documents = reader.get();

                // Chunking logic (Token based splitter)
                TokenTextSplitter splitter = new TokenTextSplitter();
                List<Document> splittedDocs = splitter.apply(documents);

                // Store to Vector DB
                vectorStore.add(splittedDocs);
                System.out.println("Ingested: " + file.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Future: Agentic Implementation
    // To add agents (e.g. for external data like stock prices or account details),
    // register Functions here or use the Spring AI 'FunctionCallback' API.
    // Example: chatClient.call(prompt, functionCallbacks);

    // RAG Retrieval & Generation
    public String generateAnswer(String query) {
        // 1. Retrieve similar documents
        List<Document> similarDocuments = vectorStore.similaritySearch(query);
        String context = similarDocuments.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n"));

        // 2. Construct System Prompt
        String systemText = """
                You are a helpful banking assistant.
                You have access to a general RAG context for general banking questions, and you have access to a 'getCustomerDetails' function to lookup a user's account details (balances, types, number, info) if they provide their First Name, Last Name, and the last 4 digits of their SSN.

                If the user asks for personal account information over chat, use the function tool to fetch it and report it conversationally, DO NOT just dump the raw text.

                Context for general queries:
                {context}
                """;

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("context", context));

        // 3. call Chat Client
        Message userMessage = new UserMessage(query);

        org.springframework.ai.openai.OpenAiChatOptions options = org.springframework.ai.openai.OpenAiChatOptions.builder()
                .withFunction("getCustomerDetails")
                .build();

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

        // Note: Temperature should be configured in properties or via ChatOptions here
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
