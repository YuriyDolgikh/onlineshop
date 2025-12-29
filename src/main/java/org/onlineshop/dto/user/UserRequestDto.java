package org.onlineshop.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank
    @Size(min = 3, max = 15, message = "Username must be between 3 and 15 characters")
    private String name;

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must contain only digits and may start with +, length 7–15"
    )
    private String phoneNumber;

    @NotBlank
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String hashPassword;
}
