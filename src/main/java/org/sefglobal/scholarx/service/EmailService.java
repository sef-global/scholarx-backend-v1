package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.model.Mail;
import org.sefglobal.scholarx.util.EmailUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private final EmailUtil emailUtil;

    public EmailService(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    public void sendEmail(String emailAddress, String subject, String message, boolean showButton) throws IOException {
        Mail mail = new Mail();
        mail.setEmail(emailAddress);
        mail.setSubject(subject);
        mail.setMessage(message);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("emailAddress", emailAddress);
        model.put("subject", subject);
        model.put("message", message);
        model.put("showButton", showButton);
        mail.setProps(model);

        emailUtil.sendEmail(mail);
    }
}
