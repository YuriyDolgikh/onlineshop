package org.onlineshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.user.UserRequestDto;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.dto.user.UserUpdateRequestDto;
import org.onlineshop.entity.User;
import org.onlineshop.exception.AlreadyExistException;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.converter.UserConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final ConfirmationCodeService confirmationCodeService;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userConverter.fromUsers(userRepository.findAll());
    }

    @Override
    public UserResponseDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id = " + id + " not found"));
        return userConverter.toDto(user);
    }

    @Override
    public String confirmationEmail(String code) {
        User user = confirmationCodeService.changeConfirmationStatusByCode(code);
        user.setStatus(User.Status.CONFIRMED);
        userRepository.save(user);
        return "Email " + user.getEmail() + " is successfully confirmed";
    }

    @Override
    public UserResponseDto updateUser(Integer userId, UserUpdateRequestDto updateRequest) {

        Optional<User> userToUpdateOptional = userRepository.findById(userId);
        if (userToUpdateOptional.isEmpty()) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        User userToUpdate = userToUpdateOptional.get();
        // Check that the user for update is the same as the current user
        User currentUser = getCurrentUser();
        if (!currentUser.getUserId().equals(userId)) {
            throw new BadRequestException("You can't update another user");
        }
        // Update all presented fields.
        // Is not known in advance which fields the user wants to change,
        // so in the JSON (in the request body) there will be only those fields (that are not empty),
        // which the user wants to change (not obligatory all)
        if (updateRequest.getUsername() != null && !updateRequest.getUsername().isBlank()) {
            userToUpdate.setUsername(updateRequest.getUsername());
        }
        if (updateRequest.getPhoneNumber() != null && !updateRequest.getPhoneNumber().isBlank()) {
            userToUpdate.setPhoneNumber(updateRequest.getPhoneNumber());
        }
        if (updateRequest.getHashPassword() != null && !updateRequest.getHashPassword().isBlank()) {
            userToUpdate.setHashPassword(passwordEncoder.encode(updateRequest.getHashPassword()));
        }
        // Save the updated user
        userRepository.save(currentUser);
        return userConverter.toDto(currentUser);
    }

    @Transactional
    @Override
    public UserResponseDto deleteUser(Integer userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        User currentUser = getCurrentUser();
        if (userToDelete.getRole().equals(User.Role.ADMIN)) {
            throw new BadRequestException("User with role ADMIN can't be deleted");
        }
        if (!currentUser.getUserId().equals(userId) && !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new BadRequestException("You can't delete another user");
        }
        confirmationCodeService.deleteConfirmationCodeByUser(userToDelete);
        userToDelete.setStatus(User.Status.DELETED);
        userRepository.save(userToDelete);
        return userConverter.toDto(userToDelete);
    }

    @Override
    public UserResponseDto renewUser(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email must be provided to renew user");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " not found"));
        if (!user.getStatus().equals(User.Status.DELETED)) {
            throw new BadRequestException("You can't renew a user that is not deleted");
        }
        confirmationCodeService.confirmationCodeManager(user);
        user.setStatus(User.Status.NOT_CONFIRMED);
        userRepository.save(user);
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

    public User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " not found"));
    }

    public User getCurrentUser() {
        return getUserByEmailOrThrow(getCurrentUserEmail());
    }

    public String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
