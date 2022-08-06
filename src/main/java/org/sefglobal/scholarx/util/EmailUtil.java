package org.sefglobal.scholarx.util;

import org.sefglobal.scholarx.model.Mail;
import org.sefglobal.scholarx.service.MailConnection;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EmailUtil {

    private final SpringTemplateEngine templateEngine;

    public EmailUtil(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void sendEmail(Mail mail) {

        Context context = new Context();
        context.setVariables(mail.getProps());

        String htmlText = templateEngine.process("scholarx", context);

         Email email = EmailBuilder.startingBlank()
                .to(mail.getEmail())
                .withSubject(mail.getSubject())
                .withHTMLText(htmlText)
                .buildEmail();

        MailConnection.getInstance().sendMail(email);
    }
}
