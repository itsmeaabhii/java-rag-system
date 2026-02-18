# Java RAG System - Spring Boot

A complete **Retrieval Augmented Generation (RAG)** system built with Java, Spring Boot, Apache Lucene, and Ollama.

## 🎯 Features

- **PDF Upload & Processing**: Upload PDF documents and extract text
- **Text Chunking**: Intelligent text splitting with configurable overlap
- **Vector Embeddings**: Generate embeddings using Ollama
- **Semantic Search**: Apache Lucene-based vector similarity search
- **RAG Pipeline**: Complete retrieval-augmented generation workflow
- **REST API**: Clean, documented REST endpoints
- **Health Monitoring**: Comprehensive health checks with system metrics
- **Performance Tracking**: Request timing and performance logging
- **CORS Support**: Frontend integration ready
- **Modern UI**: CogniChat - Beautiful, responsive web interface with dark mode support

## 🛠️ Tech Stack

- **Java 17+**
- **Spring Boot 3.2.1**
- **Apache Lucene 9.9.1** (Vector Search)
- **Apache PDFBox 3.0.1** (PDF Processing)
- **Ollama** (Embeddings & LLM)
- **Maven** (Build Tool)

## 📋 Prerequisites

1. **Java 17 or higher**
   ```bash
   java -version
   ```

2. **Maven**
   ```bash
   mvn -version
   ```

3. **Ollama** (Running locally)
   ```bash
   # Install Ollama: https://ollama.ai
   ollama pull nomic-embed-text
   ollama pull llama3
   ```

## 🚀 Quick Start

### 1. Clone/Navigate to Project
```bash
cd "/Users/abhishek/Desktop/java congichat"
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The server will start at `http://localhost:8080/api`

## 📡 API Endpoints

### Document Management

#### Upload PDF
```bash
POST /api/documents/upload
Content-Type: multipart/form-data

# Example with curl:
curl -X POST http://localhost:8080/api/documents/upload \
  -F "file=@/path/to/document.pdf"
```

**Response:**
```json
{
  "documentId": "uuid",
  "documentName": "document.pdf",
  "chunksCreated": 42,
  "status": "SUCCESS",
  "message": "Document processed and indexed successfully",
  "processingTimeMs": 5432
}
```

#### Get System Status
```bash
GET /api/documents/status

# Example:
curl http://localhost:8080/api/documents/status
```

#### Delete Document
```bash
DELETE /api/documents/{documentName}

# Example:
curl -X DELETE http://localhost:8080/api/documents/mydoc.pdf
```

### Query Operations

#### RAG Query (Retrieval + Generation)
```bash
POST /api/query
Content-Type: application/json

{
  "query": "What is the main topic of the document?",
  "topK": 5,
  "documentName": null
}
```

**Response:**
```json
{
  "answer": "The main topic is...",
  "query": "What is the main topic?",
  "retrievedChunks": [
    {
      "text": "chunk content...",
      "documentName": "doc.pdf",
      "score": 0.89,
      "chunkIndex": 0
    }
  ],
  "responseTimeMs": 2341
}
```

#### Semantic Search (No LLM)
```bash
POST /api/query/search
Content-Type: application/json

{
  "query": "machine learning",
  "topK": 5
}
```

#### Health Check
```bash
GET /api/query/health
```

## ⚙️ Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server
server.port=8080

# File Upload
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# Ollama Configuration
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

# Data Directories
app.upload.dir=./data/uploads
app.index.dir=./data/lucene-index
```

## 📁 Project Structure

```
src/main/java/com/ragchat/
├── RagApplication.java              # Main Spring Boot application
├── config/
│   ├── AppConfig.java              # Application configuration
│   └── CorsConfig.java             # CORS settings
├── controller/
│   ├── DocumentController.java     # Document upload/management
│   └── QueryController.java        # Query/search endpoints
├── service/
│   ├── PDFProcessorService.java    # PDF text extraction
│   ├── ChunkingService.java        # Text chunking
│   ├── OllamaService.java          # Ollama API client
│   ├── VectorStoreService.java     # Lucene vector search
│   ├── RAGService.java             # RAG pipeline orchestration
│   └── DocumentService.java        # Document processing
├── model/
│   ├── Chunk.java                  # Text chunk model
│   └── Document.java               # Document model
├── dto/
│   ├── QueryRequest.java
│   ├── QueryResponse.java
│   ├── UploadResponse.java
│   └── Ollama*.java                # Ollama API DTOs
└── exception/
    └── GlobalExceptionHandler.java # Error handling
