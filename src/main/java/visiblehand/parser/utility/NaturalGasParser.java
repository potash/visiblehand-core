package visiblehand.parser.utility;

import lombok.Getter;
import visiblehand.entity.utility.NaturalGasReceipt;
import visiblehand.entity.utility.Utility;
import visiblehand.parser.MessageParser;

public abstract class NaturalGasParser extends MessageParser<NaturalGasReceipt> {
	public abstract Utility getUtility();
	
	@Getter
	private final Class<NaturalGasReceipt> receiptClass = NaturalGasReceipt.class;
}