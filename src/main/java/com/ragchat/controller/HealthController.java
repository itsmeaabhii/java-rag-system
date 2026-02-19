package com.ragchat.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.time.LocalDateTime;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
    
    private final long startTime = System.currentTimeMillis();
    
    /**
     * Health check endpoint with detailed system metrics
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
        
        // Add system metrics
        Map<String, Object> systemMetrics = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        
        systemMetrics.put("usedMemoryMB", usedMemory);
        systemMetrics.put("freeMemoryMB", freeMemory);
        systemMetrics.put("maxMemoryMB", maxMemory);
        systemMetrics.put("memoryUsagePercent", String.format("%.2f%%", memoryUsagePercent));
        systemMetrics.put("processors", runtime.availableProcessors());
        
        // Uptime formatting
        long uptimeSeconds = (System.currentTimeMillis() - startTime) / 1000;
        systemMetrics.put("uptimeSeconds", uptimeSeconds);
        systemMetrics.put("uptimeFormatted", formatUptime(uptimeSeconds));
        
        // Thread metrics
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        Map<String, Object> threadMetrics = new HashMap<>();
        threadMetrics.put("threadCount", threadBean.getThreadCount());
        threadMetrics.put("peakThreadCount", threadBean.getPeakThreadCount());
        threadMetrics.put("daemonThreadCount", threadBean.getDaemonThreadCount());
        systemMetrics.put("threads", threadMetrics);
        
        // Garbage Collection stats
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        Map<String, Object> gcMetrics = new HashMap<>();
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            Map<String, Object> gcInfo = new HashMap<>();
            gcInfo.put("collectionCount", gcBean.getCollectionCount());
            gcInfo.put("collectionTimeMs", gcBean.getCollectionTime());
            gcMetrics.put(gcBean.getName(), gcInfo);
        }
        systemMetrics.put("garbageCollection", gcMetrics);
        
        // Disk space metrics
        File root = new File("/");
        Map<String, Object> diskMetrics = new HashMap<>();
        diskMetrics.put("totalSpaceGB", root.getTotalSpace() / (1024 * 1024 * 1024));
        diskMetrics.put("freeSpaceGB", root.getFreeSpace() / (1024 * 1024 * 1024));
        diskMetrics.put("usableSpaceGB", root.getUsableSpace() / (1024 * 1024 * 1024));
        systemMetrics.put("disk", diskMetrics);
        
        response.put("system", systemMetrics);
        
        Map<String, String> services = new HashMap<>();
        services.put("vector-store", "operational");
        services.put("document-processing", "operational");
        services.put("ollama-integration", "operational");
        
        response.put("services", services);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Format uptime into human-readable format
     */
    private String formatUptime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        StringBuilder uptime = new StringBuilder();
        if (days > 0) {
            uptime.append(days).append("d ");
        }
        if (hours > 0 || days > 0) {
            uptime.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            uptime.append(minutes).append("m ");
        }
        uptime.append(secs).append("s");
        
        return uptime.toString();
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
    
    /**
     * Simple metrics endpoint
     * 
     * GET /api/health/metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> metrics() {
        Map<String, Object> response = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        
        response.put("uptimeSeconds", (System.currentTimeMillis() - startTime) / 1000);
        response.put("memoryUsedMB", (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024));
        response.put("memoryMaxMB", runtime.maxMemory() / (1024 * 1024));
        response.put("threadCount", ManagementFactory.getThreadMXBean().getThreadCount());
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
}
