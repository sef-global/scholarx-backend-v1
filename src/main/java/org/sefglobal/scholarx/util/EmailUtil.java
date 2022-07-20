package org.sefglobal.scholarx.util;

import org.sefglobal.scholarx.model.Mail;
import org.sefglobal.scholarx.service.MailInstance;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;

public class EmailUtil {
    private final MailInstance mailInstance;

    public EmailUtil(MailInstance mailInstance) {
        this.mailInstance = mailInstance;
    }

    public void sendEmail(Mail mail) {

        Email email = EmailBuilder.startingBlank()
                .to(mail.getEmailAddress())
                .withSubject(mail.getSubject())
                .withHTMLText(mail.getMessage())
                .buildEmail();

        Mailer mailer = mailInstance.getInstance();
        mailer.sendMail(email);
    }
}
