package visiblehand.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

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

	private double fuelBurn;
	private double distance;
}