package org.sefglobal.scholarx.model;

import lombok.Data;
import org.sefglobal.scholarx.util.MailGroup;

import java.util.List;

@Data
public class BulkEmailDto {
	private String subject;
	private String message;
	private MailGroup mailGroup;
	private List<String> additionalEmails;

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

	public MailGroup getMailGroup() {
		return mailGroup;
	}

	public void setMailGroup(MailGroup mailGroup) {
		this.mailGroup = mailGroup;
	}

	public List<String> getAdditionalEmails() {
		return additionalEmails;
	}

	public void setAdditionalEmails(List<String> additionalEmails) {
		this.additionalEmails = additionalEmails;
	}
}
