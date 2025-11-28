package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.security.service.AuthService;
import org.onlineshop.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerGetAllUsersTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsersWhenOk() {
        int page = 0;
        int size = 20;

        UserResponseDto user1 = UserResponseDto.builder()
                .id(1)
                .username("user1")
                .email("user1@example.com")
                .role("USER")
                .status("CONFIRMED")
                .build();

        UserResponseDto user2 = UserResponseDto.builder()
                .id(2)
                .username("user2")
                .email("user2@example.com")
                .role("MANAGER")
                .status("CONFIRMED")
                .build();

        List<UserResponseDto> users = Arrays.asList(user1, user2);
        Page<UserResponseDto> userPage = new PageImpl<>(users, PageRequest.of(page, size), users.size());

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        ResponseEntity<Page<UserResponseDto>> response = userController.getAllUsers(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        assertEquals("user1", response.getBody().getContent().get(0).getUsername());
        assertEquals("user2", response.getBody().getContent().get(1).getUsername());

        verify(userService).getAllUsers(any(Pageable.class));
        verifyNoMoreInteractions(userService, authService);
    }

    @Test
    void getAllUsersWhenNoUsers() {
        int page = 0;
        int size = 20;

        Page<UserResponseDto> emptyPage = Page.empty();
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(emptyPage);

        ResponseEntity<Page<UserResponseDto>> response = userController.getAllUsers(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        assertEquals(0, response.getBody().getContent().size());

        verify(userService).getAllUsers(any(Pageable.class));
    }

    @Test
    void getAllUsersWhenMultiplePages() {
        int page = 1;
        int size = 5;

        List<UserResponseDto> users = Arrays.asList(
                UserResponseDto.builder().id(6).username("user6").email("user6@example.com").build(),
                UserResponseDto.builder().id(7).username("user7").email("user7@example.com").build()
        );

        Page<UserResponseDto> userPage = new PageImpl<>(users, PageRequest.of(page, size), 12L);
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        ResponseEntity<Page<UserResponseDto>> response = userController.getAllUsers(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        assertEquals(12L, response.getBody().getTotalElements());
        assertEquals(3, response.getBody().getTotalPages());

        verify(userService).getAllUsers(argThat(pageable ->
                pageable.getPageNumber() == 1 && pageable.getPageSize() == 5
        ));
    }
}