package visiblehand.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "name", "alias", "iata", "icao", "callsign",
		"country", "active" })
@Entity
public @Data
class Airline {
	@Id
	private int id;
	private String name;
	private String alias;
	private String IATA;
	private String ICAO;
	private String callsign;
	private String country;
	private Boolean active;
}
