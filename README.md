# Adventure Book Application

An interactive adventure book application built for the Pictet Technologies technical exercise.

- **Backend:** Java 21, Spring Boot 4.1.0, Maven
- **Frontend:** Angular 22, TypeScript, Vitest

## Objective status

- **Objective 1 (list, search, filter):** complete.
- **Objective 2 (start a game, basic navigation, no consequences):** complete.
- **Objective 3 (consequences, health, game over):** complete. Health starts at 10, capped at 20, and reaching 0 ends the game as a death — distinct from reaching a real ending. Dying takes priority over an ending reached on the same move.
- **Objective 4 (save the user's progression):** complete. Progress is saved on demand (not automatically) via a "Save Progress" button, one save per book, persisted as a JSON file on disk. Reaching an ending or dying automatically deletes that book's save. Starting a book that has an existing save prompts the player to resume or start over; starting over deletes the stale save.
- **Objectives 5:** not attempted yet.

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

Game and save endpoints (`POST /games`, `POST /games/{sessionId}/choices`, `POST /games/{sessionId}/save`, `POST /games/resume`, `GET /saves/{title}`, `DELETE /saves/{title}`) require a JSON body or path parameters and are best tested via Postman, curl, or through the frontend itself rather than a browser address bar.

Saved games are written to a `saves/` folder created next to wherever the backend runs from (a plain JSON file per book, not a database — see Design notes).

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

## Design notes

- Books are validated on load; a book is excluded from the list (rather than crashing the application) if it violates any of the following: no single BEGIN section, no END section, an option referencing a non-existent section, or a non-ending section with no options.
- Of the four book files originally provided, three (`crystal-caverns`, `pirates-jade-sea`, `the-prisoner`) are each excluded by the same validation rule — a deliberately unreachable dead-end node with no options — and `dragon-quest.json` is an empty file. Two additional books (`whispering-lighthouse`, `clockwork-heist`) were added to demonstrate a fully working library.
- Search matches title or author, case-insensitively; difficulty filtering is exact-match. Both can be combined. Search input is debounced (300ms) on the frontend to avoid firing a request on every keystroke.
- The book list (`GET /books`) is cached in-memory on the backend, so repeated requests don't re-parse and re-validate every book file from disk each time.
- Game sessions are held in-memory on the backend (a thread-safe map, keyed by a generated session id), not persisted to a database. An in-progress session that hasn't been explicitly saved is lost if the backend restarts — this is deliberate: only explicit, player-initiated saves are meant to survive a restart, matching the "Save Progress" button in the original design.
- A choice is validated against the current section's own options before advancing, so a client cannot jump to an arbitrary section by submitting an arbitrary `gotoId`.
- Saved games are stored as one JSON file per book (filename derived from the book's title), on the local filesystem — a deliberately simple form of persistence, sufficient to survive a backend restart without the added complexity of a database for this scope.