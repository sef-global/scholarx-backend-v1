package org.sefglobal.scholarx.service;
import org.sefglobal.scholarx.model.Email;
import org.sefglobal.scholarx.util.EmailUtil;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private final EmailUtil emailUtil;

    public EmailService(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    public Email sendEmail(String emailAddress, String subject, String message) throws IOException, MessagingException {
        Email email = new Email();
        email.setEmail(emailAddress);
        email.setSubject(subject);
        email.setMessage(message);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("emailAddress", emailAddress);
        model.put("subject", subject);
        model.put("message", message);
        email.setProps(model);

        emailUtil.sendSimpleMessage(email);

        return email;
    }
}
