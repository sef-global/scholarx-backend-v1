package org.sefglobal.scholarx.util;

import org.sefglobal.scholarx.model.Mail;
import org.sefglobal.scholarx.service.MailConnection;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EmailUtil {

    private final SpringTemplateEngine templateEngine;
    private static Environment env;

    public EmailUtil(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void sendEmail(Mail mail) {

        String username = env.getProperty("spring.mail.username");
        Context context = new Context();
        context.setVariables(mail.getProps());

        String htmlText = templateEngine.process("scholarx", context);

         Email email = EmailBuilder.startingBlank()
                .from(username)
                .to(mail.getEmail())
                .withSubject(mail.getSubject())
                .withHTMLText(htmlText)
                .buildEmail();

        MailConnection.getInstance().sendMail(email);
    }
}
