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
    @NotNull
    @Size(min = 3, max = 15, message = "Username must be between 3 and 15 characters")
    private String name;

    @Email(regexp = "^[A-Za-z0-9.-]+@[A-Za-z0-9]+\\.[A-Za-z]{2,}$", message = "Invalid email")
    @NotBlank
    @NotNull
    private String email;

    @NotBlank
    @NotNull
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must contain only digits and may start with +, length 7–15"
    )
    private String phoneNumber;

    @NotBlank
    @NotNull
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String hashPassword;
}
