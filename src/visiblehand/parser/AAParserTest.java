package visiblehand.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;

import org.junit.Test;

import visiblehand.VisibleHand;

public class AAParserTest {

	@Test
	public void testParse() throws FileNotFoundException, ParseException {
		File dir = new File("data/mail/AAParser/");
		AAParser parser = new AAParser();
		VisibleHand.loadData();
		for (File file : dir.listFiles()) {
			System.out.println(file);
			Scanner scanner = new Scanner(file);
			String content = scanner.useDelimiter("\\Z").next();
			System.out.println(parser.parse(content, new Date(2012, 1, 11)));
			scanner.close();

		}
	}

}
