package com.ragchat.controller;

import com.ragchat.dto.UploadResponse;
import com.ragchat.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for document management operations
 */
@RestController
@RequestMapping("/documents")
@CrossOrigin(origins = "*")
public class DocumentController {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    
    private final DocumentService documentService;
    
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    /**
     * Upload and process a PDF document
     * 
     * POST /api/documents/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadDocument(
            @RequestParam("file") MultipartFile file) {
        
        logger.info("Received upload request for file: {}", file.getOriginalFilename());
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(UploadResponse.builder()
                        .status("FAILED")
                        .message("File is empty")
                        .build());
            }
            
            UploadResponse response = documentService.processDocument(file);
            
            if ("SUCCESS".equals(response.getStatus())) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error processing upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(UploadResponse.builder()
                    .status("FAILED")
                    .message("Server error: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Delete a document and its chunks
     * 
     * DELETE /api/documents/{documentName}
     */
    @DeleteMapping("/{documentName}")
    public ResponseEntity<Map<String, String>> deleteDocument(
            @PathVariable String documentName) {
        
        logger.info("Received delete request for document: {}", documentName);
        
        try {
            documentService.deleteDocument(documentName);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Document deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error deleting document", e);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "FAILED");
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
        }
    }
    
    /**
     * Get system status and statistics
     * 
     * GET /api/documents/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        logger.info("Received status request");
        
        Map<String, Object> status = new HashMap<>();
        status.put("status", "ONLINE");
        status.put("totalIndexedChunks", documentService.getTotalIndexedChunks());
        status.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }
}
