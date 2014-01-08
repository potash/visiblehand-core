package visiblehand.parser.air;

import java.io.IOException;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import visiblehand.entity.air.AirReceipt;
import visiblehand.entity.air.Airline;
import visiblehand.entity.air.Airport;
import visiblehand.entity.air.Flight;
import visiblehand.entity.air.Route;

import com.avaje.ebean.Ebean;

// United Airlines email receipt parser

public @Data
class JetBlueParser extends AirParser {
	private final String fromString = "reservations@jetblue.com";
	private final String[] subjectStrings = { "Itinerary for your upcoming trip" };
	private final String bodyString = "";

	@Getter(lazy = true)
	private final Airline airline = Ebean.find(Airline.class).where().eq("name", "JetBlue Airways").findUnique();
	
	private final Date parserDate = new Date(1387047326);	// December 14, 2013
	private final Date searchDate = new Date(1387047326);	// December 14, 2013
	
	private static final Pattern confirmationPattern = Pattern.compile("Your confirmation number is (?<confirmation>\\w*)"),
								 timePattern = Pattern.compile("\u00a0(a|p)\\.m\\..*");
	
	private static final String[] datePatterns = {
		"EEE, MMM d h:mma", "MMMM d hh:mma"
	};

	public AirReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {

		AirReceipt receipt = new AirReceipt();
		String content = getContent(message);
		receipt.setFlights(getFlights(getContent(message), message.getSentDate()));
		receipt.setAirline(getAirline());
		receipt.setConfirmation(getConfirmation(content));
		receipt.setDate(message.getSentDate());

		return receipt;
	}

	protected static String getConfirmation(String content)
			throws ParseException {
		Matcher matcher = confirmationPattern.matcher(content);
		if (matcher.find()) {
			return matcher.group("confirmation");
		} else {
			throw new ParseException("Confirmation number not found.", 0);
		}
	}

	protected List<Flight> getFlights(String content, Date sentDate) throws ParseException {
		List<Flight> flights = new ArrayList<Flight>();

		Document doc = Jsoup.parse(content);
		Element flightHeader = doc.select("td:containsOwn(Date) ~ td:containsOwn(Departs) ~ "
				+ "td:containsOwn(Route) ~ td:containsOwn(Flight) ~ td:containsOwn(Travelers) ~ "
				+ "td:containsOwn(Seats) ~ td:containsOwn(Terminal)").first().parent();
		
		boolean frequentFlyer = (flightHeader.select("> td:containsOwn(Frequent Flyer)").size() > 0);

		Element flightTable = flightHeader.parent().parent();
		// the flight rows are the ones with times in them
		for (Element flightRow : flightTable.select("tr:gt(0):has(td:gt(4))")) {
			Elements cells = flightRow.select("td");
			
			Date date = getDate(sentDate, cells.get(0).text(), cells.get(1).text());
			
			
			String[] airports = cells.get(2).text().split(" to ");
			Airport source = getAirport(airports[0]),
					destination = getAirport(airports[1]);
			Route route = Route.find(getAirline(), source, destination);
			
			int number = Integer.parseInt(cells.get(3).text());
			
			Flight flight = Flight.find(route, date, number, null);
			flights.add(flight);
		}
		return flights;
	}

	protected static Date getDate(Date sentDate, String dateString, String timeString) throws ParseException {
		Date date = null;
		Matcher matcher = timePattern.matcher(timeString);
		matcher.find();
		String text = dateString + " " + matcher.replaceFirst(matcher.group(1)+"m");
		for(String datePattern : datePatterns) {
			try {
				date = getNextDate(datePattern, text, sentDate);
				break;
			} catch (ParseException e) { }
		}
		if (date == null) {
			throw new ParseException("Could not parse date: " + text, 0);
		}
		return date;
	}
	
	protected Airport getAirport(String string) throws ParseException {
		string = string.trim();
		String[] strings = splitLastInstanceOf(string, ", ");
		// if the last string is two letters, it is a state abbreviation?
		String country = strings[1].length() > 2 ? strings[1] : "United States";
		
		String[] s = splitLastInstanceOf(strings[0], " ");
		strings[0] = s[1].length() == 2 ? s[0] : strings[0];
		return getAirport(strings[0], getAirline(), country);
	}
}
