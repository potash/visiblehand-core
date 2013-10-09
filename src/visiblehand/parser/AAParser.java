package visiblehand.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;
import visiblehand.Flight;
import visiblehand.entity.Airline;
import visiblehand.entity.Airport;
import visiblehand.entity.Route;

import com.avaje.ebean.Ebean;

// American Airlines email receipt parser

public class AAParser extends AirParser {
	private static final String fromString = "aa.com";
	private static final String subjectString = "ticket confirmation";
	
	public String getFromString() {
		return fromString;
	}
	
	public String getSubjectString() {
		return subjectString;
	}

	@Transient
	@Getter(lazy = true)
	private final Airline airline = Ebean.find(Airline.class, 24);

	public List<Flight> getFlights(Message message) {
		try {
			Multipart mp = (Multipart) message.getContent();
			BodyPart bp = mp.getBodyPart(0);
			return parse(bp.getContent().toString(), message.getSentDate());
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Flight>();
		}
	}

	public List<Flight> parse(String message, Date messageDate) {
		List<Flight> flights = new ArrayList<Flight>();

		Pattern pattern = Pattern
				.compile("(\\d{2})(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC).*\\b"
						+ "\\s*LV  ((?:\\w+\\s?)+\\w).*\\b"
						+ "\\s*AR  ((?:\\w+\\s?)+\\w).*\\b"
						+ "\\s*(?:OPERATED BY ((?:\\w+\\s?)+\\w))?");
		Matcher matcher = pattern.matcher(message);
		// groups are month, day, from airport, to airport
		while (matcher.find()) {
			Date date = getDate(messageDate, matcher.group(1), matcher.group(2));
			Airport source = getAirport(matcher.group(3)), destination = getAirport(matcher
					.group(4));
			Airline airline = getAirline();
			String operator = matcher.group(5);
			if (operator != null && !operator.equalsIgnoreCase("American Eagle")) {
				System.out.println(matcher.group(5));
				List<Airline> airlines = Ebean.find(Airline.class).where().ieq("name", matcher.group(5)).eq("active", true).findList();
				if (airlines.size() > 0) {
					// TODO don't pick an airline with zero routes
					airline = airlines.get(0);
				}
			}
			Route route = Ebean.find(Route.class).where()
					.eq("airline", airline).eq("source", source)
					.eq("destination", destination).findUnique();
			// TODO: if route doesn't exist, add it!
			flights.add(new Flight(date, route));
			System.out.println(route);
		}

		return flights;
	}

	private Date getDate(Date sentDate, String group1, String group2) {
		// TODO Auto-generated method stub
		return null;
	}

	// matches city to city and airport to beginning of airport
	public static Airport getAirport(String string) {
		int index = string.lastIndexOf(' ');
		List<Airport> airports = null;
		if (index > 0) {
			String city = "", airport = "";
			airport = string.substring(index + 1);
			city = string.substring(0, index);

			if (airport.length() == 3) {
				// guessing its an airport code
				airports = Ebean.find(Airport.class).where()
						.istartsWith("city", city).icontains("code", airport)
						.findList();
			} else if (airport.length() == 4) {
				airports = Ebean.find(Airport.class).where()
						.istartsWith("city", city).icontains("ICAO", airport)
						.findList();
			}

			if (airports == null || airports.size() == 0) {
				airports = Ebean.find(Airport.class).where()
						.istartsWith("city", city).icontains("name", airport)
						.findList();
			}
		}
		if (airports == null || airports.size() == 0) {
			// only one word (or above didn't match) means its a city?
			airports = Ebean.find(Airport.class).where()
					.istartsWith("city", string).isNotNull("code").findList();
		}

		// return best match
		if (airports.size() == 0) {
			return null;
		} else if (airports.size() == 1) {
			return airports.get(0);
		} else {
			// if there is more than one, prefer one that has an ICAO?
			List<Airport> filtered = Ebean.filter(Airport.class)
					.ne("ICAO", "\\N").filter(airports);
			// then pick the first (arbitrary)
			if (filtered.size() >= 1) {
				return filtered.get(0);
			} else {
				return airports.get(0);
			}
		}
	}
}
