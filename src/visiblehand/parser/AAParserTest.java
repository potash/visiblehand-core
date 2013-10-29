package visiblehand.parser;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

import visiblehand.entity.Airport;

public class AAParserTest extends MessageParserTest {
	@Test
	public void testGetDate() {
		assertEquals(AAParser.getDate(new Date(2012,2,1), "JAN", "01"), new Date(2013,1,1));
		//TODO add more tests
	}
	
	@Test
	public void testGetAirport() throws ParseException {
		Airport airport = AAParser.getAirport("RALEIGH DURHAM");
		assertEquals(airport.getId(), 3626);
		
		airport = AAParser.getAirport("WASHINGTON REAGAN");
		assertEquals(airport.getId(), 3520);
		
		airport = AAParser.getAirport("DALLAS FT WORTH");
		assertEquals(airport.getId(), 3670);
		
		airport = AAParser.getAirport("AUSTIN");
		assertEquals(airport.getId(), 3673);
		
		airport = AAParser.getAirport("NEW YORK JFK");
		assertEquals(airport.getId(), 3797);
		
		airport = AAParser.getAirport("NEW YORK LGA");
		assertEquals(airport.getId(), 3697);
		
		airport = AAParser.getAirport("CHICAGO OHARE");
		assertEquals(airport.getId(), 3830);
		
		airport = AAParser.getAirport("NEWARK");
		assertEquals(airport.getId(), 3494);
		
		airport = AAParser.getAirport("SALT LAKE CITY");
		assertEquals(airport.getId(), 3536);
		
		airport = AAParser.getAirport("LONDON HEATHROW");
		assertEquals(airport.getId(), 507);
		
		airport = AAParser.getAirport("BARCELONA");
		assertEquals(airport.getId(), 1218);
	}
}
