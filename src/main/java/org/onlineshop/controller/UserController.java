package org.onlineshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.user.UserRequestDto;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.dto.user.UserUpdateRequestDto;
import org.onlineshop.security.dto.AuthRequestDto;
import org.onlineshop.security.dto.AuthResponseDto;
import org.onlineshop.security.service.AuthService;
import org.onlineshop.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * Registers a new user based on the provided user details.
     *
     * @param userRequestDto the user data for registration
     * @return a ResponseEntity containing the created UserResponseDto
     * *         with HTTP status 201 (Created) if the registration was successful,
     * *         or HTTP status 400 (Bad Request) if the registration failed.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registration(userRequestDto));
    }

    /**
     * Authenticates a user based on the provided credentials and generates a JWT token.
     *
     * @param authRequestDto the authentication request containing the user's credentials
     * @return ResponseEntity containing an AuthResponseDto with the generated JWT token with HTTP status 200 (OK)
     * *         or HTTP status 401 (Unauthorized) if the authentication failed.
     */
    @PostMapping("/login")
    ResponseEntity<AuthResponseDto> authenticate(@Valid @RequestBody AuthRequestDto authRequestDto) {
        String jwt = authService.generateJwt(authRequestDto);
        return ResponseEntity.ok(new AuthResponseDto(jwt));
    }

    /**
     * Confirms a user's email based on the provided confirmation code.
     *
     * @param code the unique confirmation code sent to the user's email
     * @return a ResponseEntity containing a success message if the confirmation is successful
     * with HTTP status 200 (OK)
     * *         or HTTP status 404 (Not Found) if the confirmation code is invalid.
     */
    @GetMapping("/confirmation")
    public ResponseEntity<String> confirmation(@Valid @RequestParam String code) {
        return ResponseEntity.ok(userService.confirmationEmail(code));
    }

    /**
     * Updates the currently authenticated user's information based on the provided data.
     *
     * @param requestDto the UserUpdateRequestDto containing updated user details
     * @return a ResponseEntity containing the updated UserResponseDto
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @PathVariable Integer userId, @RequestBody UserUpdateRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateUser(userId, requestDto));
    }

    /**
     * Deletes the currently authenticated user.
     * User ADMIN can delete any user, but users with role USER or MANAGER can delete only themselves.
     *
     * @param userId the ID of the user to be deleted
     * @return a ResponseEntity containing the deleted UserResponseDto with HTTP status 200 (OK)
     * or HTTP status 400 (Bad Request) if the user can't be deleted
     * or HTTP status 404 (Not Found) if the user with the specified ID does not exist.
     */
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/renew/{email}")
    public ResponseEntity<UserResponseDto> renewUser(@PathVariable String email) {
        return ResponseEntity.ok(userService.renewUser(email));
    }

}
