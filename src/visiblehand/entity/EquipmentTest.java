package visiblehand.entity;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;

public class EquipmentTest {

	@Test
	public void test() {
		fail("This should test fueldata coverage of the equipment dataset, weighted by route?");
		String sql = "select e, count(*) c from (select unnest (equipment) e from route) t group by e order by count(*) desc";

		// System.out.println(getId());
		SqlQuery sqlQuery = Ebean.createSqlQuery(sql);

		List<SqlRow> rows = sqlQuery.findList();
		int count = 0, route_count = 0;
		for (SqlRow row : rows) {
			String iata = row.getString("e");
			Equipment eq = Ebean.find(Equipment.class).where().eq("iata", iata)
					.findUnique();

			if (eq != null && eq.getFuelData().size() == 0) {
				int aems = 0;
				List<Equipment> relatives = eq.getParents();
				relatives.addAll(eq.getChildren());
				for (Equipment e : relatives) {
					aems += e.getFuelData().size();
				}
				if (aems == 0
						&& !(eq.getSimilar() != null && eq.getSimilar()
								.getFuelData().size() > 0)) {
					count++;
					int c = row.getInteger("c");
					route_count += c;
					System.out.println(iata + ": " + c);
					System.out.println(eq.getParents().size());
					System.out.println(eq.getChildren().size());
				}
			}
		}
		System.out.println(count + " of " + rows.size() + " equipment codes = "
				+ (double) count / rows.size() + "%");
		int num_routes = Ebean.find(Route.class).findRowCount();
		System.out.println(route_count + " of " + num_routes + " routes = "
				+ (double) route_count / num_routes + "%");

	}

}
