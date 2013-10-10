package visiblehand.entity;

import org.junit.Test;

import com.avaje.ebean.Ebean;
import static org.junit.Assert.*;


public class RouteTest {

	@Test
	public void testFuelBurn() {
		fail("This should test the coverage of routes by fuel burn data");
		Airline aa = Ebean.find(Airline.class, 24);
//		List<Route> routes = Ebean.find(Route.class).where().eq("airline", aa).where().eq("codeshare", false)
//				.findList();
//		int count = 0;
//		for (Route r : routes) {
//			DescriptiveStatistics burn = r.getFuelBurn();
//			if (burn.getMean() == Double.NaN || burn.getValues().length == 0) {
//				count++;
//				System.out.println(r);
//			}
//		}
//		System.out.println(count + " / " + routes.size() + " = " + ((double)count)/routes.size());
//		FuelData cr7 = Ebean.find(FuelData.class, 79);
//		FuelData cr9 = Ebean.find(FuelData.class, 43);
//		System.out.println(cr7);
//		System.out.println(cr7.getFuelBurn(139)); /// .81 / 3.785);
		System.out.println(Ebean.find(Route.class, 4510).getFuelBurn());
	}

}
