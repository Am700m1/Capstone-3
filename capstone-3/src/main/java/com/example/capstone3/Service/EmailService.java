package com.example.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    // JavaMailSender delivers notification emails through the configured mail server.
    private final JavaMailSender mailSender;

    // Sends a plain-text email to the supplied recipient.
    public void sendEmail(String to, String subject, String message) {

        // SimpleMailMessage stores the recipient, subject, and plain-text content.
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(message);
        // JavaMailSender sends the completed message.
        mailSender.send(mail);
    }
}
