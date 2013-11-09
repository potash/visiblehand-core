package visiblehand.parser.air;

import java.io.IOException;
import java.text.ParseException;

import javax.mail.MessagingException;

import org.junit.Test;

import visiblehand.parser.MessageParserTest;

public class AirParserTest extends MessageParserTest {
	@Test
	public void testAAParser() throws ParseException, MessagingException,
			IOException {
		testParser(new AAParser());
	}

	@Test
	public void testContinentalParser() throws ParseException,
			MessagingException, IOException {
		testParser(new ContinentalParser());
	}

	@Test
	public void testDeltaParser() throws ParseException, MessagingException,
			IOException {
		testParser(new DeltaParser());
	}

	@Test
	public void testJetBlueParser() throws ParseException, MessagingException,
			IOException {
		testParser(new JetBlueParser());
	}

	@Test
	public void testSouthwestParser() throws ParseException,
			MessagingException, IOException {
		testParser(new SouthwestParser());
	}

	@Test
	public void testUnitedParser2() throws ParseException, MessagingException,
			IOException {
		testParser(new UnitedParser2());
	}

	@Test
	public void testUnitedParser() throws ParseException,
			MessagingException, IOException {
		testParser(new UnitedParser());
	}
}
