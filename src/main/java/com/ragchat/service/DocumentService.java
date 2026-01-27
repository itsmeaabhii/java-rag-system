package com.ragchat.service;

import com.ragchat.dto.UploadResponse;
import com.ragchat.model.Chunk;
import com.ragchat.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing document uploads and processing
 */
@Service
public class DocumentService {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    
    @Value("${app.upload.dir:./data/uploads}")
    private String uploadDir;
    
    private final PDFProcessorService pdfProcessorService;
    private final ChunkingService chunkingService;
    private final OllamaService ollamaService;
    private final VectorStoreService vectorStoreService;
    
    public DocumentService(PDFProcessorService pdfProcessorService,
                          ChunkingService chunkingService,
                          OllamaService ollamaService,
                          VectorStoreService vectorStoreService) {
        this.pdfProcessorService = pdfProcessorService;
        this.chunkingService = chunkingService;
        this.ollamaService = ollamaService;
        this.vectorStoreService = vectorStoreService;
    }
    
    /**
     * Process and index an uploaded PDF document
     * 
     * @param file The uploaded PDF file
     * @return Upload response with processing details
     */
    public UploadResponse processDocument(MultipartFile file) {
        long startTime = System.currentTimeMillis();
        
        logger.info("Processing uploaded document: {}", file.getOriginalFilename());
        
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }
            
            if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                throw new IllegalArgumentException("Only PDF files are supported");
            }
            
            // Save file
            File savedFile = saveFile(file);
            
            // Validate PDF
            if (!pdfProcessorService.isValidPDF(savedFile)) {
                throw new IllegalArgumentException("Invalid PDF file");
            }
            
            // Extract text
            String text = pdfProcessorService.extractText(savedFile);
            
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("No text content found in PDF");
            }
            
            // Chunk text
            List<Chunk> chunks = chunkingService.chunkText(text, file.getOriginalFilename());
            
            if (chunks.isEmpty()) {
                throw new IllegalArgumentException("Failed to create chunks from document");
            }
            
            logger.info("Created {} chunks, generating embeddings...", chunks.size());
            
            // Generate embeddings and index chunks
            int indexedChunks = 0;
            for (Chunk chunk : chunks) {
                try {
                    float[] embedding = ollamaService.generateEmbedding(chunk.getText());
                    chunk.setEmbedding(embedding);
                    vectorStoreService.indexChunk(chunk);
                    indexedChunks++;
                    
                    if (indexedChunks % 10 == 0) {
                        logger.info("Indexed {}/{} chunks", indexedChunks, chunks.size());
                    }
                } catch (Exception e) {
                    logger.error("Failed to index chunk {}", chunk.getId(), e);
                }
            }
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            logger.info("Successfully processed document: {} ({} chunks in {}ms)",
                       file.getOriginalFilename(), indexedChunks, processingTime);
            
            return UploadResponse.builder()
                .documentId(UUID.randomUUID().toString())
                .documentName(file.getOriginalFilename())
                .chunksCreated(indexedChunks)
                .status("SUCCESS")
                .message("Document processed and indexed successfully")
                .processingTimeMs(processingTime)
                .build();
                
        } catch (Exception e) {
            logger.error("Failed to process document: {}", file.getOriginalFilename(), e);
            
            return UploadResponse.builder()
                .documentName(file.getOriginalFilename())
                .chunksCreated(0)
                .status("FAILED")
                .message("Error: " + e.getMessage())
                .processingTimeMs(System.currentTimeMillis() - startTime)
                .build();
        }
    }
    
    /**
     * Save uploaded file to disk
     */
    private File saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Path.of(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        
        Files.copy(file.getInputStream(), filePath);
        
        logger.info("File saved to: {}", filePath);
        
        return filePath.toFile();
    }
    
    /**
     * Delete a document and its chunks from the index
     */
    public void deleteDocument(String documentName) throws IOException {
        logger.info("Deleting document: {}", documentName);
        vectorStoreService.deleteByDocument(documentName);
    }
    
    /**
     * Get total number of indexed chunks
     */
    public int getTotalIndexedChunks() {
        return vectorStoreService.getTotalChunks();
    }
}
