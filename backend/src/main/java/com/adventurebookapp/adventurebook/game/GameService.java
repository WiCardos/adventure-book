package com.adventurebookapp.adventurebook.game;

import com.adventurebookapp.adventurebook.loading.BookLibrary;
import com.adventurebookapp.adventurebook.model.Book;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameService {

    private final BookLibrary bookLibrary;
    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();

    public GameService(BookLibrary bookLibrary) {
        this.bookLibrary = bookLibrary;
    }

    public GameStartResult startGame(String title) {
        Book book = bookLibrary.getAllBooks().stream()
                .filter(b -> b.title().equals(title))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No book found with title: " + title));

        GameSession session = new GameSession(book);
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, session);

        return new GameStartResult(sessionId, toSectionView(session));
    }

    public SectionView makeChoice(String sessionId, int gotoId) {
        GameSession session = sessions.get(sessionId);
        if (session == null) {
            throw new NoSuchElementException("No active game session with id: " + sessionId);
        }
        session.choose(gotoId);
        return toSectionView(session);
    }

    private SectionView toSectionView(GameSession session) {
        return SectionView.from(session.getCurrentSection(), session.getHealth(), session.isDead());
    }
}