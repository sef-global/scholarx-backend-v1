package org.sefglobal.scholarx.service;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class SimpleJavaMail {

    public void sendEmail(String fname,String emailAddress, String subject, String message, boolean showButton) {

        String username = "pasindur2@gmail.com";
        String password = "wwooravkrkhorobx";
        String host = "smtp.mailtrap.io";
        int port = 2525;

        Email email = EmailBuilder.startingBlank()
                .from("Sustainable Education Foundation", username)
                .to(fname, emailAddress)
                .withSubject(subject)
                .withHTMLText(message)
                .buildEmail();

        MailerBuilder
                .withSMTPServer(host, port, username, password)
                .buildMailer()
                .sendMail(email);

        System.out.println("Email sent successfully");
    }
}
