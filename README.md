# Adventure Book Application

An interactive adventure book application built for the Pictet Technologies technical exercise.

- **Backend:** Java 21, Spring Boot 4.1.0, Maven
- **Frontend:** Angular 22, TypeScript, Vitest

## Objective status

- **Objective 1 (list, search, filter):** complete.
- **Objective 2 (start a game, basic navigation, no consequences):** complete. Health/consequences (Objective 3) are intentionally not yet implemented — choosing an option currently only moves between sections.
- **Objectives 3–5:** not attempted, given time constraints.

## Project structure

```
adventure-book/
├── backend/     Spring Boot REST API
└── frontend/    Angular single-page application
```

## Prerequisites

- **Java 21** (project developed and tested against Amazon Corretto 21)
- **Maven** (or use the bundled `./mvnw` wrapper — no separate Maven install required)
- **Node.js ≥ 20.19** and **npm** (developed against Node 22/24)
- **Angular CLI 22** (`npm install -g @angular/cli`) — optional; `npm start`/`npm test` work without a global install

## Running the backend

```bash
cd backend
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. Confirm it's running by visiting `http://localhost:8080/books` in a browser — it should return a JSON list of available books.

Game endpoints (`POST /games`, `POST /games/{sessionId}/choices`) require a JSON body and are best tested via Postman, curl, or through the frontend itself rather than a browser address bar.

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

The application opens on `http://localhost:4200`. **The backend must already be running** (see above) for the book list to load.

### Frontend tests

```bash
cd frontend
npm test
```

## Design notes

- Books are validated on load; a book is excluded from the list (rather than crashing the application) if it violates any of the following: no single BEGIN section, no END section, an option referencing a non-existent section, or a non-ending section with no options.
- Of the four book files originally provided, three (`crystal-caverns`, `pirates-jade-sea`, `the-prisoner`) are each excluded by the same validation rule — a deliberately unreachable dead-end node with no options — and `dragon-quest.json` is an empty file. Two additional books (`whispering-lighthouse`, `clockwork-heist`) were added to demonstrate a fully working library.
- Search matches title or author, case-insensitively; difficulty filtering is exact-match. Both can be combined.
- Search input is debounced (300ms) on the frontend to avoid firing a request on every keystroke.
- Game sessions are held in-memory on the backend (a thread-safe map, keyed by a generated session id), not persisted to a database. A session is lost if the backend restarts; this is a deliberate scope limit, not an oversight — persistence is Objective 4.
- A choice is validated against the current section's own options before advancing, so a client cannot jump to an arbitrary section by submitting an arbitrary `gotoId`.
