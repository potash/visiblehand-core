package visiblehand.parser.utility;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;

import lombok.Getter;
import visiblehand.entity.utility.ElectricityReceipt;
import visiblehand.entity.utility.Utility;
import visiblehand.parser.MessageParser;

// A skeleton for an utility email receipt parser

public abstract class ElectricityParser extends MessageParser {
	public abstract Utility getUtility();
	public abstract ElectricityReceipt parse(Message message) throws java.text.ParseException, MessagingException, IOException;

	@Getter
	private final Class<ElectricityReceipt> receiptClass = ElectricityReceipt.class;
	
	public static Date subtractMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
	}
}