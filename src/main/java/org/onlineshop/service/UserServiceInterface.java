package org.onlineshop.service;

import org.onlineshop.dto.user.UserRequestDto;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.dto.user.UserUpdateRequestDto;

import java.util.List;

public interface UserServiceInterface {

    UserResponseDto registration(UserRequestDto request);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Integer id);

    String confirmationEmail(String code);

    UserResponseDto updateUser(UserUpdateRequestDto updateRequest);

    boolean deleteUser(Integer userId);

    UserResponseDto renewUser(String email);

}
