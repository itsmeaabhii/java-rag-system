# Contributing to Java RAG System

Thank you for your interest in contributing to the Java RAG System! This document provides guidelines and instructions for contributing to this project.

## 🤝 Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on what is best for the community
- Show empathy towards other community members

## 🚀 Getting Started

### Prerequisites

Before contributing, ensure you have:
- Java 17 or higher installed
- Maven for dependency management
- Ollama running locally for embeddings and LLM
- A code editor (IntelliJ IDEA, VS Code, or Eclipse)

### Setting Up Development Environment

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/your-username/java-rag-system.git
   cd java-rag-system
   ```

3. Install dependencies:
   ```bash
   mvn clean install
   ```

4. Start Ollama and pull required models:
   ```bash
   ollama pull nomic-embed-text
   ollama pull llama3
   ```

5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## 📝 How to Contribute

### Reporting Bugs

If you find a bug, please create an issue with:
- Clear, descriptive title
- Steps to reproduce the issue
- Expected behavior vs actual behavior
- Screenshots (if applicable)
- Environment details (OS, Java version, etc.)

### Suggesting Enhancements

For feature requests:
- Use a clear, descriptive title
- Provide detailed description of the proposed feature
- Explain why this feature would be useful
- Include examples or mockups if possible

### Pull Requests

1. **Create a Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```
   or
   ```bash
   git checkout -b fix/your-bug-fix
   ```

2. **Make Your Changes**
   - Follow the code style guidelines
   - Write meaningful commit messages
   - Add tests for new features
   - Update documentation as needed

3. **Test Your Changes**
   ```bash
   mvn test
   mvn spring-boot:run
   ```

4. **Commit Your Changes**
   ```bash
   git add .
   git commit -m "Add: brief description of changes"
   ```

5. **Push to Your Fork**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create Pull Request**
   - Go to the original repository
   - Click "New Pull Request"
   - Select your branch
   - Fill in the PR template

## 📋 Code Style Guidelines

### Java Code Style

- Follow standard Java naming conventions
- Use meaningful variable and method names
- Keep methods focused and concise (Single Responsibility Principle)
- Add JavaDoc comments for public methods and classes
- Maximum line length: 120 characters
- Use 4 spaces for indentation (no tabs)

### Example:

```java
/**
 * Processes a PDF document and extracts text content.
 *
 * @param file the PDF file to process
 * @return extracted text content
 * @throws IOException if file reading fails
 */
public String processPDF(File file) throws IOException {
    // Implementation
}
```

### Commit Message Guidelines

Use conventional commits format:

- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `style:` Code style changes (formatting, etc.)
- `refactor:` Code refactoring
- `test:` Adding or updating tests
- `chore:` Maintenance tasks

Example:
```
feat: add support for DOCX file processing
fix: resolve vector search accuracy issue
docs: update API documentation for query endpoint
```

## 🧪 Testing

- Write unit tests for new features
- Ensure all tests pass before submitting PR
- Aim for meaningful test coverage
- Test both happy path and error cases

Run tests:
```bash
mvn test
```

## 📚 Documentation

When adding new features:
- Update README.md if needed
- Add JavaDoc comments
- Update API_TESTING.md for new endpoints
- Add examples in QUICK_REFERENCE.md

## 🔍 Code Review Process

1. All PRs require review before merging
2. Address review comments promptly
3. Keep PRs focused and reasonably sized
4. Be open to feedback and suggestions

## 🌟 Areas for Contribution

We welcome contributions in:

- **Features**: New document formats, improved chunking strategies
- **Performance**: Optimization of vector search, caching improvements
- **Documentation**: Better examples, tutorials, API docs
- **Testing**: More comprehensive test coverage
- **UI/UX**: Frontend improvements, accessibility
- **Bug Fixes**: Any reported issues
- **Code Quality**: Refactoring, better error handling

## 💡 Need Help?

- Review existing issues and PRs
- Check the documentation in README.md
- Ask questions in issue comments
- Reach out to maintainers

## 📄 License

By contributing, you agree that your contributions will be licensed under the same license as the project.

---

Thank you for contributing to make this project better! 🎉
