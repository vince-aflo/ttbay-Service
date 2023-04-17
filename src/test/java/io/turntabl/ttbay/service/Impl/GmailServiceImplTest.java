package io.turntabl.ttbay.service.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GmailServiceImplTest{
    @Mock
    private JavaMailSender javaMailSender;
    private MimeMessage mimeMessage;
    @InjectMocks
    private GmailServiceImpl gmailService;


    @BeforeEach
    void setUp(){
        var javaMailSender = new JavaMailSenderImpl();
        mimeMessage = javaMailSender.createMimeMessage();
    }

    @Test
    void sendHtmlMessage_givenRecipientMail_shouldSendMail() throws MessagingException{
        doReturn(mimeMessage).when(javaMailSender).createMimeMessage();
        gmailService.sendHtmlMessage("aikins.dwamena@turntabl.io", "Test", "");
        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendHtmlMessage_givenRecipientMails_shouldSendMail() throws MessagingException{
        doReturn(mimeMessage).when(javaMailSender).createMimeMessage();
        gmailService.sendHtmlMessage(Arrays.array("aikins.dwamena@turntabl.io", "aikinsakenten@gmail.com"), "Test", "");
        verify(javaMailSender, times(1)).send(mimeMessage);
    }
}