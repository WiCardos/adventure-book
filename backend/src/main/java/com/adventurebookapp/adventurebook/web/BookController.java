package com.adventurebookapp.adventurebook.web;

import com.adventurebookapp.adventurebook.loading.BookLibrary;
import com.adventurebookapp.adventurebook.model.BookSummary;
import com.adventurebookapp.adventurebook.model.Difficulty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController {

    private final BookLibrary bookLibrary;

    public BookController(BookLibrary bookLibrary) {
        this.bookLibrary = bookLibrary;
    }

    @GetMapping("/books")
    public List<BookSummary> getBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Difficulty difficulty) {
        return bookLibrary.findBooks(search, difficulty).stream()
                .map(BookSummary::from)
                .toList();
    }
}