package visiblehand.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import visiblehand.parser.MessageParserTest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonView;

@ToString(of = { "id", "name" })
@EqualsAndHashCode(of={"id"})
@Entity
public @Data
class Airline {
	@Id @JsonView(MessageParserTest.TestView.class)
	private int id;
	private String name;
	private String alias;
	private String IATA;
	private String ICAO;
	private String callsign;
	private String country;
	private Boolean active;
}
