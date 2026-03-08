# API Documentation & Service Registry

## Overview
This document outlines the services and REST API endpoints available in the Bank RAG Application. The system combines a traditional Banking Service (customer data) with an AI-Augmented RAG Service (chatbot).

---

## Services

### 1. RagService (`com.bank.rag.service.RagService`)
**Purpose**: Handles the Retrieval-Augmented Generation flow.
*   **Ingestion**: Recursively scans a given directory path for `.md` files. It supports large datasets (tested with streamed simulated files).
*   **Chunking**: Uses `TokenTextSplitter` to break documents into segments safe for LLM context windows.
*   **Vector Storage**: Stores embeddings in the configured `VectorStore` (defaulting to configured bean, e.g., PGVector).
*   **Generation**: Constructs a prompt with retrieved context and queries the AI Model (Temperature: 0.0).

### 2. BankingService (`com.bank.rag.service.BankingService`)
**Purpose**: Manages core banking entities (Customers, Accounts).
*   **Persistence**: Uses `CustomerRepository` (JPA) implementation.
*   **Operations**: CRUD operations for Customers and linked Accounts.
*   **Data Seeding**: Helper method to populate initial demo data.

---

## REST API Endpoints

### Chat / RAG
| Method | Endpoint | Description | Request Body | Response |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/chat/ask` | Ask the banking assistant a question. | `{"content": "query", "role": "user"}` | `{"content": "answer", ...}` |
| `POST` | `/api/chat/ingest` | Trigger data ingestion from local path. | Query Param: `path=/path/to/data` | Success Message |

### Banking Operations
| Method | Endpoint | Description | Request Body | Response |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/banking/customers` | Retrieve all customers. | N/A | List of Customers |
| `GET` | `/api/banking/customers/{id}` | Retrieve specific customer by ID. | N/A | Customer Object |
| `POST` | `/api/banking/customers` | Create a new customer/account. | Customer JSON | Created Customer |
| `POST` | `/api/banking/seed` | Seed database with sample data. | N/A | Success Message |

---

## Data Models

### Customer
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phoneNumber": "555-0100",
  "accounts": [...]
}
```

### Account
```json
{
  "id": 101,
  "accountNumber": "chk_123456",
  "accountType": "CHECKING",
  "balance": 1500.00
}
```

## Setup for 100k+ Files
To handle the scale of 100,000 Markdown files:
1.  Ensure the `path` param in `/ingest` points to the root directory containing the files.
2.  The `RagService` streams file paths to avoid loading all into memory at once.
3.  **Note**: Ingestion of 100k files will take significant time depending on the Embedding Model API rate limits (e.g., OpenAI).
