package visiblehand.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@ToString(of = { "id", "name" })
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
