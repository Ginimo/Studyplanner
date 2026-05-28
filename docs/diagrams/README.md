### Domain-Specific Diagrams (Backend)

- **domain_class_diagram.puml**:
    - **Model**: Core data structures
    - **Service**: Location of the business logic
    - **Repository**: Abstraction of data access
    - **Controller**: Link between UI and services
<br>
- **domain_sequence_diagram.puml**: dynamic flow of the study plan calculation between controller, service, and model
<br>
- **domain_crud_sequence_diagram.puml**: dynamic flow for CRUD operations (e.g., creating a study module)

### UI Diagrams (Frontend)

- **ui_class_diagram.puml**:
    - **Forms**: DTOs for data binding of HTML forms
    - **Views**: Data objects for display in templates
    - **DummyDataGenerator**: Generates temporary test data for UI development
<br>
- **ui_workflow.puml**: flow between different pages (views) and the corresponding HTTP endpoints (URLs)
