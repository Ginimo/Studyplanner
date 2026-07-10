## Tech Stack
| Category | Technology | Description |
|---|---|---|
| **Core Framework** | Spring Boot 3.4.4 | Full-stack Java framework |
| **Core Framework** | Java 25 | Programming language |
| **Web & MVC** | Spring Web | RESTful web services and MVC |
| **Web & MVC** | Thymeleaf | Server-side template engine |
| **Web & MVC** | Spring Validation | Input validation and constraint annotations |
| **Data Processing** | Jackson | JSON serialization/deserialization |
| **Data Processing** | Jackson Datatype JSR310 | Java 8+ date/time support |
| **Testing & Quality** | JUnit 5 | Testing framework |
| **Testing & Quality** | Mockito | Mocking framework for unit tests |
| **Testing & Quality** | ArchUnit 1.4.1 | Architecture tests and violations detection |
| **Testing & Quality** | JMolecules ArchUnit 0.21.0 | DDD architecture rules validation |
| **Testing & Quality** | EqualsVerifier 3.14.3 | Verifies equals() and hashCode() implementations |
| **Build** | Maven 3.x | Build automation and dependency management |
| **Build** | Spotless 3.6.0 | Code formatting (Google Java Format 1.35.0) — enforced on `mvn verify` |
| **Build** | git-build-hook 3.6.0 | Installs Git hooks from `.githooks/` on `mvn install` |

## Domain-Specific Diagrams (Backend)
- **Model: Core data structures**
- **Service: Location of the business logic**
- **Repository: Abstraction of data access**
- **Controller: Link between UI and services**

## UI Diagrams (Frontend)
- **Forms: DTOs for data binding of HTML forms**
- **Views: Data objects for display in templates**
