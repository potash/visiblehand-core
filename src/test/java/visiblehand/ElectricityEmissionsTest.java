package visiblehand;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import visiblehand.entity.utility.ElectricityReceipt;
import visiblehand.parser.MessageParserTest;
import visiblehand.parser.utility.ElectricityParser;

public class ElectricityEmissionsTest extends EbeanTest {
	static final Logger logger = LoggerFactory.getLogger(ElectricityEmissionsTest.class);
	
	@Test
	public void test() throws FileNotFoundException, MessagingException, ParseException, IOException {
		//List<Flight> flights = new ArrayList<Flight>();
		int receipts = 0;
		for (ElectricityParser parser : VisibleHand.electricParsers) {
			if (parser.isActive()) {
				for (Message message : MessageParserTest.getTestMessages(parser)) {
					ElectricityReceipt receipt = parser.parse(message);
					receipts++;
				}
			}
		}
		System.out.println("Receipts: " + receipts);
		//System.out.println("Flights: " + flights.size());
		//VisibleHand.printStatistics(flights);
	}

}
