package visiblehand.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avaje.ebean.Ebean;

// Data sources: http://realbigtree.com/resources/iataicao-aircraft-codes/
//				 http://www.avcodes.co.uk/acrtypes.asp

//@Entity
//@ToString(of = { "id", "IATA", "name" })
//@EqualsAndHashCode(of={"id"})
public @Data class Equipment {
	static final Logger logger = LoggerFactory.getLogger(Equipment.class);
	
	@Id
	private Long id;
	private String IATA;
	private String ICAO;
	private String name;
	private Character WTC;

	@ManyToOne
	@JoinColumn(name = "similar_id")
	private Equipment similar;

	@Transient
	@Getter(lazy=true)
	private final List<Equipment> children = children();
	
	private List<Equipment> children() {
		List<EquipmentAggregate> aggregates = Ebean.find(EquipmentAggregate.class).where().eq("parentIATA", getIATA()).findList();
		List<String> IATAs = new ArrayList<String>(aggregates.size());
		for(EquipmentAggregate aggregate : aggregates) {
			IATAs.add(aggregate.getChildIATA());
		}
		return Equipment.findByIATA(IATAs);
	}
	
	@Transient
	@Getter(lazy=true)
	private final List<Equipment> parents = parent();
	
	private List<Equipment> parent() {
		List<EquipmentAggregate> aggregates = Ebean.find(EquipmentAggregate.class).where().eq("childIATA", getIATA()).findList();
		List<String> IATAs = new ArrayList<String>(aggregates.size());
		for(EquipmentAggregate aggregate : aggregates) {
			IATAs.add(aggregate.getParentIATA());
		}
		return Equipment.findByIATA(IATAs);
	}

	@Transient
	@Getter(lazy = true)
	private final List<Equipment> siblings = siblings();

	private List<Equipment> siblings() {
		List<Long> ids = new ArrayList<Long>();
		for (Equipment parent : getParents()) {
			for (Equipment child : parent.getChildren()) {
				ids.add(child.getId());
			}
		}
		return Ebean.find(Equipment.class).where().in("id", ids)
				.ne("id", getId()).findList();
	}

	@Transient
	@Getter(lazy = true)
	private final List<FuelData> fuelData = Ebean.find(FuelData.class).where()
			.eq("icao", getICAO()).findList();

	// exact fuel data matches
	@Transient
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

	public static Equipment findByName(String name) {
		name = name.replaceAll("CRJ", "Canadair");
		name = name.replaceAll("ERJ", "Embraer");
		name = name.replaceAll("Q(1|2|3|4)", "DHC \\1");
		name = name.replaceAll("(^|$|\\s|-|/)", "%");
		List<Equipment> e = Ebean.find(Equipment.class)
				.where().ilike("name", name)
				.findList();
		if (e.size() > 0) {
			if (e.size() > 1) {
				logger.warn(" More than one equipment match: " + name);
			}
			return e.get(0);
		} else {
			logger.warn("No equipment match: " + name);
			return null;
		}
	}
	
	public static List<Equipment> findByIATA(List<String> IATAs) {
		return Ebean.find(Equipment.class).where().in("IATA", IATAs).findList();
	}
}