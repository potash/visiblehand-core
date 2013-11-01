package visiblehand.parser.utility;

import visiblehand.parser.MessageParserTest;
import visiblehand.parser.Receipt;
import visiblehand.parser.MessageParserTest.TestView;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonView;
import com.sun.mail.imap.Utility;

public @Data class UtilityReceipt extends Receipt {
	@JsonView(MessageParserTest.TestView.class)
	Utility utility;
	@JsonView(MessageParserTest.TestView.class)
	double cost;
}
