package org.sefglobal.scholarx.service;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class MailInstance {
    private static Environment env;

    private MailInstance(Environment env){
        this.env = env;
    }
    private static String username = env.getProperty("spring.mail.username");
    private static String password = env.getProperty("spring.mail.password");
    private static String host = env.getProperty("spring.mail.host");
    private static String port = env.getProperty("spring.mail.port");
    private static Mailer instance = null;

    public static Mailer getInstance() {
        if (instance == null){
            instance = MailerBuilder
                                    .withSMTPServer(host, Integer.valueOf(port), username, password)
                                    .buildMailer();
        }
        return instance;
    }
}
