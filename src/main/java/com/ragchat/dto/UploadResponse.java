package com.ragchat.dto;

/**
 * Response DTO for document upload
 */
public class UploadResponse {
    
    private String documentId;
    private String documentName;
    private int chunksCreated;
    private String status;
    private String message;
    private long processingTimeMs;
    
    // Default constructor
    public UploadResponse() {
    }
    
    // All-args constructor
    public UploadResponse(String documentId, String documentName, int chunksCreated, 
                         String status, String message, long processingTimeMs) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.chunksCreated = chunksCreated;
        this.status = status;
        this.message = message;
        this.processingTimeMs = processingTimeMs;
    }
    
    // Builder pattern
    public static UploadResponseBuilder builder() {
        return new UploadResponseBuilder();
    }
    
    // Getters and Setters
    public String getDocumentId() {
        return documentId;
    }
    
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    
    public String getDocumentName() {
        return documentName;
    }
    
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
    
    public int getChunksCreated() {
        return chunksCreated;
    }
    
    public void setChunksCreated(int chunksCreated) {
        this.chunksCreated = chunksCreated;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public long getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
    
    // Builder class
    public static class UploadResponseBuilder {
        private String documentId;
        private String documentName;
        private int chunksCreated;
        private String status;
        private String message;
        private long processingTimeMs;
        
        public UploadResponseBuilder documentId(String documentId) {
            this.documentId = documentId;
            return this;
        }
        
        public UploadResponseBuilder documentName(String documentName) {
            this.documentName = documentName;
            return this;
        }
        
        public UploadResponseBuilder chunksCreated(int chunksCreated) {
            this.chunksCreated = chunksCreated;
            return this;
        }
        
        public UploadResponseBuilder status(String status) {
            this.status = status;
            return this;
        }
        
        public UploadResponseBuilder message(String message) {
            this.message = message;
            return this;
        }
        
        public UploadResponseBuilder processingTimeMs(long processingTimeMs) {
            this.processingTimeMs = processingTimeMs;
            return this;
        }
        
        public UploadResponse build() {
            return new UploadResponse(documentId, documentName, chunksCreated, status, message, processingTimeMs);
        }
    }
}
