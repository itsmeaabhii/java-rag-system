package com.ragchat.model;

/**
 * Represents a text chunk with its metadata and embedding
 */
public class Chunk {
    
    private String id;
    private String documentName;
    private String text;
    private int chunkIndex;
    private float[] embedding;
    private long timestamp;
    
    // Default constructor
    public Chunk() {
    }
    
    // Constructor for creating a new chunk without embedding
    public Chunk(String id, String documentName, String text, int chunkIndex) {
        this.id = id;
        this.documentName = documentName;
        this.text = text;
        this.chunkIndex = chunkIndex;
        this.timestamp = System.currentTimeMillis();
    }
    
    // All-args constructor
    public Chunk(String id, String documentName, String text, int chunkIndex, float[] embedding, long timestamp) {
        this.id = id;
        this.documentName = documentName;
        this.text = text;
        this.chunkIndex = chunkIndex;
        this.embedding = embedding;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getDocumentName() {
        return documentName;
    }
    
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public int getChunkIndex() {
        return chunkIndex;
    }
    
    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }
    
    public float[] getEmbedding() {
        return embedding;
    }
    
    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
