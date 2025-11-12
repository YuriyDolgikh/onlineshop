package org.onlineshop.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ConfirmationCodeServiceGenerateCodeTest {

    @InjectMocks
    private ConfirmationCodeService confirmationCodeService;

    @Test
    void testGenerateCodeReturnValidUUID() {
        String result = confirmationCodeService.generateCode();
        assertDoesNotThrow(() -> UUID.fromString(result));
    }

    @Test
    void testGenerateCodeReturnNotNullString() {
        String result = confirmationCodeService.generateCode();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGenerateCodeReturnUniqueValues() {
        String code1 = confirmationCodeService.generateCode();
        String code2 = confirmationCodeService.generateCode();
        String code3 = confirmationCodeService.generateCode();

        assertNotEquals(code1, code2);
        assertNotEquals(code1, code3);
        assertNotEquals(code2, code3);
    }

    @Test
    void testGenerateCodeCorrectUUIDFormat() {
        String result = confirmationCodeService.generateCode();

        assertEquals(36, result.length());
        assertEquals(5, result.split("-").length);
    }
}