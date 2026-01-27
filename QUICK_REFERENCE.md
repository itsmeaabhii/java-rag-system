# Quick Reference Guide - Java RAG System

## 🚀 Quick Commands

### Start the Application
```bash
./run.sh
# OR
mvn spring-boot:run
```

### Test Endpoints
```bash
# Health check
curl http://localhost:8080/api/query/health

# System status
curl http://localhost:8080/api/documents/status

# Upload document
curl -X POST http://localhost:8080/api/documents/upload -F "file=@document.pdf"

# Query system
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d '{"query":"What is this about?","topK":5}'
```

## 📋 API Endpoints Summary

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/documents/upload` | Upload PDF |
| DELETE | `/api/documents/{name}` | Delete document |
| GET | `/api/documents/status` | Get statistics |
| POST | `/api/query` | RAG query (with LLM) |
| POST | `/api/query/search` | Semantic search only |
| GET | `/api/query/health` | Health check |

## 🔧 Configuration Quick Reference

### application.properties
```properties
server.port=8080                              # Server port
spring.servlet.multipart.max-file-size=50MB   # Max upload size
ollama.base-url=http://localhost:11434        # Ollama URL
ollama.embedding-model=nomic-embed-text       # Embedding model
ollama.chat-model=llama3                      # Chat model
chunking.size=500                             # Chunk size
chunking.overlap=100                          # Chunk overlap
vector.top-k=5                                # Results to retrieve
```

## 📁 Project Structure

```
java congichat/
├── pom.xml                          # Maven configuration
├── run.sh                           # Setup & run script
├── README.md                        # Main documentation
├── ARCHITECTURE.md                  # System architecture
├── DEPLOYMENT.md                    # Deployment guide
├── API_TESTING.md                   # Testing guide
├── QUICK_REFERENCE.md              # This file
├── .gitignore                       # Git ignore rules
├── frontend/
│   └── index.html                   # Test frontend
├── data/                            # Created on first run
│   ├── uploads/                     # Uploaded PDFs
│   └── lucene-index/                # Vector index
└── src/main/
    ├── java/com/ragchat/
    │   ├── RagApplication.java
    │   ├── config/
    │   │   ├── AppConfig.java
    │   │   └── CorsConfig.java
    │   ├── controller/
    │   │   ├── DocumentController.java
    │   │   └── QueryController.java
    │   ├── service/
    │   │   ├── DocumentService.java
    │   │   ├── RAGService.java
    │   │   ├── PDFProcessorService.java
    │   │   ├── ChunkingService.java
    │   │   ├── OllamaService.java
    │   │   └── VectorStoreService.java
    │   ├── model/
    │   │   ├── Chunk.java
    │   │   └── Document.java
    │   ├── dto/
    │   │   ├── QueryRequest.java
    │   │   ├── QueryResponse.java
    │   │   ├── UploadResponse.java
    │   │   └── Ollama*.java
    │   └── exception/
    │       └── GlobalExceptionHandler.java
    └── resources/
        └── application.properties
```

## 🛠️ Common Tasks

### Add a New Document
```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -F "file=@/path/to/document.pdf"
```

### Ask a Question
```bash
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d '{"query":"Explain the main concept","topK":5}'
```

### Search Without LLM
```bash
curl -X POST http://localhost:8080/api/query/search \
  -H "Content-Type: application/json" \
  -d '{"query":"machine learning","topK":3}'
```

### Delete a Document
```bash
curl -X DELETE http://localhost:8080/api/documents/document.pdf
```

### Check System Status
```bash
curl http://localhost:8080/api/documents/status | jq
```

## 🐛 Troubleshooting Quick Fixes

### "Connection refused" Error
```bash
# Start Ollama
ollama serve

# Verify it's running
curl http://localhost:11434/api/tags
```

### "Port already in use"
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# OR change port in application.properties
server.port=8081
```

### "Model not found"
```bash
# Pull required models
ollama pull nomic-embed-text
ollama pull llama3

# List available models
ollama list
```

### Clear Index and Start Fresh
```bash
# Stop application
# Delete index
rm -rf data/lucene-index

# Restart application
./run.sh
```

## 📊 Key Metrics

### Expected Performance
- **Document Upload**: 5-30 seconds (depends on size)
- **Query (RAG)**: 2-5 seconds
- **Semantic Search**: < 1 second
- **Embedding Generation**: ~200ms per chunk

### Resource Usage
- **Memory**: 1-4 GB (depends on JVM settings)
- **Disk**: Varies (uploads + index)
- **CPU**: Moderate (spikes during processing)

## 🔑 Important Files

### Configuration
- `src/main/resources/application.properties` - Main config
- `pom.xml` - Dependencies

### Main Application
- `src/main/java/com/ragchat/RagApplication.java` - Entry point

