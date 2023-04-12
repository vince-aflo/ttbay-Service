package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.service.GmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GmailServiceImpl implements GmailService {
    private final JavaMailSender emailSender;


    @Override
    public void sendHtmlMessage(String to , String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody,true);

        emailSender.send(message);
    }


    @Override
    public void sendHtmlMessage(String[] to , String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody,true);

        emailSender.send(message);
    }
}
