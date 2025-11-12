package org.onlineshop.controller;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.user.UserRequestDto;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.entity.User;
import org.onlineshop.exception.AlreadyExistException;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserControllerRegisterTest {

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
        User newTestUser = User.builder()
                .username("User")
                .email("User@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        userRepository.save(newTestUser);
    }

    @Test
    void testRegisterIfOk() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("testUser@email.com")
                .phoneNumber("+49785632147")
                .name("TestUser")
                .hashPassword("1234")
                .build();

        ResponseEntity<UserResponseDto> responseDto = userController.register(userRequestDto);

        assertNotNull(responseDto);
        assertEquals(responseDto.getBody().getUsername(), userRequestDto.getName());
    }

    @Test
    void testRegisterIfEmailBlank() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email(" ")
                .phoneNumber("+49785632147")
                .name("TestUser")
                .hashPassword("1234")
                .build();

        assertThrows(ConstraintViolationException.class, () -> userController.register(userRequestDto));
    }

    @Test
    void testRegisterIfNameBlank() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("testUser@email.com")
                .phoneNumber("+49785632147")
                .name(" ")
                .hashPassword("1234")
                .build();

        assertThrows(BadRequestException.class, () -> userController.register(userRequestDto));
    }

    @Test
    void testRegisterIfHashPasswordBlank() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("testUser@email.com")
                .phoneNumber("+49785632147")
                .name("TestUser")
                .hashPassword(" ")
                .build();

        assertThrows(BadRequestException.class, () -> userController.register(userRequestDto));
    }

    @Test
    void testRegisterIfPhoneBlank() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("testUser@email.com")
                .phoneNumber(" ")
                .name("TestUser")
                .hashPassword("1234")
                .build();

        assertThrows(ConstraintViolationException.class, () -> userController.register(userRequestDto));
    }

    @Test
    void testRegisterIfEmailNull() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email(null)
                .phoneNumber("+49785632147")
                .name("TestUser")
                .hashPassword("1234")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> userController.register(userRequestDto));
    }

    @Test
    void testRegisterIfNameNull() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("testUser@email.com")
                .phoneNumber("+49785632147")
                .name(null)
                .hashPassword("1234")
                .build();

        assertThrows(BadRequestException.class, () -> userController.register(userRequestDto));
    }

    @Test
    void testRegisterIfHashPasswordNull() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("testUser@email.com")
                .phoneNumber("+49785632147")
                .name("TestUser")
                .hashPassword(null)
                .build();

        assertThrows(BadRequestException.class, () -> userController.register(userRequestDto));
    }

    @Test
    void testRegisterIfPhoneNull() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("testUser@email.com")
                .phoneNumber(null)
                .name("TestUser")
                .hashPassword("1234")
                .build();

        assertThrows(ConstraintViolationException.class, () -> userController.register(userRequestDto));
    }


    @Test
    void testRegisterIfPhoneAlreadyExists() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("testUser@email.com")
                .phoneNumber("+494949494949")
                .name("TestUser")
                .hashPassword("1234")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> userController.register(userRequestDto));
    }

    @Test
    void testRegisterIfEmailAlreadyExist() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("User@email.com")
                .phoneNumber("+4985252546")
                .name("TestUser")
                .hashPassword("1234")
                .build();

        assertThrows(AlreadyExistException.class, () -> userController.register(userRequestDto));
    }
}