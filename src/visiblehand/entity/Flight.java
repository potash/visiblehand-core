package visiblehand.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
public @Data class Flight {
	@Id
	private int id;
	@ManyToOne
	@JoinColumn(name="airline_id")
	@JsonView(JsonViews.Id.class)
	private Airline airline;
	@ManyToOne
	@JoinColumn(name="route_id")
	@JsonView(JsonViews.Id.class)
	private Route route;
	@JsonView(JsonViews.Id.class)
	private Date date;
	@JsonView(JsonViews.Id.class)
	private Integer number;
	@ManyToOne
	@JoinColumn(name="equipment_id")
	@JsonView(JsonViews.Id.class)
	private Equipment equipment;
}