package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ConfirmationCodeServiceDeleteConfirmationCodeTest {

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationCodeService confirmationCodeService;

    @AfterEach
    void tearDown() {
        confirmationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindCodesByUserIfPresent() {
        User newTestUser = User.builder()
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        User savedUser = userRepository.save(newTestUser);

        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code("someConfirmationCode")
                .user(savedUser)
                .expireDataTime(LocalDateTime.now().plusDays(1))
                .build();

        confirmationCodeRepository.save(confirmationCode);

        confirmationCodeService.deleteConfirmationCodeByUser(savedUser);

        Optional<ConfirmationCode> deletedConfirmationCode = confirmationCodeRepository.findByUser(savedUser);

        assertTrue(deletedConfirmationCode.isEmpty());
    }

    @Test
    void testDeleteCodesByUserWhenNoCodesFound() {
        User user = User.builder()
                .email("test@example.com")
                .username("testUser")
                .hashPassword("password")
                .phoneNumber("+1234567890")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        assertThrows(NotFoundException.class, () -> confirmationCodeService.deleteConfirmationCodeByUser(savedUser));
    }

    @Test
    void testFindCodesByUserWhenUserIsNull() {
        User user = null;
        assertThrows(IllegalArgumentException.class, () -> confirmationCodeService.deleteConfirmationCodeByUser(user));
    }
}