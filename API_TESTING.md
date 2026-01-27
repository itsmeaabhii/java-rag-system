# API Testing Guide - Java RAG System

## Quick Start with curl

### 1. Check System Status
```bash
curl http://localhost:8080/api/documents/status
```

### 2. Upload a PDF Document
```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -F "file=@/path/to/your/document.pdf"
```

### 3. Query the System (RAG)
```bash
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d '{
    "query": "What is the main topic discussed in the documents?",
    "topK": 5
  }'
```

### 4. Semantic Search Only
```bash
curl -X POST http://localhost:8080/api/query/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "artificial intelligence",
    "topK": 3
  }'
```

### 5. Delete a Document
```bash
curl -X DELETE http://localhost:8080/api/documents/document.pdf
```

## Testing with Postman

### Setup
1. Create a new Collection: "Java RAG System"
2. Set Base URL: `http://localhost:8080/api`

### Request Examples

#### Upload Document
- **Method**: POST
- **URL**: `{{baseUrl}}/documents/upload`
- **Body**: form-data
  - Key: `file`
  - Type: File
  - Value: Select your PDF

#### Query RAG
- **Method**: POST
- **URL**: `{{baseUrl}}/query`
- **Headers**: `Content-Type: application/json`
- **Body**: raw JSON
```json
{
  "query": "Explain the key concepts",
  "topK": 5,
  "documentName": null
}
```

#### Get Status
- **Method**: GET
- **URL**: `{{baseUrl}}/documents/status`

## Testing with JavaScript (Frontend)

### Upload Document
```javascript
async function uploadDocument(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch('http://localhost:8080/api/documents/upload', {
    method: 'POST',
    body: formData
  });
  
  return await response.json();
}
```

### Query System
```javascript
async function queryRAG(question) {
  const response = await fetch('http://localhost:8080/api/query', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      query: question,
      topK: 5
    })
  });
  
  return await response.json();
}
```

## Sample Test Scenarios

### Scenario 1: Basic RAG Flow
1. Upload a PDF about "Machine Learning"
2. Query: "What is machine learning?"
3. Verify response contains relevant information
4. Check retrieved chunks are from correct document

### Scenario 2: Multiple Documents
1. Upload multiple PDFs
2. Query without document filter
3. Query with specific document filter
4. Compare results

### Scenario 3: Semantic Search
1. Upload technical documents
2. Search for: "neural networks"
3. Verify similarity scores
4. Check chunk ordering by relevance

## Expected Response Times

- **Document Upload**: 5-30 seconds (depends on size)
- **Query (RAG)**: 2-5 seconds
- **Semantic Search**: < 1 second
- **Status Check**: < 100ms

## Common Issues

### 1. "Ollama not available"
**Solution**: 
```bash
ollama serve
ollama pull nomic-embed-text
ollama pull llama3
```

### 2. "File too large"
**Solution**: Increase limit in `application.properties`
```properties
spring.servlet.multipart.max-file-size=100MB
```

### 3. "No chunks found"
**Solution**: Upload documents first before querying

### 4. CORS errors from browser
**Solution**: CORS is already configured in `CorsConfig.java`

## Performance Benchmarks

### Small Document (10 pages)
- Chunking: ~500ms
- Embedding Generation: ~2s
- Indexing: ~100ms
- Total: ~3s

### Medium Document (50 pages)
- Chunking: ~2s
- Embedding Generation: ~10s
- Indexing: ~500ms
- Total: ~13s

### Query Performance
- Embedding query: ~200ms
- Vector search: ~100ms
- LLM generation: ~2s
- Total: ~2.5s

## Monitoring

### Check Logs
```bash
tail -f logs/spring.log
```

### Check Indexed Chunks
```bash
curl http://localhost:8080/api/documents/status | jq .totalIndexedChunks
```

### Test Ollama Directly
```bash
# Test embeddings
curl http://localhost:11434/api/embeddings \
  -d '{
    "model": "nomic-embed-text",
    "prompt": "test"
  }'

# Test generation
curl http://localhost:11434/api/generate \
  -d '{
    "model": "llama3",
    "prompt": "Hello",
    "stream": false
  }'
```

## Load Testing (Optional)

### Using Apache Bench
```bash
# Test status endpoint
ab -n 100 -c 10 http://localhost:8080/api/documents/status

# Test query endpoint (save request body to query.json)
ab -n 10 -c 2 -p query.json -T application/json http://localhost:8080/api/query
```

### Using curl in a loop
```bash
for i in {1..10}; do
  echo "Request $i"
  curl -X POST http://localhost:8080/api/query \
    -H "Content-Type: application/json" \
    -d '{"query":"test", "topK":3}' \
    -w "\nTime: %{time_total}s\n"
done
```

## Integration Testing

Create test scripts to verify the entire workflow:

```bash
#!/bin/bash

echo "1. Testing status..."
curl -s http://localhost:8080/api/documents/status | jq

echo "\n2. Uploading document..."
curl -s -X POST http://localhost:8080/api/documents/upload \
  -F "file=@test.pdf" | jq

echo "\n3. Waiting for processing..."
sleep 5

echo "\n4. Querying system..."
curl -s -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d '{"query":"What is this about?","topK":3}' | jq

echo "\nTests completed!"
```

## Debugging Tips

1. **Enable Debug Logging**
   ```properties
   logging.level.com.ragchat=DEBUG
   ```

2. **Check Ollama Logs**
   ```bash
   ollama logs
   ```

3. **Inspect Lucene Index**
   - Location: `./data/lucene-index`
   - Check if directory is created
   - Verify it's not empty after uploads

4. **Test Each Component**
   - PDF extraction: Check logs for extracted text length
   - Chunking: Verify chunk count
   - Embeddings: Check Ollama API response
   - Search: Test semantic search before full RAG
