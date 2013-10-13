package visiblehand.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Scanner;

import javax.swing.text.html.HTMLDocument.HTMLReader.HiddenAction;

import org.junit.Test;

import visiblehand.VisibleHand;

public class UnitedParserOldTest extends MessageParserTest {

	@Test
	public void testParse() throws FileNotFoundException, ParseException {
		File dir = new File("data/mail/UnitedParserOld/");
		VisibleHand.loadData();
		for (File file : dir.listFiles(hiddenFileFilter)) {
			Scanner scanner = new Scanner(file);
			System.out.println(file.getName());
			String content = scanner.useDelimiter("\\Z").next();
			System.out.println(new UnitedParserOld().getFlights(content));
			System.out.println(new UnitedParserOld().getConfirmation(content));
			scanner.close();
		}
	}

}
