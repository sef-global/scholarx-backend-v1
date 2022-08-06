package org.sefglobal.scholarx.service;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;

public class MailConnection {

    private MailConnection(){}
    private static Mailer instance = null;

    public static Mailer getInstance() {
        if (instance == null){
            instance = MailerBuilder.buildMailer();
        }
        return instance;
    }
}
