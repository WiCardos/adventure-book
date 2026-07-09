package com.adventurebookapp.adventurebook.loading;

import com.adventurebookapp.adventurebook.model.Book;
import com.adventurebookapp.adventurebook.model.Difficulty;
import com.adventurebookapp.adventurebook.validation.BookValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BookLibrary {

    private final BookLoader bookLoader;
    private final BookValidator bookValidator;
    private final List<String> bookResourcePaths;

    public BookLibrary(BookLoader bookLoader, BookValidator bookValidator, List<String> bookResourcePaths) {
        this.bookLoader = bookLoader;
        this.bookValidator = bookValidator;
        this.bookResourcePaths = bookResourcePaths;
    }

    @Cacheable("books")
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        for (String path : bookResourcePaths) {
            try (InputStream stream = getClass().getResourceAsStream(path)) {
                Book book = bookLoader.load(stream);
                if (bookValidator.validate(book).valid()) {
                    books.add(book);
                }
            } catch (RuntimeException | IOException e) {
                log.warn("Failed to load book from {}: {}", path, e.getMessage());
            }
        }
        return books;
    }

    public List<Book> findBooks(String searchTerm, Difficulty difficulty) {
        return getAllBooks().stream()
                .filter(book -> matchesSearch(book, searchTerm))
                .filter(book -> matchesDifficulty(book, difficulty))
                .toList();
    }

    private boolean matchesSearch(Book book, String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return true;
        }
        String term = searchTerm.toLowerCase();
        return book.title().toLowerCase().contains(term) || book.author().toLowerCase().contains(term);
    }

    private boolean matchesDifficulty(Book book, Difficulty difficulty) {
        return difficulty == null || book.difficulty() == difficulty;
    }
}