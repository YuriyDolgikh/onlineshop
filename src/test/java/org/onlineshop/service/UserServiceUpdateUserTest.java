package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.dto.user.UserUpdateRequestDto;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserServiceUpdateUserTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    private UserService userService;

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

        userRepository.save(newTestUser);

        userId = newTestUser.getUserId();
    }

    private Integer userId;

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "USER")
    void testUpdateUserUpdateUserName() {
        UserUpdateRequestDto updateRequest = UserUpdateRequestDto.builder()
                .username("testUser")
                .build();

        UserResponseDto response = userService.updateUser(userId, updateRequest);
        assertEquals("testUser", response.getUsername());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "USER")
    void testUpdateUserUpdateUserPhoneNumber() {
        UserUpdateRequestDto updateRequest = UserUpdateRequestDto.builder()
                .phoneNumber("+494774545654")
                .build();

        UserResponseDto response = userService.updateUser(userId, updateRequest);
        assertEquals("+494774545654", response.getPhoneNumber());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "USER")
    void testUpdateUserUpdatePassword() {
        UserUpdateRequestDto updateRequest = UserUpdateRequestDto.builder()
                .hashPassword("admin123")
                .build();

        UserResponseDto response = userService.updateUser(userId, updateRequest);
        assertEquals("testUser@email.com", response.getEmail());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "USER")
    void testUpdateUserWhenUserNotFound() {
        UserUpdateRequestDto updateRequest = UserUpdateRequestDto.builder()
                .username("newUserName")
                .build();

        assertThrows(NotFoundException.class, () -> userService.updateUser(100000, updateRequest));
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "USER")
    void testUpdateUserNameWhenAnotherUser() {
        User newUser = User.builder()
                .username("newUser")
                .email("newUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+4912451212")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        userRepository.save(newUser);

        UserUpdateRequestDto updateRequest = UserUpdateRequestDto.builder()
                .username("newUser")
                .build();

        Exception exception = assertThrows(BadRequestException.class, () -> userService.updateUser(newUser.getUserId(), updateRequest));
        assertEquals("You can't update another user", exception.getMessage());
    }
}