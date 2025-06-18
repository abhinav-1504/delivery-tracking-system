package com.delivery_tracking_system.service;

import com.delivery_tracking_system.entity.Order;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final JavaMailSender mailSender;

    public void sendOrderStatusEmail(String toEmail, Order order) {
        logger.info("Sending email to : {} for order ID: {}", toEmail, order.getId());

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(toEmail);
            helper.setSubject("Order Status Update - Order #" + order.getId());
            String emailContent = loadEmailTemplate(order);
            helper.setText(emailContent, true);

            mailSender.send(message);
            logger.info("Email sent successfully to : {}", toEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send email to : {}. Error: {}", toEmail, e.getMessage());
        }
    }

    private String loadEmailTemplate(Order order) {
        try {
            Path templatePath = new ClassPathResource("templates/email-template.html").getFile().toPath();
            String template = Files.readString(templatePath, StandardCharsets.UTF_8);

            return template
                    .replace("{orderId}", order.getId().toString())
                    .replace("{status}", order.getStatus())
                    .replace("{deliveryAddress}", order.getDeliveryAddress());

        } catch (IOException e) {
            logger.error("Error loading email template: {}", e.getMessage());
            return "Unable to load email content.";
        }
    }
}
