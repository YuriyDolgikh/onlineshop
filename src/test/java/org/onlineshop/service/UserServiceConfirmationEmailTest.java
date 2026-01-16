package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.mail.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceConfirmationEmailTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfirmationCodeService confirmationCodeService;

    @MockBean
    private MailUtil mailUtil;

    private User testUser;

    @BeforeEach
    void setUp() {
        confirmationCodeRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .username("testUser")
                .email("test@example.com")
                .hashPassword("encodedPassword")
                .phoneNumber("+1234567890")
                .status(User.Status.NOT_CONFIRMED)
                .role(User.Role.USER)
                .build();

        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        confirmationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void confirmationEmail_SuccessWithValidCode() {
        ConfirmationCode validConfirmationCode = ConfirmationCode.builder()
                .code("VALID_CODE")
                .user(testUser)
                .isConfirmed(false)
                .expireDataTime(LocalDateTime.now().plusHours(24))
                .build();
        confirmationCodeRepository.save(validConfirmationCode);

        String result = userService.confirmationEmail("VALID_CODE");

        assertEquals("Email test@example.com is successfully confirmed", result);

        User updatedUser = userRepository.findById(testUser.getUserId()).orElseThrow();
        assertEquals(User.Status.CONFIRMED, updatedUser.getStatus());

        ConfirmationCode updatedCode = confirmationCodeRepository.findByCode("VALID_CODE").orElseThrow();
        assertTrue(updatedCode.isConfirmed());
    }

    @Test
    void confirmationEmail_ReturnsInvalidCodeMessage_WhenCodeDoesNotExist() {
        doNothing().when(mailUtil).sendConfirmationEmail(any(User.class), any(String.class));

        String result = userService.confirmationEmail("NON_EXISTENT_CODE");

        assertEquals("Invalid confirmation code", result);

        User user = userRepository.findById(testUser.getUserId()).orElseThrow();
        assertEquals(User.Status.NOT_CONFIRMED, user.getStatus());
    }

    @Test
    void confirmationEmail_HandlesExpiredCode() {
        doNothing().when(mailUtil).sendConfirmationEmail(any(User.class), any(String.class));

        ConfirmationCode expiredCode = ConfirmationCode.builder()
                .code("EXPIRED_CODE")
                .user(testUser)
                .isConfirmed(false)
                .expireDataTime(LocalDateTime.now().minusMinutes(30))
                .build();
        confirmationCodeRepository.save(expiredCode);

        String result = userService.confirmationEmail("EXPIRED_CODE");

        assertTrue(result.contains("Confirmation code for email: test@example.com is expired."));
        assertTrue(result.contains("Please, check your email again for the new one."));
    }

    @Test
    void confirmationEmail_HandlesAlreadyConfirmedCode() {
        ConfirmationCode alreadyConfirmedCode = ConfirmationCode.builder()
                .code("CONFIRMED_CODE")
                .user(testUser)
                .isConfirmed(true)
                .expireDataTime(LocalDateTime.now().plusHours(24))
                .build();
        confirmationCodeRepository.save(alreadyConfirmedCode);

        String result = userService.confirmationEmail("CONFIRMED_CODE");

        assertEquals("Confirmation code already confirmed", result);

        User user = userRepository.findById(testUser.getUserId()).orElseThrow();
        assertEquals(User.Status.NOT_CONFIRMED, user.getStatus());
    }

    @Test
    void confirmationEmail_HandlesNullCode() {
        String result = userService.confirmationEmail(null);

        assertEquals("Code is null or blank", result);
    }

    @Test
    void confirmationEmail_HandlesEmptyCode() {
        String result = userService.confirmationEmail("");

        assertEquals("Code is null or blank", result);
    }

    @Test
    void confirmationEmail_HandlesBlankCode() {
        String result = userService.confirmationEmail("   ");

        assertEquals("Code is null or blank", result);
    }

    @Test
    void confirmationEmail_MultipleCallsWithSameValidCode() {
        ConfirmationCode validCode = ConfirmationCode.builder()
                .code("VALID_CODE")
                .user(testUser)
                .isConfirmed(false)
                .expireDataTime(LocalDateTime.now().plusHours(24))
                .build();
        confirmationCodeRepository.save(validCode);

        String firstResult = userService.confirmationEmail("VALID_CODE");
        assertEquals("Email test@example.com is successfully confirmed", firstResult);

        String secondResult = userService.confirmationEmail("VALID_CODE");
        assertEquals("Confirmation code already confirmed", secondResult);
    }

    @Test
    void confirmationEmail_HandlesCaseSensitiveCode() {
        ConfirmationCode validCode = ConfirmationCode.builder()
                .code("VALID_CODE")
                .user(testUser)
                .isConfirmed(false)
                .expireDataTime(LocalDateTime.now().plusHours(24))
                .build();
        confirmationCodeRepository.save(validCode);

        String lowercaseResult = userService.confirmationEmail("valid_code");
        assertEquals("Invalid confirmation code", lowercaseResult);

        String uppercaseResult = userService.confirmationEmail("VALID_CODE");
        assertEquals("Email test@example.com is successfully confirmed", uppercaseResult);
    }
}