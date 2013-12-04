package visiblehand.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
public @Data class ReceiptMessage {
	private static Session session = Session.getDefaultInstance(new Properties());
	@Column(columnDefinition="varchar")
	public String message;
	
	public ReceiptMessage() {
	}
	public ReceiptMessage(Message message) {
		setMessage(message);
	}
	
	public Message getMessage() {
		MimeMessage message = null;
		try {
			message = new MimeMessage(session,	new ByteArrayInputStream(this.message.getBytes()));
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return message;
	}
	
	public void setMessage(Message message) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			message.writeTo(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.message = new String(out.toByteArray());
	}
}
