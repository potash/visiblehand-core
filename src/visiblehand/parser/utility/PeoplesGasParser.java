package visiblehand.parser.utility;

import java.io.IOException;
import java.text.ParseException;

import javax.mail.Message;
import javax.mail.MessagingException;

import lombok.Data;
import lombok.Getter;

import com.sun.mail.imap.Utility;

// United Airlines email receipt parser

public @Data class PeoplesGasParser extends UtilityParser {
	private final String fromString = "CustomerService@peoplesgas.ebillservice.net";
	private final String subjectString = "Your latest Peoples Gas e-Bill is now available";
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
