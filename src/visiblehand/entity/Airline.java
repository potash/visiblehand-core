package visiblehand.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import visiblehand.parser.MessageParserTest;

import com.avaje.ebean.Ebean;
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
	
	// Get Airline by IATA. It's not actually unique (e.g. LH, SQ)
	// If more than one, return the first one with routes in the database
	public static Airline byIATA(String IATA) {
		List<Airline> airlines = Ebean.find(Airline.class).where().eq("IATA", IATA).findList();
		if (airlines.size() == 0) {
			return null;
		} else if (airlines.size() == 1){
			return airlines.get(0);
		} else {
			System.out.println("More than one airline with IATA: " + IATA);
			for (Airline airline : airlines) {
				List<Route> routes = Ebean.find(Route.class).where()
						.eq("airline", airline)
						.findList();
				if (routes.size() > 0)
					return airline;
			}
			return airlines.get(0);
		}
	}
}
