package com.ragchat.model;

import java.util.List;

/**
 * Represents an uploaded document with its metadata
 */
public class Document {
    
    private String id;
    private String name;
    private String path;
    private long size;
    private long uploadTimestamp;
    private int totalChunks;
    private String status;
    private List<String> chunkIds;
    
    // Default constructor
    public Document() {
    }
    
    // All-args constructor
    public Document(String id, String name, String path, long size, long uploadTimestamp, 
                   int totalChunks, String status, List<String> chunkIds) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.size = size;
        this.uploadTimestamp = uploadTimestamp;
        this.totalChunks = totalChunks;
        this.status = status;
        this.chunkIds = chunkIds;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public long getSize() {
        return size;
    }
    
    public void setSize(long size) {
        this.size = size;
    }
    
    public long getUploadTimestamp() {
        return uploadTimestamp;
    }
    
    public void setUploadTimestamp(long uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }
    
    public int getTotalChunks() {
        return totalChunks;
    }
    
    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<String> getChunkIds() {
        return chunkIds;
    }
    
    public void setChunkIds(List<String> chunkIds) {
        this.chunkIds = chunkIds;
    }
}
