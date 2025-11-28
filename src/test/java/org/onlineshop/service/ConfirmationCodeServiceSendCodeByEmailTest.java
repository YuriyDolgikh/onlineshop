package org.onlineshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.entity.User;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.service.mail.MailUtil;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationCodeServiceSendCodeByEmailTest {

    @Mock
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Mock
    private MailUtil mailUtil;

    @InjectMocks
    private ConfirmationCodeService confirmationCodeService;

    @Test
    void testSendCodeByEmailIsCallMailUtilWithCorrectParameters() {
        String linkPath = "http://localhost:8080/v1/users/confirmation?code=";
        try {
            Field field = ConfirmationCodeService.class.getDeclaredField("LINK_PATH");
            field.setAccessible(true);
            field.set(confirmationCodeService, linkPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String code = "test-uuid-code-12345678";
        User newTestUserOne = User.builder()
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .role(User.Role.USER)
                .build();

        String expectedLink = linkPath + code;

        confirmationCodeService.sendCodeByEmail(code, newTestUserOne);

        verify(mailUtil).sendConfirmationEmail(eq(newTestUserOne), eq(expectedLink));
    }

    @Test
    void testSendCodeByEmailCheckNotCallRepository() {
        String code = "test-code";
        User user = User.builder().userId(10).email("test@company.com").build();

        confirmationCodeService.sendCodeByEmail(code, user);

        verify(confirmationCodeRepository, never()).save(any());
        verify(confirmationCodeRepository, never()).findByCode(any());
        verify(confirmationCodeRepository, never()).findByUser(any());
    }

    @Test
    void testSendCodeByEmailCallMailUtil() {
        String code = "test-code";
        User user = User.builder().userId(10).email("test@company.com").build();

        confirmationCodeService.sendCodeByEmail(code, user);

        verify(mailUtil, times(1)).sendConfirmationEmail(any(User.class), any(String.class));
    }
}