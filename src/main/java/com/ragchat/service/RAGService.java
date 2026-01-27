package com.ragchat.service;

import com.ragchat.dto.QueryResponse;
import com.ragchat.model.Chunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Retrieval Augmented Generation (RAG)
 * Orchestrates the entire RAG pipeline: retrieval + prompt construction + generation
 */
@Service
public class RAGService {
    
    private static final Logger logger = LoggerFactory.getLogger(RAGService.class);
    
    private final OllamaService ollamaService;
    private final VectorStoreService vectorStoreService;
    
    @Value("${vector.top-k:5}")
    private int defaultTopK;
    
    public RAGService(OllamaService ollamaService, VectorStoreService vectorStoreService) {
        this.ollamaService = ollamaService;
        this.vectorStoreService = vectorStoreService;
    }
    
    /**
     * Process a query using RAG pipeline
     * 
     * @param query User's question
     * @param topK Number of chunks to retrieve
     * @param documentName Optional document filter
     * @return Query response with answer and retrieved chunks
     */
    public QueryResponse processQuery(String query, Integer topK, String documentName) {
        long startTime = System.currentTimeMillis();
        
        logger.info("Processing RAG query: {}", query);
        
        try {
            // Step 1: Generate query embedding
            logger.debug("Generating query embedding");
            float[] queryEmbedding = ollamaService.generateEmbedding(query);
            
            // Step 2: Retrieve similar chunks
            int k = (topK != null) ? topK : defaultTopK;
            logger.debug("Retrieving top {} similar chunks", k);
            
            List<VectorStoreService.ScoredChunk> scoredChunks = 
                vectorStoreService.searchSimilar(queryEmbedding, k, documentName);
            
            if (scoredChunks.isEmpty()) {
                logger.warn("No relevant chunks found for query");
                return QueryResponse.builder()
                    .query(query)
                    .answer("I couldn't find any relevant information to answer your question. " +
                           "Please make sure documents are uploaded and indexed.")
                    .retrievedChunks(List.of())
                    .responseTimeMs(System.currentTimeMillis() - startTime)
                    .build();
            }
            
            // Step 3: Construct RAG prompt
            String prompt = constructPrompt(query, scoredChunks);
            logger.debug("Constructed prompt (length: {})", prompt.length());
            
            // Step 4: Generate answer using LLM
            logger.debug("Generating LLM response");
            String answer = ollamaService.generateResponse(prompt);
            
            // Step 5: Build response
            List<QueryResponse.RetrievedChunk> retrievedChunks = scoredChunks.stream()
                .map(sc -> QueryResponse.RetrievedChunk.builder()
                    .text(sc.getChunk().getText())
                    .documentName(sc.getChunk().getDocumentName())
                    .score(sc.getScore())
                    .chunkIndex(sc.getChunk().getChunkIndex())
                    .build())
                .collect(Collectors.toList());
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            logger.info("RAG query processed successfully in {}ms", responseTime);
            
            return QueryResponse.builder()
                .query(query)
                .answer(answer)
                .retrievedChunks(retrievedChunks)
                .responseTimeMs(responseTime)
                .build();
                
        } catch (IOException e) {
            logger.error("Error during RAG query processing", e);
            throw new RuntimeException("Failed to process query: " + e.getMessage(), e);
        }
    }
    
    /**
     * Construct RAG prompt with system instruction, context, and user question
     * 
     * @param query User's question
     * @param scoredChunks Retrieved relevant chunks
     * @return Formatted prompt for LLM
     */
    private String constructPrompt(String query, List<VectorStoreService.ScoredChunk> scoredChunks) {
        StringBuilder promptBuilder = new StringBuilder();
        
        // System instruction
        promptBuilder.append("You are a helpful AI assistant. Answer the user's question based on the provided context.\n");
        promptBuilder.append("If the context doesn't contain enough information to answer the question, say so honestly.\n");
        promptBuilder.append("Be concise and accurate in your response.\n\n");
        
        // Add context from retrieved chunks
        promptBuilder.append("Context:\n");
        promptBuilder.append("---\n");
        
        for (int i = 0; i < scoredChunks.size(); i++) {
            VectorStoreService.ScoredChunk scoredChunk = scoredChunks.get(i);
            Chunk chunk = scoredChunk.getChunk();
            
            promptBuilder.append("[Document: %s, Chunk %d]\n".formatted(
                chunk.getDocumentName(),
                chunk.getChunkIndex()));
            promptBuilder.append(chunk.getText());
            promptBuilder.append("\n\n");
        }
        
        promptBuilder.append("---\n\n");
        
        // User question
        promptBuilder.append("Question: ");
        promptBuilder.append(query);
        promptBuilder.append("\n\nAnswer:");
        
        return promptBuilder.toString();
    }
    
    /**
     * Perform semantic search without LLM generation
     * 
     * @param query Search query
     * @param topK Number of results
     * @param documentName Optional document filter
     * @return List of similar chunks
     */
    public List<QueryResponse.RetrievedChunk> semanticSearch(String query, Integer topK, String documentName) {
        logger.info("Performing semantic search: {}", query);
        
        try {
            float[] queryEmbedding = ollamaService.generateEmbedding(query);
            int k = (topK != null) ? topK : defaultTopK;
            
            List<VectorStoreService.ScoredChunk> scoredChunks = 
                vectorStoreService.searchSimilar(queryEmbedding, k, documentName);
            
            return scoredChunks.stream()
                .map(sc -> QueryResponse.RetrievedChunk.builder()
                    .text(sc.getChunk().getText())
                    .documentName(sc.getChunk().getDocumentName())
                    .score(sc.getScore())
                    .chunkIndex(sc.getChunk().getChunkIndex())
                    .build())
                .collect(Collectors.toList());
                
        } catch (IOException e) {
            logger.error("Error during semantic search", e);
            throw new RuntimeException("Failed to perform semantic search: " + e.getMessage(), e);
        }
    }
}
