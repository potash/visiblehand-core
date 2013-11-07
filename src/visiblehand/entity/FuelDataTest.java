package visiblehand.entity;

import java.util.List;

import org.junit.Test;

import visiblehand.VisibleHandTest;

import com.avaje.ebean.Ebean;

public class FuelDataTest extends VisibleHandTest {

	@Test
	public void testCruiseBurnAscending() {
		List<FuelData> fuelDatas = Ebean.find(FuelData.class).findList();
		for (FuelData fuelData : fuelDatas) {
			int i = 1;
			while (i < 16 && fuelData.getCruise(i) != null) {
				if (fuelData.getCruise(i) < fuelData.getCruise(i-1)) {
					System.out.println("Problem with " + fuelData.toString());
				}
				i++;
			}
		}
	}

}
