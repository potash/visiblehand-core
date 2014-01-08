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
import visiblehand.parser.MessageParserTest;
import visiblehand.parser.utility.ElectricityParser;

public class ElectricityTest extends EbeanTest {
	static final Logger logger = LoggerFactory.getLogger(ElectricityTest.class);
	
	@Test
	public void test() throws FileNotFoundException, MessagingException, ParseException, IOException {
		//List<Flight> flights = new ArrayList<Flight>();
		int receipts = 0;
		for (ElectricityParser parser : VisibleHand.getElectricityParsers()) {
			if (parser.isActive()) {
				for (Message message : MessageParserTest.getTestMessages(parser)) {
					System.out.println(parser.getUtility());
					ElectricityReceipt receipt = parser.parse(message);
					receipt.getElectricity().setZipCode(ZipCode.find(60647));
					receipt.getElectricity().setSplit(2);
					System.out.println("Energy: " + receipt.getElectricity().getEnergy() + " kWh");
					System.out.println("CO2: " + receipt.getElectricity().getCO2() + " lbs");
					receipts++;
				}
			}
		}
		System.out.println("Receipts: " + receipts);
		//System.out.println("Flights: " + flights.size());
		//VisibleHand.printStatistics(flights);
	}

}
