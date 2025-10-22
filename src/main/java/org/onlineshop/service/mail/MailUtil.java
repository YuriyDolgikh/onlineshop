//package org.onlineshop.service.mail;
//
//import freemarker.template.Template;
//import lombok.RequiredArgsConstructor;
//import org.onlineshop.entity.User;
//import freemarker.template.Configuration;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
//import freemarker.template.TemplateException;
//import jakarta.mail.internet.MimeMessage;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class MailUtil {
//    private final JavaMailSender mailSender;
//    private final Configuration freemakerConfiguration;
//    private final String messageSubject = "Code confirmation email";
//
//    // http://localhost:8080/api/public/confirmation?code=f9fcc1ec-6d34-4fbe-9367-69378ae89d70
//
//    public void send(User user, String linkToSend) {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
//        try {
//            helper.setTo(user.getEmail());
//            helper.setSubject(messageSubject);
//            helper.setText(createConfirmationEmail(user, linkToSend), true);
//
//        } catch (Exception e){
//            throw new MailSendingException(e.getMessage());
//        }
//        mailSender.send(message);
//    }
//
//    public String createConfirmationEmail(User user, String linkToSend) throws IOException, TemplateException {
//        Template template = freemakerConfiguration.getTemplate("confirm_registration_mail.ftlh");
//        Map<Object, Object> model = new HashMap<>();
//        model.put("name", user.getName());
//        model.put("link", linkToSend);
//        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
//
//
//    }
//}
