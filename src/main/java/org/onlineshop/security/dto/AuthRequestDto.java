package org.onlineshop.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthRequestDto {

    @NotNull
    @NotBlank
    @Email
    private String username;

    @NotNull
    @NotBlank
    private String password;

}
