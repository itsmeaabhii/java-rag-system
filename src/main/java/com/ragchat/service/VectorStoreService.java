package com.ragchat.service;

import com.ragchat.model.Chunk;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for storing and searching vector embeddings using Apache Lucene
 */
@Service
public class VectorStoreService {
    
    private static final Logger logger = LoggerFactory.getLogger(VectorStoreService.class);
    
    @Value("${app.index.dir:./data/lucene-index}")
    private String indexDir;
    
    @Value("${vector.dimension:768}")
    private int vectorDimension;
    
    private Directory directory;
    private IndexWriter indexWriter;
    private SearcherManager searcherManager;
    
    @PostConstruct
    public void init() throws IOException {
        Path indexPath = Path.of(indexDir);
        directory = FSDirectory.open(indexPath);
        
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        
        indexWriter = new IndexWriter(directory, config);
        searcherManager = new SearcherManager(indexWriter, null);
        
        logger.info("Lucene vector store initialized at: {}", indexPath);
    }
    
    @PreDestroy
    public void cleanup() throws IOException {
        if (searcherManager != null) {
            searcherManager.close();
        }
        if (indexWriter != null) {
            indexWriter.close();
        }
        if (directory != null) {
            directory.close();
        }
        logger.info("Lucene vector store closed");
    }
    
    /**
     * Index a chunk with its embedding vector
     * 
     * @param chunk The chunk to index
     */
    public void indexChunk(Chunk chunk) throws IOException {
        logger.debug("Indexing chunk: {} from document: {}", 
                    chunk.getId(), chunk.getDocumentName());
        
        Document doc = new Document();
        
        // Add metadata fields
        doc.add(new StringField("id", chunk.getId(), Field.Store.YES));
        doc.add(new StringField("documentName", chunk.getDocumentName(), Field.Store.YES));
        doc.add(new TextField("text", chunk.getText(), Field.Store.YES));
        doc.add(new IntPoint("chunkIndex", chunk.getChunkIndex()));
        doc.add(new StoredField("chunkIndex", chunk.getChunkIndex()));
        doc.add(new LongPoint("timestamp", chunk.getTimestamp()));
        doc.add(new StoredField("timestamp", chunk.getTimestamp()));
        
        // Add embedding vector as binary field
        if (chunk.getEmbedding() != null) {
            byte[] vectorBytes = floatArrayToBytes(chunk.getEmbedding());
            doc.add(new StoredField("embedding", vectorBytes));
        }
        
        indexWriter.addDocument(doc);
        indexWriter.commit();
        searcherManager.maybeRefresh();
        
        logger.debug("Successfully indexed chunk: {}", chunk.getId());
    }
    
    /**
     * Search for similar chunks using cosine similarity
     * 
     * @param queryEmbedding The query embedding vector
     * @param topK Number of results to return
     * @return List of similar chunks with scores
     */
    public List<ScoredChunk> searchSimilar(float[] queryEmbedding, int topK) throws IOException {
        return searchSimilar(queryEmbedding, topK, null);
    }
    
    /**
     * Search for similar chunks with optional document filter
     * 
     * @param queryEmbedding The query embedding vector
     * @param topK Number of results to return
     * @param documentName Optional document name filter
     * @return List of similar chunks with scores
     */
    public List<ScoredChunk> searchSimilar(float[] queryEmbedding, int topK, String documentName) 
            throws IOException {
        logger.info("Searching for top {} similar chunks", topK);
        
        IndexSearcher searcher = searcherManager.acquire();
        List<ScoredChunk> results = new ArrayList<>();
        
        try {
            Query query = new MatchAllDocsQuery();
            
            // Apply document filter if specified
            if (documentName != null && !documentName.isEmpty()) {
                query = new TermQuery(new Term("documentName", documentName));
            }
            
            TopDocs topDocs = searcher.search(query, 1000);
            
            // Calculate cosine similarity for each document
            List<ScoredDocument> scoredDocs = new ArrayList<>();
            
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                
                BytesRef embeddingBytes = doc.getBinaryValue("embedding");
                if (embeddingBytes != null) {
                    float[] docEmbedding = bytesToFloatArray(embeddingBytes.bytes);
                    float similarity = cosineSimilarity(queryEmbedding, docEmbedding);
                    
                    scoredDocs.add(new ScoredDocument(doc, similarity, scoreDoc.doc));
                }
            }
            
            // Sort by similarity score (descending)
            scoredDocs.sort((a, b) -> Float.compare(b.score, a.score));
            
            // Take top K results
            int limit = Math.min(topK, scoredDocs.size());
            for (int i = 0; i < limit; i++) {
                ScoredDocument scoredDoc = scoredDocs.get(i);
                Document doc = scoredDoc.document;
                
                Chunk chunk = new Chunk(
                    doc.get("id"),
                    doc.get("documentName"),
                    doc.get("text"),
                    doc.getField("chunkIndex").numericValue().intValue()
                );
                
                results.add(new ScoredChunk(chunk, scoredDoc.score));
            }
            
            logger.info("Found {} similar chunks", results.size());
            
        } finally {
            searcherManager.release(searcher);
        }
        
        return results;
    }
    
    /**
     * Delete all chunks for a specific document
     * 
     * @param documentName The document name
     */
    public void deleteByDocument(String documentName) throws IOException {
        logger.info("Deleting all chunks for document: {}", documentName);
        
        indexWriter.deleteDocuments(new Term("documentName", documentName));
        indexWriter.commit();
        searcherManager.maybeRefresh();
    }
    
    /**
     * Get total number of indexed chunks
     */
    public int getTotalChunks() {
        try {
            IndexSearcher searcher = searcherManager.acquire();
            try {
                return searcher.getIndexReader().numDocs();
            } finally {
                searcherManager.release(searcher);
            }
        } catch (IOException e) {
            logger.error("Failed to get total chunks", e);
            return 0;
        }
    }
    
    /**
     * Calculate cosine similarity between two vectors
     */
    private float cosineSimilarity(float[] vec1, float[] vec2) {
        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }
        
        float dotProduct = 0.0f;
        float norm1 = 0.0f;
        float norm2 = 0.0f;
        
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }
        
        if (norm1 == 0.0f || norm2 == 0.0f) {
            return 0.0f;
        }
        
        return dotProduct / (float) (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    /**
     * Convert float array to byte array
     */
    private byte[] floatArrayToBytes(float[] floats) {
        ByteBuffer buffer = ByteBuffer.allocate(floats.length * 4);
        for (float f : floats) {
            buffer.putFloat(f);
        }
        return buffer.array();
    }
    
    /**
     * Convert byte array to float array
     */
    private float[] bytesToFloatArray(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        float[] floats = new float[bytes.length / 4];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = buffer.getFloat();
        }
        return floats;
    }
    
    /**
     * Helper class for scored chunks
     */
    public static class ScoredChunk {
        private final Chunk chunk;
        private final float score;
        
        public ScoredChunk(Chunk chunk, float score) {
            this.chunk = chunk;
            this.score = score;
        }
        
        public Chunk getChunk() {
            return chunk;
        }
        
        public float getScore() {
            return score;
        }
    }
    
    /**
     * Helper class for scored documents
     */
    private static class ScoredDocument {
        final Document document;
        final float score;
        final int docId;
        
        ScoredDocument(Document document, float score, int docId) {
            this.document = document;
            this.score = score;
            this.docId = docId;
        }
    }
}
