package org.onlineshop.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(min = 3, max = 15)
    private String name;

    @Email(regexp = "^[A-Za-z0-9]+@[A-Za-z0-9]+\\.[A-Za-z]{2,}$", message = "Invalid email")
    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Size(min = 6, max = 20)
    private String hashPassword;
}
