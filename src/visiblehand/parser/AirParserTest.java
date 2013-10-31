package visiblehand.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AirParserTest extends MessageParserTest {
	// @BeforeClass
	// public static void setup() {
	// VisibleHand.loadData();
	// }

	@Test
	public void testAAParser() throws ParseException, MessagingException,
			IOException {
		testAirParser(new AAParser());
	}

	@Test
	public void testContinentalParser() throws ParseException,
			MessagingException, IOException {
		testAirParser(new ContinentalParser());
	}

	@Test
	public void testDeltaParser() throws ParseException, MessagingException,
			IOException {
		testAirParser(new DeltaParser());
	}

	@Test
	public void testJetBlueParser() throws ParseException, MessagingException,
			IOException {
		testAirParser(new JetBlueParser());
	}

	@Test
	public void testSouthwestParser() throws ParseException,
			MessagingException, IOException {
		testAirParser(new SouthwestParser());
	}

	@Test
	public void testUnitedParser() throws ParseException, MessagingException,
			IOException {
		testAirParser(new UnitedParser());
	}

	@Test
	public void testUnitedParserOld() throws ParseException,
			MessagingException, IOException {
		testAirParser(new UnitedParserOld());
	}

	private void testAirParser(AirParser airParser) throws ParseException,
			MessagingException, IOException {
		Message[] messages = getTestMessages(airParser);
		AirReceipt[] receipts = getTestReceipts(airParser);
		for (int i = 0; i < messages.length; i++) {
			AirReceipt receipt = airParser.parse(messages[i]);
			assertEquals(receipt, receipts[i]);
		}
	}

	// return (alphabetical-order) array of serialized AirReceipts
	// corresponding to expected parse results
	protected AirReceipt[] getTestReceipts(AirParser parser)
			throws JsonParseException, JsonMappingException, IOException {
		File dir = new File(testDirectoryName + parser.getClass().getSimpleName());
		File[] files = dir.listFiles(resultFileFilter);
		Arrays.sort(files);
		AirReceipt[] receipts = new AirReceipt[files.length];
		ObjectMapper mapper = new ObjectMapper();
		for (int i = 0; i < files.length; i++) {
			receipts[i] = mapper.readValue(files[i], AirReceipt.class);
		}
		return receipts;
	}
}
