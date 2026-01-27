#!/bin/bash

# Java RAG System - Setup and Run Script
# This script checks prerequisites and starts the application

echo "======================================"
echo "Java RAG System - Setup & Run"
echo "======================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check Java
echo -n "Checking Java... "
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        echo -e "${GREEN}✓ Java $JAVA_VERSION found${NC}"
    else
        echo -e "${RED}✗ Java 17+ required (found Java $JAVA_VERSION)${NC}"
        exit 1
    fi
else
    echo -e "${RED}✗ Java not found${NC}"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check Maven
echo -n "Checking Maven... "
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
    echo -e "${GREEN}✓ Maven $MVN_VERSION found${NC}"
else
    echo -e "${RED}✗ Maven not found${NC}"
    echo "Please install Maven"
    exit 1
fi

# Check Ollama
echo -n "Checking Ollama... "
if command -v ollama &> /dev/null; then
    echo -e "${GREEN}✓ Ollama found${NC}"
    
    # Check if Ollama is running
    if curl -s http://localhost:11434/api/tags > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Ollama is running${NC}"
    else
        echo -e "${YELLOW}⚠ Ollama is not running${NC}"
        echo "Starting Ollama..."
        ollama serve &
        sleep 3
    fi
    
    # Check required models
    echo "Checking Ollama models..."
    
    if ollama list | grep -q "nomic-embed-text"; then
        echo -e "${GREEN}✓ nomic-embed-text model found${NC}"
    else
        echo -e "${YELLOW}⚠ Installing nomic-embed-text model...${NC}"
        ollama pull nomic-embed-text
    fi
    
    if ollama list | grep -q "llama3"; then
        echo -e "${GREEN}✓ llama3 model found${NC}"
    else
        echo -e "${YELLOW}⚠ Installing llama3 model...${NC}"
        ollama pull llama3
    fi
else
    echo -e "${RED}✗ Ollama not found${NC}"
    echo "Please install Ollama from https://ollama.ai"
    exit 1
fi

echo ""
echo "======================================"
echo "All prerequisites satisfied!"
echo "======================================"
echo ""

# Create directories
echo "Creating data directories..."
mkdir -p data/uploads
mkdir -p data/lucene-index
echo -e "${GREEN}✓ Directories created${NC}"

# Build project
echo ""
echo "Building project..."
if mvn clean install -DskipTests; then
    echo -e "${GREEN}✓ Build successful${NC}"
else
    echo -e "${RED}✗ Build failed${NC}"
    exit 1
fi

# Start application
echo ""
echo "======================================"
echo "Starting Java RAG System..."
echo "======================================"
echo ""
echo "API will be available at: http://localhost:8080/api"
echo "Frontend will be available at: frontend/index.html"
echo ""
echo "Press Ctrl+C to stop"
echo ""

mvn spring-boot:run
