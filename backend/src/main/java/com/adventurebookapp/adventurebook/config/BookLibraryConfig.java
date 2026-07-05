package com.adventurebookapp.adventurebook.config;

import com.adventurebookapp.adventurebook.loading.BookLibrary;
import com.adventurebookapp.adventurebook.loading.BookLoader;
import com.adventurebookapp.adventurebook.validation.BookValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
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
    public BookLibrary bookLibrary(BookLoader bookLoader, BookValidator bookValidator) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:books/*.json");

        List<String> bookPaths = Arrays.stream(resources)
                .map(resource -> "/books/" + resource.getFilename())
                .toList();

        return new BookLibrary(bookLoader, bookValidator, bookPaths);
    }
}