package org.onlineshop.service;

import org.onlineshop.dto.user.UserRequestDto;
import org.onlineshop.dto.user.UserResponseDto;

public interface UserServiceInterface {

    UserResponseDto registration(UserRequestDto request);

}
