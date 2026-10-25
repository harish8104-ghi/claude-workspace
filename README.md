# spring-java25-demo

Spring Boot REST API and API traffic dashboard using Java 25, Spring Boot 4.1.2, and a profile-selected database.

Requirements
- JDK 25 installed and JAVA_HOME pointing to it
- Maven 3.9+ (or installed mvn)

Build and run

1. Build

```bash
mvn -v
mvn clean package
```

2. Run with the default SQLite profile

```bash
mvn spring-boot:run
# or
java -jar target/spring-java25-demo-0.0.1-SNAPSHOT.jar
```

SQLite is the default profile and stores data in `./data/traffic.db`. Override the location with `SQLITE_DB_PATH`.

3. Run with PostgreSQL

Create a PostgreSQL database, then provide connection details through environment variables:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/spring_java25_demo
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

The PostgreSQL profile requires `DB_USERNAME` and `DB_PASSWORD`; credentials are not stored in the project.

Authentication and access
- `/login` is the sign-in page and `/register` creates a database-backed account.
- Public registration only grants the `USER` role. User passwords are stored with BCrypt, and CSRF protection remains enabled for form submissions.
- Set `ADMIN_USERNAME` and `ADMIN_PASSWORD` before startup to bootstrap an administrator. The password must be 12-72 UTF-8 bytes. The configured administrator credentials are synchronized at startup, so rotate them through environment configuration. Admin credentials should come from a secret manager or environment configuration, not source control.
- Only authenticated `ADMIN` users can open `/` or call `/api/traffic/**`. Standard users can register and sign in but cannot view traffic data.
- If bootstrap credentials are not configured, the app logs a warning and does not create an administrator.

Connection pooling
- Spring Boot's HikariCP connection pool is configured with bounded defaults: PostgreSQL uses a maximum of 10 connections and a minimum idle pool of 2. Tune these with `DB_POOL_MAX_SIZE` and `DB_POOL_MIN_IDLE` to match your database capacity and application replica count.
- SQLite uses a single pooled connection to avoid concurrent-writer lock contention and sets a 5-second SQLite busy timeout.
- Pool connection, validation, idle, and maximum-lifetime timeouts can be tuned with `DB_POOL_CONNECTION_TIMEOUT_MS`, `DB_POOL_VALIDATION_TIMEOUT_MS`, `DB_POOL_IDLE_TIMEOUT_MS`, and `DB_POOL_MAX_LIFETIME_MS`.

Endpoint
- GET /api/greet?name=YourName -> { "message": "Hello, YourName!" }
- GET /api/traffic/stats -> API request totals, status counts, response times, recent requests, and the last hour of traffic

API traffic dashboard
- Sign in as an ADMIN at http://localhost:8080/login to view live API request traffic.
- The dashboard refreshes every five seconds. It tracks requests under `/api/` except for its own `/api/traffic` metrics endpoints.
- Request events and dashboard metrics are stored in the selected database and survive application restarts. The recent request list is capped at 100 entries.
- Hibernate creates or updates the request table at startup for the selected profile.

API documentation
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Notes
- This project targets Java 25. Ensure your Maven toolchain or container uses JDK 25.
