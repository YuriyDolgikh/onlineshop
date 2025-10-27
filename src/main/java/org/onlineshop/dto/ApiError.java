package org.onlineshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ApiError {

    // Common error description ("Validation failed", "User not found")
    private String error;

    // Detailed error description ("User with ID = 2 not found")
    private String message;

    // Parameter name, if error related to it ("userId")
    private String parameter;

    // The value that caused the error
    private Object rejectedValue;

    // Validation error list (if exists)
    private List<Map<String, Object>> errors;

    // Date and Time of error occurrence
    private LocalDateTime timestamp;

}
