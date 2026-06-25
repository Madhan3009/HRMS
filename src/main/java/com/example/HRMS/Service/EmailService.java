package com.example.HRMS.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@example.com}")
    private String fromAddress;

    @Value("${mail.enabled:false}")
    private boolean mailEnabled;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTemporaryPassword(String toEmail, String name, String tempPassword) {
        String subject = "Welcome to the Team! Your HRMS Account Credentials";
        String body = String.format(
                "Hello %s,\n\n" +
                "Welcome to the company! An HRMS account has been created for you.\n\n" +
                "Here are your login credentials:\n" +
                "Username/Email: %s\n" +
                "Temporary Password: %s\n\n" +
                "Please login and change your password as soon as possible.\n\n" +
                "Best Regards,\n" +
                "HR Department",
                name, toEmail, tempPassword
        );

        if (mailEnabled && mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromAddress);
                message.setTo(toEmail);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                log.info("Successfully sent onboarding email to {}", toEmail);
            } catch (Exception e) {
                log.error("Failed to send onboarding email to {}: {}", toEmail, e.getMessage());
            }
        } else {
            log.info("Mail dispatch disabled or mail sender not configured. Simulating onboarding email to {}:\n{}", toEmail, body);
        }
    }
}
