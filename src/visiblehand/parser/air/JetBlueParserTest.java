package visiblehand.parser.air;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

import visiblehand.entity.Airport;
import visiblehand.parser.MessageParserTest;

import com.avaje.ebean.Ebean;

public class JetBlueParserTest extends MessageParserTest {
	JetBlueParser parser = new JetBlueParser();
	
	@Test
	public void testGetAirportCity() throws ParseException {
		assertEquals(Ebean.find(Airport.class, 3448), 
				parser.getAirport("BOSTON, MA"));
		
		assertEquals(Ebean.find(Airport.class, 3469), 
				parser.getAirport("SAN FRANCISCO, CA"));
		
		assertEquals(Ebean.find(Airport.class, 3820), 
				parser.getAirport("BUFFALO, NY"));
		
		assertEquals(Ebean.find(Airport.class, 3673), 
				parser.getAirport("AUSTIN, TX"));

		assertEquals(Ebean.find(Airport.class, 3494), 
				parser.getAirport("NEWARK, NJ"));
		
		assertEquals(Ebean.find(Airport.class, 3711), 
				parser.getAirport("BURLINGTON VT, VT"));
		
		assertEquals(Ebean.find(Airport.class, 3748), 
				parser.getAirport("SAN JOSE CA, CA"));
	}
	
	@Test
	public void testGetAirportLevenshtein() throws ParseException {
		assertEquals(Ebean.find(Airport.class, 3670), 
				parser.getAirport("DALLAS FT WORTH, TX"));
	
		assertEquals(Ebean.find(Airport.class, 3830), 
				parser.getAirport("CHICAGO OHARE, IL"));
	}
	
	@Test
	public void testGetAirportCode() throws ParseException {
		//assertEquals(Ebean.find(Airport.class, 3797), 
		//		parser.getAirport("NEW YORK JFK, NY"));
	}
	
	@Test
	public void testGetAirportInternational() throws ParseException {
		assertEquals(Ebean.find(Airport.class, 1762), 
				parser.getAirport("SANTO DOMINGO, DOMINICAN REP"));
		
		assertEquals(Ebean.find(Airport.class, 2890), 
				parser.getAirport("SAN JUAN PR, PUERTO RICO"));
	}

}
