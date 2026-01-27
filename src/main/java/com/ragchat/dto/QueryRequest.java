package com.ragchat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for querying the RAG system
 */
public class QueryRequest {
    
    @NotBlank(message = "Query cannot be empty")
    private String query;
    
    @Positive(message = "Top K must be positive")
    private Integer topK = 5;
    
    private String documentName; // Optional: search only in specific document
    
    // Default constructor
    public QueryRequest() {
    }
    
    // All-args constructor
    public QueryRequest(String query, Integer topK, String documentName) {
        this.query = query;
        this.topK = topK;
        this.documentName = documentName;
    }
    
    // Getters and Setters
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public Integer getTopK() {
        return topK;
    }
    
    public void setTopK(Integer topK) {
        this.topK = topK;
    }
    
    public String getDocumentName() {
        return documentName;
    }
    
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
}
