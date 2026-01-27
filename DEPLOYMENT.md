# Deployment Guide - Java RAG System

## Prerequisites Checklist

Before deploying, ensure you have:

- ✅ Java 17+ installed
- ✅ Maven installed
- ✅ Ollama installed and running
- ✅ Required Ollama models downloaded:
  - `nomic-embed-text`
  - `llama3`

## Local Development Deployment

### Quick Start (macOS/Linux)

```bash
# Navigate to project directory
cd "/Users/abhishek/Desktop/java congichat"

# Run the setup script
./run.sh
```

The script will:
1. Check all prerequisites
2. Verify Ollama is running
3. Install missing models
4. Build the project
5. Start the application

### Manual Start

```bash
# 1. Start Ollama (if not running)
ollama serve

# 2. Pull required models
ollama pull nomic-embed-text
ollama pull llama3

# 3. Build project
mvn clean install

# 4. Run application
mvn spring-boot:run
```

### Verify Deployment

```bash
# Check if server is running
curl http://localhost:8080/api/query/health

# Expected response:
# {"status":"UP","service":"RAG Query Service"}
```

## Production Deployment

### Option 1: Standalone JAR

```bash
# 1. Build the JAR
mvn clean package

# 2. Run the JAR
java -jar target/java-rag-system-1.0.0.jar

# With custom configuration:
java -jar target/java-rag-system-1.0.0.jar \
  --server.port=8080 \
  --ollama.base-url=http://ollama-server:11434
```

### Option 2: Docker Deployment

Create `Dockerfile`:

```dockerfile
FROM openjdk:17-slim

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy JAR
COPY target/java-rag-system-1.0.0.jar app.jar

# Create data directories
RUN mkdir -p /app/data/uploads /app/data/lucene-index

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/api/query/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  ollama:
    image: ollama/ollama:latest
    container_name: ollama
    ports:
      - "11434:11434"
    volumes:
      - ollama-data:/root/.ollama
    restart: unless-stopped

  rag-backend:
    build: .
    container_name: java-rag-backend
    ports:
      - "8080:8080"
    environment:
      - OLLAMA_BASE_URL=http://ollama:11434
      - SPRING_PROFILES_ACTIVE=production
    volumes:
      - ./data:/app/data
    depends_on:
      - ollama
    restart: unless-stopped

volumes:
  ollama-data:
```

Deploy with Docker:

```bash
# Build and start services
docker-compose up -d

# Pull Ollama models
docker exec ollama ollama pull nomic-embed-text
docker exec ollama ollama pull llama3

# Check logs
docker-compose logs -f rag-backend

# Stop services
docker-compose down
```

### Option 3: Systemd Service (Linux)

Create `/etc/systemd/system/java-rag.service`:

```ini
[Unit]
Description=Java RAG System
After=network.target ollama.service

[Service]
Type=simple
User=raguser
WorkingDirectory=/opt/java-rag
ExecStart=/usr/bin/java -jar /opt/java-rag/java-rag-system-1.0.0.jar
Restart=on-failure
RestartSec=10

Environment="JAVA_OPTS=-Xmx2g -Xms512m"
Environment="SERVER_PORT=8080"

StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Manage the service:

```bash
# Reload systemd
sudo systemctl daemon-reload

# Start service
sudo systemctl start java-rag

# Enable on boot
sudo systemctl enable java-rag

# Check status
sudo systemctl status java-rag

# View logs
sudo journalctl -u java-rag -f
```

## Configuration for Production

### application-production.properties

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/api
server.compression.enabled=true

# File Upload
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# Ollama (External Server)
ollama.base-url=http://ollama-server:11434
ollama.embedding-model=nomic-embed-text
ollama.chat-model=llama3
ollama.timeout=180

# Chunking
chunking.size=500
chunking.overlap=100

# Vector Search
vector.top-k=5
vector.dimension=768

# Data Directories (Persistent Volumes)
app.upload.dir=/data/uploads
app.index.dir=/data/lucene-index

# Logging
logging.level.root=INFO
logging.level.com.ragchat=INFO
logging.file.name=/var/log/java-rag/application.log
logging.file.max-size=10MB
logging.file.max-history=30
```

Run with production profile:

```bash
java -jar app.jar --spring.profiles.active=production
```

## Reverse Proxy Setup (Nginx)

### /etc/nginx/sites-available/rag-system

```nginx
upstream rag_backend {
    server localhost:8080;
}

server {
    listen 80;
    server_name your-domain.com;

    # Redirect to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # File upload size
    client_max_body_size 100M;

    # API endpoints
    location /api/ {
        proxy_pass http://rag_backend/api/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts for long-running requests
        proxy_connect_timeout 180s;
        proxy_send_timeout 180s;
        proxy_read_timeout 180s;
    }

    # Frontend (if serving from same domain)
    location / {
        root /var/www/rag-frontend;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
}
```

Enable and restart Nginx:

```bash
sudo ln -s /etc/nginx/sites-available/rag-system /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

## Environment Variables

Set environment variables for configuration:

```bash
# Server
export SERVER_PORT=8080

# Ollama
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_EMBEDDING_MODEL=nomic-embed-text
export OLLAMA_CHAT_MODEL=llama3
export OLLAMA_TIMEOUT=120

