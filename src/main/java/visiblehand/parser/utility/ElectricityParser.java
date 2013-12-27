package visiblehand.parser.utility;

import lombok.Getter;
import visiblehand.entity.utility.ElectricityReceipt;
import visiblehand.entity.utility.Utility;
import visiblehand.parser.MessageParser;

public abstract class ElectricityParser extends MessageParser<ElectricityReceipt> {
	public abstract Utility getUtility();
	
	@Getter
	private final Class<ElectricityReceipt> receiptClass = ElectricityReceipt.class;
}