# ARCHITECTURE.md - Java RAG System

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                         Frontend (HTML/JS)                   │
│                    (Optional - User Interface)               │
└────────────────────────────┬────────────────────────────────┘
                             │ HTTP REST APIs
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                     Spring Boot Backend                      │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              REST Controllers Layer                   │  │
│  │  ┌─────────────────┐    ┌──────────────────┐        │  │
│  │  │DocumentController│    │ QueryController  │        │  │
│  │  └─────────────────┘    └──────────────────┘        │  │
│  └──────────────────────────────────────────────────────┘  │
│                             │                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Service Layer                            │  │
│  │  ┌────────────────┐  ┌──────────────┐               │  │
│  │  │DocumentService │  │  RAGService   │               │  │
│  │  └────────────────┘  └──────────────┘               │  │
│  │         │                    │                        │  │
│  │  ┌──────▼───────┐  ┌────────▼──────┐               │  │
│  │  │PDFProcessor  │  │OllamaService  │               │  │
│  │  │   Service    │  │               │               │  │
│  │  └──────────────┘  └───────────────┘               │  │
│  │         │                    │                        │  │
│  │  ┌──────▼───────┐  ┌────────▼──────┐               │  │
│  │  │  Chunking    │  │ VectorStore   │               │  │
│  │  │   Service    │  │   Service     │               │  │
│  │  └──────────────┘  └───────────────┘               │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
          ▼                  ▼                  ▼
┌──────────────────┐  ┌─────────────┐  ┌──────────────┐
│  Apache PDFBox   │  │   Ollama    │  │Apache Lucene │
│  (PDF Extract)   │  │(Embed & LLM)│  │(Vector Store)│
└──────────────────┘  └─────────────┘  └──────────────┘
          │                  │                  │
          ▼                  ▼                  ▼
    ┌─────────┐      ┌────────────┐    ┌──────────────┐
    │  PDF    │      │   Ollama   │    │    Lucene    │
    │  Files  │      │   Models   │    │    Index     │
    └─────────┘      └────────────┘    └──────────────┘
```

## Component Details

### 1. **REST Controllers**

#### DocumentController
- **Purpose**: Handle document upload and management
- **Endpoints**:
  - `POST /api/documents/upload` - Upload PDF
  - `DELETE /api/documents/{name}` - Delete document
  - `GET /api/documents/status` - Get system stats
- **Responsibilities**:
  - Validate file uploads
  - Delegate to DocumentService
  - Return appropriate HTTP responses

#### QueryController
- **Purpose**: Handle RAG queries and searches
- **Endpoints**:
  - `POST /api/query` - Full RAG query
  - `POST /api/query/search` - Semantic search only
  - `GET /api/query/health` - Health check
- **Responsibilities**:
  - Validate query requests
  - Delegate to RAGService
  - Format responses

### 2. **Service Layer**

#### DocumentService
- **Purpose**: Orchestrate document processing pipeline
- **Flow**:
  1. Save uploaded file
  2. Validate PDF
  3. Extract text (via PDFProcessorService)
  4. Chunk text (via ChunkingService)
  5. Generate embeddings (via OllamaService)
  6. Index chunks (via VectorStoreService)
- **Key Methods**:
  - `processDocument(MultipartFile)`
  - `deleteDocument(String)`
  - `getTotalIndexedChunks()`

#### RAGService
- **Purpose**: Implement RAG pipeline
- **Flow**:
  1. Generate query embedding
  2. Retrieve similar chunks
  3. Construct prompt with context
  4. Generate answer via LLM
  5. Return formatted response
- **Key Methods**:
  - `processQuery(String, Integer, String)`
  - `semanticSearch(String, Integer, String)`
  - `constructPrompt(String, List<ScoredChunk>)`

#### PDFProcessorService
- **Purpose**: Extract text from PDF files
- **Technology**: Apache PDFBox
- **Key Methods**:
  - `extractText(File)` - Extract all text
  - `isValidPDF(File)` - Validate PDF

#### ChunkingService
- **Purpose**: Split text into manageable chunks
- **Algorithm**:
  - Fixed-size chunks (default: 500 chars)
  - Overlapping chunks (default: 100 chars)
  - Smart boundary detection (sentences, words)
- **Key Methods**:
  - `chunkText(String, String)`
  - `getChunkingInfo()`

#### OllamaService
- **Purpose**: Interface with Ollama API
- **Endpoints Used**:
  - `/api/embeddings` - Generate embeddings
  - `/api/generate` - Generate text
- **Key Methods**:
  - `generateEmbedding(String)` - Vector embedding
  - `generateResponse(String)` - LLM generation
  - `isAvailable()` - Health check

#### VectorStoreService
- **Purpose**: Store and search vector embeddings
- **Technology**: Apache Lucene
- **Features**:
  - Cosine similarity search
  - Metadata storage (document name, chunk index, etc.)
  - Top-K retrieval
- **Key Methods**:
  - `indexChunk(Chunk)` - Add to index
  - `searchSimilar(float[], int, String)` - Search
  - `deleteByDocument(String)` - Remove document
  - `getTotalChunks()` - Count indexed chunks

### 3. **Data Models**

#### Chunk
```java
{
  String id;                // Unique identifier
  String documentName;      // Source document
  String text;              // Chunk content
  int chunkIndex;           // Position in document
  float[] embedding;        // Vector embedding
  long timestamp;           // Creation time
}
```

#### Document
```java
{
  String id;                // Document ID
  String name;              // File name
  String path;              // File system path
  long size;                // File size
  long uploadTimestamp;     // Upload time
  int totalChunks;          // Number of chunks
  String status;            // Processing status
  List<String> chunkIds;    // Associated chunks
}
```

### 4. **Data Transfer Objects (DTOs)**

#### QueryRequest
```java
{
  String query;             // User question
  Integer topK;             // Number of chunks to retrieve
  String documentName;      // Optional document filter
}
```

#### QueryResponse
```java
{
  String answer;            // LLM generated answer
  String query;             // Original query
  List<RetrievedChunk> retrievedChunks;  // Context used
  long responseTimeMs;      // Processing time
}
```

#### UploadResponse
```java
{
  String documentId;        // Generated ID
  String documentName;      // File name
  int chunksCreated;        // Number of chunks
  String status;            // SUCCESS/FAILED
  String message;           // Status message
  long processingTimeMs;    // Processing time
}
```

## Data Flow

### Upload Flow
```
1. User uploads PDF
   ↓
