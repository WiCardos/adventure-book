package com.adventurebookapp.adventurebook.validation;

import com.adventurebookapp.adventurebook.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BookValidatorTest {

    private final BookValidator validator = new BookValidator();

    @Test
    void bookWithNoBeginSection_isInvalid() {
        Section endSection = new Section(1, "The end.", SectionType.END, List.of());
        Book book = new Book("Test Book", "Test Author", Difficulty.EASY, List.of(endSection));

        ValidationResult result = validator.validate(book);

        assertThat(result.valid()).isFalse();
        assertThat(result.errors()).containsExactly(ValidationError.MISSING_BEGIN_SECTION);
    }


    @Test
    void bookWithMoreThanOneBeginSection_isInvalid() {
        Section firstBeginSection  = new Section(1, "The start.", SectionType.BEGIN, List.of(new Option("Continue", 1, null)));
        Section secondBeginSection = new Section(2, "The restart?", SectionType.BEGIN, List.of(new Option("Continue", 1, null)));
        Section endSection = new Section(3, "The end.", SectionType.END, List.of());
        Book book = new Book("Test Book", "Test Author", Difficulty.EASY, List.of(firstBeginSection, secondBeginSection, endSection));

        ValidationResult result = validator.validate(book);

        assertThat(result.valid()).isFalse();
        assertThat(result.errors()).containsExactly(ValidationError.MULTIPLE_BEGIN_SECTIONS);
    }


    @Test
    void bookWithNoEndSection_isInvalid() {
        Section beginSection = new Section(1, "The start.", SectionType.BEGIN,
                List.of(new Option("Continue", 1, null)));
        Book book = new Book("Test Book", "Test Author", Difficulty.EASY, List.of(beginSection));

        ValidationResult result = validator.validate(book);

        assertThat(result.valid()).isFalse();
        assertThat(result.errors()).containsExactly(ValidationError.NO_END_SECTION);
    }


    @Test
    void bookWithNoBeginAndNoEndSection_reportsBothErrors() {
        Section nodeSection = new Section(1, "Just a node.", SectionType.NODE,
                List.of(new Option("Continue", 1, null)));
        Book book = new Book("Test Book", "Test Author", Difficulty.EASY, List.of(nodeSection));

        ValidationResult result = validator.validate(book);

        assertThat(result.valid()).isFalse();
        assertThat(result.errors()).containsExactlyInAnyOrder(
                ValidationError.MISSING_BEGIN_SECTION,
                ValidationError.NO_END_SECTION
        );
    }


    @Test
    void bookWithInvalidNextSectionId_isInvalid() {
        Section firstBeginSection  = new Section(1, "The start.", SectionType.BEGIN,
                List.of(new Option("Continue", 99, null)));
        Section endSection = new Section(3, "The end.", SectionType.END, List.of());
        Book book = new Book("Test Book", "Test Author", Difficulty.EASY, List.of(firstBeginSection, endSection));

        ValidationResult result = validator.validate(book);

        assertThat(result.valid()).isFalse();
        assertThat(result.errors()).containsExactly(ValidationError.INVALID_SECTION_REFERENCE);
    }


    @Test
    void bookWithNonEndingSectionWithoutOptions_isInvalid() {
        Section firstBeginSection  = new Section(1, "The start.", SectionType.BEGIN, List.of());
        Section endSection = new Section(3, "The end.", SectionType.END, List.of());
        Book book = new Book("Test Book", "Test Author", Difficulty.EASY, List.of(firstBeginSection, endSection));

        ValidationResult result = validator.validate(book);

        assertThat(result.valid()).isFalse();
        assertThat(result.errors()).containsExactly(ValidationError.NON_ENDING_SECTION_WITHOUT_OPTIONS);
    }
}
