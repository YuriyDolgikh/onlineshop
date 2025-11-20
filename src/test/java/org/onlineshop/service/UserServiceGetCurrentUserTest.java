package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserServiceGetCurrentUserTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    private UserService userService;

    @AfterEach
    void dropDatabase() {
        confirmationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Integer adminId;

    @BeforeEach
    void setUp() {
        User admin = User.builder()
                .username("admin")
                .email("admin@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+4912121212")
                .status(User.Status.CONFIRMED)
                .role(User.Role.ADMIN)
                .build();

        User savedAdmin = userRepository.save(admin);

        adminId = admin.getUserId();

        ConfirmationCode confirmationCodeAdmin = ConfirmationCode.builder()
                .code("ConfirmationCode")
                .user(savedAdmin)
                .expireDataTime(LocalDateTime.now().plusDays(1))
                .build();

        confirmationCodeRepository.save(confirmationCodeAdmin);
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = "ADMIN")
    void testGetCurrentUser() {
        User currentUser = userService.getCurrentUser();

        assertNotNull(currentUser);
        assertEquals(currentUser.getUsername(), "admin");

    }
}