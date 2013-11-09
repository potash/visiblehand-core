package visiblehand.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import lombok.Getter;
import visiblehand.EbeanTest;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MessageParserTest extends EbeanTest {
	// Jackson view for serializing test results
	public static class TestView {
	}

	public static ObjectWriter getTestWriter() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.setSerializationInclusion(Include.NON_NULL);
		ObjectWriter writer = mapper
				.writerWithView(MessageParserTest.TestView.class);
		return writer;
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
	public static final String testDirectory = "data/test/";
	
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

	public void testParser(MessageParser parser) throws ParseException,
			MessagingException, IOException {
		Message[] messages = getTestMessages(parser);
		Receipt[] receipts = getTestReceipts(parser);
		ObjectWriter writer = getTestWriter();
		for (int i = 0; i < messages.length; i++) {
			Receipt receipt = parser.parse(messages[i]);
			System.out.println(writer.writeValueAsString(receipt));
			assertEquals(writer.writeValueAsString(receipt),
					writer.writeValueAsString(receipts[i]));
		}
	}

	// return (alphabetical-order) array of serialized AirReceipts
	// corresponding to expected parse results
	public Receipt[] getTestReceipts(MessageParser parser)
			throws JsonParseException, JsonMappingException, IOException {
		File dir = new File(getTestDirectory(parser));
		File[] files = dir.listFiles(jsonFileFilter);
		Arrays.sort(files);
		Receipt[] receipts = new Receipt[files.length];
		ObjectMapper mapper = new ObjectMapper();
		for (int i = 0; i < files.length; i++) {
			receipts[i] = mapper.readValue(files[i], parser.getReceiptClass());
		}
		return receipts;
	}
}
