package com.adventurebookapp.adventurebook.loading;

import com.adventurebookapp.adventurebook.model.*;
import com.adventurebookapp.adventurebook.validation.BookValidator;
import org.junit.jupiter.api.Test;

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

    @Test
    void getAllBooks_excludesInvalidBooks() {
        Book validBook = book("Valid Book", "Author", Difficulty.EASY);

        Book invalidBook = new Book("Invalid Book", "Author", Difficulty.EASY, List.of()); // no BEGIN, no END

        when(mockLoader.load(any())).thenReturn(validBook, invalidBook);

        BookLibrary library = new BookLibrary(mockLoader, realValidator, List.of("/test-book.json", "/test-book.json"));

        List<Book> result = library.getAllBooks();

        assertThat(result).containsExactly(validBook);
    }

    @Test
    void getAllBooks_loadsRealBookFiles_excludingInvalidOnes() {
        BookLibrary library = new BookLibrary(
                new BookLoader(),
                new BookValidator(),
                List.of(
                        "/books/crystal-caverns.json",
                        "/books/dragon-quest.json",
                        "/books/pirates-jade-sea.json",
                        "/books/the-prisoner.json",
                        "/books/clockwork-heist.json",
                        "/books/whispering-lighthouse.json"
                )
        );

        List<Book> books = library.getAllBooks();

        assertThat(books).isNotEmpty();
        assertThat(books).hasSize(2);
        books.forEach(book -> assertThat(book.title()).isNotBlank());
    }

    @Test
    void findBooks_bySearchTerm_matchesTitleCaseInsensitive() {
        Book bookOne = book("A Book with lighthouse in the title", "Sailor", Difficulty.EASY);
        Book bookTwo = book("A Book with no light in the house, in the title", "Fisherman", Difficulty.MEDIUM);

        when(mockLoader.load(any())).thenReturn(bookOne, bookTwo);

        BookLibrary library = new BookLibrary(mockLoader, realValidator, List.of("/a.json", "/b.json"));

        List<Book> result = library.findBooks("LIGHTHOUSE", null);

        assertThat(result).containsExactly(bookOne);
    }

    @Test
    void findBooks_bySearchTerm_matchesAuthorCaseInsensitive() {
        Book bookOne = book("A Book with lighthouse in the title", "Sailor", Difficulty.EASY);
        Book bookTwo = book("A Book with no light in the house, in the title", "Fisherman", Difficulty.MEDIUM);

        when(mockLoader.load(any())).thenReturn(bookOne, bookTwo);

        BookLibrary library = new BookLibrary(mockLoader, realValidator, List.of("/a.json", "/b.json"));

        List<Book> result = library.findBooks("fish", null);

        assertThat(result).containsExactly(bookTwo);
    }

    @Test
    void findBooks_byDifficulty_matchesEASYDifficulty() {
        Book bookOne = book("A Book with lighthouse in the title", "Sailor", Difficulty.EASY);
        Book bookTwo = book("A Book with no light in the house, in the title", "Fisherman", Difficulty.MEDIUM);

        when(mockLoader.load(any())).thenReturn(bookOne, bookTwo);

        BookLibrary library = new BookLibrary(mockLoader, realValidator, List.of("/a.json", "/b.json"));

        List<Book> result = library.findBooks("", Difficulty.EASY);

        assertThat(result).containsExactly(bookOne);
    }

    @Test
    void findBooks_bySearchTermAndDifficulty_matchesTitleCaseInsensitive() {
        Book bookOne = book("A Book with lighthouse in the title", "Sailor", Difficulty.EASY);
        Book bookTwo = book("A Book with no light in the house, in the title", "Fisherman", Difficulty.MEDIUM);

        when(mockLoader.load(any())).thenReturn(bookOne, bookTwo);

        BookLibrary library = new BookLibrary(mockLoader, realValidator, List.of("/a.json", "/b.json"));

        List<Book> result = library.findBooks("light", Difficulty.EASY);

        assertThat(result).containsExactly(bookOne);
    }

    @Test
    void findBooks_noParams_returnsAll() {
        Book bookOne = book("A Book with lighthouse in the title", "Sailor", Difficulty.EASY);
        Book bookTwo = book("A Book with no light in the house, in the title", "Fisherman", Difficulty.MEDIUM);

        when(mockLoader.load(any())).thenReturn(bookOne, bookTwo);

        BookLibrary library = new BookLibrary(mockLoader, realValidator, List.of("/a.json", "/b.json"));

        List<Book> result = library.findBooks("", null);

        assertThat(result).containsExactly(bookOne,bookTwo);
    }
}