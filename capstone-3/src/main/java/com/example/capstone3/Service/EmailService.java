package com.example.capstone3.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String message) {

        // SimpleMailMessage stores the recipient, subject, and plain-text content.
        SimpleMailMessage mail = new SimpleMailMessage();

        org.springframework.mail.SimpleMailMessage mail = new org.springframework.mail.SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(message);
        mailSender.send(mail);
    }

    //Sends an email containing a PDF attachment generated in-memory.

    public void sendEmailWithPdf(String to, String subject, String bodyHtml, byte[] pdfBytes, String fileName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // Set 'true' to allow multi-part messages (attachments)
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(bodyHtml, true); // This is the email body text

        // Attach the PDF bytes as a downloadable file
        ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfBytes, "application/pdf");
        helper.addAttachment(fileName, dataSource);

        mailSender.send(mimeMessage);
    }
}