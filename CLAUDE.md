# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

This is a Spring Boot-based development playground repository with the following structure:

- `DevPlaygroundBackEnd/` - Spring Boot backend application
  - Built with Gradle and Java 21
  - Uses Spring Boot 3.5.6 with Spring Security, Spring Data JPA, and Spring Web
  - H2 in-memory database for development
  - Lombok for reducing boilerplate code

## Essential Commands

All commands should be run from the `DevPlaygroundBackEnd/` directory:

### Build and Run
```bash
cd DevPlaygroundBackEnd
./gradlew build          # Build the project
./gradlew bootRun        # Run the Spring Boot application
./gradlew clean          # Clean build artifacts
```

### Testing
```bash
./gradlew test           # Run all tests
./gradlew check          # Run all checks including tests
```

### Development
```bash
./gradlew bootTestRun    # Run with test configuration
./gradlew classes        # Compile main classes only
./gradlew testClasses    # Compile test classes only
```

## Application Configuration

- **Port**: 8080 (configured in `src/main/resources/application.properties`)
- **Database**: H2 in-memory database
- **Security**: Spring Security is enabled
- **Hot Reload**: Spring Boot DevTools is included for development

## Architecture Notes

- Standard Spring Boot application structure under `com.example.DevPlayground`
- Main application class: `DevPlaygroundApplication.java`
- Uses JPA for data persistence with H2 database
- Security layer configured but minimal implementation
- Test structure mirrors main package structure