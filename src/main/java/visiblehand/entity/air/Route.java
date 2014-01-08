package visiblehand.entity.air;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.avaje.ebean.Ebean;

@Entity
@ToString(exclude={"fuelBurnStatistics", "fuelBurn"})
@UniqueConstraint(columnNames={"airline_id", "source_id", "destination_id"})
public @Data class Route {
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
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
		if (getIATA() == null) {
			return new ArrayList<Equipment>();
		} else {
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
	}

	// get seats for this equipment on the given airline
	public Integer getSeats(Equipment equipment) {
		List<Equipment> equipments = new ArrayList<Equipment>(1);
		equipments.add(equipment);
		return getSeats(equipments);
	}

	// get seats matching a list of equipment, preferring those matching this route's airline
	public Integer getSeats(List<Equipment> equipments) {
		// try exact matches on this airline
		List<Seating> seatings = Ebean.find(Seating.class).where()
				.in("equipment", equipments)
				.eq("airline", getAirline()).findList();
		if (seatings.size() > 0) {
			return (int)Seating.getSeatStatistics(seatings).getMean();
		}
		// try parent matches on this airline
		List<Equipment> parents = new ArrayList<Equipment>();
		for (Equipment equipment : equipments) {
			parents.addAll(equipment.getParents());
		}
		seatings = Ebean.find(Seating.class).where()
				.in("equipment", parents)
				.eq("airline", getAirline()).findList();
		if (seatings.size() > 0) {
			return (int)Seating.getSeatStatistics(seatings).getMean();
		}
		
		// try exact matches on any airline
		seatings = Ebean.find(Seating.class).where()
				.in("equipment", equipments).findList();
		if (seatings.size() > 0) {
			return (int)Seating.getSeatStatistics(seatings).getMean();
		}
		return null;
	}

	//@Transient
	//@Getter(lazy=true)
	private DescriptiveStatistics fuelBurnStatistics; //= fuelBurn();
	public DescriptiveStatistics getFuelBurnStatistics() {
		if (fuelBurnStatistics == null) {
			fuelBurnStatistics = fuelBurn();
		}
		return fuelBurnStatistics;
	}
	
	@Transient
	@Getter(lazy = true)
	private final double fuelBurn = getFuelBurnStatistics().getMean();

	private DescriptiveStatistics fuelBurn() {
		DescriptiveStatistics burn = new DescriptiveStatistics();
		for (Equipment equipment : getEquipment()) {
			DescriptiveStatistics equipmentBurn = getFuelBurnStatistics(equipment);
			if (equipmentBurn.getValues().length > 0) {
					burn.addValue(equipmentBurn.getMean());
			}
		}
		return burn;
	}
	
	// get fuel burn statistics. use all flag to allow equipment's relatives fuel data
	public DescriptiveStatistics getFuelBurnStatistics(Equipment equipment, boolean all) {
		DescriptiveStatistics equipmentBurn = new DescriptiveStatistics();
		List<FuelData> fuelDatas = all ? equipment.getAllFuelData() : equipment.getFuelData();
		if (fuelDatas.size() > 0) {
			Integer eSeats = getSeats(equipment);
			for (FuelData fuelData : fuelDatas) {
				Integer seats = getSeats(fuelData.getEquipment());
				// if no seating for the aem's icao, try one for the
				// equipment that it came from
				if (seats == null) {
					seats = eSeats;
				}
				// TODO if still null get average seats from other airlines
				if (seats != null) {
					equipmentBurn.addValue(fuelData
							.getFuelBurn(getDistance()) / seats);
				}
			}
		}
		return equipmentBurn;
	}
	
	// get "best" fuel burn statistics, ie use relatives only if no exact matches
	public DescriptiveStatistics getFuelBurnStatistics(Equipment equipment) {
		DescriptiveStatistics burn = getFuelBurnStatistics(equipment, false);
		if (burn.getValues().length == 0) {
			burn = getFuelBurnStatistics(equipment, true);
		}
		return burn;
	}
	
	public static Route find(Airline airline, Airport source, Airport destination) {
		Route route = Ebean.find(Route.class).where().eq("airline", airline)
				.eq("source", source).eq("destination", destination).findUnique();
		if (route == null) {
			route = new Route();
			route.setAirline(airline);
			route.setSource(source);
			route.setDestination(destination);
			
			route.setCodeshare(false);
			route.setStops(0);
		}
		
		return route;
	}
}
