# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1.0] - 2026-09-14

### Added

- Added an H2 profile for running local demonstrations and tests without PostgreSQL.
- Added support for configuring the HTTP port with the `SKAKKI_SERVER_PORT` environment variable.

### Changed

- Redesigned the user interface across public, member, organizer, arbiter, and administrator pages, including responsive layouts and automatic dark-mode styling.
- Changed the build and runtime requirement from Java 26 to Java 25.
- Expanded the project documentation with setup instructions, demo credentials, configuration options, common commands, and a technology overview.

## [1.0.0] - 2026-06-10

### Added

- Added role-based authentication and account management for administrators, tournament organizers, tournament arbiters, and members.
- Added tournament publishing, registration requests, and tournament-specific arbiter applications.
- Added tournament and member administration workflows.
- Added round creation and match registration, including normal games, byes, and forfeits.
- Added tournament leaderboards and match details with PGN-based game replay.
- Added PostgreSQL persistence and database-backed demo data.

[Unreleased]: https://github.com/loremol/skakki/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/loremol/skakki/compare/ce949aea85337d0e522f5239dbb4aa22a19797cb...v1.1.0
[1.0.0]: https://github.com/loremol/skakki/commit/ce949aea85337d0e522f5239dbb4aa22a19797cb
