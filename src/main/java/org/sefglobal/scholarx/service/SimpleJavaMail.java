package org.sefglobal.scholarx.service;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class SimpleJavaMail {

    public void sendEmail(String name,String emailAddress, String subject, String message, boolean showButton) {

        Email email = EmailBuilder.startingBlank()
                .to(name, emailAddress)
                .withSubject(subject)
                .withHTMLText(message)
                .buildEmail();

        MailBuilder.getInstance(email);

    }
}
