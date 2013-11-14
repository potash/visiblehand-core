package visiblehand.parser.air;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import lombok.Getter;
import visiblehand.entity.Airline;
import visiblehand.entity.Airport;
import visiblehand.entity.Route;
import visiblehand.parser.MessageParser;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

// A skeleton for an airline email receipt parser

public abstract class AirParser extends MessageParser {
	public abstract Airline getAirline();

	public abstract AirReceipt parse(Message message)
			throws java.text.ParseException, MessagingException, IOException;

	@Getter
	private final Class receiptClass = AirReceipt.class;

	// find airport whose name is closest to the argument string
	protected static List<Airport> getAirportsLevenshtein(String string) {
		String sql = "select id, name, city, country, code, icao as ICAO, latitude, longitude, altitude, timezone, dst as DST "
				+ "from airport "
				+ "order by levenshtein(upper(replace(name, ' Intl', '')), '"
				+ string + "') asc limit 1";
		// TODO: if string contains intl or interntnl or international...
		// replace it with intl and dont remove it from name?
		// or easier to remove it and add name like '%Intl%'?

		RawSql rawSql = RawSqlBuilder.parse(sql).create();
		Query<Airport> query = Ebean.find(Airport.class);
		query.setRawSql(rawSql);
		return query.findList();
	}

	// look for airports matching the string in city, name, code, icao
	// when like is true, looks for exact matches on city and name fields
	protected static List<Airport> getAirports(String string, boolean like) {
		String likeString = like ? string + '%' : string;
		List<Airport> airports = Ebean.find(Airport.class).where()
				.ilike("city", likeString).findList();

		// name
		if (airports.size() == 0) {
			airports = Ebean.find(Airport.class).where()
					.ilike("name", likeString).findList();
		}

		if (airports.size() == 0) {
			String[] strings = MessageParser.splitLastInstanceOf(string, " ");
			strings[0] = like ? strings[0] + '%' : strings[0];

			if (strings[1].length() == 3) {
				// city + code
				airports = Ebean.find(Airport.class).where()
						.ilike("city", strings[0]).ieq("code", strings[1])
						.findList();
			} else if (strings[1].length() == 4) {
				// city + ICAO
				airports = Ebean.find(Airport.class).where()
						.ilike("city", strings[0]).ieq("ICAO", strings[1])
						.findList();
			}
			// city + name
			if (airports.size() == 0) {
				airports = Ebean.find(Airport.class).where()
						.ilike("city", strings[0])
						.icontains("name", strings[1]).findList();
			}
		}
		return airports;
	}

	// call getAirports first with exact matches
	protected static List<Airport> getAirports(String string) {
		List<Airport> airports = getAirports(string, false);
		if (airports.size() == 0) {
			airports = getAirports(string, true);
		}
		return airports;
	}

	protected static Airport getAirport(String string, Airline airline,
			String country) throws ParseException {
		List<Airport> airports = getAirports(string);

		if (airports.size() == 0) {
			airports = getAirportsLevenshtein(string);
		}

		if (airports.size() == 0) {
			throw new ParseException("Could not find airport: " + string, 0);
		} else if (airports.size() == 1) {
			return airports.get(0);
		} else {
			if (country != null && !country.isEmpty()) {
				List<Airport> airports2 = Ebean.filter(Airport.class)
						.istartsWith("country", country).filter(airports);
				if (airports2.size() > 0) {
					airports = airports2;
				}
			}
			
			if (airports.size() > 1) {
				// if more than one, look for one that aa actually flies to!
				for (Airport airport : airports) {
					List<Route> routes = Ebean.find(Route.class).where()
							.eq("airline", airline).eq("source", airport)
							.findList();
					if (routes.size() > 0)
						return airport;
				}
			}
			return airports.get(0);
		}
	}
}