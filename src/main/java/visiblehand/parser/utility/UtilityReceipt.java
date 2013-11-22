package visiblehand.parser.utility;

import lombok.Data;
import visiblehand.parser.Receipt;

import com.sun.mail.imap.Utility;

public @Data class UtilityReceipt extends Receipt {
	Utility utility;
	double cost;
}
