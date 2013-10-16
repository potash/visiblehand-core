package visiblehand.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Scanner;

import org.junit.Test;

public class UnitedParserTest extends MessageParserTest {

	@Test
	public void testParse() throws FileNotFoundException, ParseException {
		UnitedParser parser = new UnitedParser();
		
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
