package visiblehand.entity.air;

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

import visiblehand.entity.Emission;

import com.avaje.ebean.Ebean;

@Entity
@UniqueConstraint(columnNames={"route_id", "date","number","equipment_id"})
public @Data class Flight implements Emission {
	public static final double MEGAJOULE_PER_LITER_FUEL = 34.7;
	// A-1 jet fuel properties (wikipedia)
	public static final double KG_FUEL_PER_LITER = .804;
	// emission factor of jet fuel
	// (http://www.eia.gov/oiaf/1605/coefficients.html)
	public static final double KG_CO2_PER_GALLON_FUEL = 9.57;
	
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
	private Integer split = 1;
	
	//@Transient
	//@Getter(lazy=true)
	private DescriptiveStatistics fuelBurnStatistics;// = fuelBurnStatistics();
	
	public DescriptiveStatistics getFuelBurnStatistics() {
		if (fuelBurnStatistics == null) {
			fuelBurnStatistics = fuelBurn();
		}
		return fuelBurnStatistics;
	}
	
	public Date getEmissionDate() {
		return getDate();
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
			Ebean.save(flight);
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
	private final Double CO2 = getFuelBurn() / KG_FUEL_PER_LITER / LITERS_PER_GALLON * KG_CO2_PER_GALLON_FUEL;
}