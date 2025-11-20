package org.onlineshop.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.entity.User;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.security.dto.AuthRequestDto;
import org.onlineshop.security.dto.AuthResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserControllerAuthenticateTest {
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

        User managerNotConfirmed = User.builder()
                .username("manager")
                .email("manager@example.com")
                .hashPassword("$2a$10$35bmYiB2kQKydbNporsKfeYTff58vhJc0K0mSNXzbxetiXUJWSWFe")
                .phoneNumber("+49321456789")
                .status(User.Status.NOT_CONFIRMED)
                .role(User.Role.MANAGER)
                .build();

        userRepository.save(managerNotConfirmed);
    }

    @Test
    void testAuthenticateIfOk() {
        AuthRequestDto requestDto = AuthRequestDto.builder()
                .username("admin@example.com")
                .password("admin123")
                .build();
        ResponseEntity<AuthResponseDto> responseDto = userController.authenticate(requestDto);

        assertNotNull(responseDto.getBody().getToken());
    }

    @Test
    void testAuthenticateIfUserNotConfirmed() {
        AuthRequestDto requestDto = AuthRequestDto.builder()
                .username("manager@example.com")
                .password("manager123")
                .build();

        assertThrows(DisabledException.class, () -> userController.authenticate(requestDto));
    }

    @Test
    void testAuthenticateIfUsernameIsBlank() {
        AuthRequestDto requestDto = AuthRequestDto.builder()
                .username(" ")
                .password("admin123")
                .build();

        assertThrows(NotFoundException.class, () -> userController.authenticate(requestDto));
    }

    @Test
    void testAuthenticateIfPasswordIsBlank() {
        AuthRequestDto requestDto = AuthRequestDto.builder()
                .username("admin@example.com ")
                .password(" ")
                .build();

        assertThrows(NotFoundException.class, () -> userController.authenticate(requestDto));
    }

    @Test
    void testAuthenticateIfUsernameIsNull() {
        AuthRequestDto requestDto = AuthRequestDto.builder()
                .username(null)
                .password("admin123")
                .build();

        assertThrows(NotFoundException.class, () -> userController.authenticate(requestDto));
    }

    @Test
    void testAuthenticateIfPasswordIsNull() {
        AuthRequestDto requestDto = AuthRequestDto.builder()
                .username("admin@example.com ")
                .password(null)
                .build();

        assertThrows(NotFoundException.class, () -> userController.authenticate(requestDto));
    }
}