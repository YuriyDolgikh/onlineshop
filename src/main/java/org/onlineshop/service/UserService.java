package org.onlineshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.user.UserRequestDto;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.entity.User;
import org.onlineshop.exception.AlreadyExistException;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.converter.UserConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface{

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final ConfirmationCodeService confirmationCodeService;

    @Override
    @Transactional
    public UserResponseDto registration(UserRequestDto request) {
        // Check duplicate for email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistException("User with email: " + request.getEmail() + " is already exist");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Name must be provided");
        }

        if (request.getHashPassword() == null || request.getHashPassword().isBlank()) {
            throw new BadRequestException("Password must be provided");
        }

        // When email is unique - create a new user
        User newUser = userConverter.fromDto(request);
        newUser.setRole(User.Role.USER); // by default - role is USER
        newUser.setStatus(User.Status.NOT_CONFIRMED); // by default - status is NOT_CONFIRMED
        newUser.setOrders(new ArrayList<>());
        newUser.setFavourites(new HashSet<>());

        userRepository.save(newUser);
        // After creating a new user, we need to create a new confirmation code for him and send it to him by email
        confirmationCodeService.confirmationCodeManager(newUser);
        return userConverter.toDto(newUser);
    }

    public List<UserResponseDto> getAllUsers() {
        return userConverter.fromUsers(userRepository.findAll());
    }

    public UserResponseDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id = " + id + " not found"));

        return userConverter.toDto(user);
    }

    public User getUserByIdForAdmin(Integer id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new NotFoundException("User with id = " + id + " not found"));
    }

    public List<User> getAllUsersFullDetails() {
        return userRepository.findAll();
    }

    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " not found"));

        return userConverter.toDto(user);
    }
}
