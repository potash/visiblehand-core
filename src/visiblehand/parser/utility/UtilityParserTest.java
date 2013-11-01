package visiblehand.parser.utility;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.Test;

import visiblehand.parser.MessageParserTest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UtilityParserTest extends MessageParserTest {
	@Test
	public void testComEdParser() throws ParseException, MessagingException,
			IOException {
		testUtilityParser(new ComEdParser());
	}

	@Test
	public void testPeoplesGasParser() throws ParseException,
			MessagingException, IOException {
		testUtilityParser(new PeoplesGasParser());
	}


	private void testUtilityParser(UtilityParser utilParser) throws ParseException,
			MessagingException, IOException {
		Message[] messages = getTestMessages(utilParser);
		UtilityReceipt[] receipts = getTestReceipts(utilParser);
		for (int i = 0; i < messages.length; i++) {
			UtilityReceipt receipt = utilParser.parse(messages[i]);
			System.out.println(receipt);
			System.out.println(receipts[i]);
			assertEquals(receipt, receipts[i]);
		}
	}

	// return (alphabetical-order) array of serialized UtilityReceipts
	// corresponding to expected parse results
	protected UtilityReceipt[] getTestReceipts(UtilityParser parser)
			throws JsonParseException, JsonMappingException, IOException {
		File dir = new File(testDirectoryName + parser.getClass().getSimpleName());
		File[] files = dir.listFiles(jsonFileFilter);
		Arrays.sort(files);
		UtilityReceipt[] receipts = new UtilityReceipt[files.length];
		ObjectMapper mapper = new ObjectMapper();
		for (int i = 0; i < files.length; i++) {
			receipts[i] = mapper.readValue(files[i], UtilityReceipt.class);
		}
		return receipts;
	}
}
