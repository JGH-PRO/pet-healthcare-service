# Pet Care Mobile Application - Backend
This is the backend for the Pet Care Mobile Application, built with Spring Boot 3.3.0.
## Project Overview
This application provides APIs for managing pet profiles, health logs, walk tracking, vet hospital search and booking, a community feed, product recommendations, and notifications.
## Features (Planned)
- Pet Profile Management
- Daily Health Logs
- Walk Tracking
- Vet Hospital Search & Booking
- Community Feed
- Product Recommendation & Subscription
- Notifications (Firebase Cloud Messaging)
- JWT-based Authentication (Optional OAuth2)
- Admin Panel
- OpenAPI Documentation (to be configured)
## Tech Stack
- Java
- Spring Boot 3.3.0
- Spring Data JPA
- Spring Security
- MariaDB
- Docker
# (OpenAPI/SpringDoc to be added)
## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- MariaDB server
- Docker (optional, for running with Docker)
## Setup Instructions
1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd petcare-app-backend
    ```
2.  **Configure MariaDB:**
    - Ensure you have a MariaDB server running.
    - Create a database named `petcare_db`.
    - Create a user `petcare_user` with password `petcare_password` and grant privileges to `petcare_db`.
      Alternatively, update the credentials in `src/main/resources/application.properties`.
3.  **Update `application.properties`:**
    - Open `src/main/resources/application.properties`.
    - Verify/update the `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` properties.
4.  **Add SpringDoc OpenAPI dependency (if needed):**
    If not included initially, add to `pom.xml`:
    ```xml
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>LATEST_SPRINGDOC_VERSION</version>
    </dependency>
    ```
5.  **Build the project:**
    ```bash
    mvn clean install
    ```
6.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    Or `java -jar target/petcare-app-*.jar`.
## API Documentation
(To be configured if SpringDoc was added manually)
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
[http://localhost:8080/api-docs](http://localhost:8080/api-docs)
---
*This README was last updated by an automated process.*

## Running with Docker

This application can be built and run using Docker and Docker Compose.

1.  **Prerequisites:**
    *   Docker installed ([https://docs.docker.com/get-docker/](https://docs.docker.com/get-docker/))
    *   Docker Compose installed (usually included with Docker Desktop)

2.  **Build and Run:**
    *   Navigate to the project root directory (where `Dockerfile` and `docker-compose.yml` are located).
    *   Build the application's JAR file first (if not already built):
        \`\`\`bash
        mvn clean package -DskipTests
        \`\`\`
    *   Run the application using Docker Compose:
        \`\`\`bash
        docker-compose up --build
        \`\`\`
        The `--build` flag ensures the image is built if it doesn't exist or if `Dockerfile` changed.
        To run in detached mode (in the background), use `docker-compose up -d --build`.

3.  **Accessing the Application:**
    *   API: [http://localhost:8080](http://localhost:8080)
    *   Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
    *   Database (MariaDB): Accessible on host port 3307 (maps to container port 3306).

4.  **Stopping the Application:**
    *   If running in the foreground, press `Ctrl+C`.
    *   If running in detached mode, use:
        \`\`\`bash
        docker-compose down
        \`\`\`

5.  **Data Persistence:**
    *   The MariaDB data is persisted in a Docker volume named `petcare_db_data`. This means your data will remain even if you stop and restart the containers.
    *   To remove the volume (and all data), you can run `docker-compose down -v`.

---
