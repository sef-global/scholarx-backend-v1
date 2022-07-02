package org.sefglobal.scholarx.model;

import org.sefglobal.scholarx.util.MailGroup;

import java.util.List;

public class BulkEmailDto {
	private String subject;
	private String message;
	private List<MailGroup> mailGroups;
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

	public List<MailGroup> getMailGroups() {
		return mailGroups;
	}

	public void setMailGroups(List<MailGroup> mailGroups) {
		this.mailGroups = mailGroups;
	}

	public List<String> getAdditionalEmails() {
		return additionalEmails;
	}

	public void setAdditionalEmails(List<String> additionalEmails) {
		this.additionalEmails = additionalEmails;
	}
}
