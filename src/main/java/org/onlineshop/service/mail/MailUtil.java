package org.onlineshop.service.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.User;
import org.onlineshop.exception.MailSendingException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailUtil {

    private final JavaMailSender mailSender;
    private final Configuration freemakerConfiguration;
    private final String messageSubject = "Code confirmation email";

    // http://localhost:8080/api/public/confirmation?code=f9fcc1ec-6d34-4fbe-9367-69378ae89d70

    /**
     * Sends a confirmation email to the specified user with the provided confirmation link.
     *
     * @param user the recipient of the confirmation email
     * @param linkToSend the confirmation link to be included in the email
     * @throws MailSendingException if an error occurs during the email sending process
     */
    public void sendConfirmationEmail(User user, String linkToSend) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        try {
            helper.setTo(user.getEmail());
            helper.setSubject(messageSubject);
            helper.setText(createConfirmationEmail(user, linkToSend), true);
        } catch (Exception e){
            throw new MailSendingException(e.getMessage());
        }
        mailSender.send(message);
        log.info("Confirmation email sent to {}", user.getEmail());
    }

    /**
     * Generates the content for a confirmation email using a predefined template.
     *
     * @param user the user who will receive the confirmation email
     * @param linkToSend the confirmation link to be included in the email content
     * @return the generated email content as a string
     * @throws IOException if an I/O error occurs while loading the email template
     * @throws TemplateException if an error occurs while processing the email template
     */
    public String createConfirmationEmail(User user, String linkToSend) throws IOException, TemplateException {
        Template template = freemakerConfiguration.getTemplate("confirm_registration_mail.ftlh");
        Map<Object, Object> model = new HashMap<>();
        model.put("name", user.getUsername());
        model.put("link", linkToSend);
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    /**
     * Sends an order paid confirmation email to the specified user with the provided order details
     * and attaches the corresponding PDF receipt.
     *
     * @param user the recipient of the confirmation email
     * @param order the order for which the confirmation email is being sent
     * @param pdfBytes the PDF file content to be attached to the email as bytes
     * @throws MailSendingException if an error occurs during the email sending process
     */
    public void sendOrderPaidEmail(User user, Order order, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Order Payment Confirmation - Order #" + order.getOrderId());

            // Email body text
            String text = """
                Hello %s,

                Your order #%d has been successfully paid.

                Delivery method: %s
                Delivery address: %s

                Thank you for shopping with us!
                You can find your order details in the attached PDF file.

                Best regards,
                The Online Shop Team
                """.formatted(
                    user.getUsername(),
                    order.getOrderId(),
                    order.getDeliveryMethod(),
                    order.getDeliveryAddress()
            );
            helper.setText(text, false);
            helper.addAttachment("order_" + order.getOrderId() + ".pdf",
                    new ByteArrayResource(pdfBytes));
            mailSender.send(message);
            log.info("Order payment email sent to {}", user.getEmail());
        } catch (Exception e) {
            throw new MailSendingException("Error sending order payment email: " + e.getMessage());
        }
    }
}

