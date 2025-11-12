package org.onlineshop.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserControllerConfirmationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    @AfterEach
    void dropDatabase() {
        confirmationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    void testConfirmationTestIfOk() throws Exception {
        when(userService.confirmationEmail("valid-code")).thenReturn("Email confirmed");

        ResponseEntity<String> resp = userController.confirmation("valid-code");

        assertEquals(resp.getStatusCode(), HttpStatus.OK);
        assertEquals(resp.getBody(), "Email confirmed");
    }

    @Test
    void testConfirmationTestIfCodeInvalid()  {
        when(userService.confirmationEmail("bad-code")).thenThrow(new NotFoundException("Invalid confiramtion code"));

        assertThrows(NotFoundException.class, () -> userController.confirmation("bad-code"));
    }
}