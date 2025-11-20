package org.onlineshop.exception;

import lombok.Getter;

@Getter
public class UrlValidationException extends ValidationException {
    private final UrlValidationError error;

    public UrlValidationException(UrlValidationError error) {
        super(error.getMessage());
        this.error = error;
    }
}