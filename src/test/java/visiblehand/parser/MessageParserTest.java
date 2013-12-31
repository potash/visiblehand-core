package visiblehand.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Properties;
import java.util.TimeZone;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import lombok.Getter;

import org.junit.Test;

import visiblehand.EbeanTest;
import visiblehand.entity.Receipt;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MessageParserTest extends EbeanTest {
	
	@Test
	public void testSetYear() throws ParseException {
		DateFormat format = new SimpleDateFormat("ddMMMyyyy");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		assertEquals(MessageParser.getNextDate("ddMMM", "29FEB", format.parse("01NOV2011")), 
				format.parse("29FEB2012"));
	}

	@Getter(lazy=true)
	private final static ObjectMapper testMapper = testMapper();
	
	private static final ObjectMapper testMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new TestModule());
		return mapper;
	}

	public static final FilenameFilter msgFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.matches("\\d+.msg");
		}
	};

	public static final FilenameFilter jsonFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.matches("\\d+.json");
		}
	};

	@Getter
	public static final String testDirectory = MessageParserTest.class.getResource("/data/parser").getFile().toString();
	
	public static String getTestDirectory(MessageParser parser) {
		return getTestDirectory() + "/" + parser.getClass().getSimpleName();
	}

	// returns alphabetical-order list of test messages
	// corresponding to the given parser
	public static Message[] getTestMessages(MessageParser parser)
			throws FileNotFoundException, MessagingException {
		File dir = new File(getTestDirectory(parser));
		File[] files = dir.listFiles(msgFileFilter);
		Arrays.sort(files);
		Message[] messages = new Message[files.length];
		Session session = Session.getDefaultInstance(new Properties());
		for (int i = 0; i < files.length; i++) {
			messages[i] = new MimeMessage(session,
					new FileInputStream(files[i]));
			assertTrue(files[i].toString() + " does not match search term.", parser.getSearchTerm().match(messages[i]));
		}
		return messages;
	}

	public static void testParser(MessageParser parser) throws ParseException,
			MessagingException, IOException {
		Message[] messages = getTestMessages(parser);
		Receipt[] receipts = getTestReceipts(parser);
		ObjectMapper writer = getTestMapper();
		for (int i = 0; i < messages.length; i++) {
			Receipt receipt = parser.parse(messages[i]);
			System.out.println(writer.writeValueAsString(receipt));
			assertEquals(writer.writeValueAsString(receipts[i]),
					writer.writeValueAsString(receipt));
		}
	}

	// return (alphabetical-order) array of serialized AirReceipts
	// corresponding to expected parse results
	public static Receipt[] getTestReceipts(MessageParser<Receipt> parser)
			throws JsonParseException, JsonMappingException, IOException {
		File dir = new File(getTestDirectory(parser));
		File[] files = dir.listFiles(jsonFileFilter);
		Arrays.sort(files);
		Receipt[] receipts = new Receipt[files.length];
		ObjectMapper mapper = getTestMapper();
		for (int i = 0; i < files.length; i++) {
			receipts[i] = mapper.readValue(files[i], parser.getReceiptClass());
		}
		return receipts;
	}
}
