# Skakki
Skakki is a Spring Boot application for managing a chess club. It allows publishing upcoming tournaments, sign up for them, sign up as an arbiter, register matches and the moves in PGN notation, view the leaderboard of a tournament, replay the match with a `chess.js` snippet that generates a chess board with the match moves pre-loaded.

It handles four roles: admin, tournament organizer, tournament arbiter (which is a tournament-specific role: you are an arbiter to a tournament if you applied to the position and that tournament's organizer accepted your application), and member.

## Requirements

- Java 25 SDK to compile the project.
- Java 25 to run the project.

## Quick start

### Demo mode

For a local demo with mock data, start Skakki with the in-memory H2 database and set `SKAKKI_DEMO=true`:

```sh
SKAKKI_DEMO=true ./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

Demo mode loads sample members, tournaments, rounds, matches, and requests for visualisation purposes. Open [http://localhost:8080](http://localhost:8080) to view them.

### Non-demo mode

Without `SKAKKI_DEMO=true`, Skakki starts without sample data. With an empty database, it creates an `admin` account and logs a securely generated password once during startup:

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

Use the logged credentials to sign in and change the password. If the `admin` account already exists, it is left unchanged and no new credentials are logged.

### PostgreSQL

To use PostgreSQL, set the database environment variables described in the [configuration](#configuration) section and start the application, generating a secure starting `password` for the admin account:

```sh
./mvnw spring-boot:run
```

The securely generated starting `admin` password is in the last line of the startup logs.
Add `SKAKKI_DEMO=true` to the command when sample data is desired.

## Demo credentials

These accounts are available only when `SKAKKI_DEMO=true`:

| Username | Password | Roles |
|---|---|---|
| `admin` | `admin` | Admin, organizer, member |
| `marco.bianchi` | `password` | Organizer, member |
| `giulia.rossi` | `password` | Organizer, member |
| `luca.ferrari` | `password` | Member |

## Configuration

| Environment variable | Required | Default | Description |
|---|---:|---|---|
| `SKAKKI_SERVER_PORT` | No | `8080` | Port on which the server listens. |
| `SKAKKI_DEMO` | No | `false` | When `true`, load demo data instead of creating a fresh non-demo admin account. |
| `SKAKKI_DB_URL` | PostgreSQL | n/a | JDBC URL of the PostgreSQL database. |
| `SKAKKI_DB_USER` | PostgreSQL | n/a | PostgreSQL database username. |
| `SKAKKI_DB_PASSWORD` | PostgreSQL | n/a | PostgreSQL database password. |

Example PostgreSQL configuration:

```sh
export SKAKKI_DB_URL=jdbc:postgresql://localhost:5432/skakki
export SKAKKI_DB_USER=skakki
export SKAKKI_DB_PASSWORD=secret
./mvnw spring-boot:run
```

## Available commands

| Command | Description |
|---|---|
| `SKAKKI_DEMO=true ./mvnw spring-boot:run -Dspring-boot.run.profiles=h2` | Run the demo with the in-memory H2 database. |
| `./mvnw spring-boot:run -Dspring-boot.run.profiles=h2` | Run non-demo mode with the in-memory H2 database. |
| `./mvnw spring-boot:run` | Run non-demo mode with PostgreSQL configuration. |
| `./mvnw test` | Run unit and functional tests using H2. |
| `./mvnw clean package` | Clean and build the production JAR. |

## Stack

- **Java 25**
- **Spring Boot 4.0.6**
- **Spring MVC** for web request handling
- **Spring Security** for authentication and authorization
- **Spring Data JPA / Hibernate** for persistence
- **Thymeleaf** for server-rendered HTML templates
- **PostgreSQL** for production persistence
- **H2** for local development and tests
- **chess.js** for replaying matches from PGN moves
- **Maven** for dependency management and builds

## Testing

All unit tests and functional tests can be started with `./mvnw test`, with no environment variables required. The tests run against an H2 database.
