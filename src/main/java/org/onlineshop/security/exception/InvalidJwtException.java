package org.onlineshop.security.exception;

import lombok.Generated;

@Generated
public class InvalidJwtException extends RuntimeException {
    public InvalidJwtException(String message) {
        super(message);
    }
}
