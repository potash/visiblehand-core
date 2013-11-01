package visiblehand.parser;

import java.io.IOException;
import java.text.ParseException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.HeaderTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import lombok.Getter;

public abstract class MessageParser {
	public abstract String getFromString();
	public abstract String getSubjectString();
	public abstract String getBodyString();
	
	public abstract Class getReceiptClass();
	
	@Getter
	private boolean active = false;	// inactive by default
	
	@Getter(lazy = true)
	private final SearchTerm searchTerm = searchTerm();
	
	private SearchTerm searchTerm() {
		SearchTerm searchTerm = new OrTerm(
				new FromStringTerm(getFromString()),
				new HeaderTerm("reply-to", getFromString()));
		if (!(getSubjectString() == null || getSubjectString().isEmpty())) {
			searchTerm = new AndTerm(searchTerm, new SubjectTerm(getSubjectString()));
		}
		if (!(getBodyString() == null || getBodyString().isEmpty())) {
			searchTerm = new AndTerm(searchTerm, new BodyTerm(getBodyString()));
		}
		return searchTerm;
	}
	
	public abstract Receipt parse(Message message) throws ParseException, MessagingException, IOException;
	
	public static String getContent(Message message) throws MessagingException, IOException {
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
