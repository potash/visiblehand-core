package visiblehand;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import visiblehand.entity.ZipCode;
import visiblehand.entity.utility.ElectricityReceipt;
import visiblehand.entity.utility.NaturalGasReceipt;
import visiblehand.parser.MessageParserTest;
import visiblehand.parser.utility.NaturalGasParser;

public class NaturalGasTest extends EbeanTest {
	static final Logger logger = LoggerFactory.getLogger(NaturalGasTest.class);
	
	@Test
	public void test() throws FileNotFoundException, MessagingException, ParseException, IOException {
		int receipts = 0;
		for (NaturalGasParser parser : VisibleHand.getNaturalGasParsers()) {
			if (parser.isActive()) {
				for (Message message : MessageParserTest.getTestMessages(parser)) {
					NaturalGasReceipt receipt = parser.parse(message);
					receipt.getNaturalGas().setZipCode(ZipCode.find(60647));
					receipt.getNaturalGas().setSplit(2);
					System.out.println("Volume: " + receipt.getNaturalGas().getVolume() + " cubic feet");
					System.out.println("CO2: " + receipt.getNaturalGas().getCO2() + " lbs");
					receipts++;
				}
			}
		}
		System.out.println("Receipts: " + receipts);
		//System.out.println("Flights: " + flights.size());
		//VisibleHand.printStatistics(flights);
	}

}
