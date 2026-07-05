package com.adventurebookapp.adventurebook.model;

import java.util.List;

public record Book(String title, String author, Difficulty difficulty, List<Section> sections) {
}
