package visiblehand.entity;

import java.util.ArrayList;
import java.util.List;

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
import visiblehand.parser.MessageParserTest;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.annotation.JsonView;

// Data sources: http://realbigtree.com/resources/iataicao-aircraft-codes/
//				 http://www.avcodes.co.uk/acrtypes.asp

@Entity
@ToString(of = { "id", "IATA", "name" })
@EqualsAndHashCode(of={"id"})
public @Data
class Equipment {
	@Id
	private Integer id;
	@JsonView(MessageParserTest.TestView.class)
	private String IATA;
	private String ICAO;
	private String name;
	private Character WTC;

	@ManyToOne
	@JoinColumn(name = "similar_id")
	private Equipment similar;

	@ManyToMany
	@JoinTable(name = "equipment_aggregate", joinColumns = { @JoinColumn(name = "parent_id") }, inverseJoinColumns = { @JoinColumn(name = "child_id") })
	private List<Equipment> children;

	@ManyToMany
	@JoinTable(name = "equipment_aggregate", joinColumns = { @JoinColumn(name = "child_id") }, inverseJoinColumns = { @JoinColumn(name = "parent_id") })
	private List<Equipment> parents;

	@Transient
	@Getter(lazy = true)
	private final List<Equipment> siblings = siblings();

	private List<Equipment> siblings() {
		List<Integer> ids = new ArrayList<Integer>();
		for (Equipment parent : getParents()) {
			for (Equipment child : parent.getChildren()) {
				ids.add(child.getId());
			}
		}
		return Ebean.find(Equipment.class).where().in("id", ids)
				.ne("id", getId()).findList();
	}

	@Getter(lazy = true)
	private final List<FuelData> fuelData = Ebean.find(FuelData.class).where()
			.eq("icao", getICAO()).findList();

	// exact fuel data matches
	@Getter(lazy = true)
	private final List<FuelData> allFuelData = allFuelData();

	// rough fuel data matches (children, similar, siblings)
	private List<FuelData> allFuelData() {
		List<FuelData> allFuelData = getFuelData();

		if (allFuelData.size() == 0 && getChildren() != null) {
			for (Equipment child : getChildren()) {
				allFuelData.addAll(child.getFuelData());
			}
		}

		if (allFuelData.size() == 0 && getSimilar() != null) {
			allFuelData.addAll(getSimilar().getFuelData());
		}

		if (allFuelData.size() == 0 && getSiblings() != null) {
			for (Equipment sibling : getSiblings()) {
				allFuelData.addAll(sibling.getFuelData());
			}
		}

		return allFuelData;
	}
}