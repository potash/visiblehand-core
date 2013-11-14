package visiblehand.parser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

import org.apache.commons.lang3.StringUtils;

public abstract class MessageParser {
	public abstract String getFromString();
	public abstract String[] getSubjectStrings();
	public abstract String getBodyString();
	
	public abstract Class getReceiptClass();
	
	protected static final String monthsRegex = "(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)";
	protected static final String daysRegex = "(MON|TUE|WED|THU|FRI|SAT|SUN)";

	@Getter
	private boolean active = false;	// inactive by default
	
	@Getter(lazy = true)
	private final SearchTerm searchTerm = searchTerm();
	
	private SearchTerm searchTerm() {
		SearchTerm searchTerm = new OrTerm(
				new FromStringTerm(getFromString()),
				new HeaderTerm("reply-to", getFromString()));
		if (!(getSubjectStrings() == null || getSubjectStrings().length == 0)) {
			SearchTerm subjectTerm = new SubjectTerm(getSubjectStrings()[0]);
			for (int i = 1; i < getSubjectStrings().length; i++) {
				subjectTerm = new OrTerm(subjectTerm, new SubjectTerm(getSubjectStrings()[i]));
			}
			searchTerm = new AndTerm(searchTerm, subjectTerm);
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
		return "(from:" + getFromString() + " and (subject:\"" + StringUtils.join(getSubjectStrings(), "subject:\"") + "\")";
	}
	
	protected static DateFormat getGMTSimpleDateFormat(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat;
	}
	
	// set year of date0 to minimum such that date0 > date1
	protected static Date setYear(Date date0, Date date1) {
		Calendar calDate = Calendar.getInstance();
		calDate.setTime(date1);
		int year = calDate.get(Calendar.YEAR);
	
		Calendar calDay = Calendar.getInstance();
		calDay.setTime(date0);
		calDay.set(Calendar.YEAR, year);
		
		if (calDay.compareTo(calDate) < 0) {
			calDay.set(Calendar.YEAR, year+1);
		}
		return calDay.getTime();
	}
	
	public static String[] splitLastInstanceOf(String string, String str) {
		int index = string.lastIndexOf(str);
		if (index == -1) {
			String s = new String(string);
			return new String[] {s, ""};
		} else {
			return new String[] {string.substring(0,index), 
					string.substring(index+str.length())};
		}
	}
}
