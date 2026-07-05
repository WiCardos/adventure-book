package com.adventurebookapp.adventurebook.model;

public record BookSummary(String title, String author, Difficulty difficulty, int chapterCount) {
    public static BookSummary from(Book book) {
        return new BookSummary(book.title(), book.author(), book.difficulty(), book.sections().size());
    }
}
