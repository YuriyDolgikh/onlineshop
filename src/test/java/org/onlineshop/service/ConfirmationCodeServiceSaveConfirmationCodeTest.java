package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.service.mail.MailUtil;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ConfirmationCodeServiceSaveConfirmationCodeTest {

    @Mock
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Mock
    private MailUtil mailUtil;

    @InjectMocks
    private ConfirmationCodeService confirmationCodeService;

    @AfterEach
    void tearDown() {
        confirmationCodeRepository.deleteAll();
    }

    @Test
    void testSaveConfirmationCodeCallRepositorySave() {
        String code = "test-uuid-code";
        User newTestUserOne = User.builder()
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .role(User.Role.USER)
                .build();

        confirmationCodeService.saveConfirmationCode(code, newTestUserOne);

        verify(confirmationCodeRepository).save(any(ConfirmationCode.class));
    }

    @Test
    void testSaveConfirmationCodeSaveWithCorrectCodeAndUser() {
        String codeForSave = "test-uuid-123";
        User newTestUserOne = User.builder()
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .role(User.Role.USER)
                .build();

        ConfirmationCode[] codes = new ConfirmationCode[1];

        when(confirmationCodeRepository.save(any(ConfirmationCode.class)))
                .then(invocationOnMock -> {
                    codes[0] = invocationOnMock.getArgument(0);
                    return codes[0];
                });

        confirmationCodeService.saveConfirmationCode(codeForSave, newTestUserOne);

        verify(confirmationCodeRepository).save(any(ConfirmationCode.class));
        assert codes[0] != null;
        assert codes[0].getCode().equals(codeForSave);
        assert codes[0].getUser().equals(newTestUserOne);
        assert !codes[0].isConfirmed();
        assert codes[0].getExpireDataTime() != null;
        LocalDateTime dateForTest = LocalDateTime.now().plusDays(179);
        assert codes[0].getExpireDataTime().isAfter(dateForTest);
    }

    @Test
    void testSaveConfirmationCodeCallRepositoryOnlyOnce() {
        String code = "test-code";
        User user = User.builder().userId(10).email("test@company.com").build();

        confirmationCodeService.saveConfirmationCode(code, user);

        verify(confirmationCodeRepository, times(1)).save(any(ConfirmationCode.class));
    }
}