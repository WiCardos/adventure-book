package com.adventurebookapp.adventurebook.config;

import com.adventurebookapp.adventurebook.game.GameService;
import com.adventurebookapp.adventurebook.game.SaveService;
import com.adventurebookapp.adventurebook.loading.BookLibrary;
import com.adventurebookapp.adventurebook.loading.BookLoader;
import com.adventurebookapp.adventurebook.validation.BookValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@EnableCaching
public class BookLibraryConfig {

    @Bean
    public BookLoader bookLoader() {
        return new BookLoader();
    }

    @Bean
    public BookValidator bookValidator() {
        return new BookValidator();
    }

    @Bean
    public BookLibrary bookLibrary(
            BookLoader bookLoader,
            BookValidator bookValidator,
            @Value("${app.books-directory:./books}") String booksDirectory) throws IOException {
        return new BookLibrary(bookLoader, bookValidator, java.nio.file.Path.of(booksDirectory));
    }

    @Bean
    public SaveService saveService(@Value("${app.saves-directory:./saves}") String savesDirectory) throws IOException {
        return new SaveService(java.nio.file.Path.of(savesDirectory));
    }

    @Bean
    public GameService gameService(BookLibrary bookLibrary, SaveService saveService) {
        return new GameService(bookLibrary, saveService);
    }
}