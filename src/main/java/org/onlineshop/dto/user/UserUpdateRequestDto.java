package org.onlineshop.dto.user;

import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^$|^.{3,20}$", message = "Username must be empty or between 3 and 20 characters")
    private String username;

    @Pattern(
            regexp = "^$|^\\+?[0-9]{7,15}$",
            message = "Phone number must be empty or contain 7–15 digits and may start with +"
    )
    private String phoneNumber;

    @Pattern(regexp = "^$|^.{6,20}$", message = "Password must be empty or between 6 and 20 characters")
    private String hashPassword;

}
