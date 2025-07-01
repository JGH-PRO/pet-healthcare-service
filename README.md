# Pet Care Mobile Application - Backend

This is the backend for the Pet Care Mobile Application, built with Spring Boot.

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

## Tech Stack

- Java
- Spring Boot
- Spring Data JPA
- Spring Security
- MariaDB
- Springdoc OpenAPI (Swagger)
- Docker

## Prerequisites

- Java 17 or higher (Note: Spring Boot 3.3.0 typically requires Java 17 by default)
- Maven 3.6 or higher
- MariaDB server
- Docker (optional, for running with Docker)

## Setup Instructions

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd petcare-app-backend # Or your project directory name
    ```

2.  **Configure MariaDB:**
    - Ensure you have a MariaDB server running.
    - Create a database named `petcare_db`.
    - Create a user `petcare_user` with password `petcare_password` and grant privileges to `petcare_db`.
      Alternatively, update the credentials in `src/main/resources/application.properties`.

3.  **Update `application.properties`:**
    - Open `src/main/resources/application.properties`.
    - Verify/update the `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` properties to match your MariaDB setup.

4.  **Build the project:**
    ```bash
    mvn clean install
    ```

5.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    Or run the JAR file from the `target` directory:
    ```bash
    java -jar target/petcare-app-*.jar
    ```

## API Documentation

Once the application is running, API documentation (Swagger UI) will be available at:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

The OpenAPI specification will be available at:
[http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## Running with Docker (Optional)

(Instructions to be added once Docker setup is complete)

---

*This README was last updated by an automated process.*
