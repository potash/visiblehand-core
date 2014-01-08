package visiblehand.entity.utility;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;
import visiblehand.entity.Emission;
import visiblehand.entity.UnitedState;
import visiblehand.entity.ZipCode;

@Embeddable
public @Data class Electricity implements Emission {
	private Date emissionDate;
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
					rate = ElectricityPrice.find(getZipCode().getState(), getEmissionDate());
				} else {
					rate = ElectricityPrice.find(UnitedState.find("US"), getEmissionDate());
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
	
	private Double co2() {
		if (getEnergy() != null && getEGridSubregion() != null) {
			return getEnergy() * getEGridSubregion().getCO2EmissionRate()/1000;
		} else {
			return null;
		}
	}
}
