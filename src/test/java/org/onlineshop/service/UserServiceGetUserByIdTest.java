package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.entity.User;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserServiceGetUserByIdTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @AfterEach
    void dropDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void testGetUserByIdWhenUserExists() {
        User newTestUserFirst = User.builder()
                .username("newUserTwo")
                .email("testUserFirst@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+493131313131")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        userRepository.save(newTestUserFirst);

        UserResponseDto result = userService.getUserById(newTestUserFirst.getUserId());

        assertNotNull(result);
        assertEquals(newTestUserFirst.getUserId(), result.getId());
        assertEquals("newUserTwo", result.getUsername());
        assertEquals("testUserFirst@email.com", result.getEmail());
        assertEquals(User.Role.USER.name(), result.getRole());
    }

    @Test
    void testGetUserByIdWhenUserNotExists() {

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(100000));

        assertEquals("User with id = " + "100000" + " not found", exception.getMessage());
    }

    @Test
    void testGetUserByIdWhenIdIsNull() {

        assertThrows(InvalidDataAccessApiUsageException.class, () -> userService.getUserById(null));
    }

    @Test
    void testGetUserByIdWithDifferentUserTypes() {
        User admin = User.builder()
                .username("newAdmin")
                .email("testUserFirst@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+493131313131")
                .status(User.Status.CONFIRMED)
                .role(User.Role.ADMIN)
                .build();

        User user = User.builder()
                .username("newUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+492323232323")
                .status(User.Status.NOT_CONFIRMED)
                .role(User.Role.USER)
                .build();

        User savedAdmin = userRepository.save(admin);
        User savedNotConfirmed = userRepository.save(user);

        UserResponseDto adminResult = userService.getUserById(savedAdmin.getUserId());
        assertEquals(User.Role.ADMIN.name(), adminResult.getRole());
        assertEquals("newAdmin", adminResult.getUsername());
        assertEquals("testUserFirst@email.com", adminResult.getEmail());

        UserResponseDto notConfirmedResult = userService.getUserById(savedNotConfirmed.getUserId());
        assertEquals(User.Role.USER.name(), notConfirmedResult.getRole());
        assertEquals("newUser", notConfirmedResult.getUsername());
        assertEquals("testUser@email.com", notConfirmedResult.getEmail());
    }

}