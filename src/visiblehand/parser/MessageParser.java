package visiblehand.parser;

import java.io.IOException;
import java.text.ParseException;

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
	
	@Getter
	private boolean active = false;	// inactive by default
	
	@Getter
	protected SearchTerm searchTerm = new AndTerm(new
		FromStringTerm(getFromString()),
		new SubjectTerm(getSubjectString()));
	
	public abstract Receipt parse(Message message) throws ParseException, MessagingException, IOException;
	
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
	
	public String getSearchString() {
		return "(from:" + getFromString() + " and subject:\"" + getSubjectString() + "\")";
	}
}
