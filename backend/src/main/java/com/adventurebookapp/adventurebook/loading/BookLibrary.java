package com.adventurebookapp.adventurebook.loading;

import com.adventurebookapp.adventurebook.model.Book;
import com.adventurebookapp.adventurebook.model.Difficulty;
import com.adventurebookapp.adventurebook.validation.BookValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BookLibrary {

    private final BookLoader bookLoader;
    private final BookValidator bookValidator;
    private final Path booksDirectory;

    public BookLibrary(BookLoader bookLoader, BookValidator bookValidator, Path booksDirectory) throws IOException {
        this.bookLoader = bookLoader;
        this.bookValidator = bookValidator;
        this.booksDirectory = booksDirectory;
        Files.createDirectories(booksDirectory);
    }

    @Cacheable("books")
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(booksDirectory, "*.json")) {
            for (Path path : stream) {
                try (InputStream in = Files.newInputStream(path)) {
                    Book book = bookLoader.load(in);
                    if (bookValidator.validate(book).valid()) {
                        books.add(book);
                    }
                } catch (RuntimeException | IOException e) {
                    log.warn("Failed to load book from {}: {}", path, e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read books directory", e);
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

    /*
    @CacheEvict("books")
public void addBook(Book book) {
    // write the book's JSON to the books/ directory
}
     */
}