package visiblehand.parser.air;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

// American Airlines email receipt parser

public @Data class AAParser extends AirParser {
	private final String fromString = "notify@aa.globalnotifications.com";
	private final String[] subjectStrings = {"E-Ticket Confirmation-"};
	private final String bodyString = "";
	
	private boolean active = true;
	
	private static final String datePattern ="ddMMMhh:mm aa";
	private static final DateFormat issueFormat = getGMTSimpleDateFormat("ddMMMyy");
	
	private static final Pattern issuePattern = Pattern.compile("DATE OF ISSUE - (?<issue>\\d{2}"+mmmRegex+"\\d{2})"),
								 confirmationPattern = Pattern.compile("E-TICKET CONFIRMATION/RECORD LOCATOR - (?<confirmation>[A-Z]{6})"),
								 internationalPattern = Pattern.compile("(?i)( INTL| INTERNTNL| INTERNATIONAL)$"),
								 flightPattern = Pattern.compile("(?<date>(?<day>\\d{2})(?<month>" + mmmRegex + ")).*\\b"
													+ "\\s*LV  (?<source>(?:\\w+\\s*)+[A-Z])\\s*(?<time>\\d{1,2}:\\d{2} (AM|PM)) (?<number>\\d+).*\\b"
													+ "\\s*AR  (?<destination>(?:\\w+\\s?)+[A-Z])\\s*(\\d{1,2}:\\d{2} (AM|PM)).*\\b"
													+ "\\s*(?:OPERATED BY (?<operator>(?:\\w+\\s?)+\\w))?");
	
	@Getter(lazy=true)
	private final Airline airline = Ebean.find(Airline.class).where().eq("name", "American Airlines").findUnique();
	
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
		Matcher matcher = confirmationPattern.matcher(content);
		matcher.find();
		return matcher.group("confirmation");
	}
	
	protected static Date getIssueDate(String content) throws ParseException {
		Matcher matcher = issuePattern.matcher(content);
		matcher.find();
		
		return issueFormat.parse(matcher.group("issue"));
	}

	protected List<Flight> getFlights(String content, Date messageDate)
			throws ParseException {
		List<Flight> flights = new ArrayList<Flight>();
		
		Matcher matcher = flightPattern.matcher(content);
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
			Route route = Route.find(airline, source, destination);
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
		return getNextDate(datePattern, dateString + timeString, sentDate);
	}

	protected Airport getAirport(String string) throws ParseException {
		string = string.trim();
		string = string.replaceAll("\\s+", " ");	// get rid of multiple spaces
		
		// international
		Matcher intlMatcher = internationalPattern.matcher(string);
		boolean intl = intlMatcher.find();
		if (intl) {
			string = intlMatcher.replaceFirst("");
		}
		
		String[] strings = splitLastInstanceOf(string, " ");
		String country = null;
		// is the last word a country code?
		if (strings[1].length() == 2) {
			Country c = Ebean.find(Country.class, strings[1]);
			if (c != null) {
				country = c.getName();
				string = strings[0];
			}
		}
		
		return getAirport(string, getAirline(), country);
	}
}
