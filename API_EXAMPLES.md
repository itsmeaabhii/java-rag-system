# API Examples and Usage Guide

This guide provides detailed examples of how to use the Java RAG System API endpoints.

## Base URL

```
http://localhost:8080/api
```

## Authentication

Currently, no authentication is required. All endpoints are publicly accessible.

---

## 📋 Table of Contents

1. [Query Endpoints](#query-endpoints)
2. [Document Management](#document-management)
3. [Health Check](#health-check)
4. [Error Handling](#error-handling)

---

## Query Endpoints

### 1. RAG Query (Retrieval + Generation)

Process a query using the complete RAG pipeline: retrieve relevant chunks and generate an answer.

**Endpoint:** `POST /api/query`

**Request Body:**
```json
{
  "query": "What is machine learning?",
  "topK": 5,
  "documentName": null
}
```

**Parameters:**
- `query` (required): The question or search query (3-1000 characters)
- `topK` (optional): Number of chunks to retrieve (1-20, default: 5)
- `documentName` (optional): Filter by specific document name

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d '{
    "query": "What is machine learning?",
    "topK": 5
  }'
```

**Success Response (200 OK):**
```json
{
  "query": "What is machine learning?",
  "answer": "Machine learning is a subset of artificial intelligence...",
  "retrievedChunks": [
    {
      "documentName": "ML_Guide.pdf",
      "content": "Machine learning is...",
      "score": 0.92,
      "chunkIndex": 3,
      "pageNumber": 12
    }
  ],
  "processingTimeMs": 1850,
  "timestamp": "2026-03-05T14:30:45.123"
}
```

**JavaScript/Fetch Example:**
```javascript
async function queryRAG(question) {
  const response = await fetch('http://localhost:8080/api/query', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      query: question,
      topK: 5
    })
  });
  
  const data = await response.json();
  return data;
}

// Usage
queryRAG('What is machine learning?')
  .then(result => console.log(result.answer));
```

---

### 2. Semantic Search (Without LLM)

Perform semantic search to retrieve relevant chunks without generating an answer.

**Endpoint:** `POST /api/query/search`

**Request Body:**
```json
{
  "query": "machine learning algorithms",
  "topK": 10,
  "documentName": "ML_Guide.pdf"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/query/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "machine learning algorithms",
    "topK": 10,
    "documentName": "ML_Guide.pdf"
  }'
```

**Success Response (200 OK):**
```json
{
  "query": "machine learning algorithms",
  "chunks": [
    {
      "documentName": "ML_Guide.pdf",
      "content": "Supervised learning algorithms...",
      "score": 0.89,
      "chunkIndex": 5,
      "pageNumber": 15
    }
  ],
  "totalChunks": 10,
  "processingTimeMs": 450
}
```

---

### 3. Query Statistics

Get statistics about query performance.

**Endpoint:** `GET /api/query/stats`

**cURL Example:**
```bash
curl http://localhost:8080/api/query/stats
```

**Success Response (200 OK):**
```json
{
  "totalQueries": 1523,
  "successfulQueries": 1498,
  "failedQueries": 25,
  "successRate": "98.36%",
  "averageProcessingTimeMs": 1842,
  "uptimeHours": 156.5
}
```

---

## Document Management

### 1. Upload Document

Upload a PDF document for processing and indexing.

**Endpoint:** `POST /api/documents/upload`

**Content-Type:** `multipart/form-data`

**Parameters:**
- `file` (required): PDF file to upload (max 50MB)

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -F "file=@/path/to/document.pdf"
```

**Success Response (200 OK):**
```json
{
  "message": "Document uploaded and processed successfully",
  "documentId": "12345",
  "documentName": "document.pdf",
  "numberOfChunks": 45,
  "processingTimeMs": 3200,
  "status": "indexed"
}
```

**JavaScript/Fetch Example:**
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

