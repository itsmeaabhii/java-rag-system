package com.ragchat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuration for application directories
 */
@Configuration
public class AppConfig {
    
    @Value("${app.upload.dir:./data/uploads}")
    private String uploadDir;
    
    @Value("${app.index.dir:./data/lucene-index}")
    private String indexDir;
    
    @PostConstruct
    public void init() throws IOException {
        // Create directories if they don't exist
        createDirectoryIfNotExists(uploadDir);
        createDirectoryIfNotExists(indexDir);
    }
    
    private void createDirectoryIfNotExists(String dirPath) throws IOException {
        Path path = Path.of(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}
