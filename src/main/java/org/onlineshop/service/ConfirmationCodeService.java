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
     * <p>
     * This method performs three actions:
     * <ol>
     *   <li>Generates a unique confirmation code</li>
     *   <li>Saves the code in the database</li>
     *   <li>Sends the confirmation link to the user's email</li>
     * </ol>
     * </p>
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
     * Send confirmation code by email
     *
     * @param code - confirmation code
     * @param user - user for whom we send confirmation code
     */
    @Override
    public void sendCodeByEmail(String code, User user) {
        String linkToSend = LINK_PATH + code;
        mailUtil.sendConfirmationEmail(user, linkToSend);
        System.out.printf("Confirmation code: " + linkToSend);
    }

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
     * @format -  128 bit
     * @template -  xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx ('x' - is a character or a number)
     * @example - 3f29c3b2-9fc2-11ed-a8fc-0242ac120002
     */
    @Override
    public String generateCode() {
        return UUID.randomUUID().toString();
    }

    @Override
    public User changeConfirmationStatusByCode(String code) {
        ConfirmationCode confirmationCode = repository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Confirmation code: " + code + " not found"));
        User user = confirmationCode.getUser();
        confirmationCode.setConfirmed(true);
        repository.save(confirmationCode);
        return user;
    }

    @Override
    public ConfirmationCode findCodeByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must be provided");
        }
        ConfirmationCode confirmationCode = repository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Confirmation code for user: " + user.getUsername() + " not found"));
        return confirmationCode;
    }

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
