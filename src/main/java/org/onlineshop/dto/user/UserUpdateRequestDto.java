package org.onlineshop.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequestDto {

    @NotNull
    @NotBlank(message = "Username is required and must be not blank)")
    @Size(min = 3, max = 15, message = "Username must be between 3 and 15 characters")
    private String username;

    @NotNull
    @NotBlank(message = "Phone number is required and must be not blank)")
    @Size(min = 6, max = 20, message = "Phone number must be between 6 and 20 characters")
    private String phoneNumber;

    @NotNull
    @NotBlank(message = "Password is required and must be not blank)")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String hashPassword;

}
