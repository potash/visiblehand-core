package visiblehand.parser.utility;

import java.io.IOException;
import java.text.ParseException;

import javax.mail.MessagingException;

import org.junit.Test;

import visiblehand.parser.MessageParserTest;

public class UtilityParserTest extends MessageParserTest {
	@Test
	public void testComEdParser() throws ParseException, MessagingException,
			IOException {
		testParser(new ComEdParser());
	}

	@Test
	public void testPeoplesGasParser() throws ParseException,
			MessagingException, IOException {
		testParser(new PeoplesGasParser());
	}

}
