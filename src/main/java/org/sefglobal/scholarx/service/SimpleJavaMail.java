package org.sefglobal.scholarx.service;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;

public class SimpleJavaMail {

    public void sendEmail(String name,String emailAddress, String subject, String message, boolean showButton) {

        Email email = EmailBuilder.startingBlank()
                .to(name, emailAddress)
                .withSubject(subject)
                .withHTMLText(message)
                .buildEmail();

        Mailer mailer = (Mailer) MailBuilder.getInstance();
        mailer.sendMail(email);
    }
}
