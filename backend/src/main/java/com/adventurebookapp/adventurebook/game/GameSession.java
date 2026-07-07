package com.adventurebookapp.adventurebook.game;

import com.adventurebookapp.adventurebook.model.Book;
import com.adventurebookapp.adventurebook.model.Section;
import com.adventurebookapp.adventurebook.model.SectionType;

public class GameSession {
    private final Book book;
    private Section currentSection;

    public GameSession(Book book) {
        this.book = book;
        this.currentSection = findSectionByType(SectionType.BEGIN);
    }

    public Section getCurrentSection() {
        return currentSection;
    }

    public boolean isOver() {
        return currentSection.type() == SectionType.END;
    }

    public void choose(int gotoId) {
        boolean isValidChoice = currentSection.options().stream()
                .anyMatch(option -> option.gotoId() == gotoId);
        if (!isValidChoice) {
            throw new IllegalArgumentException("gotoId " + gotoId + " is not a valid option from the current section");
        }
        currentSection = findSectionById(gotoId);
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