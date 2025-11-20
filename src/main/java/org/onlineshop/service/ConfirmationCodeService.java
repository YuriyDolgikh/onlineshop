package org.onlineshop.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.service.interfaces.ConfirmationCodeServiceInterface;
import org.onlineshop.service.mail.MailUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@Service
public class ConfirmationCodeService implements ConfirmationCodeServiceInterface {

    private final ConfirmationCodeRepository repository;
    private final MailUtil mailUtil;

    private final int EXPIRATION_PERIOD = 180; // in days
    private final String LINK_PATH = "http://localhost:8080/v1/users/confirmation?code=";

    /**
     * Main service to generate and send a confirmation code for the given user.
     *
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
    public void confirmationCodeManager(User user) {
        String code = generateCode();
        saveConfirmationCode(code, user);
        sendCodeByEmail(code, user);
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
        System.out.printf("Confirmation code: " + linkToSend);
    }

    /**
     * Saves a generated confirmation code for the specified user.
     *
     * This method creates a new {@code ConfirmationCode} entity with the given code,
     * associates it with the user provided, sets an expiration date for the code,
     * and marks it as unconfirmed. The new confirmation code is then saved
     * in the database.
     *
     * @param generatedCode the confirmation code that needs to be saved
     * @param user the user to whom the confirmation code belongs
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
     * Updates the confirmation status of a code and retrieves the associated user.
     *
     * This method finds a confirmation code in the repository using the provided code, marks it as confirmed,
     * saves the updated confirmation code, and then returns the user associated with the confirmation code.
     *
     * @param code the confirmation code that needs to have its status updated
     * @return the user associated with the provided confirmation code
     * @throws NotFoundException if the confirmation code does not exist
     */
    @Transactional
    @Override
    public User changeConfirmationStatusByCode(String code) {
        ConfirmationCode confirmationCode = repository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Confirmation code: " + code + " not found"));
        User user = confirmationCode.getUser();
        confirmationCode.setConfirmed(true);
        repository.save(confirmationCode);
        return user;
    }

    /**
     * Retrieves the confirmation code associated with the specified user.
     *
     * This method looks up the confirmation code for the given user in the repository. If the user
     * does not exist or has no associated confirmation code, an exception is thrown.
     *
     * @param user the user for whom the confirmation code is being retrieved
     * @return the confirmation code associated with the specified user
     * @throws IllegalArgumentException if the provided user is null
     * @throws NotFoundException if no confirmation code is found for the specified user
     */
    @Override
    public ConfirmationCode findCodeByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must be provided");
        }
        ConfirmationCode confirmationCode = repository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Confirmation code for user: " + user.getUsername() + " not found"));
        return confirmationCode;
    }

    /**
     * Deletes the confirmation code associated with the specified user.
     * If the user does not exist or has no associated confirmation code, an exception is thrown.
     *
     * @param user the user for whom the confirmation code is being deleted from the repository.
     * @throws IllegalArgumentException if the provided user is null
     * @throws NotFoundException if no confirmation code is found for the specified user
     */
    @Override
    public void deleteConfirmationCodeByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must be provided");
        }
        ConfirmationCode confirmationCode = repository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Confirmation code for user: " + user.getUsername() + " not found"));
        repository.delete(confirmationCode);
    }
}
