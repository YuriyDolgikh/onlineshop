package org.onlineshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.user.UserRequestDto;
import org.onlineshop.entity.User;
import org.onlineshop.exception.AlreadyExistException;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.converter.UserConverter;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceRegistrationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ConfirmationCodeService confirmationCodeService;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegistrationIfEmailIsNull() {
        UserRequestDto dto = new UserRequestDto();
        dto.setHashPassword("pass");

        assertThrows(BadRequestException.class, () -> userService.registration(dto));
    }

    @Test
    void testRegistrationIfPasswordIsNull() {
        UserRequestDto dto = new UserRequestDto();
        dto.setEmail("test@test.com");

        assertThrows(BadRequestException.class, () -> userService.registration(dto));
    }

    @Test
    void testRegistrationIfUserAlreadyExists() {
        UserRequestDto dto = new UserRequestDto();
        dto.setEmail("test@test.com");
        dto.setHashPassword("pass");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> userService.registration(dto));
    }

    @Test
    void testRegistrationSuccess() {
        UserRequestDto dto = new UserRequestDto();
        dto.setEmail("test@test.com");
        dto.setHashPassword("pass");
        dto.setName("Test");

        User user = new User();
        user.setEmail("test@test.com");
        user.setUsername("test");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(userConverter.fromDto(dto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.registration(dto);

        verify(userRepository).save(any(User.class));
        verify(confirmationCodeService).confirmationCodeManager(user);
    }

}
