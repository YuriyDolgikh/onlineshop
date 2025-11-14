package org.onlineshop.service.interfaces;

import org.onlineshop.dto.user.UserRequestDto;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.dto.user.UserUpdateRequestDto;
import org.onlineshop.entity.User;

import java.util.List;

public interface UserServiceInterface {

    UserResponseDto registration(UserRequestDto request);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Integer id);

    String confirmationEmail(String code);

    UserResponseDto updateUser(Integer userId, UserUpdateRequestDto updateRequest);

    UserResponseDto deleteUser(Integer userId);

    UserResponseDto renewUser(String email);

    User getCurrentUser();

    User saveUser(User user);
}
