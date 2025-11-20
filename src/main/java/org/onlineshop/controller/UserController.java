package org.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
@Tag(name = "User Management", description = "APIs for user registration, authentication, and management operations")
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
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided details. User will receive a confirmation email."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid input data or user already exists"
            )
    })
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
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates user credentials and returns JWT token for authorized access"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid credentials"
            )
    })
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
    @Operation(
            summary = "Confirm email address",
            description = "Confirms user's email address using the confirmation code sent to their email"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Email successfully confirmed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - invalid confirmation code"
            )
    })
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
    @Operation(
            summary = "Update user information",
            description = "Updates the authenticated user's information. Users can only update their own profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully updated",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid input data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - user not found"
            )
    })
    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @PathVariable Integer userId, @Valid @RequestBody UserUpdateRequestDto requestDto) {
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
    @Operation(
            summary = "Delete user account",
            description = "Deletes a user account. ADMIN can delete any user, while USER/MANAGER can only delete themselves."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully deleted",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - user cannot be deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - user not found"
            )
    })
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserResponseDto> deleteUser(@Valid @PathVariable Integer userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }

    /**
     * Renews the currently authenticated user's access by email.
     *
     * @param email the email address of the user to renew
     * @return new confirmation email with a link to renew the access
     */
    @Operation(
            summary = "Renew deleted user",
            description = "Restores a previously deleted user account and sends new confirmation email"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully renewed",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - user cannot be renewed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - user not found"
            )
    })
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @GetMapping("/renew/{email}")
    public ResponseEntity<UserResponseDto> renewUser(@Email @PathVariable String email) {
        return ResponseEntity.ok(userService.renewUser(email));
    }

}
