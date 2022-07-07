package org.sefglobal.scholarx.service;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.mailer.MailerBuilder;

public class MailBuilder {

    private static MailBuilder mailBuilder_instance = null;

    private String username = "pasindur2@gmail.com";
    private String password = "wwooravkrkhorobx";
    private String host = "smtp.mailtrap.io";
    private int port = 2525;

    private MailBuilder(Email email){

        MailerBuilder
                .withSMTPServer(host, port, username, password)
                .buildMailer()
                .sendMail(email);

        System.out.println("Email sent successfully");
    }

    public static MailBuilder getInstance(Email email)
    {
        if (mailBuilder_instance == null)
            mailBuilder_instance = new MailBuilder(email);

        return mailBuilder_instance;
    }

}
