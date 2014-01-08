package visiblehand.entity.air;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import visiblehand.EbeanTest;
import visiblehand.VisibleHand;
import visiblehand.entity.air.Airline;
import visiblehand.entity.air.Equipment;
import visiblehand.entity.air.Route;
import visiblehand.parser.air.AirParser;

import com.avaje.ebean.Ebean;


public class RouteTest extends EbeanTest {

	@Test
	public void test() {
		List<Airline> airlines = new ArrayList<Airline>();
		for (AirParser parser : VisibleHand.getAirParsers()) {
			airlines.add(parser.getAirline());
		}
		List<Route> routes = Ebean.find(Route.class)
				.where().in("airline", airlines)
				.findList();
		int count = 0; // the number of equipment with fuel data;
		for (Route route : routes) {
			boolean hasData = false;
			for (Equipment e : route.getEquipment()) {
				if (e.getAllFuelData().size() > 0) {
					hasData = true; break;
				}
			}
			if (hasData) {
				count++;
			} else {
				System.out.println(route);
			}
		}
		System.out.println("Fuel data exists for " + count + " / "
				+ routes.size() + " = " + (double) count / routes.size()*100
				+ "% of routes");
	}

}
