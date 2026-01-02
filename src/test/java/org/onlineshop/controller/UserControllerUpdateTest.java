package org.onlineshop.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.dto.user.UserUpdateRequestDto;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserControllerUpdateTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    private UserController userController;

    @AfterEach
    void dropDatabase() {
        confirmationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Integer adminId;

    @BeforeEach
    void setUp() {
        User adminConfirmed = User.builder()
                .username("admin")
                .email("admin@example.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+49494949")
                .status(User.Status.CONFIRMED)
                .role(User.Role.ADMIN)
                .build();

        userRepository.save(adminConfirmed);

        adminId = adminConfirmed.getUserId();

        User userConfirmed = User.builder()
                .username("user")
                .email("user@example.com")
                .hashPassword("$2a$10$35bmYiB2kQKydbNporsKfeYTff58vhJc0K0mSNXzbxetiXUJWSWFe")
                .phoneNumber("+49321456789")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        userRepository.save(userConfirmed);
    }

    @Test
    @WithMockUser(username = "admin@example.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void testUpdateUserUserNameIfOkAndRoleIsAdminManagerUser() {
        UserUpdateRequestDto userUpdateRequestDto = UserUpdateRequestDto.builder()
                .username("Alex")
                .build();

        ResponseEntity<UserResponseDto> response = userController.updateUser(adminId, userUpdateRequestDto);

        assertNotNull(response.getBody());
        assertEquals("Alex", response.getBody().getUsername());
        assertEquals(2, userRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "admin@example.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void testUpdateUserPhoneNumberIfOkAndRoleIsAdminManagerUser() {
        UserUpdateRequestDto userUpdateRequestDto = UserUpdateRequestDto.builder()
                .phoneNumber("+49321456773")
                .build();

        ResponseEntity<UserResponseDto> response = userController.updateUser(adminId, userUpdateRequestDto);

        assertNotNull(response.getBody());
        assertEquals("+49321456773", response.getBody().getPhoneNumber());
        assertEquals(2, userRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "admin@example.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void testUpdateUserPasswordIfOkAndRoleIsAdminManagerUser() {
        UserUpdateRequestDto userUpdateRequestDto = UserUpdateRequestDto.builder()
                .hashPassword("alex12345")
                .build();

        ResponseEntity<UserResponseDto> response = userController.updateUser(adminId, userUpdateRequestDto);

        assertNotNull(response.getBody());
        assertEquals("admin", response.getBody().getUsername());
        assertEquals(2, userRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "admin@example.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void testUpdateUserPhoneNumberAlreadyExistIfAndRoleIsAdminManagerUser() {
        UserUpdateRequestDto userUpdateRequestDto = UserUpdateRequestDto.builder()
                .phoneNumber("+49321456789")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> userController.updateUser(adminId, userUpdateRequestDto));
    }

    @Test
    void testUpdateUserPhoneNumberIfAndUserNotRegistered() {
        UserUpdateRequestDto userUpdateRequestDto = UserUpdateRequestDto.builder()
                .phoneNumber("+4932223654123")
                .build();

        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> userController.updateUser(adminId, userUpdateRequestDto));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void testUpdateUserPhoneNumberIfAnotherUser() {
        UserUpdateRequestDto userUpdateRequestDto = UserUpdateRequestDto.builder()
                .phoneNumber("+4932223654123")
                .build();

        assertThrows(BadRequestException.class, () -> userController.updateUser(adminId, userUpdateRequestDto));
    }
}