```

## 🔧 How It Works

### RAG Pipeline Flow

1. **PDF Upload**
   - User uploads PDF via REST API
   - File is saved to local storage
   - Text is extracted using Apache PDFBox

2. **Text Chunking**
   - Extracted text is split into chunks (default: 500 chars)
   - Chunks have configurable overlap (default: 100 chars)
   - Smart boundary detection (sentences, words)

3. **Embedding Generation**
   - Each chunk is sent to Ollama
   - Ollama generates embedding vectors (768-dim)
   - Embeddings are stored with chunk metadata

4. **Vector Indexing**
   - Lucene indexes chunks with their embeddings
   - Metadata includes: chunk ID, document name, text, index

5. **Query Processing**
   - User submits a question
   - Question is converted to embedding vector
   - Lucene performs cosine similarity search
   - Top-K most similar chunks are retrieved

6. **Answer Generation**
   - Retrieved chunks form the context
   - Prompt is constructed with context + question
   - Ollama generates the final answer
   - Answer is returned to user

## 🧪 Testing

### Test Document Upload
```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -F "file=@test.pdf"
```

### Test Query
```bash
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d '{
    "query": "What are the key findings?",
    "topK": 5
  }'
```

### Test Health
```bash
curl http://localhost:8080/api/query/health
```

## 📊 Performance Tips

1. **Adjust Chunk Size**: Smaller chunks = more precise, larger chunks = more context
2. **Tune Top-K**: More chunks = better context but slower generation
3. **Ollama Models**: 
   - `nomic-embed-text`: Fast embeddings (768-dim)
   - `llama3`: Balanced quality/speed
   - `llama3:70b`: Higher quality (requires more resources)

## 🐛 Troubleshooting

### Ollama Not Running
```bash
# Start Ollama
ollama serve

# Pull required models
ollama pull nomic-embed-text
ollama pull llama3
```

### Port Already in Use
Change port in `application.properties`:
```properties
server.port=8081
```

### File Size Limit
Increase in `application.properties`:
```properties
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
```

### Lucene Index Corruption
```bash
# Delete and rebuild index
rm -rf ./data/lucene-index
# Restart application and re-upload documents
```

## 📝 API Documentation

Complete API documentation is available via the endpoints themselves. Use tools like:
- **Postman**: Import the API collection
- **Swagger/OpenAPI**: (Can be added if needed)
- **curl**: Examples provided above

## 🎨 Web Interface (CogniChat)

The project includes a modern, responsive web interface for interacting with the RAG system.

### Features
- **Modern Design**: Clean, professional interface with gradient effects
- **Dark Mode**: Automatic theme switching with localStorage persistence
- **Drag & Drop**: Easy file upload with visual feedback
- **Real-time Feedback**: Toast notifications for all actions
- **Workflow Steps**: Visual progress indicator (Upload → Query → Results)
- **Responsive**: Works seamlessly on desktop, tablet, and mobile devices

### Access the UI
1. Start the backend server:
   ```bash
   mvn spring-boot:run
   ```

2. Open the interface:
   ```bash
   # Open in browser
   open frontend/one.html
   ```

### UI Files
- `frontend/one.html` - Modern HTML structure
- `frontend/one.css` - Comprehensive design system with dark mode
- `frontend/one.js` - Interactive functionality and API integration

## 🔐 Security Notes

⚠️ **This is a development version.** For production:

1. Add authentication/authorization
2. Validate and sanitize all inputs
3. Implement rate limiting
4. Use HTTPS
5. Secure file storage
6. Add request logging
7. Configure CORS properly (not `*`)

## 🚀 Deployment

### Build JAR
```bash
mvn clean package
```

### Run JAR
```bash
java -jar target/java-rag-system-1.0.0.jar
```

### Docker (Optional)
Create a `Dockerfile`:
```dockerfile
FROM openjdk:17-slim
COPY target/java-rag-system-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Apache Lucene](https://lucene.apache.org/)
- [Apache PDFBox](https://pdfbox.apache.org/)
- [Ollama Documentation](https://ollama.ai/docs)

## 📄 License

This project is provided as-is for educational and development purposes.

## 🤝 Contributing

Feel free to enhance and extend this RAG system based on your needs!

---

**Built with ❤️ using Java, Spring Boot, and Ollama**
