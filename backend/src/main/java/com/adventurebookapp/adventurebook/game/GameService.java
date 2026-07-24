package com.adventurebookapp.adventurebook.game;

import com.adventurebookapp.adventurebook.loading.BookLibrary;
import com.adventurebookapp.adventurebook.model.Book;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameService {

    private final BookLibrary bookLibrary;
    private final SaveService saveService;
    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();

    public GameService(BookLibrary bookLibrary, SaveService saveService) {
        this.bookLibrary = bookLibrary;
        this.saveService = saveService;
    }

    public GameStartResult startGame(String title) {
        Book book = findBook(title);
        GameSession session = new GameSession(book);
        return registerSession(session);
    }

    public GameStartResult resumeGame(String title) throws IOException {
        Book book = findBook(title);
        SavedGame savedGame = saveService.load(title)
                .orElseThrow(() -> new NoSuchElementException("No saved game found for: " + title));
        GameSession session = GameSession.resumeAt(book, savedGame.sectionId(), savedGame.health());
        return registerSession(session);
    }

    public SectionView makeChoice(String sessionId, int gotoId) {
        GameSession session = getSession(sessionId);
        session.choose(gotoId);
        if (session.isOver()) {
            saveService.delete(session.getBookTitle());
        }
        return toSectionView(session);
    }

    public void saveGame(String sessionId) {
        GameSession session = getSession(sessionId);
        if (session.getCurrentSection().type() == com.adventurebookapp.adventurebook.model.SectionType.BEGIN
                || session.isOver()) {
            throw new IllegalStateException("Cannot save at the beginning or end of a game");
        }
        try {
            saveService.save(new SavedGame(
                    session.getBookTitle(),
                    session.getCurrentSection().id(),
                    session.getHealth()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save game", e);
        }
    }

    private GameStartResult registerSession(GameSession session) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, session);
        return new GameStartResult(sessionId, toSectionView(session));
    }

    private Book findBook(String title) {
        return bookLibrary.getAllBooks().stream()
                .filter(b -> b.title().equals(title))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No book found with title: " + title));
    }

    private GameSession getSession(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null) {
            throw new NoSuchElementException("No active game session with id: " + sessionId);
        }
        return session;
    }

    private SectionView toSectionView(GameSession session) {
        return SectionView.from(session.getCurrentSection(), session.getHealth(), session.isDead());
    }

    public Optional<SavedGame> checkSave(String title) throws IOException {
        return saveService.load(title);
    }
}