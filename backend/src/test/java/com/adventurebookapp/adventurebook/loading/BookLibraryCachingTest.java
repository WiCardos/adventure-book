package com.adventurebookapp.adventurebook.loading;

import com.adventurebookapp.adventurebook.model.*;
import com.adventurebookapp.adventurebook.validation.BookValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = BookLibraryCachingTest.TestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookLibraryCachingTest {

    @TempDir
    static Path tempDir;

    @Autowired
    private BookLibrary bookLibrary;

    @Autowired
    private BookLoader mockLoader;

    @BeforeEach
    void cleanTempDir() throws IOException {
        try (var files = java.nio.file.Files.list(tempDir)) {
            for (var file : files.toList()) {
                java.nio.file.Files.deleteIfExists(file);
            }
        }
    }

    @Test
    void getAllBooks_onSecondCall_doesNotReinvokeLoader() throws IOException {
        java.nio.file.Files.writeString(tempDir.resolve("dummy.json"), "{}");

        Section begin = new Section(1, "Start", SectionType.BEGIN, List.of(new Option("Go", 2, null)));
        Section end = new Section(2, "End", SectionType.END, List.of());
        Book book = new Book("Test Book", "Author", Difficulty.EASY, List.of(begin, end));

        when(mockLoader.load(any())).thenReturn(book);

        bookLibrary.getAllBooks();
        bookLibrary.getAllBooks();

        verify(mockLoader, times(1)).load(any());
    }

    @EnableCaching
    static class TestConfig {
        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("books");
        }

        @Bean
        BookLoader bookLoader() {
            return mock(BookLoader.class);
        }

        @Bean
        BookValidator bookValidator() {
            return new BookValidator();
        }

        @Bean
        BookLibrary bookLibrary(BookLoader bookLoader, BookValidator bookValidator) throws IOException {
            return new BookLibrary(bookLoader, bookValidator, tempDir);
        }
    }

    @Test
    void addBook_evictsCache_forcingRecomputationOnNextGetAllBooks() throws IOException {
        java.nio.file.Files.writeString(tempDir.resolve("existing.json"), "{}");

        Section begin = new Section(1, "Start", SectionType.BEGIN, List.of(new Option("Go", 2, null)));
        Section end = new Section(2, "End", SectionType.END, List.of());
        Book existingBook = new Book("Existing Book", "Author", Difficulty.EASY, List.of(begin, end));

        when(mockLoader.load(any())).thenReturn(existingBook);

        bookLibrary.getAllBooks();
        bookLibrary.getAllBooks();
        verify(mockLoader, times(1)).load(any());

        Book newBook = new Book("Brand New Book", "Author", Difficulty.EASY, List.of(begin, end));
        bookLibrary.addBook(newBook);

        bookLibrary.getAllBooks();
        verify(mockLoader, times(2)).load(any());
    }
}