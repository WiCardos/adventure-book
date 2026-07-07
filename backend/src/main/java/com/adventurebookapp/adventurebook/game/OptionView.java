package com.adventurebookapp.adventurebook.game;

import com.adventurebookapp.adventurebook.model.Option;

public record OptionView(String description, int gotoId) {
    public static OptionView from(Option option) {
        return new OptionView(option.description(), option.gotoId());
    }
}