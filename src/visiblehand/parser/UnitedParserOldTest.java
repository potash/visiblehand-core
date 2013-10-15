package visiblehand.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Scanner;

import org.junit.BeforeClass;
import org.junit.Test;

import visiblehand.VisibleHand;

public class UnitedParserOldTest extends MessageParserTest {
	
	
	@Test
	public void testParse() throws FileNotFoundException, ParseException {
		File dir = new File("data/mail/UnitedParserOld/");
		UnitedParserOld parser = new UnitedParserOld();
		
		for (File file : dir.listFiles(hiddenFileFilter)) {
			Scanner scanner = new Scanner(file);
			System.out.println(file.getName());
			String content = scanner.useDelimiter("\\Z").next();
			
			System.out.println(parser.getFlights(content));
			System.out.println(UnitedParserOld.getConfirmation(content));
			
			scanner.close();
		}
	}

}
