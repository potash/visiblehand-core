package visiblehand.entity.utility;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;
import visiblehand.entity.Emission;
import visiblehand.entity.ZipCode;

import com.avaje.ebean.Ebean;

@Embeddable
public @Data class Electricity implements Emission {
	private Date date;
	private Utility utility;
	private Double cost;
	private Double energy; // kWh
	private ZipCode zipCode;
	private Integer split;
	
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
		if (energy == null && getCost() != null) {
			Double rate = null;
			if (getZipCode() != null) {
				System.out.println(getDate());
				rate = ElectricityPrice.find(getZipCode().getState(), getDate())/100;
				if (rate != null) {
					energy = getCost() / rate / split;
				}
			} else {
				//TODO: national average
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
