package io.turntabl.ttbay.service;

import jakarta.mail.MessagingException;

public interface GmailService {

    void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException;
    void sendHtmlMessage(String[] to, String subject, String htmlBody) throws MessagingException;
}
