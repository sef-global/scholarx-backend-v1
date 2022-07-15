package org.sefglobal.scholarx.service;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.core.env.Environment;

public class MailBuilder {
    private String username = "pasindur2@gmail.com";
    private String password = "wwooravkrkhorobx";
    private String host = "smtp.host.io";
    private int port = 587;
    private static MailBuilder mailBuilder_instance = null;
    private MailBuilder(){

        MailerBuilder
                .withSMTPServer(host, port, username, password)
                .buildMailer();
    }

    public static MailBuilder getInstance() {
        if (mailBuilder_instance == null){
            mailBuilder_instance = new MailBuilder();
        }
        return mailBuilder_instance;
    }
}
