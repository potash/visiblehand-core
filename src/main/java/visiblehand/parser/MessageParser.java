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

import visiblehand.entity.Receipt;

public abstract class MessageParser<R extends Receipt> {
	public abstract R parse(Message message) throws ParseException, MessagingException, IOException;
	
	public abstract String getFromString();
	public abstract String[] getSubjectStrings();
	public abstract String getBodyString();
	
	public abstract Class<R> getReceiptClass();
	
	protected static final String mmmmRegex = "(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER)";
	protected static final String mmmRegex = "(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)";
	protected static final String dddRegex = "(MON|TUE|WED|THU|FRI|SAT|SUN)";
	protected static final TimeZone GMT = TimeZone.getTimeZone("GMT");
	
	public boolean isActive() {
		return getParserDate() != null;
	}
	
	public abstract Date getParserDate();
	public abstract Date getSearchDate();
	
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
		return "(from:" + getFromString() + 
				(getSubjectStrings() != null ? (" and (subject:\"" + StringUtils.join(getSubjectStrings(), "\" || subject:\"") + "\")") : "") 
				+ ")";
	}
	
	public static DateFormat getGMTSimpleDateFormat(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(GMT);
		return dateFormat;
	}
	
	// set year of time to minimum such that it is later than date
	protected static Date getNextDate(String timeOfYearFormat, String timeOfYear, Date date) throws ParseException {
		Calendar calDate = Calendar.getInstance(GMT);
		calDate.setTime(date);
		int year = calDate.get(Calendar.YEAR);
		
		DateFormat format = getGMTSimpleDateFormat("yyyy" + timeOfYearFormat);
		Date d = format.parse(year + timeOfYear);
		
		if (d.compareTo(date) < 0) {
			d = format.parse((year+1) + timeOfYear);
		}
		
		return d;
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
	
	public static Date subtractMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
	}
}
