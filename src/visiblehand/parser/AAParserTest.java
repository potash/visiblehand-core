package visiblehand.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;

import javax.swing.text.html.HTMLDocument.HTMLReader.HiddenAction;

import org.junit.Test;

import visiblehand.VisibleHand;

public class AAParserTest extends MessageParserTest{

	@Test
	public void testParse() throws FileNotFoundException, ParseException {
		File dir = new File("data/mail/AAParser/");
		AAParser parser = new AAParser();
		VisibleHand.loadData();
		for (File file : dir.listFiles(hiddenFileFilter)) {
			System.out.println(file.getName().substring(22,28));
			Scanner scanner = new Scanner(file);
			String content = scanner.useDelimiter("\\Z").next();
			System.out.println(parser.getFlights(content, new Date(2012, 1, 11)));
			scanner.close();
		}
	}

}
