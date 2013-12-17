package visiblehand.entity.utility;

import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
public @Data class Electricity {
	private Utility utility;
	private Double cost;
	private Double energy;
	private Integer zipCode;
}
