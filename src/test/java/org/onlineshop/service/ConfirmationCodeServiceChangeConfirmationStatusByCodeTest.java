package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ConfirmationCodeServiceChangeConfirmationStatusByCodeTest {

    @Mock
    private ConfirmationCodeRepository confirmationCodeRepository;

    @InjectMocks
    private ConfirmationCodeService confirmationCodeService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        confirmationCodeRepository.deleteAll();

    }

    @Test
    void testChangeConfirmationStatusByCodeFullProcessOk() {
        String code = "valid-code";

        User newTestUser = User.builder()
                .userId(10)
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .role(User.Role.USER)
                .status(User.Status.NOT_CONFIRMED)
                .cart(null)
                .orders(new ArrayList<>())
                .favourites(new HashSet<>())
                .build();

        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code(code)
                .user(newTestUser)
                .expireDataTime(LocalDateTime.now().plusDays(1))
                .build();

        when(confirmationCodeRepository.findByCode(code)).thenReturn(Optional.of(confirmationCode));

        User result = confirmationCodeService.changeConfirmationStatusByCode(code);

        assertEquals(newTestUser, result);
        assertTrue(confirmationCode.isConfirmed());
        verify(confirmationCodeRepository).save(confirmationCode);
    }

    @Test
    void testChangeConfirmationStatusByCodeWhenCodeNotFound() {
        String invalidCode = "invalid-code";
        when(confirmationCodeRepository.findByCode(invalidCode)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> confirmationCodeService.changeConfirmationStatusByCode(invalidCode));

        assertTrue(exception.getMessage().contains(invalidCode));
    }

    @Test
    void testChangeConfirmationStatusByCodeReturnCorrectUser() {
        String code = "test-code";
        User newTestUser = User.builder()
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .role(User.Role.USER)
                .status(User.Status.NOT_CONFIRMED)
                .build();

        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code(code)
                .user(newTestUser)
                .expireDataTime(LocalDateTime.now().plusDays(1))
                .build();

        when(confirmationCodeRepository.findByCode(code)).thenReturn(Optional.of(confirmationCode));

        User result = confirmationCodeService.changeConfirmationStatusByCode(code);

        assertEquals(newTestUser, result);
        assertEquals(newTestUser.getUserId(), result.getUserId());
        assertEquals(newTestUser.getEmail(), result.getEmail());
    }

    @Test
    void testChangeConfirmationStatusByCodeWhenUserDeleted() {
        String code = "deleted-user-code";

        User deletedUser = User.builder()
                .userId(99)
                .username("deletedUser")
                .email("deleted@email.com")
                .hashPassword("hash")
                .phoneNumber("+499999999999")
                .role(User.Role.USER)
                .status(User.Status.DELETED)
                .build();

        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code(code)
                .user(deletedUser)
                .expireDataTime(LocalDateTime.now().plusDays(1))
                .build();

        when(confirmationCodeRepository.findByCode(code)).thenReturn(Optional.of(confirmationCode));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> confirmationCodeService.changeConfirmationStatusByCode(code)
        );

        assertTrue(exception.getMessage().contains("User has been deleted"));
    }
}