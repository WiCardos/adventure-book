package com.adventurebookapp.adventurebook.validation;

import java.util.List;

public class InvalidBookException extends RuntimeException {
    private final List<ValidationError> errors;

    public InvalidBookException(List<ValidationError> errors) {
        super("Book failed validation: " + errors);
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
