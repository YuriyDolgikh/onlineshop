package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ConfirmationCodeServiceFindCodesByUserTest {

    @Mock
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    private UserRepository userRepository;

    @InjectMocks
    private ConfirmationCodeService confirmationCodeService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        confirmationCodeRepository.deleteAll();
    }

//    @Test
//    void testFindCodesByUserIfPresent() {
//        User newTestUser = User.builder()
//                .username("newTestUser")
//                .email("testUser@email.com")
//                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
//                .phoneNumber("+494949494949")
//                .status(User.Status.CONFIRMED)
//                .role(User.Role.USER)
//                .build();
//
//        userRepository.save(newTestUser);
//
//        ConfirmationCode confirmationCode = ConfirmationCode.builder()
//                .code("someConfirmationCode")
//                .user(newTestUser)
//                .expireDataTime(LocalDateTime.now().plusDays(1))
//                .build();
//
//        confirmationCodeRepository.save(confirmationCode);
//
//           ConfirmationCode code = confirmationCodeService.findCodeByUser(newTestUser);
//
//           assertNotNull(code);


//       //ВторойСпособ
//
//        String code = "test-uuid-code";
//        User newTestUserOne = User.builder()
//                .username("newTestUser")
//                .email("testUser@email.com")
//                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
//                .phoneNumber("+494949494949")
//                .role(User.Role.USER)
//                .build();
//
//        confirmationCodeService.saveConfirmationCode(code, newTestUserOne);
//
//        ConfirmationCode codeForCheck = confirmationCodeService.findCodeByUser(newTestUserOne);
//
//        assertNotNull(code);
//
//    }

    @Test
    void testFindCodesByUserWhenNoCodesFound() {
        User user = User.builder().userId(10).email("test@example.com").build();

        assertThrows(NotFoundException.class, () -> confirmationCodeService.findCodeByUser(user));
    }

    @Test
    void testFindCodesByUserWhenUserIsNull() {
        User user = null;
        assertThrows(IllegalArgumentException.class, () -> confirmationCodeService.findCodeByUser(user));
    }

}