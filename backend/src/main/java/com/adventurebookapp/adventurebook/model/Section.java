package com.adventurebookapp.adventurebook.model;

import java.util.List;

public record Section(int id, String text, SectionType type, List<Option> options) {
    public Section {
        options = options != null ? options : List.of();
    }
}
