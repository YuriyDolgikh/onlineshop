package org.onlineshop.service.converter;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.user.UserRequestDto;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserConverter {

    private final PasswordEncoder passwordEncoder;

    public User fromDto(UserRequestDto request){

        String encodedPassword = passwordEncoder.encode(request.getHashPassword());

        return User.builder()
                .username(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .hashPassword(encodedPassword)
                .build();
    }

    public UserResponseDto toDto(User user){
        return UserResponseDto.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
    }

    public List<UserResponseDto> fromUsers(List<User> users){
        return users.stream()
                .map(this::toDto)
                .toList();
    }
}
