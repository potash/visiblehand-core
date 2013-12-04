package visiblehand;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import visiblehand.entity.AirReceipt;
import visiblehand.entity.Flight;
import visiblehand.parser.MessageParserTest;
import visiblehand.parser.air.AirParser;

public class VisibleHandTest extends EbeanTest {
	static final Logger logger = LoggerFactory.getLogger(VisibleHandTest.class);
	
	@Test
	public void test() throws FileNotFoundException, MessagingException, ParseException, IOException {
		List<Flight> flights = new ArrayList<Flight>();
		int receipts = 0;
		for (AirParser parser : VisibleHand.airParsers) {
			if (parser.isActive()) {
				for (Message message : MessageParserTest.getTestMessages(parser)) {
					AirReceipt receipt = parser.parse(message);
					if (receipt.getFlights().size() == 0) {
						fail("No flights parsed from receipt!");
					} else {
						flights.addAll(parser.parse(message).getFlights());
					}
					receipts++;
				}
			}
		}
		System.out.println("Receipts: " + receipts);
		System.out.println("Flights: " + flights.size());
		VisibleHand.printStatistics(flights);
	}

}
