package visiblehand.parser.utility;

import java.io.IOException;
import java.text.ParseException;

import javax.mail.Message;
import javax.mail.MessagingException;

import lombok.Data;
import lombok.Getter;

import com.sun.mail.imap.Utility;

// United Airlines email receipt parser

public @Data class ComEdParser extends UtilityParser {
	private final String fromString = "mycheckfree@customercenter.net";
	private final String subjectString = "You have a new bill from ComEd - Commonwealth Edison.";
	private final String  bodyString = "";
	
	@Getter(lazy = true)
	private final Utility utility = null;

	public UtilityReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {

		UtilityReceipt receipt = new UtilityReceipt();
		String content = getContent(message);
		receipt.setUtility(getUtility());
	
		return receipt;
	}
}
