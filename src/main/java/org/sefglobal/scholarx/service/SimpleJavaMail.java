package org.sefglobal.scholarx.service;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.email.EmailBuilder;

public class SimpleJavaMail {

    public void sendEmail(String name,String emailAddress, String subject, String message, boolean showButton) {

        String username = "pasindur2@gmail.com";

        Email email = EmailBuilder.startingBlank()
                .from("Sustainable Education Foundation", username)
                .to(name, emailAddress)
                .withSubject(subject)
                .withHTMLText(message)
                .buildEmail();

        MailBuilder.getInstance(email);

        System.out.println("Email sent successfully");
    }
}
