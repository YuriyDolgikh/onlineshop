package org.onlineshop.controller;


import jakarta.validation.ConstraintViolationException;
import org.onlineshop.dto.ApiError;
import org.onlineshop.exception.*;
import org.onlineshop.security.exception.InvalidJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handlerDateTimeParseException(DateTimeParseException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handlerNullPointerException(NullPointerException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handlerNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<String> handlerAlreadyExistException(AlreadyExistException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MailSendingException.class)
    public ResponseEntity<String> handlerMailSendingException(MailSendingException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handlerConstraintViolationException(ConstraintViolationException e) {
        StringBuilder responseMessage = new StringBuilder();

        e.getConstraintViolations().forEach(
                constraintViolation -> {
                    String currentField = constraintViolation.getPropertyPath().toString();
                    String currentMessage = constraintViolation.getMessage();
                    responseMessage.append("Field : " + currentField + " : " + currentMessage);
                    responseMessage.append("\n");
                }
        );
        return new ResponseEntity<>(responseMessage.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Validation failed");
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(Map.of("error", "This User is not registered"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(Map.of("error", "Wrong login or password"));
    }

    /**
     * Handles  thrown when the request body
     * cannot be parsed or converted to the target Java type (invalid JSON,
     * wrong enum constant, or incorrect date format).
     *
     * @param ex the thrown  HttpMessageNotReadableException
     * @return a  ResponseEntity containing a descriptive error message
     * and HTTP 400 (Bad Request) status
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getRootCause() != null ? ex.getRootCause() : ex.getMostSpecificCause();

        String msg = "Invalid request body";

        //Json: invalid field format (enum and LocalDate)
        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            com.fasterxml.jackson.databind.exc.InvalidFormatException ife =
                    (com.fasterxml.jackson.databind.exc.InvalidFormatException) cause;

            Class<?> target = ife.getTargetType();
            String field = ife.getPath().isEmpty() ? "value" : ife.getPath().get(0).getFieldName();

            //LocalDate
            if (java.time.LocalDate.class.equals(target)) {
                msg = "Invalid value for '" + field + "'. Use date format yyyy-MM-dd (e.g. 2025-10-08)";
                return bad(msg);
            }

            // Enum
            if (target.isEnum()) {
                String allowed = java.util.Arrays.stream(target.getEnumConstants())
                        .map(Object::toString)
                        .collect(java.util.stream.Collectors.joining(", "));
                msg = "Invalid value for '" + field + "'. Allowed: " + allowed;
                return bad(msg);
            }
        }

        //DateTimeParseException
        if (cause instanceof DateTimeParseException) {
            msg = "Invalid date format. Use yyyy-MM-dd (e.g. 2025-10-08)";
            return bad(msg);
        }

        return bad(msg);
    }

    /**
     * Builds a standardized ResponseEntity for bad request responses (HTTP 400).
     * Used internally by exception handlers to return error messages in JSON format.
     *
     * @param message human-readable error description
     * @return ResponseEntity containing a map with key {@code "message"}
     */
    private ResponseEntity<Map<String, Object>> bad(String message) {
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJwt(InvalidJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ApiError error = ApiError.builder()
                .error("Invalid parameter")
                .message("Failed to convert parameter value")
                .parameter(e.getName())
                .rejectedValue(e.getValue())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal Server Error: " + ex.getMessage());
    }

}
