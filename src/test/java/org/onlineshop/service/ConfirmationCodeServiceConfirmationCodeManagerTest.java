package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.service.util.MailUtil;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ConfirmationCodeServiceConfirmationCodeManagerTest {

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
    void testConfirmationCodeManagerCheckFullProcess() {
        User newTestUserOne = User.builder()
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .role(User.Role.USER)
                .build();

        confirmationCodeService.confirmationCodeManager(newTestUserOne);

        verify(confirmationCodeRepository).save(any(ConfirmationCode.class));
        verify(mailUtil).sendConfirmationEmail(eq(newTestUserOne), any(String.class));
    }

    @Test
    void testConfirmationCodeManagerCallWithDifferentUsers() {
        User newTestUserOne = User.builder()
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .role(User.Role.USER)
                .build();

        User newTestUserTwo = User.builder()
                .username("newTest")
                .email("test@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494912")
                .role(User.Role.USER)
                .build();
        confirmationCodeService.confirmationCodeManager(newTestUserOne);
        confirmationCodeService.confirmationCodeManager(newTestUserTwo);

        verify(confirmationCodeRepository, times(2)).save(any(ConfirmationCode.class));
        verify(mailUtil, times(1)).sendConfirmationEmail(eq(newTestUserOne), any(String.class));
        verify(mailUtil, times(1)).sendConfirmationEmail(eq(newTestUserTwo), any(String.class));
    }
}