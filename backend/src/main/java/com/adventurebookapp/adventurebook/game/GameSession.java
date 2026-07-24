package com.adventurebookapp.adventurebook.game;

import com.adventurebookapp.adventurebook.model.*;

public class GameSession {
    private static final int MIN_HEALTH = 0;
    private static final int MAX_HEALTH = 20;
    private static final int STARTING_HEALTH = 10;

    private final Book book;
    private Section currentSection;
    private int health = STARTING_HEALTH;
    private boolean dead = false;

    public GameSession(Book book) {
        this.book = book;
        this.currentSection = findSectionByType(SectionType.BEGIN);
    }

    public static GameSession resumeAt(Book book, int sectionId, int health) {
        GameSession session = new GameSession(book);
        session.currentSection = session.findSectionById(sectionId);
        session.health = health;
        return session;
    }

    public Section getCurrentSection() {
        return currentSection;
    }

    public int getHealth() {
        return health;
    }

    public String getBookTitle() {
        return book.title();
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isOver() {
        return dead || currentSection.type() == SectionType.END;
    }

    public void choose(int gotoId) {
        Option chosenOption = currentSection.options().stream()
                .filter(option -> option.gotoId() == gotoId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "gotoId " + gotoId + " is not a valid option from the current section"));

        applyConsequence(chosenOption.consequence());
        currentSection = findSectionById(gotoId);
    }

    private void applyConsequence(Consequence consequence) {
        if (consequence == null) {
            return;
        }
        int delta = consequence.type() == ConsequenceType.LOSE_HEALTH
                ? -consequence.value()
                : consequence.value();
        health = Math.min(MAX_HEALTH, Math.max(MIN_HEALTH, health + delta));
        if (health <= MIN_HEALTH) {
            dead = true;
        }
    }

    private Section findSectionByType(SectionType type) {
        return book.sections().stream()
                .filter(s -> s.type() == type)
                .findFirst()
                .orElseThrow();
    }

    private Section findSectionById(int id) {
        return book.sections().stream()
                .filter(s -> s.id() == id)
                .findFirst()
                .orElseThrow();
    }
}