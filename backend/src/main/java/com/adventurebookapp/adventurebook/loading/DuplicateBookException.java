package com.adventurebookapp.adventurebook.loading;

public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(String title) {
        super("A book with this title already exists: " + title);
    }
}
