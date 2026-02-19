package com.ragchat.controller;

import com.ragchat.dto.QueryRequest;
import com.ragchat.dto.QueryResponse;
import com.ragchat.service.RAGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * REST Controller for RAG query operations
 */
@RestController
@RequestMapping("/query")
@CrossOrigin(origins = "*")
@Validated
public class QueryController {
    
    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);
    
    private final RAGService ragService;
    
    // Query statistics
    private final AtomicInteger totalQueries = new AtomicInteger(0);
    private final AtomicInteger successfulQueries = new AtomicInteger(0);
    private final AtomicInteger failedQueries = new AtomicInteger(0);
    private final AtomicLong totalProcessingTimeMs = new AtomicLong(0);
    private final long startupTime = System.currentTimeMillis();
    
    public QueryController(RAGService ragService) {
        this.ragService = ragService;
    }
    
    /**
     * Process a RAG query (retrieval + generation)
     * 
     * POST /api/query
     */
    @PostMapping
    public ResponseEntity<?> query(@Valid @RequestBody QueryRequest request) {
        long startTime = System.currentTimeMillis();
        totalQueries.incrementAndGet();
        logger.info("Received query request: {}", request.getQuery());
        
        try {
            QueryResponse response = ragService.processQuery(
                request.getQuery(),
                request.getTopK(),
                request.getDocumentName()
            );
            
            long processingTime = System.currentTimeMillis() - startTime;
            totalProcessingTimeMs.addAndGet(processingTime);
            successfulQueries.incrementAndGet();
            logger.info("Query processed successfully in {}ms", processingTime);
            
            // Add processing time to response metadata
            Map<String, Object> enhancedResponse = new HashMap<>();
            enhancedResponse.put("query", response.getQuery());
            enhancedResponse.put("answer", response.getAnswer());
            enhancedResponse.put("retrievedChunks", response.getRetrievedChunks());
            enhancedResponse.put("processingTimeMs", processingTime);
            enhancedResponse.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(enhancedResponse);
            
        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            totalProcessingTimeMs.addAndGet(processingTime);
            failedQueries.incrementAndGet();
            logger.error("Error processing query after {}ms: {}", processingTime, e.getMessage(), e);
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process query");
            error.put("message", e.getMessage());
            error.put("processingTimeMs", String.valueOf(processingTime));
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
        }
    }
    
    /**
     * Perform semantic search without LLM generation
     * 
     * POST /api/query/search
     */
    @PostMapping("/search")
    public ResponseEntity<?> semanticSearch(@Valid @RequestBody QueryRequest request) {
        logger.info("Received semantic search request: {}", request.getQuery());
        
        try {
            List<QueryResponse.RetrievedChunk> results = ragService.semanticSearch(
                request.getQuery(),
                request.getTopK(),
                request.getDocumentName()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("query", request.getQuery());
            response.put("results", results);
            response.put("count", results.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error performing semantic search", e);
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to perform search");
            error.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
        }
    }
    
    /**
     * Health check endpoint
     * 
     * GET /api/query/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "RAG Query Service");
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Query statistics endpoint
     * 
     * GET /api/query/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> statistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalQueries", totalQueries.get());
        stats.put("successfulQueries", successfulQueries.get());
        stats.put("failedQueries", failedQueries.get());
        
        int total = totalQueries.get();
        if (total > 0) {
            double successRate = (double) successfulQueries.get() / total * 100;
            double avgProcessingTime = (double) totalProcessingTimeMs.get() / total;
            stats.put("successRatePercent", String.format("%.2f", successRate));
            stats.put("avgProcessingTimeMs", String.format("%.2f", avgProcessingTime));
        } else {
            stats.put("successRatePercent", "0.00");
            stats.put("avgProcessingTimeMs", "0.00");
        }
        
        stats.put("uptimeSeconds", (System.currentTimeMillis() - startupTime) / 1000);
        stats.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(stats);
    }
}
