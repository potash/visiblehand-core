package visiblehand.entity;

import static visiblehand.VisibleHand.*;
import java.util.Date;

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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.avaje.ebean.Ebean;

@Entity
@UniqueConstraint(columnNames={"route_id", "date","number","equipment_id"})
public @Data class Flight {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JoinColumn(name="route_id")
	private Route route;
	private Date date;
	private Integer number;
	@ManyToOne
	@JoinColumn(name="equipment_id")
	private Equipment equipment;
	
	//@Transient
	//@Getter(lazy=true)
	private DescriptiveStatistics fuelBurnStatistics;// = fuelBurnStatistics();
	
	public DescriptiveStatistics getFuelBurnStatistics() {
		if (fuelBurnStatistics == null) {
			fuelBurnStatistics = fuelBurn();
		}
		return fuelBurnStatistics;
	}
	
	public Flight() {
		
	}
	// find existing flight or create new one
	public static Flight find(Route route, Date date, Integer number, Equipment equipment) {
		Flight flight = Ebean.find(Flight.class).where().eq("route", route).eq("date", date)
				.eq("number", number).eq("equipment", equipment).findUnique();
		
		if (flight == null) {
			flight = new Flight();
			flight.setRoute(route);
			flight.setDate(date);
			flight.setNumber(number);
			flight.setEquipment(equipment);
		}
		
		return flight;
	}
	
	private DescriptiveStatistics fuelBurn() {
		DescriptiveStatistics burn = null;
		if (getEquipment() != null) {
			burn = getRoute().getFuelBurnStatistics(getEquipment());
		}
		if (burn == null || burn.getValues().length == 0) {
			burn = getRoute().getFuelBurnStatistics();
		}
		return burn;
	}
	
	@Transient
	@Getter(lazy=true)
	private final double fuelBurn = getFuelBurnStatistics().getMean();
	
	@Transient
	@Getter(lazy=true)
	private final double CO2 = getFuelBurn() / KG_FUEL_PER_LITER / LITERS_PER_GALLON * KG_CO2_PER_GALLON_FUEL;
}