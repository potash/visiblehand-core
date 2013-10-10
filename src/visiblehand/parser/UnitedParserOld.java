package visiblehand.parser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import visiblehand.Flight;
import visiblehand.entity.Airline;
import visiblehand.entity.Airport;
import visiblehand.entity.Equipment;
import visiblehand.entity.Route;

import com.avaje.ebean.Ebean;

// American Airlines email receipt parser

public @Data
class UnitedParserOld extends AirParser {
	private String fromString = "united.com";
	private String subjectString = "Your United flight confirmation";
	
	private DateFormat dateFormat = new SimpleDateFormat(
			"h:mm a EEE, MMM d, yyyy");

	@Transient
	@Getter(lazy = true)
	private final Airline airline = Ebean.find(Airline.class, 24);

	public List<Flight> parse(Message message) throws ParseException, MessagingException, IOException {
		return parse(getContent(message));
	}
	
	public List<Flight> parse(String content) throws ParseException {
		//try {
			List<Flight> flights = new ArrayList<Flight>();

			Document doc = Jsoup.parse(content);
			Elements flightTable = doc.select("#flightTable");
			
			if (flightTable.size() == 0) {
				throw new ParseException("No flight table found.", 0);
			}
			
			Elements flightRows = flightTable.get(0)
					.select("tr:has(td:contains(Depart))");
			for (Element flightRow : flightRows) {
				Elements cells = flightRow.nextElementSibling().select("td");
				String number = cells.get(0).text(), depart = cells.get(1)
						.text(), arrive = cells.get(2).text(), seatClass = cells
						.get(3).text();
				System.out.println(depart);
				System.out.println(arrive);
				Flight flight = new Flight();
				flight.setAirline(getAirline());
				flight.setNumber(new Integer(number.split("United ")[1]));
				Airport source = Ebean.find(Airport.class).where()
						.eq("code", depart.substring(0, 3)).findUnique();
				Airport destination = Ebean.find(Airport.class).where()
						.eq("code", arrive.substring(0, 3)).findUnique();
				// System.out.println(source);
				// System.out.println(destination);

				Date date = dateFormat.parse(depart.substring(4));
				flight.setDate(date);

				Route route = Ebean.find(Route.class).where()
						.eq("airline", getAirline()).eq("source", source)
						.eq("destination", destination).findUnique();
				System.out.println(route);
				flight.setRoute(route);
				// next line has equipment, duration, fare code, miles and meal
				// info
				String info = flightRow.nextElementSibling()
						.nextElementSibling().select("td").get(0).text();
				String equipment = info.split("(Equipment:\\s*|\\W*\\|)")[1];
				System.out.println(equipment);
				List<Equipment> e = Ebean.find(Equipment.class).where()
						.like("name", equipment + "%").findList();
				if (e.size() > 0) {
					if (e.size() > 1) {
						System.out.println("More than one equipment match!");
						// TODO if more than one pick the parent. or reference
						// with seatings. or route.getEquipment()
					}
					flight.setEquipment(e.get(0));
				}

				flights.add(flight);
			}
			return flights;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ArrayList<Flight>();
//		}

		// Pattern pattern = Pattern
		// .compile("(\\d{2})(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC).*\\b"
		// + "\\s*LV  ((?:\\w+\\s?)+\\w).*\\b"
		// + "\\s*AR  ((?:\\w+\\s?)+\\w).*\\b"
		// + "\\s*(?:OPERATED BY ((?:\\w+\\s?)+\\w))?");
		// Matcher matcher = pattern.matcher(message);
		// // groups are month, day, from airport, to airport
		// while (matcher.find()) {
		// Date date = getDate(messageDate, matcher.group(1), matcher.group(2));
		// Airport source = getAirport(matcher.group(3)), destination =
		// getAirport(matcher
		// .group(4));
		// Airline airline = getAirline();
		// String operator = matcher.group(5);
		// if (operator != null && !operator.equalsIgnoreCase("American Eagle"))
		// {
		// System.out.println(matcher.group(5));
		// List<Airline> airlines =
		// Ebean.find(Airline.class).where().ieq("name",
		// matcher.group(5)).eq("active", true).findList();
		// if (airlines.size() > 0) {
		// // TODO don't pick an airline with zero routes
		// airline = airlines.get(0);
		// }
		// }
		// Route route = Ebean.find(Route.class).where()
		// .eq("airline", airline).eq("source", source)
		// .eq("destination", destination).findUnique();
		// // TODO: if route doesn't exist, add it!
		// flights.add(new Flight(date, route));
		// System.out.println(route);
		// }
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
