package org.onlineshop.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserControllerDeleteTest {
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

    private Integer userId;

    private Integer userIdTwo;

    @BeforeEach
    void setUp() {
        User adminConfirmed = User.builder()
                .username("admin")
                .email("admin@example.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .status(User.Status.CONFIRMED)
                .role(User.Role.ADMIN)
                .build();

        userRepository.save(adminConfirmed);

        ConfirmationCode confirmationCodeOne = ConfirmationCode.builder()
                .code("some")
                .user(adminConfirmed)
                .expireDataTime(LocalDateTime.now().plusDays(1))
                .build();

        confirmationCodeRepository.save(confirmationCodeOne);

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

        userIdTwo = userConfirmed.getUserId();

        ConfirmationCode confirmationCodeTwo = ConfirmationCode.builder()
                .code("someConfirmation")
                .user(userConfirmed)
                .expireDataTime(LocalDateTime.now().plusDays(1))
                .build();

        confirmationCodeRepository.save(confirmationCodeTwo);

        User userTwoConfirmed = User.builder()
                .username("userTwo")
                .email("userTwo@example.com")
                .hashPassword("$2a$10$35bmYiB2kQKydbNporsKfeYTff58vhJc0K0mSNXzbxetiXUJWSWFe")
                .phoneNumber("+493214568513")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        userRepository.save(userTwoConfirmed);

        userId = userTwoConfirmed.getUserId();

        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code("someConfirmationCode")
                .user(userTwoConfirmed)
                .expireDataTime(LocalDateTime.now().plusDays(1))
                .build();

        confirmationCodeRepository.save(confirmationCode);
    }

    @Test
    @WithMockUser(username = "userTwo@example.com", roles = "USER")
    void testDeleteUserIfOkAndRoleUser() {
        ResponseEntity<UserResponseDto> response = userController.deleteUser(userId);

        assertEquals("DELETED", response.getBody().getStatus());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "USER")
    void testDeleteUserIfIdNotFoundAndRoleUser() {
        assertThrows(NotFoundException.class, () -> userController.deleteUser(100000));
    }

    @Test
    @WithMockUser(username = "userTwo@example.com", roles = "USER")
    void testDeleteUserIfAnotherUserAndRoleUser() {
        Exception exception = assertThrows(BadRequestException.class, () -> userController.deleteUser(userIdTwo));
        assertEquals("You can't delete another user", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void testDeleteUserIfRoleAdmin() {
        assertThrows(BadRequestException.class, () -> userController.deleteUser(adminId));
    }

}