package org.sefglobal.scholarx.util;

import org.sefglobal.scholarx.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(Email email) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getEmail());
        message.setSubject(email.getSubject());
        message.setText(email.getMessage());
        emailSender.send(message);
    }
}
