package visiblehand.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avaje.ebean.Ebean;

@ToString(of = { "id", "name" })
@EqualsAndHashCode(of={"id"})
@Entity
public @Data
class Airline {
	static final Logger logger = LoggerFactory.getLogger(Airline.class);
	
	@Id
	private Long id;
	private String name;
	private String alias;
	private String IATA;
	private String ICAO;
	private String callsign;
	private String country;
	private Boolean active;
	
	// Get Airline by IATA. It's not actually unique (e.g. LH, SQ)
	// If more than one, return the first one with routes in the database
	public static Airline findByIATA(String IATA) {
		List<Airline> airlines = Ebean.find(Airline.class).where().eq("IATA", IATA).findList();
		if (airlines.size() == 0) {
			return null;
		} else if (airlines.size() == 1){
			return airlines.get(0);
		} else {
			logger.warn("More than one airline with IATA: " + IATA);
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
