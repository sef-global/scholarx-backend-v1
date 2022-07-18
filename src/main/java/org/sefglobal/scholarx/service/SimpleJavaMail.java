package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.model.Mail;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;

public class SimpleJavaMail {

    private final MailInstance mailInstance;

    public SimpleJavaMail(MailInstance mailInstance) {
        this.mailInstance = mailInstance;
    }

    public void sendEmail(Mail mail) {

        Email email = EmailBuilder.startingBlank()
                .to(mail.getName(), mail.getEmailAddress())
                .withSubject(mail.getSubject())
                .withHTMLText(mail.getMessage())
                .buildEmail();

        Mailer mailer = mailInstance.getInstance();
        mailer.sendMail(email);
    }
}
