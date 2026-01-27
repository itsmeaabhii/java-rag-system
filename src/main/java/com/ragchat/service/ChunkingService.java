package com.ragchat.service;

import com.ragchat.model.Chunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for chunking text into smaller segments
 */
@Service
public class ChunkingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChunkingService.class);
    
    @Value("${chunking.size:500}")
    private int chunkSize;
    
    @Value("${chunking.overlap:100}")
    private int chunkOverlap;
    
    /**
     * Split text into overlapping chunks
     * 
     * @param text The text to chunk
     * @param documentName The source document name
     * @return List of text chunks
     */
    public List<Chunk> chunkText(String text, String documentName) {
        logger.info("Chunking text from document: {} (length: {} chars)", 
                   documentName, text.length());
        
        List<Chunk> chunks = new ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            logger.warn("Empty text provided for chunking");
            return chunks;
        }
        
        int index = 0;
        int position = 0;
        
        while (position < text.length()) {
            int endPosition = Math.min(position + chunkSize, text.length());
            
            // Try to break at sentence or word boundary
            if (endPosition < text.length()) {
                int lastPeriod = text.lastIndexOf('.', endPosition);
                int lastSpace = text.lastIndexOf(' ', endPosition);
                
                if (lastPeriod > position) {
                    endPosition = lastPeriod + 1;
                } else if (lastSpace > position) {
                    endPosition = lastSpace + 1;
                }
            }
            
            String chunkText = text.substring(position, endPosition).trim();
            
            if (!chunkText.isEmpty()) {
                Chunk chunk = new Chunk(
                    UUID.randomUUID().toString(),
                    documentName,
                    chunkText,
                    index++
                );
                chunks.add(chunk);
            }
            
            // Move position forward with overlap
            int nextPosition = endPosition - chunkOverlap;
            
            // Prevent infinite loop - ensure we always move forward
            if (nextPosition <= position) {
                position = endPosition;
            } else {
                position = nextPosition;
            }
            
            // Stop if we've reached or passed the end
            if (position >= text.length()) {
                break;
            }
        }
        
        logger.info("Created {} chunks from document: {}", chunks.size(), documentName);
        return chunks;
    }
    
    /**
     * Get current chunk configuration
     */
    public String getChunkingInfo() {
        return "Chunk size: %d, Overlap: %d".formatted(chunkSize, chunkOverlap);
    }
}
