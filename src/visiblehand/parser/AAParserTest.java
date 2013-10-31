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
		AAParser parser = new AAParser();
		Airport airport = parser.getAirport("RALEIGH DURHAM");
		assertEquals(airport.getId(), 3626);
		
		airport = parser.getAirport("ALBUQUERQUE");
		assertEquals(airport.getId(), 4019);
		
		airport = parser.getAirport("LOS ANGELES");
		assertEquals(airport.getId(), 3484);
		
		airport = parser.getAirport("WASHINGTON REAGAN");
		assertEquals(airport.getId(), 3520);
		
		airport = parser.getAirport("DALLAS FT WORTH");
		assertEquals(airport.getId(), 3670);
		
		airport = parser.getAirport("AUSTIN");
		assertEquals(airport.getId(), 3673);
		
		airport = parser.getAirport("NEW YORK JFK");
		assertEquals(airport.getId(), 3797);
		
		airport = parser.getAirport("NEW YORK LGA");
		assertEquals(airport.getId(), 3697);
		
		airport = parser.getAirport("CHICAGO OHARE");
		assertEquals(airport.getId(), 3830);
		
		airport = parser.getAirport("NEWARK");
		assertEquals(airport.getId(), 3494);
		
		airport = parser.getAirport("SALT LAKE CITY");
		assertEquals(airport.getId(), 3536);
		
		airport = parser.getAirport("LONDON HEATHROW");
		assertEquals(airport.getId(), 507);
		
		airport = parser.getAirport("BARCELONA");
		assertEquals(airport.getId(), 1218);
		
		airport = parser.getAirport("SAN JUAN");
		assertEquals(airport.getId(), 2890);
	}
}
