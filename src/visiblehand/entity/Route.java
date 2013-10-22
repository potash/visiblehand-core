package visiblehand.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.avaje.ebean.Ebean;

@ToString(of = { "id", "airline", "source", "destination", "IATA" })
@Entity
public @Data
class Route {
	@Id
	private int id;
	@ManyToOne
	@JoinColumn(name = "airline_id")
	private Airline airline;
	@ManyToOne
	@JoinColumn(name = "source_id")
	private Airport source;
	@ManyToOne
	@JoinColumn(name = "destination_id")
	private Airport destination;
	private boolean codeshare;
	private int stops;
	
	// Careful, for compatability with OpenFlight data IATA is a space-separated
	// list of iata codes of equipment used on this route
	private String IATA;

	@Transient
	@Getter(lazy = true)
	private final double distance = Airport.getDistance(getSource(),
			getDestination());

	@Transient
	@Getter(lazy = true)
	private final List<Equipment> equipment = equipment();

	private List<Equipment> equipment() {
		List<Equipment> equipment = null;
		for (String iata : getIATA().split(" ")) {
			List<Equipment> e = Ebean.find(Equipment.class).where()
					.eq("iata", iata).findList();
			if (equipment == null) {
				equipment = e;
			} else {
				equipment.addAll(e);
			}
		}
		return equipment;
	}

	// get number of seats for this equipment on the given airline
	public Integer getSeats(Equipment equipment) {
		List<Seating> seatings = Ebean.find(Seating.class).where()
				.eq("equipment", equipment).eq("airline", getAirline())
				.findList();
		// pick the first
		if (seatings.size() > 0) {
			return seatings.get(0).getSeats();
			// TODO if more than one, choose domestic or international etc.
		}

		for (Equipment e : equipment.getParents()) {
			seatings = Ebean.find(Seating.class).where().eq("equipment", e)
					.eq("airline", getAirline()).findList();
			if (seatings.size() > 0) {
				return seatings.get(0).getSeats();
			}
		}

		return null;
	}

	public Integer getSeats(FuelData fuelData) {
		List<Seating> seatings = Ebean.find(Seating.class).where()
				.in("equipment", fuelData.getEquipment())
				.eq("airline", getAirline()).findList();
		if (seatings.size() > 0) {
			return seatings.get(0).getSeats();
		}
		return null;
	}

	@Transient
	@Getter(lazy = true)
	private final DescriptiveStatistics fuelBurn = fuelBurn();

	private DescriptiveStatistics fuelBurn() {
		DescriptiveStatistics burn = new DescriptiveStatistics();
		for (Equipment e : getEquipment()) {
			DescriptiveStatistics equipmentBurn = new DescriptiveStatistics();
			if (e.getAllFuelData().size() > 0) {
				Integer eSeats = getSeats(e);
				for (FuelData fuelData : e.getAllFuelData()) {
					Integer seats = getSeats(fuelData);
					// if no seating for the aem's icao, try one for the
					// equipment that it came from
					if (seats == null)
						seats = eSeats;
					// TODO getSeats(aem) e.g. 752 instead of 757
					if (seats != null) {
						equipmentBurn.addValue(fuelData
								.getFuelBurn(getDistance()) / seats);
					}
				}
				if (equipmentBurn.getValues().length > 0) {
					burn.addValue(equipmentBurn.getMean());
				}
			}
		}

		return burn;
	}
}
