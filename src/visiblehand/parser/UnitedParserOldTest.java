package visiblehand.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Scanner;

import org.junit.Test;

public class UnitedParserOldTest {

	@Test
	public void testParse() throws FileNotFoundException, ParseException {
		File dir = new File("data/mail/UnitedParserOld/");
		for (File file : dir.listFiles()) {
			Scanner scanner = new Scanner(file);
			String content = scanner.useDelimiter("\\Z").next();
			System.out.println(new UnitedParserOld().parse(content));
			scanner.close();
		}
	}

}
