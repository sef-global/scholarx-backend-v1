package org.sefglobal.scholarx.util;

import org.sefglobal.scholarx.model.Email;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;


@Component
public class EmailUtil {

    private final JavaMailSender emailSender;

    private final SpringTemplateEngine templateEngine;

    public EmailUtil(JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    public void sendSimpleMessage(Email email) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(email.getProps());

        String html = templateEngine.process("scholarx", context);

        helper.setTo(email.getEmail());
        helper.setSubject(email.getSubject());
        helper.setText(html, true);

        emailSender.send(message);
    }
}