### Core Services
- `VectorStoreService.java` - Vector search (Lucene)
- `OllamaService.java` - LLM interface
- `RAGService.java` - RAG pipeline

### REST APIs
- `DocumentController.java` - Upload/manage docs
- `QueryController.java` - Query/search

## 📝 Request/Response Examples

### Upload Request
```bash
POST /api/documents/upload
Content-Type: multipart/form-data
Body: file=@document.pdf
```

**Response:**
```json
{
  "documentId": "uuid-here",
  "documentName": "document.pdf",
  "chunksCreated": 42,
  "status": "SUCCESS",
  "message": "Document processed and indexed successfully",
  "processingTimeMs": 5432
}
```

### Query Request
```json
{
  "query": "What is machine learning?",
  "topK": 5,
  "documentName": null
}
```

**Response:**
```json
{
  "answer": "Machine learning is...",
  "query": "What is machine learning?",
  "retrievedChunks": [
    {
      "text": "Machine learning is a subset...",
      "documentName": "ml-guide.pdf",
      "score": 0.89,
      "chunkIndex": 0
    }
  ],
  "responseTimeMs": 2341
}
```

## 🔐 Environment Variables

Set these before running:

```bash
# Server
export SERVER_PORT=8080

# Ollama
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_EMBEDDING_MODEL=nomic-embed-text
export OLLAMA_CHAT_MODEL=llama3

# Paths
export APP_UPLOAD_DIR=./data/uploads
export APP_INDEX_DIR=./data/lucene-index
```

## 🎯 Development Tips

### Enable Debug Logging
```properties
logging.level.com.ragchat=DEBUG
```

### Hot Reload (Spring DevTools)
Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

### Test Different Models
```properties
# Faster, smaller model
ollama.chat-model=llama3:8b

# Higher quality, slower
ollama.chat-model=llama3:70b
```

### Adjust Chunk Size
```properties
# Smaller chunks (more precise)
chunking.size=300
chunking.overlap=50

# Larger chunks (more context)
chunking.size=800
chunking.overlap=150
```

## 📚 Additional Resources

- **README.md** - Complete overview and usage
- **ARCHITECTURE.md** - System design details
- **DEPLOYMENT.md** - Production deployment
- **API_TESTING.md** - Testing strategies
- **Ollama Docs** - https://ollama.ai/docs
- **Spring Boot** - https://spring.io/projects/spring-boot
- **Lucene** - https://lucene.apache.org/

## 💡 Tips & Tricks

### 1. Optimize for Speed
```properties
chunking.size=400        # Smaller chunks
vector.top-k=3           # Fewer results
ollama.chat-model=llama3:8b  # Faster model
```

### 2. Optimize for Quality
```properties
chunking.size=800        # Larger chunks
vector.top-k=7           # More context
ollama.chat-model=llama3:70b  # Better model
```

### 3. Test with Sample Data
```bash
# Download a test PDF
curl -o test.pdf https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf

# Upload it
curl -X POST http://localhost:8080/api/documents/upload -F "file=@test.pdf"

# Query it
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d '{"query":"What is this document about?","topK":3}'
```

### 4. Monitor Performance
```bash
# Watch logs in real-time
tail -f logs/application.log | grep "Processing"

# Check memory usage
jps -l | grep RagApplication
jstat -gc <PID>
```

### 5. Batch Processing
Upload multiple files:
```bash
for file in documents/*.pdf; do
  echo "Uploading $file..."
  curl -X POST http://localhost:8080/api/documents/upload -F "file=@$file"
  sleep 2
done
```

## 🎓 Learning Path

1. **Start Here**: README.md
2. **Understand Architecture**: ARCHITECTURE.md
3. **Try API**: API_TESTING.md
4. **Deploy**: DEPLOYMENT.md
5. **Quick Reference**: This file

## 🆘 Getting Help

### Check Logs
```bash
# Application logs
tail -f logs/application.log

# Spring Boot startup logs
cat logs/spring.log
```

### Verify Prerequisites
```bash
# Java version
java -version

# Maven version
mvn -version

# Ollama status
ollama list
```

### Test Components Individually

**Test Ollama:**
```bash
curl http://localhost:11434/api/embeddings \
  -d '{"model":"nomic-embed-text","prompt":"test"}'
```

**Test Spring Boot:**
```bash
curl http://localhost:8080/api/query/health
```

**Test PDF Processing:**
```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -F "file=@small-test.pdf"
```

---

**Quick Start Command:**
```bash
cd "/Users/abhishek/Desktop/java congichat" && ./run.sh
```

**Test Command:**
```bash
curl http://localhost:8080/api/query/health && echo " ✓ Server is running!"
```

---

Keep this file handy for quick reference! 📌
