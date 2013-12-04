package visiblehand.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

@Entity
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
	
}