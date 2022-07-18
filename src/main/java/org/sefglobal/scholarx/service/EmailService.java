package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.model.Mail;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private SimpleJavaMail simpleJavaMail;

    public void sendEmail(String name, String emailAddress, String subject, String message, boolean showButton) throws IOException {
        Mail email = new Mail();
        email.setName(name);
        email.setEmail(emailAddress);
        email.setSubject(subject);
        email.setMessage(message);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", name);
        model.put("emailAddress", emailAddress);
        model.put("subject", subject);
        model.put("message", message);
        model.put("showButton", showButton);
        email.setProps(model);

        simpleJavaMail.sendEmail(email);
    }
}
