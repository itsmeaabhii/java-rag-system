package com.ragchat.dto;

/**
 * DTO for Ollama generate/chat API response
 */
public class OllamaGenerateResponse {
    
    private String model;
    private String response;
    private boolean done;
    private long total_duration;
    
    public OllamaGenerateResponse() {
    }
    
    public OllamaGenerateResponse(String model, String response, boolean done, long total_duration) {
        this.model = model;
        this.response = response;
        this.done = done;
        this.total_duration = total_duration;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public boolean isDone() {
        return done;
    }
    
    public void setDone(boolean done) {
        this.done = done;
    }
    
    public long getTotal_duration() {
        return total_duration;
    }
    
    public void setTotal_duration(long total_duration) {
        this.total_duration = total_duration;
    }
}
