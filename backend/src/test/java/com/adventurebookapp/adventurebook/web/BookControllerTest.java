package com.adventurebookapp.adventurebook.web;

import com.adventurebookapp.adventurebook.loading.BookLibrary;
import com.adventurebookapp.adventurebook.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookLibrary bookLibrary;

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
}