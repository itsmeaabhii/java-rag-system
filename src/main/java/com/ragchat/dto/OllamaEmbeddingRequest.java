package com.ragchat.dto;

/**
 * DTO for Ollama embedding API request
 */
public class OllamaEmbeddingRequest {
    
    private String model;
    private String prompt;
    
    public OllamaEmbeddingRequest() {
    }
    
    public OllamaEmbeddingRequest(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
