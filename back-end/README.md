# Lumière Backend

Spring Boot REST API for the Lumière cosmetics shop. It manages the catalogue, sessions, carts, saved products, accounts, and orders.

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

## First Run

Set `LUMIERE_ADMIN_EMAIL` and `LUMIERE_ADMIN_PASSWORD` in the backend process environment before the first start. The backend creates this account if no administrator exists. Use a password of at least eight characters. After the account is created, remove the bootstrap password from the process environment.

Set `LUMIERE_SEED_CATALOGUE=true` for a local sample catalogue. The seed runs only when the product table is empty. You can also add brands, categories, and products in the admin portal. Open `/admin` in the frontend to sign in.

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

## Storefront API

- `/api/products`, `/api/brands`, and `/api/categories` serve the public catalogue. Catalogue writes and management views require an administrator.
- `/api/auth/register`, `/api/auth/login`, `/api/auth/me`, and `/api/auth/logout` manage the browser session.
- `/api/cart` stores the guest bag in the session and shares the signed-in bag and saved products across devices. Signing in merges the guest selection into the account.
- `POST /api/orders` places a cash-on-delivery order. It uses server prices, reserves stock, saves the order and its items, and clears the bag in one transaction.
- `GET /api/orders/my` returns the signed-in customer's orders. `GET /api/admin/orders` and `/api/users` require an administrator.

The frontend development and preview servers proxy `/api` to Spring. For calls outside the frontend, send `X-Lumiere-Request: 1` with writes and preserve the session cookie for authenticated requests.
