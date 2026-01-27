package com.ragchat.dto;

/**
 * DTO for Ollama embedding API response
 */
public class OllamaEmbeddingResponse {
    
    private float[] embedding;
    
    public OllamaEmbeddingResponse() {
    }
    
    public OllamaEmbeddingResponse(float[] embedding) {
        this.embedding = embedding;
    }
    
    public float[] getEmbedding() {
        return embedding;
    }
    
    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
}
