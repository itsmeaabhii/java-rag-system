// ============================================
// Theme Management
// ============================================

function initTheme() {
    const theme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', theme);
}

function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'light' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('theme', newTheme);
    
    showToast('Theme changed', `Switched to ${newTheme} mode`, 'info');
}

// ============================================
// Toast Notifications
// ============================================

function showToast(title, message, type = 'info') {
    const toast = document.getElementById('toast');
    const toastIcon = toast.querySelector('.toast-icon');
    const toastTitle = toast.querySelector('.toast-title');
    const toastMessage = toast.querySelector('.toast-message');
    
    // Remove previous type classes
    toast.classList.remove('success', 'error', 'info');
    
    // Set content
    toastTitle.textContent = title;
    toastMessage.textContent = message;
    
    // Add type class
    toast.classList.add(type);
    
    // Show toast
    toast.classList.add('show');
    
    // Hide after 3 seconds
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// ============================================
// File Upload Handling
// ============================================

let uploadedFile = null;

function initFileUpload() {
    const uploadArea = document.getElementById('upload-area');
    const fileInput = document.getElementById('file-input');
    const fileInfo = document.getElementById('file-info');
    
    // Click to upload
    uploadArea.addEventListener('click', () => {
        fileInput.click();
    });
    
    // File selected
    fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            handleFileSelect(file);
        }
    });
    
    // Drag and drop
    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.style.borderColor = 'var(--blue-500)';
        uploadArea.style.background = 'var(--blue-50)';
    });
    
    uploadArea.addEventListener('dragleave', () => {
        uploadArea.style.borderColor = '';
        uploadArea.style.background = '';
    });
    
    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        uploadArea.style.borderColor = '';
        uploadArea.style.background = '';
        
        const file = e.dataTransfer.files[0];
        if (file) {
            handleFileSelect(file);
        }
    });
}

