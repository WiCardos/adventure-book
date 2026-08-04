package com.adventurebookapp.adventurebook.loading;

import com.adventurebookapp.adventurebook.model.*;
import com.adventurebookapp.adventurebook.validation.BookValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

class BookLibraryTest {

    private final BookLoader mockLoader = mock(BookLoader.class);
    private final BookValidator realValidator = new BookValidator(); // already trustworthy, no need to fake this

    private static final Section VALID_BEGIN =
            new Section(1, "Start", SectionType.BEGIN, List.of(new Option("Go", 2, null)));
    private static final Section VALID_END =
            new Section(2, "End", SectionType.END, List.of());

    private Book book(String title, String author, Difficulty difficulty) {
        return new Book(title, author, difficulty, List.of(VALID_BEGIN, VALID_END));
    }

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        Files.writeString(tempDir.resolve("a.json"), "{}");
        Files.writeString(tempDir.resolve("b.json"), "{}");
    }

    @Test
    void getAllBooks_excludesInvalidBooks() throws IOException {
        Book validBook = book("Valid Book", "Author", Difficulty.EASY);
        Book invalidBook = new Book("Invalid Book", "Author", Difficulty.EASY, List.of()); // no BEGIN, no END

        when(mockLoader.load(any())).thenReturn(validBook, invalidBook);

        BookLibrary library = new BookLibrary(mockLoader, realValidator, tempDir);

        List<Book> result = library.getAllBooks();

        assertThat(result).containsExactly(validBook);
    }

    @Test
    void getAllBooks_loadsRealBookFiles_excludingInvalidOnes() throws IOException {
        copyTestResourceInto(tempDir, "test-valid-book.json");
        copyTestResourceInto(tempDir, "test-empty-book.json");

        BookLibrary library = new BookLibrary(
                new BookLoader(),
                new BookValidator(),
                tempDir
        );

        List<Book> books = library.getAllBooks();

        assertThat(books).hasSize(1);
        assertThat(books.get(0).title()).isNotBlank();
    }

    @Test
    void findBooks_bySearchTerm_matchesTitleCaseInsensitive() throws IOException {
        Book bookOne = book("A Book with lighthouse in the title", "Sailor", Difficulty.EASY);
        Book bookTwo = book("A Book with no light in the house, in the title", "Fisherman", Difficulty.MEDIUM);

        when(mockLoader.load(any())).thenReturn(bookOne, bookTwo);

        BookLibrary library = new BookLibrary(mockLoader, realValidator, tempDir);

        List<Book> result = library.findBooks("LIGHTHOUSE", null);

        assertThat(result).containsExactly(bookOne);
    }

    @Test
    void findBooks_bySearchTerm_matchesAuthorCaseInsensitive() throws IOException {
        Book bookOne = book("A Book with lighthouse in the title", "Sailor", Difficulty.EASY);
        Book bookTwo = book("A Book with no light in the house, in the title", "Fisherman", Difficulty.MEDIUM);

        when(mockLoader.load(any())).thenReturn(bookOne, bookTwo);

        BookLibrary library = new BookLibrary(mockLoader, realValidator, tempDir);

        List<Book> result = library.findBooks("fish", null);

        assertThat(result).containsExactly(bookTwo);
    }

    @Test
    void findBooks_byDifficulty_matchesEASYDifficulty() throws IOException {
        Book bookOne = book("A Book with lighthouse in the title", "Sailor", Difficulty.EASY);
        Book bookTwo = book("A Book with no light in the house, in the title", "Fisherman", Difficulty.MEDIUM);

        when(mockLoader.load(any())).thenReturn(bookOne, bookTwo);

        BookLibrary library = new BookLibrary(mockLoader, realValidator, tempDir);

        List<Book> result = library.findBooks("", Difficulty.EASY);

        assertThat(result).containsExactly(bookOne);
    }

    @Test
    void findBooks_bySearchTermAndDifficulty_matchesTitleCaseInsensitive() throws IOException {
        Book bookOne = book("A Book with lighthouse in the title", "Sailor", Difficulty.EASY);
        Book bookTwo = book("A Book with no light in the house, in the title", "Fisherman", Difficulty.MEDIUM);

        when(mockLoader.load(any())).thenReturn(bookOne, bookTwo);

        BookLibrary library = new BookLibrary(mockLoader, realValidator, tempDir);

        List<Book> result = library.findBooks("light", Difficulty.EASY);

        assertThat(result).containsExactly(bookOne);
    }

    @Test
    void findBooks_noParams_returnsAll() throws IOException {
        Book bookOne = book("A Book with lighthouse in the title", "Sailor", Difficulty.EASY);
        Book bookTwo = book("A Book with no light in the house, in the title", "Fisherman", Difficulty.MEDIUM);

        when(mockLoader.load(any())).thenReturn(bookOne, bookTwo);

        BookLibrary library = new BookLibrary(mockLoader, realValidator, tempDir);

        List<Book> result = library.findBooks("", null);

        assertThat(result).containsExactly(bookOne,bookTwo);
    }

    private void copyTestResourceInto(Path tempDir, String resourceName) throws IOException {
        try (var in = getClass().getResourceAsStream("/" + resourceName)) {
            Files.copy(in, tempDir.resolve(resourceName));
        }
    }
}