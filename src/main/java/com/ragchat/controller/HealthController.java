package com.ragchat.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check endpoint for monitoring and status verification
 */
@RestController
@RequestMapping("/health")
@CrossOrigin(origins = "*")
public class HealthController {
    
    @Value("${spring.application.name:RAG Chat Application}")
    private String applicationName;
    
    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;
    
    @Value("${ollama.base-url}")
    private String ollamaUrl;
    
    /**
     * Health check endpoint
     * 
     * GET /api/health
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", applicationName);
        response.put("version", applicationVersion);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("ollamaUrl", ollamaUrl);
        
        Map<String, String> services = new HashMap<>();
        services.put("vector-store", "operational");
        services.put("document-processing", "operational");
        services.put("ollama-integration", "operational");
        
        response.put("services", services);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Readiness probe endpoint
     * 
     * GET /api/health/ready
     */
    @GetMapping("/ready")
    public ResponseEntity<Map<String, String>> ready() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ready");
        response.put("message", "Application is ready to accept requests");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Liveness probe endpoint
     * 
     * GET /api/health/live
     */
    @GetMapping("/live")
    public ResponseEntity<Map<String, String>> live() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "alive");
        response.put("message", "Application is running");
        return ResponseEntity.ok(response);
    }
}
