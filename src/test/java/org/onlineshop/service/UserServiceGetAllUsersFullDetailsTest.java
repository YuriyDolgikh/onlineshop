package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserServiceGetAllUsersFullDetailsTest {

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
    void testGetAllUsersFullDetailsWhenUsersExist() {
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

        User admin = User.builder()
                .username("admin")
                .email("admin@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+4912121212")
                .status(User.Status.NOT_CONFIRMED)
                .role(User.Role.ADMIN)
                .build();

        User savedAdmin = userRepository.save(admin);

        ConfirmationCode confirmationCodeAdmin = ConfirmationCode.builder()
                .code("ConfirmationCode")
                .user(savedAdmin)
                .expireDataTime(LocalDateTime.now().plusDays(1))
                .build();

        confirmationCodeRepository.save(confirmationCodeAdmin);

        List<User> result = userService.getAllUsersFullDetails();

        assertNotNull(result);
        assertEquals(2, result.size());

        User savedUser1 = result.get(0);
        User savedUser2 = result.get(1);

        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("testUser@email.com")));
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("admin@email.com")));

        assertEquals("USER", savedUser1.getRole().name());
        assertEquals("ADMIN", savedUser2.getRole().name());

        assertEquals("CONFIRMED", savedUser1.getStatus().name());
        assertEquals("NOT_CONFIRMED", savedUser2.getStatus().name());
    }

    @Test
    void testGetAllUsersFullDetailsWhenUsersNotExists() {
        List<User> result = userService.getAllUsersFullDetails();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllUsersFullDetailsWithCorrectData() {
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

        List<User> result = userService.getAllUsersFullDetails();

        User userFromResult = result.get(0);
        assertEquals("newTestUser", userFromResult.getUsername());
        assertEquals("testUser@email.com", userFromResult.getEmail());
        assertEquals("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2", userFromResult.getHashPassword());
        assertEquals(User.Role.USER, userFromResult.getRole());
        assertEquals(User.Status.CONFIRMED, userFromResult.getStatus());
    }

}