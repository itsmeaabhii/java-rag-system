# CogniChat - Modern RAG UI

A beautiful, responsive web interface for the Java RAG Chat System.

## 🎨 Features

### Design
- **Modern Aesthetics**: Clean, professional interface with subtle gradients
- **Dark Mode**: Seamless theme switching with automatic persistence
- **Responsive Layout**: Optimized for all screen sizes
- **Visual Feedback**: Animated transitions and toast notifications

### User Experience
- **3-Step Workflow**: Visual progress through Upload → Query → Results
- **Drag & Drop**: Intuitive file upload with drag-and-drop support
- **Real-time Updates**: Character counters, loading states, and progress indicators
- **Multiple Input Methods**: Upload files OR paste text content directly

### Technical
- **No Build Required**: Pure HTML, CSS, and JavaScript
- **localStorage**: Theme preference persistence
- **Modern CSS**: CSS Variables, Grid, Flexbox
- **Vanilla JavaScript**: No framework dependencies

## 📁 Files

### `one.html`
Main HTML structure including:
- Document upload form with file input and text area
- Query interface with search functionality
- Results display with answer and sources sections
- Empty state, loading state, and success state UI

### `one.css`
Comprehensive design system:
- CSS variables for colors, spacing, and typography
- Light and dark theme definitions
- Component styles (cards, buttons, forms, etc.)
- Responsive breakpoints for mobile/tablet/desktop
- Animations and transitions

### `one.js`
Interactive functionality:
- Theme management (toggle between light/dark)
- File upload handling (validation, drag & drop)
- Document ingestion workflow
- Query processing and display
- Toast notification system
- Workflow step progression

## 🚀 Quick Start

1. **Start the Backend**
   ```bash
   cd "/Users/abhishek/Desktop/java congichat"
   mvn spring-boot:run
   ```

2. **Open the UI**
   ```bash
   # macOS
   open frontend/one.html
   
   # Linux
   xdg-open frontend/one.html
   
   # Windows
   start frontend/one.html
   ```

## 🎯 Usage Guide

### 1. Upload a Document
- Enter a document name (required)
- Either:
  - Drag and drop a file (PDF, TXT, MD, DOC, DOCX)
  - Click to browse and select a file
  - Paste text content directly
- Click "Ingest Document"

### 2. Ask a Question
- Type your question in the query input
- Press Enter or click "Get Answer"

### 3. View Results
- See AI-generated answer with formatting
- Review source sections from the documents
- Use the information for your needs

## ⚙️ Configuration

### API Endpoint
By default, the UI expects the backend at `http://localhost:8080/api`. To change this, modify the API URLs in `one.js`:

```javascript
// Find and update these URLs
const UPLOAD_URL = 'http://your-api-url:port/api/documents/upload';
const QUERY_URL = 'http://your-api-url:port/api/query';
```

### File Upload Limits
Default limits in `one.js`:
```javascript
const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
const ALLOWED_TYPES = ['.txt', '.md', '.doc', '.docx', '.pdf'];
```

### Theme
The default theme is set to 'light'. To change the default, modify `one.js`:
```javascript
function initTheme() {
    const theme = localStorage.getItem('theme') || 'dark'; // Change 'light' to 'dark'
    document.documentElement.setAttribute('data-theme', theme);
}
```

## 🎨 Customization

### Colors
Edit CSS variables in `one.css`:
```css
:root {
    --blue-600: #2563eb;  /* Change primary color */
    --purple-600: #7c3aed; /* Change accent color */
    /* ... more variables */
}
```

### Fonts
Change font families in the HTML or CSS:
```html
<!-- In one.html, update the Google Fonts link -->
<link href="https://fonts.googleapis.com/css2?family=Your+Font&display=swap">
```

## 🔧 Browser Support

- Chrome/Edge 90+
- Firefox 88+
- Safari 14+
- Opera 76+

## 📱 Responsive Breakpoints

- **Desktop**: 1024px and above (2-column layout)
- **Tablet**: 768px - 1023px (1-column layout)
- **Mobile**: Below 768px (compact layout)

## 🐛 Troubleshooting

### UI Not Loading
- Check browser console for errors
- Ensure all three files (HTML, CSS, JS) are in the same directory
- Verify file references in `one.html` are correct

### Backend Connection Issues
- Confirm backend is running on `http://localhost:8080`
- Check browser console for CORS errors
- Verify CORS is properly configured in Spring Boot

### Theme Not Persisting
- Check browser localStorage is enabled
- Try clearing localStorage and re-selecting theme
- Ensure JavaScript is enabled

## 💡 Tips

1. **Better Results**: Upload relevant documents before querying
2. **Clear Queries**: Ask specific, well-formed questions
3. **Mobile Use**: The interface is fully functional on mobile devices
4. **Keyboard Shortcuts**: Press Enter in query input to submit

---

**Enjoy using CogniChat! 🚀**
