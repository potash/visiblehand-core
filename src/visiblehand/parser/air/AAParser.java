package visiblehand.parser.air;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;

import lombok.Data;
import lombok.Getter;
import visiblehand.entity.Airline;
import visiblehand.entity.Airport;
import visiblehand.entity.Country;
import visiblehand.entity.Flight;
import visiblehand.entity.Route;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

// American Airlines email receipt parser

public @Data class AAParser extends AirParser {
	private final String fromString = "notify@aa.globalnotifications.com";
	private final String[] subjectStrings = {"E-Ticket Confirmation-"};
	private final String bodyString = "";
	
	private boolean active = true;

	@Getter(lazy=true)
	private final Airline airline = Ebean.find(Airline.class, 24);
	
	private static final String months = "(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)";

	public AirReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {
		AirReceipt receipt = new AirReceipt();
		String content = getContent(message);
		Date date = getIssueDate(content);
		receipt.setFlights(getFlights(content,
				date));
		receipt.setAirline(getAirline());
		receipt.setConfirmation(getConfirmation(content));
		receipt.setDate(date);
		
		return receipt;
	}

	protected static String getConfirmation(String content) {
		Pattern pattern = Pattern.compile("E-TICKET CONFIRMATION/RECORD LOCATOR - (?<confirmation>[A-Z]{6})");
		Matcher matcher = pattern.matcher(content);
		matcher.find();
		return matcher.group("confirmation");
	}
	
	protected static Date getIssueDate(String content) throws ParseException {
		Pattern pattern = Pattern.compile("DATE OF ISSUE - (?<issue>\\d{2}"+months+"\\d{2})");
		Matcher matcher = pattern.matcher(content);
		matcher.find();
		DateFormat format = new SimpleDateFormat("ddMMMyy");
		return format.parse(matcher.group("issue"));
	}

	protected List<Flight> getFlights(String content, Date messageDate)
			throws ParseException {
		List<Flight> flights = new ArrayList<Flight>();
		
		Pattern pattern = Pattern
				.compile("(?<date>(?<day>\\d{2})(?<month>" + months + ")).*\\b"
						+ "\\s*LV  (?<source>(?:\\w+\\s*)+[A-Z])\\s*(?<time>\\d{1,2}:\\d{2} (AM|PM)) (?<number>\\d+).*\\b"
						+ "\\s*AR  (?<destination>(?:\\w+\\s?)+[A-Z])\\s*(\\d{1,2}:\\d{2} (AM|PM)).*\\b"
						+ "\\s*(?:OPERATED BY (?<operator>(?:\\w+\\s?)+\\w))?");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			Date date = getDate(messageDate, matcher.group("date"), matcher.group("time"));//matcher.group("month"), matcher.group("day"));
			Airport source = getAirport(matcher.group("source")),
					destination = getAirport(matcher.group("destination"));
			Airline airline = getAirline();
			String operator = matcher.group("operator");
			if (operator != null
					&& !operator.equalsIgnoreCase("American Eagle")) {
				List<Airline> airlines = Ebean.find(Airline.class).where()
						.ieq("name", operator).eq("active", true)
						.findList();
				if (airlines.size() > 0) {
					// TODO don't pick an airline with zero routes
					airline = airlines.get(0);
				}
			}
			Route route = Ebean.find(Route.class).where()
					.eq("airline", airline).eq("source", source)
					.eq("destination", destination).findUnique();
			if (route == null) {
				route = new Route();
				route.setAirline(airline);
				// TODO: if airline is not aa, need an AA entry with codeshare=true
				route.setCodeshare(false);
				route.setSource(source);
				route.setDestination(destination);
				route.setStops(0);
				route.setIATA(route.findIATA());
				// TODO route.datasource?
				Ebean.save(route);
			}
			
			Flight flight = new Flight();
			flight.setDate(date);
			flight.setRoute(route);
			flight.setAirline(airline);
			flight.setNumber(Integer.parseInt(matcher.group("number")));
			flights.add(flight);
		}

		return flights;
	}

	protected static Date getDate(Date sentDate, String dateString, String timeString) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(sentDate);
		int sentYear = cal.get(Calendar.YEAR);
		
		DateFormat format = new SimpleDateFormat("ddMMMyyyyhh:mm aa");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = format.parse(dateString + sentYear + timeString);
		if (date.compareTo(sentDate) < 0) {
			date = format.parse(dateString + (sentYear + 1) + timeString);
		}
		return date;
	}

	protected Airport getAirport(String string) throws ParseException {
		string = string.trim();
		string = string.replaceAll("\\s+", " ");
		
		int index = string.lastIndexOf(' ');
		String string1 = "", string2 = "";
		string2 = string.substring(index + 1);
		string1 = string.substring(0, Math.max(index, 0));
		
		List<Airport> airports = null;
		// is the last word a country code?
		if (string2.length() == 2) {
			Country country = Ebean.find(Country.class, string2);
			if (country != null) {
				String city = string.substring(0, Math.max(index, 0));
				airports = Ebean.find(Airport.class).where().ieq("city", string1).ieq("country", country.getName()).findList();
			}
		}
		if (airports == null || airports.size() == 0) {
			airports = Ebean.find(Airport.class).where().ieq("city", string).findList();
		}
		if (airports == null || airports.size() == 0) {
			if (string2.length() == 3) {
				// guessing its an airport code
				airports = Ebean.find(Airport.class).where()
						.istartsWith("city", string1).icontains("code", string2)
						.findList();
			} else if (string2.length() == 4) {
				airports = Ebean.find(Airport.class).where()
						.istartsWith("city", string1).icontains("ICAO", string2)
						.findList();
			}

			if (airports == null || airports.size() == 0) {
				airports = Ebean.find(Airport.class).where()
						.istartsWith("city", string1).icontains("name", string2)
						.findList();
			}
		}

		// if no match just try Levenshtein distance on airport name
		if (airports.size() == 0) {
			String sql = "select id, name, city, country, code, icao as ICAO, latitude, longitude, altitude, timezone, dst as DST "
					+ "from airport "
					+ "order by levenshtein(upper(replace(name, ' Intl', '')), '" + string + "') asc limit 1";
			// TODO: if string contains intl or interntnl or international...
			// replace it with intl and dont remove it from name? 
			// or easier to remove it and add name like '%Intl%'?
			
			RawSql rawSql = RawSqlBuilder.parse(sql).create();
			Query<Airport> query = Ebean.find(Airport.class);
			query.setRawSql(rawSql);
			Airport airport = query.findUnique();
			if (airport == null)
				throw new ParseException("Airport not found: " + string, 0);
			return airport;
		} else if (airports.size() == 1) {
			return airports.get(0);
		} else {
			//if more than one, look for one that aa actually flies to!
			for (Airport airport : airports) {
				List<Route> routes = Ebean.find(Route.class).where()
						.eq("airline", getAirline()).eq("source", airport).findList();
				if (routes.size() > 0)
					return airport;
			}
			return airports.get(0);
		}
	}
}
