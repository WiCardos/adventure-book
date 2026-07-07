package com.adventurebookapp.adventurebook.game;

import com.adventurebookapp.adventurebook.model.Section;
import com.adventurebookapp.adventurebook.model.SectionType;

import java.util.List;

public record SectionView(String text, List<OptionView> options, boolean isEnding) {
    public static SectionView from(Section section) {
        List<OptionView> options = section.options().stream().map(OptionView::from).toList();
        return new SectionView(section.text(), options, section.type() == SectionType.END);
    }
}