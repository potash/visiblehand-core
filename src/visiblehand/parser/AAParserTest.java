package visiblehand.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;

import org.junit.Test;

import visiblehand.entity.Airline;

import com.avaje.ebean.Ebean;

public class AAParserTest extends MessageParserTest {

	@Test
	public void testParse() throws FileNotFoundException, ParseException {
		System.out.println(Ebean.find(Airline.class, 24));
		File dir = new File("data/mail/AAParser/");
		AAParser parser = new AAParser();
		
		for (File file : dir.listFiles(hiddenFileFilter)) {
			Scanner scanner = new Scanner(file);
			String content = scanner.useDelimiter("\\Z").next();
			
			System.out.println(parser.getConfirmation(file.getName()));
			System.out.println(parser.getFlights(content, new Date(2012, 1, 11)));
			
			scanner.close();
		}
	}
	
	@Test
	public void testGetDate() {
		assertEquals(AAParser.getDate(new Date(2012,2,1), "JAN", "01"), new Date(2013,1,1));
		//TODO add more tests
	}

}
