package visiblehand.entity.air;

import org.junit.Test;

import visiblehand.entity.air.Airport;

import com.avaje.ebean.Ebean;

public class AirportTest {

	@Test
	public void testGetDistance() {
		Airport JFK = Ebean.find(Airport.class, 3797),
				ORD = Ebean.find(Airport.class, 3830),
				HND = Ebean.find(Airport.class, 2359);
		System.out.println(Airport.getDistance(HND, JFK));
	}

}
