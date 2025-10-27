package org.onlineshop.validation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorsDto> handlerValidationException(MethodArgumentNotValidException e) {
        List<ValidationErrorDto> validationErrors = new ArrayList<>();

        List<ObjectError> objectErrors = e.getBindingResult().getAllErrors();

        for (ObjectError currentError : objectErrors) {

            FieldError fieldError = (FieldError) currentError;

            ValidationErrorDto errorDto = ValidationErrorDto.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();

            if (fieldError.getRejectedValue() != null) {
                errorDto.setRejectedValue(fieldError.getRejectedValue().toString());
            }

            validationErrors.add(errorDto);
        }

        return ResponseEntity
                .badRequest()
                .body(ValidationErrorsDto.builder()
                        .errors(validationErrors)
                        .build());
    }
}
