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
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserControllerRenewTest {
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

    private String userEmailTwo;

    @BeforeEach
    void setUp() {
        User userConfirmed = User.builder()
                .username("user")
                .email("user@example.com")
                .hashPassword("$2a$10$35bmYiB2kQKydbNporsKfeYTff58vhJc0K0mSNXzbxetiXUJWSWFe")
                .phoneNumber("+49321456789")
                .status(User.Status.DELETED)
                .role(User.Role.USER)
                .build();

        userRepository.save(userConfirmed);

        userEmailTwo = userConfirmed.getEmail();
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void testRenewUserIfOkAndRoleUser() {
        ResponseEntity<UserResponseDto> response = userController.renewUser(userEmailTwo);

        assertEquals("NOT_CONFIRMED", response.getBody().getStatus());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "USER")
    void testRenewUserIfEmailNotFoundAndRoleUser() {
        assertThrows(NotFoundException.class, () -> userController.renewUser("NotFound@example.com"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "USER")
    void testRenewUserIfNotDeleted() {
        User userTwoConfirmed = User.builder()
                .username("userTwo")
                .email("userTwo@example.com")
                .hashPassword("$2a$10$35bmYiB2kQKydbNporsKfeYTff58vhJc0K0mSNXzbxetiXUJWSWFe")
                .phoneNumber("+493214568513")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        userRepository.save(userTwoConfirmed);

        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code("someConfirmationCode")
                .user(userTwoConfirmed)
                .expireDataTime(LocalDateTime.now().plusDays(1))
                .build();

        confirmationCodeRepository.save(confirmationCode);


        assertThrows(BadRequestException.class, () -> userController.renewUser(userTwoConfirmed.getEmail()));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "ADMIN")
    void testRenewUserIfEmailNull() {
        assertThrows(BadRequestException.class, () -> userController.renewUser(null));
    }

    @Test
    void testRenewUserIfUserNotRegistered() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> userController.renewUser("another@email.com"));
    }

}