// Usage with file input
document.getElementById('fileInput').addEventListener('change', (e) => {
  const file = e.target.files[0];
  if (file) {
    uploadDocument(file)
      .then(result => console.log('Upload successful:', result));
  }
});
```

---

### 2. List Documents

Get a list of all uploaded documents.

**Endpoint:** `GET /api/documents/list`

**cURL Example:**
```bash
curl http://localhost:8080/api/documents/list
```

**Success Response (200 OK):**
```json
{
  "documents": [
    {
      "name": "ML_Guide.pdf",
      "uploadedAt": "2026-03-01T10:30:00",
      "numberOfChunks": 45,
      "size": "2.5 MB"
    },
    {
      "name": "AI_Basics.pdf",
      "uploadedAt": "2026-03-02T14:15:00",
      "numberOfChunks": 32,
      "size": "1.8 MB"
    }
  ],
  "totalDocuments": 2
}
```

---

### 3. Delete Document

Delete a document and its associated chunks.

**Endpoint:** `DELETE /api/documents/{documentName}`

**cURL Example:**
```bash
curl -X DELETE http://localhost:8080/api/documents/ML_Guide.pdf
```

**Success Response (200 OK):**
```json
{
  "message": "Document deleted successfully",
  "documentName": "ML_Guide.pdf"
}
```

---

## Health Check

### System Health Status

Check the health and status of the application and its dependencies.

**Endpoint:** `GET /api/health`

**cURL Example:**
```bash
curl http://localhost:8080/api/health
```

**Success Response (200 OK):**
```json
{
  "status": "UP",
  "timestamp": "2026-03-05T14:30:45.123",
  "checks": {
    "ollama": {
      "status": "UP",
      "embeddingModel": "nomic-embed-text",
      "chatModel": "llama3",
      "responseTimeMs": 45
    },
    "lucene": {
      "status": "UP",
      "indexPath": "./data/lucene-index",
      "totalDocuments": 124
    },
    "storage": {
      "status": "UP",
      "uploadPath": "./data/uploads",
      "availableSpace": "15.2 GB"
    }
  },
  "uptime": "156h 30m"
}
```

---

## Error Handling

### Error Response Format

All error responses follow a consistent format:

```json
{
  "error": "Error type",
  "message": "Detailed error message",
  "timestamp": "2026-03-05T14:30:45.123",
  "path": "/api/query"
}
```

### Common HTTP Status Codes

- `200 OK`: Request successful
- `400 Bad Request`: Invalid input parameters
- `404 Not Found`: Resource not found
- `413 Payload Too Large`: File size exceeds limit
- `415 Unsupported Media Type`: Invalid file format
- `500 Internal Server Error`: Server-side error

### Validation Errors

**Example - Query too short:**
```json
{
  "error": "Validation Failed",
  "message": "Query must be between 3 and 1000 characters",
  "field": "query",
  "rejectedValue": "AI"
}
```

**Example - TopK out of range:**
```json
{
  "error": "Validation Failed",
  "message": "Top K cannot exceed 20",
  "field": "topK",
  "rejectedValue": 50
}
```

---

## Best Practices

### 1. Query Optimization

- Keep queries specific and focused
- Use topK between 3-10 for best results
- Filter by documentName when searching specific documents

### 2. Document Upload

- Ensure PDFs are text-based (not scanned images)
- Optimal file size: under 10MB
- Use descriptive filenames

### 3. Error Handling

Always implement proper error handling:

```javascript
async function safeQuery(question) {
  try {
    const response = await fetch('http://localhost:8080/api/query', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ query: question, topK: 5 })
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Query failed:', error.message);
    return null;
  }
}
```

---

## Rate Limiting

Currently, there are no rate limits. For production use, consider implementing:
- Request rate limiting per IP
- Concurrent query limits
- File upload frequency limits

---

## Support

For issues or questions:
1. Check the error message in the response
2. Review the logs for detailed error information
3. Ensure Ollama is running and models are available
4. Verify file formats and sizes

---

**Last Updated:** March 5, 2026  
**API Version:** 1.0.0
