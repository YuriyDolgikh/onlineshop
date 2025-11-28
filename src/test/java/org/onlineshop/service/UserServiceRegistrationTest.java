package org.onlineshop.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.user.UserRequestDto;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.onlineshop.exception.AlreadyExistException;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserServiceRegistrationTest {

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

    @BeforeEach
    void setUp() {
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

        doNothing().when(confirmationCodeService).confirmationCodeManager(any(User.class));
    }

    @Test
    void testRegisterUserIfOk() {
        UserRequestDto request = UserRequestDto.builder()
                .name("userOne")
                .email("testUserOk@email.com")
                .phoneNumber("+491213144578")
                .hashPassword("Pass111")
                .build();

        UserResponseDto responseDto = userService.registration(request);
        assertNotNull(responseDto);
        assertEquals(2, userRepository.findAll().size());
    }

    @Test
    void testWhenDuplicatedEmail() {
        UserRequestDto request = UserRequestDto.builder()
                .name("userOne")
                .email("testUser@email.com")
                .hashPassword("Pass111")
                .build();

        assertThrows(AlreadyExistException.class, () -> userService.registration(request));
    }

    @Test
    void testWhenEmailHasWrongFormat() {
        UserRequestDto request = UserRequestDto.builder()
                .name("userOne")
                .email("testUsercompany.com")
                .hashPassword("Pass111")
                .build();

        assertThrows(ConstraintViolationException.class, () -> userService.registration(request));
    }

    @Test
    void testWhenNameIsTooShort() {
        UserRequestDto request = UserRequestDto.builder()
                .name("u")
                .email("testUser1@company.com")
                .hashPassword("Pass111")
                .build();

        assertThrows(ConstraintViolationException.class, () -> userService.registration(request));
    }

    @Test
    void testWhenNameIsTooLong() {
        UserRequestDto request = UserRequestDto.builder()
                .name("TestUserTestUserTestUserTestUserTestUserTestUserTestUserTestUserTestUserTestUserTestUserTestUserTestUserTestUserTestUser")
                .email("testUser1@company.com")
                .hashPassword("Pass111")
                .build();

        assertThrows(ConstraintViolationException.class, () -> userService.registration(request));
    }

    @Test
    void testWhenEmailIsNull() {
        UserRequestDto request = UserRequestDto.builder()
                .name("userName")
                .email(null)
                .hashPassword("easrgf3223")
                .phoneNumber("+491213144578")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> userService.registration(request));
    }

    @Test
    void testWhenPhoneNumberIsNull() {
        UserRequestDto request = UserRequestDto.builder()
                .name("userName")
                .email("newEmail.google.com")
                .hashPassword("easrgf3223")
                .phoneNumber(null)
                .build();

        assertThrows(ConstraintViolationException.class, () -> userService.registration(request));
    }

    @Test
    void testWhenPhoneNumberIsBlank() {
        UserRequestDto request = UserRequestDto.builder()
                .name("userName")
                .email("newEmail.google.com")
                .hashPassword("easrgf3223")
                .phoneNumber(" ")
                .build();

        assertThrows(ConstraintViolationException.class, () -> userService.registration(request));
    }

    @Test
    void testWhenNameIsNull() {
        UserRequestDto request = UserRequestDto.builder()
                .name(null)
                .email("testUser1@company.com")
                .hashPassword("easrgf3223")
                .phoneNumber("+491213144578")
                .build();

        assertThrows(BadRequestException.class, () -> userService.registration(request));
    }

    @Test
    void testWhenPasswordIsNull() {
        UserRequestDto request = UserRequestDto.builder()
                .name("userName")
                .email("testUser1@company.com")
                .hashPassword(null)
                .phoneNumber("+491213144578")
                .build();

        assertThrows(BadRequestException.class, () -> userService.registration(request));
    }

    @Test
    void testWhenEmailIsBlank() {
        UserRequestDto request = UserRequestDto.builder()
                .name("userName")
                .email("  ")
                .hashPassword("easrgf3223")
                .phoneNumber("+491213144578")
                .build();

        assertThrows(ConstraintViolationException.class, () -> userService.registration(request));
    }

    @Test
    void testWhenNameIsBlank() {
        UserRequestDto request = UserRequestDto.builder()
                .name("  ")
                .email("testUser1@company.com")
                .hashPassword("easrgf3223")
                .phoneNumber("+491213144578")
                .build();

        assertThrows(BadRequestException.class, () -> userService.registration(request));
    }

    @Test
    void testWhenPasswordIsBlank() {
        UserRequestDto request = UserRequestDto.builder()
                .name("userName")
                .email("testUser1@company.com")
                .phoneNumber("+491213144578")
                .hashPassword("  ")
                .build();

        assertThrows(BadRequestException.class, () -> userService.registration(request));
    }


}