function handleFileSelect(file) {
    const fileInfo = document.getElementById('file-info');
    const allowedTypes = ['.txt', '.md', '.doc', '.docx', '.pdf'];
    const fileExt = '.' + file.name.split('.').pop().toLowerCase();
    
    if (!allowedTypes.includes(fileExt)) {
        showToast('Invalid file type', 'Please upload TXT, MD, DOC, DOCX, or PDF files', 'error');
        return;
    }
    
    if (file.size > 10 * 1024 * 1024) { // 10MB
        showToast('File too large', 'Maximum file size is 10MB', 'error');
        return;
    }
    
    uploadedFile = file;
    fileInfo.textContent = `📄 ${file.name} (${formatFileSize(file.size)})`;
    fileInfo.classList.add('show');
    
    showToast('File uploaded', `${file.name} ready to process`, 'success');
    updateWorkflowStep(1);
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

// ============================================
// Text Counter
// ============================================

function initTextCounter() {
    const textInput = document.getElementById('text-input');
    const counter = document.getElementById('text-counter');
    
    textInput.addEventListener('input', () => {
        const text = textInput.value.trim();
        const charCount = text.length;
        counter.textContent = `${charCount} character${charCount !== 1 ? 's' : ''}`;
        
        if (text) {
            updateWorkflowStep(1);
        }
    });
}

// ============================================
// Workflow Progress
// ============================================

function updateWorkflowStep(step) {
    const steps = document.querySelectorAll('.workflow-step');
    steps.forEach((s, index) => {
        if (index < step) {
            s.classList.add('active');
        }
    });
}

// ============================================
// Document Ingestion
// ============================================

async function ingestDocument() {
    const sourceName = document.getElementById('source-name').value.trim();
    const textInput = document.getElementById('text-input').value.trim();
    
    // Validation
    if (!sourceName) {
        showToast('Missing information', 'Please enter a document name', 'error');
        return;
    }
    
    if (!uploadedFile && !textInput) {
        showToast('No content', 'Please upload a file or paste text content', 'error');
        return;
    }
    
    // Show loading
    showToast('Processing', 'Ingesting document into knowledge base...', 'info');
    
    // Simulate processing
    await sleep(1500);
    
    // Success
    showToast('Success', `Document "${sourceName}" ingested successfully`, 'success');
    updateWorkflowStep(2);
}

// ============================================
// Clear Functions
// ============================================

function clearIngest() {
    document.getElementById('source-name').value = '';
    document.getElementById('text-input').value = '';
    document.getElementById('file-input').value = '';
    document.getElementById('file-info').classList.remove('show');
    document.getElementById('file-info').textContent = '';
    document.getElementById('text-counter').textContent = '0 characters';
    uploadedFile = null;
    
    showToast('Cleared', 'Input fields have been reset', 'info');
}

function clearQuery() {
    document.getElementById('query-input').value = '';
    showToast('Cleared', 'Query field has been reset', 'info');
}

// ============================================
// Query Processing
// ============================================

async function processQuery() {
    const query = document.getElementById('query-input').value.trim();
    
    if (!query) {
        showToast('Empty query', 'Please enter a question', 'error');
        return;
    }
    
    // Update UI states
    document.getElementById('empty-state').style.display = 'none';
    document.getElementById('loading-state').classList.add('show');
    document.getElementById('results').classList.remove('show');
    
    // Simulate API call
    await sleep(2000);
    
    // Mock response
    const answer = generateMockAnswer(query);
    const sources = generateMockSources();
    
    // Display results
    displayResults(answer, sources);
    updateWorkflowStep(3);
}

function generateMockAnswer(query) {
    return `Based on the analyzed documents, here's a comprehensive answer to your query about "${query}":

The information shows that this topic involves multiple interconnected concepts. The primary finding indicates significant correlations between the key elements discussed in the source material. 

Key points include:
• Detailed analysis of the core concepts
• Evidence-based conclusions from the document
• Practical implications and applications
• Recommended approaches based on the findings

This answer synthesizes information from the ingested documents to provide you with actionable insights.`;
}

function generateMockSources() {
    return [
        'Section 2.3: "Introduction to Core Concepts" - This section provides foundational understanding of the main topic.',
        'Section 4.1: "Detailed Analysis" - Contains in-depth examination of key principles and their applications.',
        'Section 6.2: "Practical Implications" - Discusses real-world scenarios and implementation strategies.'
    ];
}

function displayResults(answer, sources) {
    // Hide loading
    document.getElementById('loading-state').classList.remove('show');
    
    // Show results container
    const resultsContainer = document.getElementById('results');
    resultsContainer.classList.add('show');
    
    // Display answer
    const answerSection = document.getElementById('answer-section');
    const answerText = document.getElementById('answer-text');
    answerText.textContent = answer;
    answerSection.classList.add('show');
    
    // Display sources
    const sourcesSection = document.getElementById('sources-section');
    const sourcesList = document.getElementById('sources-list');
    sourcesList.innerHTML = '';
    
    sources.forEach(source => {
        const sourceItem = document.createElement('div');
        sourceItem.className = 'source-item';
        sourceItem.textContent = source;
        sourcesList.appendChild(sourceItem);
    });
    
    sourcesSection.classList.add('show');
    
    showToast('Complete', 'Analysis finished successfully', 'success');
}

// ============================================
// Utility Functions
// ============================================

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

// ============================================
// Event Listeners
// ============================================

function initEventListeners() {
    // Theme toggle
    document.getElementById('theme-toggle').addEventListener('click', toggleTheme);
    
    // Ingest button
    document.getElementById('ingest-button').addEventListener('click', ingestDocument);
    
    // Clear ingest button
    document.getElementById('clear-ingest-button').addEventListener('click', clearIngest);
    
    // Query button
    document.getElementById('query-button').addEventListener('click', processQuery);
    
    // Clear query button
    document.getElementById('clear-query-button').addEventListener('click', clearQuery);
    
    // Enter key for query
    document.getElementById('query-input').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            processQuery();
        }
    });
}

// ============================================
// Initialization
// ============================================

document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    initFileUpload();
    initTextCounter();
    initEventListeners();
    
    console.log('CogniChat initialized successfully');
});