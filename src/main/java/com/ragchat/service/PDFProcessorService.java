package com.ragchat.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Service for processing PDF files and extracting text
 */
@Service
public class PDFProcessorService {
    
    private static final Logger logger = LoggerFactory.getLogger(PDFProcessorService.class);
    
    /**
     * Extract text from a PDF file
     * 
     * @param pdfFile The PDF file to process
     * @return Extracted text content
     * @throws IOException if file reading fails
     */
    public String extractText(File pdfFile) throws IOException {
        logger.info("Extracting text from PDF: {}", pdfFile.getName());
        
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            
            String text = stripper.getText(document);
            
            logger.info("Successfully extracted {} characters from {}", 
                       text.length(), pdfFile.getName());
            
            return text;
        } catch (IOException e) {
            logger.error("Failed to extract text from PDF: {}", pdfFile.getName(), e);
            throw new IOException("Failed to process PDF file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validate if a file is a valid PDF
     * 
     * @param file The file to validate
     * @return true if valid PDF, false otherwise
     */
    public boolean isValidPDF(File file) {
        try (PDDocument document = Loader.loadPDF(file)) {
            return document.getNumberOfPages() > 0;
        } catch (IOException e) {
            logger.warn("Invalid PDF file: {}", file.getName());
            return false;
        }
    }
}
