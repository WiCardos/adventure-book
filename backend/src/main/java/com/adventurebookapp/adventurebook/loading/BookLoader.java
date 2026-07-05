package com.adventurebookapp.adventurebook.loading;

import com.adventurebookapp.adventurebook.model.Book;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;

public class BookLoader {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    public Book load(InputStream jsonStream) {
        return jsonMapper.readValue(jsonStream, Book.class);
    }
}