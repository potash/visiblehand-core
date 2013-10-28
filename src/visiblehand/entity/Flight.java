package visiblehand.entity;

import java.util.Date;

import javax.persistence.Entity;

import lombok.Data;

@Entity
public @Data class Flight {
	private Route route;
	private Date date;
	private Integer number;
	private Equipment equipment;
	private Airline airline;
	
	private final double fuelBurn = getRoute().getFuelBurn();
}
