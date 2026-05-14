package edu.cit.atillo.circulend.features.shared.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendWelcomeEmailReturnsTrueOnSuccess() {
        ReflectionTestUtils.setField(emailService, "from", "noreply@example.com");

        boolean sent = emailService.sendWelcomeEmail("user@example.com", "User");

        assertTrue(sent);
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertEquals("noreply@example.com", captor.getValue().getFrom());
        assertEquals("Welcome to CircuLend", captor.getValue().getSubject());
    }

    @Test
    void sendWelcomeEmailReturnsFalseOnFailure() {
        ReflectionTestUtils.setField(emailService, "from", "noreply@example.com");
        doThrow(new RuntimeException("smtp down")).when(mailSender).send(any(SimpleMailMessage.class));

        boolean sent = emailService.sendWelcomeEmail("user@example.com", "User");

        assertFalse(sent);
    }
}
