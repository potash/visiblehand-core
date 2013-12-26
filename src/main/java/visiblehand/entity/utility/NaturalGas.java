package visiblehand.entity.utility;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;
import visiblehand.entity.Emission;
import visiblehand.entity.UnitedState;
import visiblehand.entity.ZipCode;

@Embeddable
public @Data class NaturalGas implements Emission {
	// EIA coefficient
	public static final double KG_CO2_PER_MMBTU = 53.06;
	// national average
	public static final double BTU_PER_SCF = 1029;
	
	private Date date;
	private Utility utility;
	private Double cost;
	private Double volume; // standard cubic feet
	private ZipCode zipCode;
	private Integer split = 1;
	
	public Double getVolume() {
		if (volume == null) {
			if (getCost() != null) {
				Double rate = null;
				if (getZipCode() != null) {
					rate = NaturalGasPrice.find(getZipCode().getState(), getDate());
				} else {
					rate = NaturalGasPrice.find(UnitedState.find("US"), getDate());
				}
				if (rate != null) {
					volume = getCost() / rate / split * 1000;
				}
			}
		}
		return volume;
	}
	
	@Transient
	@Getter(lazy = true)
	private final Double CO2 = co2();
	
	private double co2() {
		return getVolume() * BTU_PER_SCF * KG_CO2_PER_MMBTU / 1000000;
	}
}
