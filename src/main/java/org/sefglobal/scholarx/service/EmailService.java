package org.sefglobal.scholarx.service;
import org.sefglobal.scholarx.model.Email;
import org.sefglobal.scholarx.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private EmailUtil emailUtil;

    public Email sendEmail(String emailAddress, String subject, String message){
        Email email = new Email();
        email.setEmail(emailAddress);
        email.setSubject(subject);
        email.setMessage(message);
        emailUtil.sendSimpleMessage(email);

        return email;
    }
}
