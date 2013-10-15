package visiblehand.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;

import org.junit.Test;

public class SouthwestParserTest extends MessageParserTest {
	
	
	@Test
	public void testParse() throws FileNotFoundException, ParseException {
		File dir = new File("data/mail/SouthwestParser/");
		SouthwestParser parser = new SouthwestParser();
		for (File file : dir.listFiles(hiddenFileFilter)) {
			Scanner scanner = new Scanner(file);
			System.out.println(file.getName());
			String content = scanner.useDelimiter("\\Z").next();
			System.out.println(parser.getFlights(content, new Date(12,11,2012)));
			System.out.println(SouthwestParser.getConfirmation(file.getName()));
			scanner.close();
		}
	}

}
