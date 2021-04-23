package org.sefglobal.scholarx.model;

import lombok.Data;

@Data
public class Email {
    private String emailAddress;
    private String subject;
    private String message;

    public String getEmail() {
        return emailAddress;
    }

    public void setEmail(String email) {
        this.emailAddress = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
