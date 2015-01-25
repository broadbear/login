package org.mike.mailers;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.mike.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

public class ActionMailer {
	final static Logger log = LoggerFactory.getLogger(ActionMailer.class);

	MimeMessage message;
	
	public void mailTo(String to, String from, String subject, String text) {
		String smtpHost = Config.getSingleton().getSmtpHost();
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", smtpHost);
		Session session = Session.getDefaultInstance(properties);
		
		try {
			message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setText(text);
		} catch (MessagingException mex) {
			log.error("There was a problem creating message.", mex);
		}
	}
	
	public void deliverNow() {
		try {
			Transport.send(message);
		} catch (MessagingException mex) {
			log.error("There was a problem sending message.", mex);
		}
	}
	
	static ST createTemplate(String name) {
		STGroup g = new STGroupDir(Config.getSingleton().getEmailTemplatePath(), '$', '$');
		ST st = g.getInstanceOf(name);
		return st;
	}
}
