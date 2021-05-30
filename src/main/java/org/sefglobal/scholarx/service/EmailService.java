package org.sefglobal.scholarx.service;
import org.sefglobal.scholarx.model.Email;
import org.sefglobal.scholarx.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private EmailUtil emailUtil;

    public Email sendEmail(String email, String subject, String message){
        Email emailObj = new Email();
        emailObj.setEmail(email);
        emailObj.setSubject(subject);
        emailObj.setMessage(message);
        emailUtil.sendSimpleMessage(emailObj);

        return emailObj;
    }
}
