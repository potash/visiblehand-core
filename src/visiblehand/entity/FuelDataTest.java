package visiblehand.entity;

import static org.junit.Assert.*;

import org.junit.Test;

import com.avaje.ebean.Ebean;

public class FuelDataTest {

	@Test
	public void testGetCruiseBurn() {
		fail("This should be a sanit check on getCruiseBurn()");
		for (int i = 1; i <= 75; i++) {
			FuelData fd = Ebean.find(FuelData.class, i);
			System.out.println(i);
		}
	}

}
