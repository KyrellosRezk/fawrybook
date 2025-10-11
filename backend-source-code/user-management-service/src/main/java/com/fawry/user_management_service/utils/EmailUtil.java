package com.fawry.user_management_service.utils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "EmailUtil")
public class EmailUtil {

    private final JavaMailSender injectedMailSender;
    private static JavaMailSender mailSender;

    @PostConstruct
    public void init() {
        mailSender = this.injectedMailSender;
    }

    public static void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your Fawry otp Code");
            message.setText("""
                    Dear User,
                    
                    Your one-time password (otp) is: %s
                    
                    This code will expire in 5 minutes.
                    
                    Regards,
                    Fawry Security Team
                    """.formatted(otp));

            mailSender.send(message);
            log.info("otp email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send otp email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send otp email", e);
        }
    }
}