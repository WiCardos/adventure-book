# Adventure Book Application

An interactive adventure book application built for the Pictet Technologies technical exercise.

- **Backend:** Java 21, Spring Boot 4.1.0, Maven
- **Frontend:** Angular 22, TypeScript, Vitest

## Objective status

- **Objective 1 (list, search, filter):** complete.
- **Objective 2 (start a game, basic navigation, no consequences):** complete.
- **Objective 3 (consequences, health, game over):** complete. Health starts at 10, capped at 20, and reaching 0 ends the game as a death — distinct from reaching a real ending. Dying takes priority over an ending reached on the same move.
- **Objective 4 (save the user's progression):** complete. Progress is saved on demand (not automatically) via a "Save Progress" button, one save per book, persisted as a JSON file on disk. Reaching an ending or dying automatically deletes that book's save. Starting a book that has an existing save prompts the player to resume or start over; starting over deletes the stale save.
- **Objective 5 (add new books):** complete (backend). Uploading a `.json` file via `POST /books` (multipart) validates the file is parseable JSON, runs it through the same `BookValidator` rules used for the built-in books, and rejects duplicate titles. Returns `201` on success, `400` with specific validation errors for a structurally invalid book, `400` for a non-JSON/unparseable file, and `409` for a duplicate title. A successful upload evicts the book-list cache so it appears immediately. Frontend upload form not yet built.

## Project structure

```
adventure-book/
├── backend/     Spring Boot REST API
└── frontend/    Angular single-page application
```

## Prerequisites

- **Java 21** (developed and tested against Amazon Corretto 21)
- **Maven** (or use the bundled `./mvnw` wrapper — no separate Maven install required)
- **Node.js ≥ 20.19** and **npm** (developed against Node 22/24)
- **Angular CLI 22** (`npm install -g @angular/cli`) — optional; `npm start`/`npm test` work without a global install

## Running the backend

```bash
cd backend
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. Confirm it's running by visiting `http://localhost:8080/books` in a browser — it should return a JSON list of available books.

**Note for IntelliJ users:** the default Spring Boot run configuration's working directory may default to the project root rather than `backend/`, which breaks the relative `../books` / `./saves` paths below. If `/books` returns an empty list when running via IntelliJ's Run button (as opposed to `./mvnw spring-boot:run`, or Docker, which are unaffected), open the run configuration (Run → Edit Configurations... → "Modify options" → enable "Working directory") and set it explicitly to the `backend` folder. This is a one-time, per-machine fix.

Game and save endpoints (`POST /games`, `POST /games/{sessionId}/choices`, `POST /games/{sessionId}/save`, `POST /games/resume`, `GET /saves/{title}`, `DELETE /saves/{title}`) require a JSON body or path parameters and are best tested via Postman, curl, or through the frontend itself rather than a browser address bar.

Saved games are written to a `saves/` folder created next to wherever the backend process's working directory is at startup (in practice, the project root when run via IntelliJ's default configuration, or `backend/` if run via `cd backend && ./mvnw spring-boot:run`) — a plain JSON file per book, not a database (see Design notes). This location is configurable via the `app.saves-directory` property in `backend/src/main/resources/application.properties`.

### Backend tests

```bash
cd backend
./mvnw test
```

## Running the frontend

```bash
cd frontend
npm install
npm start
```

The application opens on `http://localhost:4200`. **The backend must already be running** (see above) for the book list to load. The frontend's API base URL is configured via `frontend/src/environments/environment.ts` (development) and `environment.prod.ts` (production build), rather than hardcoded, so it can point at a different backend without code changes.

### Frontend tests

```bash
cd frontend
npm test
```

## Running with Docker

As an alternative to running the backend and frontend manually (see above), the entire stack can be started with a single command:

```bash
docker compose up --build
```

This builds both the backend (Java 21, multi-stage build producing a slim JRE-based image) and the frontend (Angular build compiled to static files, served via nginx) and starts them together. The frontend is available at `http://localhost:4200`, the backend at `http://localhost:8080`, matching the manual setup exactly.

Saved games are stored in a named Docker volume (`saves-data`), so progress survives `docker compose down` / `docker compose up` cycles — it is only lost if the volume itself is explicitly removed (`docker compose down -v`).

## Continuous Integration

A GitHub Actions workflow (`.github/workflows/ci.yml`) runs automatically on every push or pull request targeting `main`: it runs the full backend test suite (`./mvnw test`) and the full frontend test suite (`npm test -- --watch=false`) in parallel, independent jobs. A failing test in either half fails the workflow, giving immediate feedback before anything is merged.

Docker image builds are not currently part of CI — tests only, for now. Building and (optionally) publishing the images built by `docker-compose.yml` would be a natural next step.

Requires [Docker Desktop](https://www.docker.com/products/docker-desktop/) (or an equivalent Docker engine) running locally.

## Design notes

- Books are validated on load; a book is excluded from the list (rather than crashing the application) if it violates any of the following: no single BEGIN section, no END section, an option referencing a non-existent section, or a non-ending section with no options.
- Of the four book files originally provided, three (`crystal-caverns`, `pirates-jade-sea`, `the-prisoner`) are each excluded by the same validation rule — a deliberately unreachable dead-end node with no options — and `dragon-quest.json` is an empty file. Two additional books (`whispering-lighthouse`, `clockwork-heist`) were added to demonstrate a fully working library.
- Search matches title or author, case-insensitively; difficulty filtering is exact-match. Both can be combined. Search input is debounced (300ms) on the frontend to avoid firing a request on every keystroke.
- The book list (`GET /books`) is cached in-memory on the backend, so repeated requests don't re-parse and re-validate every book file from disk each time.
- Game sessions are held in-memory on the backend (a thread-safe map, keyed by a generated session id), not persisted to a database. An in-progress session that hasn't been explicitly saved is lost if the backend restarts — this is deliberate: only explicit, player-initiated saves are meant to survive a restart, matching the "Save Progress" button in the original design.
- A choice is validated against the current section's own options before advancing, so a client cannot jump to an arbitrary section by submitting an arbitrary `gotoId`.
- Saved games are stored as one JSON file per book (filename derived from the book's title), on the local filesystem — a deliberately simple form of persistence, sufficient to survive a backend restart without the added complexity of a database for this scope.