package com.ragchat.service;

import com.ragchat.dto.OllamaEmbeddingRequest;
import com.ragchat.dto.OllamaEmbeddingResponse;
import com.ragchat.dto.OllamaGenerateRequest;
import com.ragchat.dto.OllamaGenerateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Service for interacting with Ollama API for embeddings and generation
 */
@Service
public class OllamaService {
    
    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);
    
    private final WebClient webClient;
    
    @Value("${ollama.embedding-model:nomic-embed-text}")
    private String embeddingModel;
    
    @Value("${ollama.chat-model:llama3}")
    private String chatModel;
    
    @Value("${ollama.timeout:120}")
    private int timeoutSeconds;
    
    public OllamaService(@Value("${ollama.base-url:http://localhost:11434}") String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("Ollama base URL cannot be null or empty");
        }
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }
    
    /**
     * Generate embedding for a text using Ollama
     * 
     * @param text The text to embed
     * @return Embedding vector
     */
    public float[] generateEmbedding(String text) {
        logger.debug("Generating embedding for text (length: {})", text.length());
        
        try {
            OllamaEmbeddingRequest request = new OllamaEmbeddingRequest(embeddingModel, text);
            
            OllamaEmbeddingResponse response = webClient.post()
                .uri("/api/embeddings")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OllamaEmbeddingResponse.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .block();
            
            if (response != null && response.getEmbedding() != null) {
                logger.debug("Successfully generated embedding with dimension: {}", 
                           response.getEmbedding().length);
                return response.getEmbedding();
            } else {
                throw new RuntimeException("Empty response from Ollama embedding API");
            }
            
        } catch (Exception e) {
            logger.error("Failed to generate embedding", e);
            throw new RuntimeException("Failed to generate embedding: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate text response using Ollama LLM
     * 
     * @param prompt The prompt to send to the LLM
     * @return Generated text response
     */
    public String generateResponse(String prompt) {
        logger.info("Generating LLM response");
        
        try {
            OllamaGenerateRequest request = OllamaGenerateRequest.builder()
                .model(chatModel)
                .prompt(prompt)
                .stream(false)
                .options(OllamaGenerateRequest.Options.builder()
                    .temperature(0.7)
                    .num_predict(512)
                    .top_k(40)
                    .top_p(0.9)
                    .build())
                .build();
            
            OllamaGenerateResponse response = webClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OllamaGenerateResponse.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .block();
            
            if (response != null && response.getResponse() != null) {
                logger.info("Successfully generated response (length: {})", 
                          response.getResponse().length());
                return response.getResponse();
            } else {
                throw new RuntimeException("Empty response from Ollama generate API");
            }
            
        } catch (Exception e) {
            logger.error("Failed to generate LLM response", e);
            throw new RuntimeException("Failed to generate response: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if Ollama service is available
     */
    public boolean isAvailable() {
        try {
            webClient.get()
                .uri("/api/tags")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .block();
            return true;
        } catch (Exception e) {
            logger.warn("Ollama service is not available: {}", e.getMessage());
            return false;
        }
    }
}
