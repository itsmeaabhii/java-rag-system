package com.ragchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

/**
 * Main application class for RAG Chat System
 * 
 * A Retrieval Augmented Generation system that provides intelligent
 * document search and AI-powered question answering capabilities.
 * 
 * @author RAG Team
 * @version 1.0.0
 */
@SpringBootApplication
public class RagApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(RagApplication.class);
    
    private final Environment environment;
    
    public RagApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }
    
    /**
     * Log application information after startup
     */
    @EventListener(ApplicationReadyEvent.class)
    public void logApplicationStartup() {
        String port = environment.getProperty("server.port", "8080");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        String baseUrl = "http://localhost:" + port + contextPath;
        
        logger.info("\n" +
            "==============================================\n" +
            "   RAG Chat Application Started Successfully\n" +
            "==============================================\n" +
            "   📚 API Base URL: {}\n" +
            "   🏥 Health Check: {}/health\n" +
            "   📄 Upload Docs: {}/documents/upload\n" +
            "   💬 Query API: {}/query\n" +
            "   🔧 Ollama URL: {}\n" +
            "==============================================",
            baseUrl,
            baseUrl,
            baseUrl,
            baseUrl,
            environment.getProperty("ollama.base-url", "http://localhost:11434")
        );
    }
}
