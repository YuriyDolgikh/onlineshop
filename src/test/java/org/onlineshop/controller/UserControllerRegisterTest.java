package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.user.UserRequestDto;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.exception.AlreadyExistException;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import jakarta.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerRegisterTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testRegisterIfOk() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("testUser@email.com")
                .phoneNumber("+49785632147")
                .name("TestUser")
                .hashPassword("1234")
                .build();

        UserResponseDto responseDto = UserResponseDto.builder()
                .username("TestUser")
                .email("testUser@email.com")
                .build();

        when(userService.registration(any(UserRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<UserResponseDto> response = userController.register(userRequestDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(userRequestDto.getName(), response.getBody().getUsername());
        assertEquals("testUser@email.com", response.getBody().getEmail());
    }

    @Test
    void testRegisterIfEmailBlank() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email(" ")
                .phoneNumber("+49785632147")
                .name("TestUser")
                .hashPassword("1234")
                .build();

        when(userService.registration(any(UserRequestDto.class)))
                .thenThrow(new ConstraintViolationException("Email cannot be blank", null));

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

        when(userService.registration(any(UserRequestDto.class)))
                .thenThrow(new BadRequestException("Name cannot be blank"));

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

        when(userService.registration(any(UserRequestDto.class)))
                .thenThrow(new BadRequestException("Password cannot be blank"));

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

        when(userService.registration(any(UserRequestDto.class)))
                .thenThrow(new ConstraintViolationException("Phone number cannot be blank", null));

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

        when(userService.registration(any(UserRequestDto.class)))
                .thenThrow(new DataIntegrityViolationException("Email cannot be null"));

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

        when(userService.registration(any(UserRequestDto.class)))
                .thenThrow(new BadRequestException("Name cannot be null"));

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

        when(userService.registration(any(UserRequestDto.class)))
                .thenThrow(new BadRequestException("Password cannot be null"));

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

        when(userService.registration(any(UserRequestDto.class)))
                .thenThrow(new ConstraintViolationException("Phone number cannot be null", null));

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

        when(userService.registration(any(UserRequestDto.class)))
                .thenThrow(new DataIntegrityViolationException("Phone number already exists"));

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

        when(userService.registration(any(UserRequestDto.class)))
                .thenThrow(new AlreadyExistException("Email already exists"));

        assertThrows(AlreadyExistException.class, () -> userController.register(userRequestDto));
    }
}