package visiblehand.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import visiblehand.parser.MessageParserTest;

import com.fasterxml.jackson.annotation.JsonView;

@ToString(of = { "id", "name" })
@EqualsAndHashCode(of={"id"})
@Entity
public @Data
class Airline {
	@Id
	private int id;
	@JsonView(MessageParserTest.TestView.class)
	private String name;
	private String alias;
	private String IATA;
	private String ICAO;
	private String callsign;
	private String country;
	private Boolean active;
}
