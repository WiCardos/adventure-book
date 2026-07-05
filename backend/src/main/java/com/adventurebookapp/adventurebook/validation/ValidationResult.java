package com.adventurebookapp.adventurebook.validation;

import java.util.List;

public record ValidationResult(boolean valid, List<ValidationError> errors) {

    public static ValidationResult success() {
        return new ValidationResult(true, List.of());
    }

    public static ValidationResult invalid(ValidationError... errors) {
        return new ValidationResult(false, List.of(errors));
    }
}
