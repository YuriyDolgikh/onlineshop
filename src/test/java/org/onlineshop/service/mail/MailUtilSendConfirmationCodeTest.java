package org.onlineshop.service.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.entity.User;
import org.onlineshop.exception.MailSendingException;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailUtilSendConfirmationCodeTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private Configuration freemarkerConfig;

    @InjectMocks
    private MailUtil mailUtil;

    @Test
    void testSendConfirmationEmail() throws Exception {

        MimeMessage fakeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(fakeMessage);

        Template fakeTemplate = mock(Template.class);
        when(freemarkerConfig.getTemplate(anyString())).thenReturn(fakeTemplate);

        doAnswer(invocation -> {
            Writer writer = invocation.getArgument(1);
            writer.write("<html>Confirm your email: https://example.com/confirm?code=123</html>");
            return null;
        }).when(fakeTemplate).process(any(), any());

        User user = User.builder()
                .email("test@mail.com")
                .username("testUser")
                .build();

        String link = "https://example.com/confirm?code=123";

        mailUtil.sendConfirmationEmail(user, link);

        verify(mailSender, times(1)).send(fakeMessage);

        String html = (String) fakeMessage.getContent();
        assert html.contains("https://example.com/confirm?code=123");
    }

    @Test
    void testSendConfirmationEmailThrowsException() throws Exception {
        MimeMessage fakeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(fakeMessage);

        MailUtil spyMailUtil = Mockito.spy(mailUtil);

        doThrow(new RuntimeException("Template error"))
                .when(spyMailUtil)
                .createConfirmationEmail(any(), anyString());

        User user = User.builder()
                .email("test@mail.com")
                .username("testUser")
                .build();

        String link = "https://example.com/confirm?code=123";

        assertThrows(MailSendingException.class, () -> {
            spyMailUtil.sendConfirmationEmail(user, link);
        });
    }
}