package visiblehand.parser.air;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Test;

import visiblehand.EbeanTest;
import visiblehand.entity.air.Airport;

public class AAParserTest extends EbeanTest {
	@Test
	public void testGetDate() throws ParseException {
		DateFormat format = new SimpleDateFormat("ddMMMyyyy hh:mm aa");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		assertEquals(AAParser.getDate(
				format.parse("11NOV2012 12:00 am"), "01JAN", "1:23 AM"), 
				format.parse("01JAN2013 1:23 AM"));
		
		assertEquals(AAParser.getDate(
				format.parse("28FEB2012 12:00 am"), "16MAR", "4:15 PM"), 
				format.parse("16MAR2012 4:15 PM"));
	}
	
	@Test
	public void testGetAirport() throws ParseException {
		AAParser parser = new AAParser();
		Airport airport = parser.getAirport("RALEIGH DURHAM");
		assertEquals(airport.getId().longValue(), 3626);
		
		airport = parser.getAirport("ALBUQUERQUE");
		assertEquals(airport.getId().longValue(), 4019);
		
		airport = parser.getAirport("LOS ANGELES");
		assertEquals(airport.getId().longValue(), 3484);
		
		airport = parser.getAirport("WASHINGTON REAGAN");
		assertEquals(airport.getId().longValue(), 3520);
		
		airport = parser.getAirport("DALLAS FT WORTH");
		assertEquals(airport.getId().longValue(), 3670);
		
		airport = parser.getAirport("AUSTIN");
		assertEquals(airport.getId().longValue(), 3673);
		
		airport = parser.getAirport("NEW YORK JFK");
		assertEquals(airport.getId().longValue(), 3797);
		
		airport = parser.getAirport("NEW YORK LGA");
		assertEquals(airport.getId().longValue(), 3697);
		
		airport = parser.getAirport("CHICAGO OHARE");
		assertEquals(airport.getId().longValue(), 3830);
		
		airport = parser.getAirport("NEWARK");
		assertEquals(airport.getId().longValue(), 3494);
		
		airport = parser.getAirport("SALT LAKE CITY");
		assertEquals(airport.getId().longValue(), 3536);
		
		airport = parser.getAirport("LONDON HEATHROW");
		assertEquals(airport.getId().longValue(), 507);
		
		airport = parser.getAirport("BARCELONA");
		assertEquals(airport.getId().longValue(), 1218);
		
		airport = parser.getAirport("SAN JUAN       PR");
		assertEquals(airport.getId().longValue(), 2890);
		
		airport = parser.getAirport("SARASOTA/BRADENTN");
		assertEquals(airport.getId().longValue(), 4067);
		
		airport = parser.getAirport("HONOLULU");
		assertEquals(airport.getId().longValue(), 3728);
		
		airport = parser.getAirport("LAS VEGAS");
		assertEquals(airport.getId().longValue(), 3877);
		
		airport = parser.getAirport("SAN FRANCISCO");
		assertEquals(airport.getId().longValue(), 3469);
		
		airport = parser.getAirport("ORLANDO INTL");
		assertEquals(airport.getId().longValue(), 3878);
		
		airport = parser.getAirport("CHAMPAIGN");
		assertEquals(airport.getId().longValue(), 4049);
		
		airport = parser.getAirport("BOSTON");
		assertEquals(airport.getId().longValue(), 3448);
		
		airport = parser.getAirport("MIAMI INTERNTNL");
		assertEquals(airport.getId().longValue(), 3576);
		
		airport = parser.getAirport("SANTA ANA");
		assertEquals(airport.getId().longValue(), 3867);
		
		airport = parser.getAirport("MONTREAL TRUDEAU");
		assertEquals(airport.getId().longValue(), 146);
	}
}
