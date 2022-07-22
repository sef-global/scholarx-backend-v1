package org.sefglobal.scholarx.util;

import org.sefglobal.scholarx.model.Mail;
import org.sefglobal.scholarx.service.MailConnection;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;

public class EmailUtil {
    private final MailConnection mailConnection;

    public EmailUtil(MailConnection mailConnection) {
        this.mailConnection = mailConnection;
    }

    public void sendEmail(Mail mail) {

        Email email = EmailBuilder.startingBlank()
                .to(mail.getEmailAddress())
                .withSubject(mail.getSubject())
                .withHTMLText(mail.getMessage())
                .buildEmail();

        Mailer mailer = mailConnection.getInstance();
        mailer.sendMail(email);
    }
}
