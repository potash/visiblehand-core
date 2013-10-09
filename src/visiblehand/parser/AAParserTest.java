package visiblehand.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Scanner;

import org.junit.Test;

public class AAParserTest {

	@Test
	public void testParse() throws FileNotFoundException {
		String content = new Scanner(new File("data/mail/aa.txt")).useDelimiter("\\Z").next();
		new AAParser().parse(content, new Date(2012, 1, 11));
		
	}

}
