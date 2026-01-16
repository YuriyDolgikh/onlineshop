package org.onlineshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.converter.UserConverter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceRenewUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @Mock
    private ConfirmationCodeService confirmationCodeService;

    @InjectMocks
    private UserService userService;

    @Test
    void testRenewUserSuccess() {
        User user = new User();
        user.setEmail("test@company.com");
        user.setStatus(User.Status.DELETED);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        userService.renewUser(user.getEmail());

        assertEquals(User.Status.NOT_CONFIRMED, user.getStatus());
    }

    @Test
    void testRenewUserIfEmailNotConfirmed() {
        User user = new User();
        user.setEmail("test@company.com");
        user.setStatus(User.Status.NOT_CONFIRMED);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> userService.renewUser(user.getEmail()));
    }

    @Test
    void testRenewUserIfUserNotFound() {
        when(userRepository.findByEmail("missing@company.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.renewUser("missing@company.com"));
    }

    @Test
    void testRenewUserIfUserNull() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.renewUser(null));
    }
}