2. DocumentController validates file
   ↓
3. DocumentService saves file
   ↓
4. PDFProcessorService extracts text
   ↓
5. ChunkingService splits into chunks
   ↓
6. For each chunk:
   a. OllamaService generates embedding
   b. VectorStoreService indexes chunk
   ↓
7. Return upload response to user
```

### Query Flow
```
1. User submits question
   ↓
2. QueryController validates request
   ↓
3. RAGService starts processing
   ↓
4. OllamaService generates query embedding
   ↓
5. VectorStoreService searches similar chunks
   ↓
6. RAGService constructs prompt:
   - System instruction
   - Retrieved chunks (context)
   - User question
   ↓
7. OllamaService generates answer
   ↓
8. Return QueryResponse with:
   - Generated answer
   - Retrieved chunks
   - Processing time
```

## Technology Stack Details

### Spring Boot 3.2.1
- **Why**: Modern, production-ready framework
- **Features Used**:
  - Spring Web (REST APIs)
  - Spring Validation (Input validation)
  - Spring WebFlux (Reactive HTTP client for Ollama)
  - Dependency Injection
  - Auto-configuration

### Apache Lucene 9.9.1
- **Why**: Battle-tested full-text search engine
- **Features Used**:
  - Document indexing
  - Binary field storage (for vectors)
  - Custom similarity calculation
  - SearcherManager for thread-safe searches

### Apache PDFBox 3.0.1
- **Why**: Pure Java PDF library
- **Features Used**:
  - Text extraction
  - PDF validation
  - Position-based text sorting

### Ollama
- **Why**: Local LLM inference
- **Models Used**:
  - `nomic-embed-text` - 768-dim embeddings
  - `llama3` - Text generation
- **Advantages**:
  - No API keys needed
  - Privacy (local processing)
  - No usage limits

## Scalability Considerations

### Current Design
- Single-node deployment
- In-memory Lucene index
- Synchronous processing
- Local file storage

### Potential Improvements

1. **Distributed Processing**
   - Use Kafka for async document processing
   - Separate embedding generation workers
   - Queue-based job processing

2. **Database Integration**
   - PostgreSQL with pgvector
   - Store document metadata
   - Track processing status

3. **Caching**
   - Redis for query results
   - Cache embeddings
   - Store frequent queries

4. **Horizontal Scaling**
   - Stateless application design
   - Shared file storage (S3, NFS)
   - Clustered Lucene (Solr/Elasticsearch)

5. **Performance Optimization**
   - Batch embedding generation
   - Parallel chunk processing
   - Connection pooling
   - Async API responses

## Security Considerations

### Current State (Development)
- No authentication
- Open CORS policy
- Local file storage
- No input sanitization beyond validation

### Production Requirements
1. **Authentication & Authorization**
   - JWT tokens
   - User-specific document access
   - Role-based access control

2. **Input Validation**
   - File type verification
   - Size limits enforcement
   - Content scanning
   - SQL injection prevention

3. **Data Protection**
   - Encrypted file storage
   - Secure document deletion
   - Audit logging
   - HTTPS only

4. **Rate Limiting**
   - Request throttling
   - Concurrent upload limits
   - Query rate limits

## Configuration

### Key Settings

```properties
# Server
server.port=8080

# File Upload
spring.servlet.multipart.max-file-size=50MB

# Ollama
ollama.base-url=http://localhost:11434
ollama.embedding-model=nomic-embed-text
ollama.chat-model=llama3
ollama.timeout=120

# Chunking
chunking.size=500
chunking.overlap=100

# Vector Search
vector.top-k=5
vector.dimension=768
```

## Monitoring and Logging

### Logging Strategy
- **INFO**: High-level operations (uploads, queries)
- **DEBUG**: Detailed processing steps
- **ERROR**: Failures and exceptions

### Metrics to Track
- Document upload count
- Average processing time
- Query latency
- Embedding generation time
- Index size
- Error rates

### Health Checks
- `/api/query/health` - Application health
- `/api/documents/status` - System statistics
- Ollama availability check

## Future Enhancements

1. **Multi-format Support**
   - Word documents (.docx)
   - Text files (.txt)
   - HTML content

2. **Advanced Search**
   - Filters (date, document type)
   - Hybrid search (keyword + semantic)
   - Relevance tuning

3. **Improved Chunking**
   - Semantic chunking
   - Paragraph-aware splitting
   - Document structure preservation

4. **Chat History**
   - Conversation context
   - Follow-up questions
   - Session management

5. **Admin Interface**
   - Document management UI
   - Index statistics
   - Configuration management
