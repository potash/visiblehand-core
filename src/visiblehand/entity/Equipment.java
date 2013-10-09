package visiblehand.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;

// Data sources: http://realbigtree.com/resources/iataicao-aircraft-codes/
//				 http://www.avcodes.co.uk/acrtypes.asp

@Entity
@ToString(exclude = { "fuelData", "similar", "children", "parents" })
@EqualsAndHashCode(exclude = { "fuelData", "similar", "children", "parents" })
public @Data
class Equipment {
	@Id
	private int id;
	private String ICAO;
	private String IATA;
	private String name;
	private Character WTC;
	@ManyToOne
	@JoinColumn(name = "similar_id")
	private Equipment similar;

	@ManyToMany
	@JoinTable(name = "equipment_aggregate", joinColumns = { @JoinColumn(name = "parent_id") }, inverseJoinColumns = { @JoinColumn(name = "child_id") })
	private List<Equipment> children;

	@ManyToMany
	@JoinTable(name = "equipment_aggregate", inverseJoinColumns = { @JoinColumn(name = "parent_id") }, joinColumns = { @JoinColumn(name = "child_id") })
	private List<Equipment> parents;

	// TODO use parents, children, similar
	@Transient
	@Getter(lazy = true)
	private final List<FuelData> fuelData = fuelData();

	private List<FuelData> fuelData() {
		List<FuelData> fuelData = Ebean.find(FuelData.class).where().eq("icao", getICAO())
				.findList();

		if (fuelData.size() == 0) {
			if (getChildren() != null) {
				for (Equipment child : getChildren()) {
					fuelData.addAll(child.getFuelData());
				}
			}
		}
		
		if (fuelData.size() == 0 && getSimilar() != null) {
			fuelData.addAll(getSimilar().getFuelData());
		}
		return fuelData;
	}

	public static List<Object> getKeysFromValue(Map<?, ?> hm, Object value) {
		List<Object> list = new ArrayList<Object>();
		for (Object o : hm.keySet()) {
			if (hm.get(o).equals(value)) {
				list.add(o);
			}
		}
		return list;
	}

	public static void main(String[] args) {
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