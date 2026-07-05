package com.adventurebookapp.adventurebook.validation;

import com.adventurebookapp.adventurebook.model.Book;
import com.adventurebookapp.adventurebook.model.Section;
import com.adventurebookapp.adventurebook.model.SectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookValidator {
    public ValidationResult validate(Book book) {
        List<ValidationError> errors = new ArrayList<>();

        long beginCount = book.sections().stream()
                .filter(section -> section.type() == SectionType.BEGIN)
                .count();

        if (beginCount == 0) {
            errors.add(ValidationError.MISSING_BEGIN_SECTION);
        } else if (beginCount > 1) {
            errors.add(ValidationError.MULTIPLE_BEGIN_SECTIONS);
        }


        boolean hasEndSection = book.sections().stream()
                .anyMatch(section -> section.type() == SectionType.END);

        if (!hasEndSection) {
            errors.add(ValidationError.NO_END_SECTION);
        }


        Set<Integer> sectionIds = book.sections().stream()
                .map(Section::id)
                .collect(Collectors.toSet());

        boolean hasInvalidReference = book.sections().stream()
                .flatMap(section -> section.options().stream())
                .anyMatch(option -> !sectionIds.contains(option.gotoId()));

        if (hasInvalidReference) {
            errors.add(ValidationError.INVALID_SECTION_REFERENCE);
        }


        boolean hasNonEndingSectionWithoutOptions = book.sections().stream()
                .filter(section -> section.type() != SectionType.END)
                .anyMatch(section -> section.options().isEmpty());

        if (hasNonEndingSectionWithoutOptions) {
            errors.add(ValidationError.NON_ENDING_SECTION_WITHOUT_OPTIONS);
        }


        if (errors.isEmpty()) {
            return ValidationResult.success();
        }

        return ValidationResult.invalid(errors.toArray(new ValidationError[0]));

    }
}
