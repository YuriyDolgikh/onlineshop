package org.onlineshop.exception;

import lombok.Getter;


@Getter
public enum UrlValidationError {

    INVALID_LENGTH("Image URL too long"),
    INVALID_DOMAIN("Image URL domain not allowed"),
    INVALID_EXTENSION("Image URL extension not allowed"),
    UNREACHABLE("Image URL not reachable");

    private final String message;

    UrlValidationError(String message) {
        this.message = message;
    }

}