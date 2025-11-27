package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.entity.User;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserServiceGetAllUsersTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @AfterEach
    void dropDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void testGetAllUsersWhenUsersExist() {
        User newTestUserSecond = User.builder()
                .username("newUserOne")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        userRepository.save(newTestUserSecond);

        User newTestUserFirst = User.builder()
                .username("newUserTwo")
                .email("testUserFirst@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+493131313131")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        userRepository.save(newTestUserFirst);

        Page<UserResponseDto> result = userService.getAllUsers(PageRequest.of(0, 2));

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("testUser@email.com")));
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("testUserFirst@email.com")));
    }

    @Test
    void testGetAllUsersWhenNoUsers() {
        Page<UserResponseDto> result = userService.getAllUsers(PageRequest.of(0, 2));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllUsersWithCorrectData() {
        User newTestUserFirst = User.builder()
                .username("newUserTwo")
                .email("testUserFirst@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+493131313131")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        userRepository.save(newTestUserFirst);

        Page<UserResponseDto> result = userService.getAllUsers(PageRequest.of(0, 2));

        UserResponseDto dto = result.getContent().get(0);
        assertEquals("newUserTwo", dto.getUsername());
        assertEquals("testUserFirst@email.com", dto.getEmail());
        assertEquals(User.Role.USER.name(), dto.getRole());
    }
}