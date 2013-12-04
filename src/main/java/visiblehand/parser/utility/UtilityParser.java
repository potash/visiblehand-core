package visiblehand.parser.utility;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;

import lombok.Getter;
import visiblehand.entity.UtilityReceipt;
import visiblehand.parser.MessageParser;

import com.sun.mail.imap.Utility;

// A skeleton for an utility email receipt parser

public abstract class UtilityParser extends MessageParser {
	public abstract Utility getUtility();
	public abstract UtilityReceipt parse(Message message) throws java.text.ParseException, MessagingException, IOException;

	@Getter
	private final Class<UtilityReceipt> receiptClass = UtilityReceipt.class;
}