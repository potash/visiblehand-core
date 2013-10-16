package visiblehand.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;

import org.junit.Test;

public class DeltaParserTest extends MessageParserTest {
	
	
	@Test
	public void testParse() throws FileNotFoundException, ParseException {
		DeltaParser parser = new DeltaParser();
		
		for (File file : getTestMessages()) {
			Scanner scanner = new Scanner(file);
			System.out.println(file.getName());
			String content = scanner.useDelimiter("\\Z").next();
			System.out.println(parser.getFlights(content));
			System.out.println(SouthwestParser.getConfirmation(file.getName()));
			scanner.close();
		}
	}

}
