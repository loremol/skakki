# Skakki
Skakki is a Spring Boot application for managing a chess club. It allows publishing upcoming tournaments, sign up for them, sign up as an arbiter, register matches and the moves in PGN notation, view the leaderboard of a tournament, replay the match with a `chess.js` snippet that generates a chess board with the match moves pre-loaded.

It handles four roles: admin, tournament organizer, tournament arbiter (which is a tournament-specific role: you are an arbiter to a tournament if you applied to the position and that tournament's organizer accepted your application), and member.
# Requirements
Java 25
# Usage
The port the server listens to is `8080` by default, and can be changed setting the env var `SKAKKI_SERVER_PORT`.
## H2
A quick start with the H2 database can be started with:
```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

## Postgres
To use a postgres database, skakki needs the following env vars:
1. `SKAKKI_DB_URL`
2. `SKAKKI_DB_USER`
3. `SKAKKI_DB_PASSWORD`

and can be started with
```sh
./mvnw spring-boot:run
```
# Testing
All unit tests and functional tests can be started with `./mvnw test`, no env vars required. The tests run in a H2 db.