# File paths
export APP_UPLOAD_DIR=/data/uploads
export APP_INDEX_DIR=/data/lucene-index

# Chunking
export CHUNKING_SIZE=500
export CHUNKING_OVERLAP=100

# Vector search
export VECTOR_TOP_K=5
export VECTOR_DIMENSION=768
```

## Monitoring Setup

### Application Metrics (Spring Boot Actuator)

Add to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Configure in `application.properties`:

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
```

Access metrics:
- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`

### Log Aggregation

Use standard logging tools:
- **ELK Stack** (Elasticsearch, Logstash, Kibana)
- **Grafana Loki**
- **Splunk**

## Backup Strategy

### 1. Backup Lucene Index

```bash
#!/bin/bash
# backup-index.sh

BACKUP_DIR="/backups/lucene-index"
INDEX_DIR="/data/lucene-index"
DATE=$(date +%Y%m%d_%H%M%S)

# Create backup
tar -czf "${BACKUP_DIR}/index_backup_${DATE}.tar.gz" -C "${INDEX_DIR}" .

# Keep only last 7 backups
ls -t "${BACKUP_DIR}"/index_backup_*.tar.gz | tail -n +8 | xargs rm -f
```

### 2. Backup Uploaded Documents

```bash
#!/bin/bash
# backup-documents.sh

BACKUP_DIR="/backups/documents"
UPLOAD_DIR="/data/uploads"
DATE=$(date +%Y%m%d_%H%M%S)

# Create backup
tar -czf "${BACKUP_DIR}/docs_backup_${DATE}.tar.gz" -C "${UPLOAD_DIR}" .

# Keep only last 30 backups
ls -t "${BACKUP_DIR}"/docs_backup_*.tar.gz | tail -n +31 | xargs rm -f
```

### 3. Automated Backups (Cron)

```bash
# Add to crontab (crontab -e)

# Backup index every 6 hours
0 */6 * * * /opt/scripts/backup-index.sh

# Backup documents daily at 2 AM
0 2 * * * /opt/scripts/backup-documents.sh
```

## Performance Tuning

### JVM Options

```bash
java -Xms1g -Xmx4g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/var/log/java-rag/heap-dump.hprof \
     -jar app.jar
```

### Connection Pooling

Configure in `application.properties`:

```properties
# Thread pool
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=10

# Connection timeout
server.connection-timeout=60000
```

## Troubleshooting

### Check Application Logs

```bash
# If using journald
sudo journalctl -u java-rag -f

# If using file logging
tail -f /var/log/java-rag/application.log
```

### Check Ollama Connection

```bash
# Test Ollama endpoint
curl http://localhost:11434/api/tags

# Test embedding generation
curl http://localhost:11434/api/embeddings \
  -d '{"model":"nomic-embed-text","prompt":"test"}'
```

### Check Disk Space

```bash
# Check available space
df -h

# Check index size
du -sh /data/lucene-index

# Check uploads size
du -sh /data/uploads
```

### Restart Services

```bash
# Restart application
sudo systemctl restart java-rag

# Restart Ollama
sudo systemctl restart ollama

# Restart Nginx
sudo systemctl restart nginx
```

## Security Hardening

### 1. Firewall Rules

```bash
# Allow only necessary ports
sudo ufw allow 22/tcp   # SSH
sudo ufw allow 80/tcp   # HTTP
sudo ufw allow 443/tcp  # HTTPS
sudo ufw enable

# Block direct access to application port
sudo ufw deny 8080/tcp
```

### 2. Run as Non-Root User

```bash
# Create dedicated user
sudo useradd -r -s /bin/false raguser

# Set ownership
sudo chown -R raguser:raguser /opt/java-rag
sudo chown -R raguser:raguser /data
```

### 3. Secure File Permissions

```bash
# Application files
chmod 755 /opt/java-rag
chmod 644 /opt/java-rag/*.jar

# Data directories
chmod 700 /data/uploads
chmod 700 /data/lucene-index
```

## Scaling Considerations

### Vertical Scaling
- Increase JVM heap size
- Add more CPU cores
- Increase RAM

### Horizontal Scaling
- Use external vector database (pgvector, Weaviate)
- Shared file storage (NFS, S3)
- Load balancer for multiple instances
- Redis for caching

## Cost Optimization

### Ollama Model Selection

| Model | Size | Speed | Quality | Use Case |
|-------|------|-------|---------|----------|
| nomic-embed-text | 274MB | Fast | Good | Embeddings |
| llama3:8b | 4.7GB | Fast | Good | General chat |
| llama3:70b | 40GB | Slow | Excellent | High-quality answers |

Choose based on your requirements and available resources.

## Support and Maintenance

### Regular Maintenance Tasks

**Daily**
- Monitor application logs
- Check disk space
- Verify backup completion

**Weekly**
- Review error logs
- Check performance metrics
- Update dependencies (if needed)

**Monthly**
- Review and optimize index
- Clean up old documents
- Security updates

**Quarterly**
- Capacity planning review
- Performance optimization
- User feedback integration

---

**Deployment completed!** 🚀

For issues or questions, check:
- Application logs
- [README.md](README.md) for basic usage
- [ARCHITECTURE.md](ARCHITECTURE.md) for system details
- [API_TESTING.md](API_TESTING.md) for testing
