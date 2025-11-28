package org.onlineshop.service.interfaces;

import org.onlineshop.dto.user.UserRequestDto;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.dto.user.UserUpdateRequestDto;
import org.onlineshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserServiceInterface {

    UserResponseDto registration(UserRequestDto request);

    Page<UserResponseDto> getAllUsers(Pageable pageable);

    UserResponseDto getUserById(Integer id);

    String confirmationEmail(String code);

    UserResponseDto updateUser(Integer userId, UserUpdateRequestDto updateRequest);

    UserResponseDto deleteUser(Integer userId);

    UserResponseDto renewUser(String email);

    User getCurrentUser();

    User saveUser(User user);
}