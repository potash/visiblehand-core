package visiblehand.parser;

import java.io.IOException;
import java.text.ParseException;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.BeforeClass;
import org.junit.Test;

import visiblehand.VisibleHand;

public class AirParserTest extends MessageParserTest {
	@BeforeClass
	public static void setup() {
		VisibleHand.loadData();
	}

	@Test
	public void testAAParser() throws ParseException, MessagingException, IOException {
		testAirParser(new AAParser());
	}
	
	@Test
	public void testContinentalParser() throws ParseException, MessagingException, IOException {
		testAirParser(new ContinentalParser());
	}
	
	@Test
	public void testDeltaParser() throws ParseException, MessagingException, IOException {
		testAirParser(new DeltaParser());
	}
	
	@Test
	public void testJetBlueParser() throws ParseException, MessagingException, IOException {
		testAirParser(new JetBlueParser());
	}
	
	@Test
	public void testSouthwestParser() throws ParseException, MessagingException, IOException {
		testAirParser(new SouthwestParser());
	}
	
	@Test
	public void testUnitedParser() throws ParseException, MessagingException, IOException {
		testAirParser(new UnitedParser());
	}
	
	@Test
	public void testUnitedParserOld() throws ParseException, MessagingException, IOException {
		testAirParser(new UnitedParserOld());
	}


	private void testAirParser(AirParser airParser) throws ParseException, MessagingException, IOException {
		for (Message message : getTestMessages(airParser)) {
			try {
				AirReceipt receipt = airParser.parse(message);
				//System.out.println(receipt.getFlights());
			} catch (Exception e) {
				System.out.println(MessageParser.getContent(message));
				e.printStackTrace();
			}
		}
	}
}
