package com.ragchat.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for querying the RAG system
 */
public class QueryRequest {
    
    @NotBlank(message = "Query cannot be empty")
    @Size(min = 3, max = 1000, message = "Query must be between 3 and 1000 characters")
    private String query;
    
    @Min(value = 1, message = "Top K must be at least 1")
    @Max(value = 20, message = "Top K cannot exceed 20")
    private Integer topK = 5;
    
    @Size(max = 255, message = "Document name cannot exceed 255 characters")
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
    
    @Override
    public String toString() {
        return "QueryRequest{" +
                "query='" + query + '\'' +
                ", topK=" + topK +
                ", documentName='" + documentName + '\'' +
                '}';
    }
}
