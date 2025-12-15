package org.onlineshop.service.mail;

import freemarker.template.Configuration;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.entity.User;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;


import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailUtilCreateConfirmationEmailTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private Configuration freeMarkerConfiguration;

    @Mock
    private Template template;

    private MailUtil mailUtil;

    @BeforeEach
    void setUp() {
        mailUtil= new MailUtil(mailSender,freeMarkerConfiguration);
    }

    @Test
    void createConfirmationEmailIfOk() throws IOException, TemplateException, MessagingException {

        User user = new User();
        user.setUsername("TestUser");
        user.setEmail("test@example.com");
        String link = "http://example.com/confirm";
        String expectedHtml = "<html>Hello TestUser, confirm: http://example.com/confirm</html>";

        when(freeMarkerConfiguration.getTemplate("confirm_registration_mail.ftlh")).thenReturn(template);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailUtil.sendConfirmationEmail(user, link);
        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        MimeMessage sentMessage = messageCaptor.getValue();

        MimeMessageHelper helper = new MimeMessageHelper(sentMessage, "UTF-8");
        helper.setTo(user.getEmail());
        helper.setSubject("Code confirmation email");
        helper.setText(expectedHtml, true);
    }


}