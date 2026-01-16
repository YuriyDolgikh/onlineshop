package org.onlineshop.service;

import lombok.Data;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.service.interfaces.ConfirmationCodeServiceInterface;
import org.onlineshop.service.mail.MailUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Data
@RequiredArgsConstructor
@Service
public class ConfirmationCodeService implements ConfirmationCodeServiceInterface {

    private final ConfirmationCodeRepository repository;
    private final MailUtil mailUtil;

    @Value("${confirmation.expiration-period}")
    private int EXPIRATION_PERIOD; // in days

    @Value("${confirmation.link-path}")
    private String LINK_PATH;

    /**
     * Main service to generate and send a confirmation code for the given user.
     * <p>
     * This method performs three actions:
     * <ol>
     *   <li>Generates a unique confirmation code</li>
     *   <li>Saves the code in the database</li>
     *   <li>Sends the confirmation link to the user's email</li>
     * </ol>
     *
     * @param user the user for whom the confirmation code will be generated and sent
     */
    @Override
    @Transactional
    public void confirmationCodeManager(User user) {
        String code = generateCode();
        log.info("Generated confirmation code: {}", code);
        saveConfirmationCode(code, user);
        log.info("Confirmation code saved for user: {}", user.getUsername());
        sendCodeByEmail(code, user);
        log.info("Confirmation email sent to user: {}", user.getUsername());
    }

    /**
     * Sends a confirmation code to the user's email.
     * The method constructs a confirmation link by combining the base link path with the provided code
     * and uses the mail utility service to send the confirmation email to the specified user.
     *
     * @param code the confirmation code to be included in the email
     * @param user the user to whom the confirmation email will be sent
     */
    @Override
    public void sendCodeByEmail(String code, User user) {
        String linkToSend = LINK_PATH + code;
        mailUtil.sendConfirmationEmail(user, linkToSend);
    }

    /**
     * Saves a generated confirmation code for the specified user.
     * <p>
     * This method creates a new {@code ConfirmationCode} entity with the given code,
     * associates it with the user provided, sets an expiration date for the code,
     * and marks it as unconfirmed. The new confirmation code is then saved
     * in the database.
     *
     * @param generatedCode the confirmation code that needs to be saved
     * @param user          the user to whom the confirmation code belongs
     */
    @Override
    public void saveConfirmationCode(String generatedCode, User user) {
        ConfirmationCode newCode = ConfirmationCode.builder()
                .code(generatedCode)
                .user(user)
                .expireDataTime(LocalDateTime.now().plusDays(EXPIRATION_PERIOD))
                .isConfirmed(false)
                .build();
        repository.save(newCode);
    }

    /**
     * Generate a random code for confirmation
     *
     * @return String variable with random code
     * @UUID - universal uniq identifier
     * @format - 128 bit
     * @template - xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx ('x' - is a character or a number)
     * @example - 3f29c3b2-9fc2-11ed-a8fc-0242ac120002
     */
    @Override
    public String generateCode() {
        return UUID.randomUUID().toString();
    }

    /**
     * Changes the confirmation status of a user by validating a provided confirmation code.
     * <p>
     * The method retrieves the confirmation code from the repository and checks if it exists,
     * has not expired, and has not already been confirmed. If the confirmation code is valid,
     * it updates its status to confirmed and logs the action. The associated user is returned.
     *
     * @param code the confirmation code used to verify and update the user's status
     * @return the user associated with the provided confirmation code, with the updated status
     * @throws NotFoundException   if the confirmation code cannot be found
     * @throws BadRequestException if the confirmation code is expired, already confirmed,
     *                             or the user associated with it has been deleted
     */
    @Transactional
    @Override
    public User changeConfirmationStatusByCode(String code) {
        ConfirmationCode confirmationCode = repository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Confirmation code: " + code + " not found"));
        if (confirmationCode.getExpireDataTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Confirmation code expired");
        }
        if (confirmationCode.isConfirmed()) {
            throw new BadRequestException("Confirmation code already confirmed");
        }
        User user = confirmationCode.getUser();
        if (user.getStatus().equals(User.Status.DELETED)) {
            throw new BadRequestException("User has been deleted");
        }
        confirmationCode.setConfirmed(true);
        repository.save(confirmationCode);
        log.info("Confirmation code {} updated for user {}. Set to confirmed.", code, user.getUsername());
        return user;
    }

    /**
     * Retrieves the confirmation code associated with the specified user.
     * <p>
     * This method looks up the confirmation code for the given user in the repository. If the user
     * does not exist or has no associated confirmation code, an exception is thrown.
     *
     * @param user the user for whom the confirmation code is being retrieved
     * @return the confirmation code associated with the specified user
     * @throws IllegalArgumentException if the provided user is null
     * @throws NotFoundException        if no confirmation code is found for the specified user
     */
    @Override
    @Transactional(readOnly = true)
    public ConfirmationCode findCodeByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must be provided");
        }
        return repository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Confirmation code for user: " + user.getUsername() + " not found"));
    }

    /**
     * Deletes the confirmation code associated with the specified user.
     * If the user does not exist or has no associated confirmation code, an exception is thrown.
     *
     * @param user the user for whom the confirmation code is being deleted from the repository.
     * @throws IllegalArgumentException if the provided user is null
     * @throws NotFoundException        if no confirmation code is found for the specified user
     */
    @Override
    @Transactional
    public void deleteConfirmationCodeByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must be provided");
        }
        ConfirmationCode confirmationCode = repository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Confirmation code for user: " + user.getUsername() + " not found"));
        repository.delete(confirmationCode);
        log.info("Confirmation code for user {} deleted.", user.getUsername());
    }

    /**
     * Checks if the confirmation code has expired.
     * <p>
     * This method verifies whether the confirmation code associated with the
     * provided code string has an expiration time that has already passed.
     * If the confirmation code does not exist, a {@code NotFoundException}
     * is thrown.
     *
     * @param code the confirmation code to be checked for expiration
     * @return {@code true} if the confirmation code is expired, otherwise {@code false}
     * @throws NotFoundException if the confirmation code is not found
     */
    @Generated
    @Transactional(readOnly = true)
    public boolean isConfirmationCodeExpired(String code) {
        Optional<ConfirmationCode> confirmationCodeOptional = repository.findByCode(code);
        if (confirmationCodeOptional.isEmpty()) {
            throw new NotFoundException("Confirmation code not found");
        }
        return confirmationCodeOptional.get().getExpireDataTime().isBefore(LocalDateTime.now());
    }

    /**
     * Retrieves a confirmation code entity by its associated code value.
     * Searches the repository for a matching confirmation code and throws
     * a {@link BadRequestException} if no match is found.
     *
     * @param code the unique code used to find the corresponding ConfirmationCode entity
     * @return the ConfirmationCode entity corresponding to the given code
     * @throws BadRequestException if the confirmation code is not found in the repository
     */
    @Generated
    @Transactional(readOnly = true)
    public ConfirmationCode getConfirmationCodeByCode(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Confirmation code not found"));
    }
}
