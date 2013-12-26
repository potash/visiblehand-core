package visiblehand.entity.utility;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;
import visiblehand.entity.Emission;
import visiblehand.entity.UnitedState;
import visiblehand.entity.ZipCode;

import com.avaje.ebean.Ebean;

@Embeddable
public @Data class Electricity implements Emission {
	private Date date;
	private Utility utility;
	private Double cost;
	private Double energy; // kWh
	private ZipCode zipCode;
	private Integer split = 1;
	
	@Transient
	private EGridSubregion eGridSubregion;
	private EGridSubregion getEGridSubregion() {
		if (eGridSubregion == null && getZipCode() != null) {
			eGridSubregion = zipCode.getEGridSubregion();
		}
		// TODO: use a national average if no zipcode is specified
		return eGridSubregion;
	}
	
	public Double getEnergy() {
		if (energy == null) {
			if (getCost() != null) {
				Double rate = null;
				if (getZipCode() != null) {
					rate = ElectricityPrice.find(getZipCode().getState(), getDate());
				} else {
					rate = ElectricityPrice.find(UnitedState.find("US"), getDate());
				}
				if (rate != null) {
					energy = getCost() / rate / split;
				}
			}
		}
		return energy;
	}
	
	@Transient
	@Getter(lazy = true)
	private final Double CO2 = co2();
	
	private double co2() {
		return getEnergy() * getEGridSubregion().getCO2EmissionRate()/1000;
	}
}
