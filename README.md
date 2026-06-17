# klp-case

Spring Boot REST API for the KLP senior developer technical case study.

## Stack

- Java 25, Spring Boot 4.1.0, Maven
- Spring Data JPA + H2 (in-memory)
- Log4j2, SpringDoc / Swagger UI

## Notable implementation choices

- **Records** - `UserRequest` and `UserResponse` are Java records. They're immutable data carriers, so there's no reason for them to be mutable classes with setters.
- **Virtual threads** - enabled via `spring.threads.virtual.enabled=true`. The app does I/O on every request (DB + external HTTP), so virtual threads are a natural fit.

## Run

```bash
./mvnw spring-boot:run
```

Starts on `http://localhost:8080`.

```bash
./mvnw clean package
java -jar target/klp-case-0.0.1-SNAPSHOT.jar
```

## Endpoints

### POST /user

```json
{ "email": "user@example.com", "type": "USER" }
```

`type` must be `USER` or `ADMIN`. Returns `201` with the created user.

### GET /user/{id}

Returns the user with the given ID. `404` if not found.

### GET /user?type-filter=ADMIN

Returns all users. `type-filter` is optional - omit to return all.

### GET /county/{countyNumber}

Looks up the Norwegian county name from Kartverket. `countyNumber` must be exactly 2 digits (e.g. `03`). Returns plain text.

## H2 Console

`http://localhost:8080/h2-console` - JDBC URL: `jdbc:h2:mem:klpdb`, username: `sa`, no password.

## Swagger UI

`http://localhost:8080/swagger-ui.html`

## Tests

```bash
./mvnw test
```
