package org.onlineshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.user.UserRequestDto;
import org.onlineshop.dto.user.UserResponseDto;
import org.onlineshop.dto.user.UserUpdateRequestDto;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.User;
import org.onlineshop.exception.AlreadyExistException;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.converter.UserConverter;
import org.onlineshop.service.interfaces.UserServiceInterface;
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

    /**
     * Registers a new user in the system. Validates input data and handles duplicate email checks.
     * After registration, sets default roles, status, and initializes collections for the user.
     * Additionally, generates and sends a confirmation code to the user's email.
     *
     * @param request the UserRequestDto object containing user registration data
     *                such as name, email, and hashed password
     * @return a UserResponseDto object containing the details of the newly registered user
     * @throws AlreadyExistException if a user with the provided email already exists
     * @throws BadRequestException if the name or hashed password is missing or blank
     */
    @Override
    @Transactional
    public UserResponseDto registration(UserRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistException("User with email: " + request.getEmail() + " is already exist");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Name must be provided");
        }
        if (request.getHashPassword() == null || request.getHashPassword().isBlank()) {
            throw new BadRequestException("Password must be provided");
        }
        User newUser = userConverter.fromDto(request);
        newUser.setRole(User.Role.USER);
        newUser.setStatus(User.Status.NOT_CONFIRMED);
        newUser.setOrders(new ArrayList<>());
        newUser.setFavourites(new HashSet<>());

        Cart newCartForUser = new Cart();
        newCartForUser.setUser(newUser);
        newUser.setCart(newCartForUser);

        User finalUser = userRepository.save(newUser);

        confirmationCodeService.confirmationCodeManager(finalUser);
        return userConverter.toDto(finalUser);
    }

    /**
     * Retrieves a list of all users in the system.
     *
     * @return a list of UserResponseDto objects containing the details of all users in the system
     */
    @Override
    public List<UserResponseDto> getAllUsers() {
        return userConverter.toDtos(userRepository.findAll());
    }

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id the ID of the user to be retrieved - must not be null
     * @return a UserResponseDto object containing the details of the retrieved user
     * @throws NotFoundException if the user with the specified ID is not found
     */
    @Override
    public UserResponseDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id = " + id + " not found"));
        return userConverter.toDto(user);
    }

    /**
     * Confirms the email associated with a given confirmation code.
     * Verifies the provided confirmation code, checks if it has expired, and updates the user's status accordingly.
     * If the code is expired, a new code is generated and sent to the user.
     *
     * @param code the confirmation code associated with the user's email
     * @return a message indicating success, failure, or additional actions required for email confirmation
     */
    @Override
    @Transactional
    public String confirmationEmail(String code) {
        if (code == null || code.isBlank()) {
            return "Code is null or blank";
        }
        User user = confirmationCodeService.getConfirmationCodeByCode(code).getUser();
        if (confirmationCodeService.isConfirmationCodeExpired(code)){
            confirmationCodeService.deleteConfirmationCodeByUser(user);
            confirmationCodeService.confirmationCodeManager(user);
            return "Confirmation code for email: " + user.getEmail() + " is expired. " +
                    "Please, check your email again for the new one.";
        }
        confirmationCodeService.changeConfirmationStatusByCode(code);
        user.setStatus(User.Status.CONFIRMED);
        userRepository.save(user);
        return "Email " + user.getEmail() + " is successfully confirmed";
    }

    /**
     * Updates the details of an existing user based on the provided update request. The update
     * is restricted to the currently authenticated user, ensuring users can only modify their own data.
     *
     * @param userId the ID of the user to be updated
     * @param updateRequest an object containing the user details to be updated; only the fields that
     *                      are not null or blank will be updated
     * @return a UserResponseDto object containing the updated user details
     * @throws NotFoundException if the user with the specified ID does not exist
     * @throws BadRequestException if the authenticated user attempts to update another user's details
     */
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
        userRepository.save(userToUpdate);
        return userConverter.toDto(userToUpdate);
    }

    /**
     * Deletes a user based on the provided user ID. If the user is not found,
     * an exception is thrown. Validation is performed to ensure the current
     * user's permissions allow for the deletion.
     *
     * @param userId the ID of the user to be deleted
     * @return a UserResponseDto containing information about the deleted user
     * @throws NotFoundException if the user with the provided ID is not found
     * @throws BadRequestException if the current user does not have permission
     *         to delete the user or if the user to be deleted is an ADMIN
     */
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

    /**
     * Renews a previously deleted user by reactivating their account.
     * This method validates the provided email address, ensures the user exists,
     * and checks that the user's status is marked as DELETED before performing the renewal process.
     *
     * @param email the email address of the user to be renewed; must not be null or blank
     * @return a {@link UserResponseDto} containing the updated user information
     * @throws BadRequestException if the email is null, blank, or if the user's status is not DELETED
     * @throws NotFoundException if no user with the provided email exists
     */
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

    /**
     * Retrieves a user by their ID for admin purposes.
     *
     * @param id - user id to retrieve
     * @return - user object
     */
    public User getUserByIdForAdmin(Integer id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new NotFoundException("User with id = " + id + " not found"));
    }

    /**
     * Retrieves a list of all users with their full details.
     *
     * @return a list containing all User objects from the data repository.
     */
    public List<User> getAllUsersFullDetails() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user based on the provided email address.
     *
     * @param email the email address of the user to be retrieved
     * @return a UserResponseDto object containing the details of the retrieved user
     * @throws NotFoundException if no user with the specified email is found
     */
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " not found"));

        return userConverter.toDto(user);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to be retrieved
     * @return the User object associated with the specified email
     * @throws NotFoundException if no user is found with the specified email
     */
    public User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " not found"));
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * This method fetches the user associated with the currently authenticated email address.
     *
     * @return the currently authenticated User object
     */
    public User getCurrentUser() {
        return getUserByEmailOrThrow(getCurrentUserEmail());
    }

    /**
     * Retrieves the email address of the currently authenticated user from the security context.
     *
     * @return the email address of the current user as a String, or null if no user is authenticated.
     */
    public String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Saves the given user to the repository.
     *
     * @param user the user entity to be saved
     * @return the saved user entity
     */
    public User saveUser(User user) {
        userRepository.save(user);
        return user;
    }
}
