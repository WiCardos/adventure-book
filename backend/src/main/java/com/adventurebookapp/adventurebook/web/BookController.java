package com.adventurebookapp.adventurebook.web;

import com.adventurebookapp.adventurebook.loading.BookLibrary;
import com.adventurebookapp.adventurebook.loading.BookLoader;
import com.adventurebookapp.adventurebook.loading.DuplicateBookException;
import com.adventurebookapp.adventurebook.model.Book;
import com.adventurebookapp.adventurebook.model.BookSummary;
import com.adventurebookapp.adventurebook.model.Difficulty;
import com.adventurebookapp.adventurebook.validation.InvalidBookException;
import com.adventurebookapp.adventurebook.validation.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class BookController {

    private final BookLibrary bookLibrary;
    private final BookLoader bookLoader;

    public BookController(BookLibrary bookLibrary, BookLoader bookLoader) {
        this.bookLibrary = bookLibrary;
        this.bookLoader = bookLoader;
    }

    @GetMapping("/books")
    public List<BookSummary> getBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Difficulty difficulty) {
        return bookLibrary.findBooks(search, difficulty).stream()
                .map(BookSummary::from)
                .toList();
    }

    @PostMapping("/books")
    public ResponseEntity<?> uploadBook(@RequestParam("file") MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".json")) {
            return ResponseEntity.badRequest().body("Invalid file");
        }

        Book book;
        try {
            book = bookLoader.load(file.getInputStream());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid file");
        }

        bookLibrary.addBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body("Book added");
    }

    @ExceptionHandler(InvalidBookException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ValidationError> handleInvalidBook(InvalidBookException e) {
        return e.getErrors();
    }

    @ExceptionHandler(DuplicateBookException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateBook(DuplicateBookException e) {
        return e.getMessage();
    }
}