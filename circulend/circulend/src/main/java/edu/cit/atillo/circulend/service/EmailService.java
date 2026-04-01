package edu.cit.atillo.circulend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${app.mail.from:${spring.mail.username}}")
    private String from;

    public void sendWelcomeEmail(String to, String firstName) {
        sendSafe(to, "Welcome to CircuLend",
                "Hi " + firstName + ",\n\nWelcome to CircuLend!");
    }

    public void sendBorrowReceipt(String to, String itemName, String borrowedAt) {
        sendSafe(to, "CircuLend Borrow Receipt",
                "You borrowed: " + itemName + "\nTime: " + borrowedAt);
    }

    public void sendReturnReceipt(String to, String itemName, String returnedAt) {
        sendSafe(to, "CircuLend Return Receipt",
                "You returned: " + itemName + "\nTime: " + returnedAt);
    }

    public void sendVerificationEmail(String to, String firstName, String verifyUrl) {
        sendSafe(to, "Verify your CircuLend account",
                "Hi " + firstName + ",\n\nClick to verify:\n" + verifyUrl);
    }

    private void sendSafe(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
        } catch (Exception ex) {
            // fail-safe: log only, do not break transaction flow
            log.error("SMTP send failed to {} subject={}: {}", to, subject, ex.getMessage());
        }
    }
}
