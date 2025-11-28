package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserServiceConfirmationEmailTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @AfterEach
    void dropDatabase() {
        confirmationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testConfirmationEmailSuccess() {
        User newTestUser = User.builder()
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .status(User.Status.NOT_CONFIRMED)
                .role(User.Role.USER)
                .build();

        User savedUser = userRepository.save(newTestUser);

        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code("validConfirmationCode")
                .user(savedUser)
                .isConfirmed(false)
                .expireDataTime(LocalDateTime.now().plusHours(24))
                .build();

        confirmationCodeRepository.save(confirmationCode);

        String result = userService.confirmationEmail("validConfirmationCode");

        assertEquals("Email testUser@email.com is successfully confirmed", result);

        User updatedUser = userRepository.findById(savedUser.getUserId()).orElseThrow();
        assertEquals(User.Status.CONFIRMED, updatedUser.getStatus());

        ConfirmationCode updatedCode = confirmationCodeRepository.findByCode("validConfirmationCode").get();
        assertTrue(updatedCode.isConfirmed());
    }

    @Test
    void testConfirmationEmailInvalidCode() {
        String invalidCode = "InvalidCode";
        assertThrows(BadRequestException.class, () -> userService.confirmationEmail(invalidCode));
    }

    @Test
    void testConfirmationEmailNullCode() {
        assertThrows(BadRequestException.class, () -> userService.confirmationEmail(null));
    }

    @Test
    void testConfirmationEmailEmptyCode() {
        assertThrows(BadRequestException.class, () -> userService.confirmationEmail(" "));
    }

    @Test
    void testConfirmationEmailOneTransaction() {
        User newTestUser = User.builder()
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .status(User.Status.NOT_CONFIRMED)
                .role(User.Role.USER)
                .build();

        User savedUser = userRepository.save(newTestUser);

        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code("validConfirmationCode")
                .user(savedUser)
                .isConfirmed(false)
                .expireDataTime(LocalDateTime.now().plusHours(24))
                .build();

        confirmationCodeRepository.save(confirmationCode);

        String result = userService.confirmationEmail("validConfirmationCode");

        assertEquals("Email testUser@email.com is successfully confirmed", result);

        User updatedUser = userRepository.findById(savedUser.getUserId()).orElseThrow();
        ConfirmationCode updatedCode = confirmationCodeRepository.findByCode("validConfirmationCode").get();

        assertEquals(User.Status.CONFIRMED, updatedUser.getStatus());
        assertTrue(updatedCode.isConfirmed());
    }

}