package org.onlineshop.service;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserServiceRenewUserTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private ConfirmationCodeService confirmationCodeService;

    @AfterEach
    void dropDatabase() {
        confirmationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String userEmailDeleted;
    private String userConfirmedEmail;

    @BeforeEach
    void setUp() {
        User newTestUser = User.builder()
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .status(User.Status.DELETED)
                .role(User.Role.USER)
                .build();

        userRepository.save(newTestUser);

        userEmailDeleted = newTestUser.getEmail();

        User newUser = User.builder()
                .username("newUser")
                .email("User@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494945454545")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .cart(null)
                .orders(new ArrayList<>())
                .favourites(new HashSet<>())
                .build();

        User savedUser = userRepository.save(newUser);

        userConfirmedEmail = newUser.getEmail();

        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code("someConfirmationCode")
                .user(savedUser)
                .expireDataTime(LocalDateTime.now().plusDays(1))
                .build();

        confirmationCodeRepository.save(confirmationCode);

        doNothing().when(confirmationCodeService).confirmationCodeManager(any(User.class));
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "USER")
    void testRenewUserIfOk() {
        UserResponseDto renewUser = userService.renewUser(userEmailDeleted);

        assertEquals("NOT_CONFIRMED", renewUser.getStatus());
    }

    @Test
    @WithMockUser(username = "User@email.com", roles = "USER")
    void testRenewUserIfUserNotDeleted() {
        assertThrows(BadRequestException.class, () -> userService.renewUser(userConfirmedEmail));
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "USER")
    void testRenewUserIfUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.renewUser("notFound.company.com"));
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "USER")
    void testRenewUserIfUserNull() {
        assertThrows(BadRequestException.class, () -> userService.renewUser(null));
    }
}