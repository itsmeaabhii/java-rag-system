# Changelog

All notable changes to the RAG Chat Application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-02-17

### Added
- Health check endpoint with detailed system status
  - `/api/health` - Comprehensive health information
  - `/api/health/ready` - Readiness probe for container orchestration
  - `/api/health/live` - Liveness probe for monitoring
- Enhanced application startup logging with ASCII banner
- Detailed startup information showing all available endpoints
- Application metadata (name and version) in configuration
- Better error handling and logging across services

### Changed
- Improved application properties organization with application metadata
- Enhanced main application class with startup event listener
- Better code documentation and JavaDoc comments

### Technical Details
- Added HealthController for comprehensive monitoring
- Implemented ApplicationReadyEvent listener for startup logging
- Added version information to configuration
