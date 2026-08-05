package com.adventurebookapp.adventurebook.web;

import com.adventurebookapp.adventurebook.loading.BookLibrary;
import com.adventurebookapp.adventurebook.loading.BookLoader;
import com.adventurebookapp.adventurebook.loading.DuplicateBookException;
import com.adventurebookapp.adventurebook.model.*;
import com.adventurebookapp.adventurebook.validation.InvalidBookException;
import com.adventurebookapp.adventurebook.validation.ValidationError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookLibrary bookLibrary;

    @MockitoBean
    private BookLoader bookLoader;

    private static final Section VALID_BEGIN =
            new Section(1, "Start", SectionType.BEGIN, List.of(new Option("Go", 2, null)));
    private static final Section VALID_END =
            new Section(2, "End", SectionType.END, List.of());

    private Book book(String title, String author, Difficulty difficulty) {
        return new Book(title, author, difficulty, List.of(VALID_BEGIN, VALID_END));
    }

    @Test
    void getBooks_returnsSummariesOfAllBooks() throws Exception {
        Book book = book("Test Book", "Test Author", Difficulty.EASY);

        when(bookLibrary.findBooks(null, null)).thenReturn(List.of(book));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author").value("Test Author"))
                .andExpect(jsonPath("$[0].chapterCount").value(2));
    }

    @Test
    void getBooks_withSearchAndDifficultyParams_delegatesToFindBooks() throws Exception {
        Book book = book("Lighthouse Tale", "Corwin", Difficulty.EASY);

        when(bookLibrary.findBooks("lighthouse", Difficulty.EASY)).thenReturn(List.of(book));

        mockMvc.perform(get("/books")
                        .param("search", "lighthouse")
                        .param("difficulty", "EASY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Lighthouse Tale"));
    }

    @Test
    void uploadBook_withValidBook_returns201() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "new-book.json", "application/json",
                "{\"title\":\"New Book\"}".getBytes());

        doNothing().when(bookLibrary).addBook(any());
        when(bookLoader.load(any())).thenReturn(book("New Book", "Author", Difficulty.EASY));

        mockMvc.perform(multipart("/books").file(file))
                .andExpect(status().isCreated());
    }

    @Test
    void uploadBook_withNonJsonFile_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "not json at all".getBytes());

        mockMvc.perform(multipart("/books").file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadBook_withInvalidBookStructure_returns400WithErrors() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "bad-book.json", "application/json",
                "{\"title\":\"Bad Book\",\"sections\":[]}".getBytes());

        doThrow(new InvalidBookException(List.of(ValidationError.MISSING_BEGIN_SECTION)))
                .when(bookLibrary).addBook(any());

        mockMvc.perform(multipart("/books").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("MISSING_BEGIN_SECTION"));
    }

    @Test
    void uploadBook_withDuplicateTitle_returns409() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "dup-book.json", "application/json",
                "{\"title\":\"Existing Book\"}".getBytes());

        doThrow(new DuplicateBookException("Existing Book")).when(bookLibrary).addBook(any());

        mockMvc.perform(multipart("/books").file(file))
                .andExpect(status().isConflict());
    }
}