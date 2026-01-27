package com.ragchat.dto;

import java.util.List;

/**
 * Response DTO for RAG query results
 */
public class QueryResponse {
    
    private String answer;
    private String query;
    private List<RetrievedChunk> retrievedChunks;
    private long responseTimeMs;
    
    // Default constructor
    public QueryResponse() {
    }
    
    // All-args constructor
    public QueryResponse(String answer, String query, List<RetrievedChunk> retrievedChunks, long responseTimeMs) {
        this.answer = answer;
        this.query = query;
        this.retrievedChunks = retrievedChunks;
        this.responseTimeMs = responseTimeMs;
    }
    
    // Builder pattern - static method to start building
    public static QueryResponseBuilder builder() {
        return new QueryResponseBuilder();
    }
    
    // Getters and Setters
    public String getAnswer() {
        return answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public List<RetrievedChunk> getRetrievedChunks() {
        return retrievedChunks;
    }
    
    public void setRetrievedChunks(List<RetrievedChunk> retrievedChunks) {
        this.retrievedChunks = retrievedChunks;
    }
    
    public long getResponseTimeMs() {
        return responseTimeMs;
    }
    
    public void setResponseTimeMs(long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }
    
    // Builder class - helps you understand the Builder design pattern!
    public static class QueryResponseBuilder {
        private String answer;
        private String query;
        private List<RetrievedChunk> retrievedChunks;
        private long responseTimeMs;
        
        public QueryResponseBuilder answer(String answer) {
            this.answer = answer;
            return this;
        }
        
        public QueryResponseBuilder query(String query) {
            this.query = query;
            return this;
        }
        
        public QueryResponseBuilder retrievedChunks(List<RetrievedChunk> retrievedChunks) {
            this.retrievedChunks = retrievedChunks;
            return this;
        }
        
        public QueryResponseBuilder responseTimeMs(long responseTimeMs) {
            this.responseTimeMs = responseTimeMs;
            return this;
        }
        
        public QueryResponse build() {
            return new QueryResponse(answer, query, retrievedChunks, responseTimeMs);
        }
    }
    
    // Nested class for retrieved chunk
    public static class RetrievedChunk {
        private String text;
        private String documentName;
        private float score;
        private int chunkIndex;
        
        // Default constructor
        public RetrievedChunk() {
        }
        
        // All-args constructor
        public RetrievedChunk(String text, String documentName, float score, int chunkIndex) {
            this.text = text;
            this.documentName = documentName;
            this.score = score;
            this.chunkIndex = chunkIndex;
        }
        
        // Builder pattern
        public static RetrievedChunkBuilder builder() {
            return new RetrievedChunkBuilder();
        }
        
        // Getters and Setters
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
        
        public String getDocumentName() {
            return documentName;
        }
        
        public void setDocumentName(String documentName) {
            this.documentName = documentName;
        }
        
        public float getScore() {
            return score;
        }
        
        public void setScore(float score) {
            this.score = score;
        }
        
        public int getChunkIndex() {
            return chunkIndex;
        }
        
        public void setChunkIndex(int chunkIndex) {
            this.chunkIndex = chunkIndex;
        }
        
        // Builder for RetrievedChunk
        public static class RetrievedChunkBuilder {
            private String text;
            private String documentName;
            private float score;
            private int chunkIndex;
            
            public RetrievedChunkBuilder text(String text) {
                this.text = text;
                return this;
            }
            
            public RetrievedChunkBuilder documentName(String documentName) {
                this.documentName = documentName;
                return this;
            }
            
            public RetrievedChunkBuilder score(float score) {
                this.score = score;
                return this;
            }
            
            public RetrievedChunkBuilder chunkIndex(int chunkIndex) {
                this.chunkIndex = chunkIndex;
                return this;
            }
            
            public RetrievedChunk build() {
                return new RetrievedChunk(text, documentName, score, chunkIndex);
            }
        }
    }
}
