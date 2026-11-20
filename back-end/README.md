# Lumière Backend

Spring Boot REST API for the Lumière cosmetics shop. It manages products, brands, categories, users, customer orders, and order items.

## Technologies

- Java 25
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA and Hibernate
- MySQL
- Maven Wrapper
- BCrypt password hashing through Spring Security Crypto
- H2 database for automated tests
- Apache Tomcat

## Requirements

- Java Development Kit (JDK) 25
- MySQL 8 or newer

Maven does not need to be installed globally because the project includes the Maven Wrapper.

## Installation

From the project root, open the backend directory:

```bash
cd back-end
```

Download dependencies and compile the project:

```bash
./mvnw clean package -DskipTests
```

On Windows, use:

```bat
mvnw.cmd clean package -DskipTests
```

## Database Setup

Start MySQL, log in as an administrator, and run:

```sql
CREATE DATABASE lumiere_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER 'user'@'localhost' IDENTIFIED BY 'user@12345';

GRANT ALL PRIVILEGES ON lumiere_db.* TO 'user'@'localhost';
FLUSH PRIVILEGES;
```

The default database configuration is defined in `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lumiere_db
    username: user
    password: user@12345
```

If the database is hosted elsewhere, set `DB_URL` before starting the application:

```bash
export DB_URL=jdbc:mysql://localhost:3306/lumiere_db
```

On Windows Command Prompt, use:

```bat
set DB_URL=jdbc:mysql://localhost:3306/lumiere_db
```

To use a different username or password, update `src/main/resources/application.yaml`.

Hibernate uses `ddl-auto: update` by default, so the tables are created or updated automatically when the backend starts.

## Run Instructions

Start the API on Linux or macOS:

```bash
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

The API will be available at:

```text
http://localhost:8080/api
```

For example:

```text
GET http://localhost:8080/api/products
```

Stop the server with `Ctrl+C`.

## Run Tests

Tests use an in-memory H2 database, so MySQL is not required for testing:

```bash
./mvnw test
```

On Windows:

```bat
mvnw.cmd test
```

## API Documentation

The Word API reference is available at `../docs/Lumiere_API_Documentation.docx`.
