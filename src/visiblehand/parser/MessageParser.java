package visiblehand.parser;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.search.AndTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import lombok.Getter;

public abstract class MessageParser {
	public abstract String getFromString();
	public abstract String getSubjectString();
	
	@Getter(lazy=true)
	private final SearchTerm searchTerm = new AndTerm(new
		FromStringTerm(getFromString()),
		new SubjectTerm(getSubjectString()));
	
	public String getContent(Message message) throws MessagingException, IOException {
		String content;
		if (message.getContent() instanceof Multipart) {
			Multipart mp = (Multipart) message.getContent();
			BodyPart bp = mp.getBodyPart(0);
			content = bp.getContent().toString();
		} else {
			content = message.getContent().toString();
		}
		return content;
	}
}
