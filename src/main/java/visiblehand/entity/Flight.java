package visiblehand.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

@Entity
public @Data class Flight {
	@Id
	private int id;
	@ManyToOne
	@JoinColumn(name="airline_id")
	private Airline airline;
	@ManyToOne
	@JoinColumn(name="route_id")
	private Route route;
	private Date date;
	private Integer number;
	@ManyToOne
	@JoinColumn(name="equipment_id")
	private Equipment equipment;
	
	//@Transient
	//private DescriptiveStatistics fuelBurnStatistics = fuelBurnStatistics();
	
	public DescriptiveStatistics getFuelBurnStatistics() {
		return fuelBurnStatistics();
	}
	private DescriptiveStatistics fuelBurnStatistics() {
		DescriptiveStatistics burn = null;
		if (getEquipment() != null) {
			burn = getRoute().getFuelBurnStatistics(getEquipment());
		}
		if (burn == null || burn.getValues().length == 0) {
			burn = getRoute().getFuelBurnStatistics();
		}
		return burn;
	}
}