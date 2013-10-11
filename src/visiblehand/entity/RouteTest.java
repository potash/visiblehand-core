package visiblehand.entity;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import visiblehand.VisibleHand;

import com.avaje.ebean.Ebean;


public class RouteTest {

	
	@BeforeClass
	public static void setUp() throws Exception {
		VisibleHand.loadData();
	}
	
	@Test
	public void testGetEquipment() {
		Route route = Ebean.find(Route.class, 4510);
		assertEquals(route.getEquipment().size(), 4);
	}
	
	@Test
	public void testFuelBurn() {
		assertTrue(Ebean.find(Route.class, 4510).getFuelBurn().getMean() != Double.NaN);
	}

}
