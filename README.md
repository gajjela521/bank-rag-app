# Bank RAG Chatbot Application

This project implements a Retrieval-Augmented Generation (RAG) chatbot for a banking application. It uses **Spring Boot** for the backend logic and integration, and a **React** frontend for the customer chat interface.

## Architecture

1.  **Backend (`bank-rag-backend`)**:
    *   **Framework**: Spring Boot 3.2+
    *   **Database**: DB2 (Placeholder config provided), VectorStore (e.g., PGVector or Simple).
    *   **AI Integration**: Spring AI for interfacing with LLMs (OpenAI, Azure OpenAI) and Vector Databases.
    *   **RAG Flow**:
        *   **Ingestion**: Scrapes/Reads Markdown files (simulation of 100k+ documents).
        *   **Chunking**: Splits documents into manageble tokens.
        *   **Embedding**: Converts chunks to vector embeddings.
        *   **Retrieval**: Finds relevant chunks for user queries.
        *   **Generation**: Uses LLM (Temperature 0.0) to generate precise answers based *only* on the context.

2.  **Frontend (`bank-rag-frontend`)**:
    *   **Framework**: React + Vite + TypeScript.
    *   **Styling**: Custom CSS (Dark Theme, Glassmorphism).
    *   **Communication**: REST API to Backend.

## Directory Structure

```
bank-rag-app/
├── bank-rag-backend/       # Spring Boot Application
│   ├── src/main/java/      # Source Code (Controller, Service, Model)
│   └── src/main/resources/ # Configuration
├── bank-rag-frontend/      # React Application
│   ├── src/                # UI Components and Logic
│   └── index.css           # Styling
└── data/                   # Sample Bank Data (Markdown files)
```

## Setup & Running

### Prerequisites
*   Java 17+
*   Node.js 18+
*   OpenAI API Key (or alternative LLM provider key)
*   DB2 instance (optional for demo, defaults to placeholder)

### 1. Backend Setup
1.  Navigate to `bank-rag-backend`.
2.  Update `src/main/resources/application.properties` with your API Key:
    ```properties
    spring.ai.openai.api-key=sk-your-key-here
    ```
    (Note: Temperature is set to 0.0 for precise answers).
3.  Run the application:
    ```bash
    ./mvnw spring-boot:run
    ```
    The server will start on `http://localhost:8080`.

### 2. Frontend Setup
1.  Navigate to `bank-rag-frontend`.
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Start the development server:
    ```bash
    npm run dev
    ```
4.  Open the URL shown (usually `http://localhost:5173`).

### 3. Ingesting Data
To populate the RAG system with the bank data:
1.  Ensure backend is running.
2.  Trigger ingestion via API (or curl):
    ```bash
    curl -X POST "http://localhost:8080/api/chat/ingest?path=/absolute/path/to/bank-rag-app/data"
    ```
    *Note: For the 100k+ file requirement, the `RagService` uses a stream-based walker which is efficient, but embedding generation will take time.*

## Future Roadmap: Agentic AI
The current implementation handles "General Data" via RAG.
For the "Agentic AI" requirement (fetching external data from different services):
*   Logic is placed in `RagService.java` (see comments).
*   Use `FunctionCallback` in Spring AI to register tools (e.g., `getAccountBalance`, `checkStockPrice`).
*   The LLM will automatically decide when to call these tools based on the user query.
