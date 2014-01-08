package visiblehand.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Data;

@Entity
public @Data class ReceiptMessage {
	private static Session session = Session.getDefaultInstance(new Properties());
	
	@Id
	private Long id;
	
	@Column(columnDefinition="varchar")
	private String messageString;
	
	@Transient
	private Message message;
	
	public ReceiptMessage() {
	}
	
	public ReceiptMessage(Message message) {
		setMessage(message);
	}
	
	public Message getMessage() {
		if (message == null) {
			try {
				message = new MimeMessage(session,	new ByteArrayInputStream(getMessageString().getBytes()));
			} catch (MessagingException e) {
				e.printStackTrace();
			}
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
		setMessageString(new String(out.toByteArray()));
	}
}
