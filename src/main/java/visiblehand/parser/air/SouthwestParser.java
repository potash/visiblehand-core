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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import visiblehand.entity.AirReceipt;
import visiblehand.entity.Airline;
import visiblehand.entity.Airport;
import visiblehand.entity.Flight;
import visiblehand.entity.Route;

import com.avaje.ebean.Ebean;

public @Data
class SouthwestParser extends AirParser {
	private final String fromString = "SouthwestAirlines@luv.southwest.com";
	private final String[] subjectStrings = {"Southwest Airlines Confirmation", "Flight reservation"};
	private final String bodyString = "";

	@Getter(lazy = true)
	private final Airline airline = Ebean.find(Airline.class).where().eq("name", "Southwest Airlines").findUnique();
	
	private boolean active = true;

	private static final String flightDatePattern = "EEE MMM d h:mm aa";
	private static final DateFormat confirmationDateFormat = getGMTSimpleDateFormat("M/d/yyyy");
	
	private static final Pattern confirmationDatePattern = Pattern.compile("\\s*Confirmation Date:\\s*(?<date>\\d{2}/\\d{1,2}/\\d{4})\\s*"),
								 confirmationCodePattern = Pattern.compile("\\s*AIR Confirmation:\\s*(?<confirmation>\\w*)\\s*"),
							     flightPattern = Pattern.compile(".*\\((?<depart>\\w{3})\\).*(?<time>\\d{1,2}:\\d{2} (A|P)M).*\\((?<arrive>\\w{3})\\).*");
	
	public AirReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {

		AirReceipt receipt = new AirReceipt(message);
		String content = getContent(message);
		content = content.replaceAll("<!-- Start Flight Info -->",
				"<flightInfo></flightInfo>");
		Document doc = Jsoup.parse(content);
		receipt.setFlights(getFlights(doc, message.getSentDate()));
		receipt.setAirline(getAirline());
		receipt.setConfirmation(getConfirmation(doc));
		receipt.setDate(getConfirmationDate(doc));

		return receipt;
	}

	private Date getConfirmationDate(Document doc) throws ParseException {
		Element e =  doc.select(":containsOwn(Confirmation Date)").first();
		Matcher matcher = confirmationDatePattern.matcher(e.text());
		matcher.find();
		return confirmationDateFormat.parse(matcher.group("date"));
	}

	protected static String getConfirmation(Document doc)
			throws ParseException {
		Element e =  doc.select(":containsOwn(AIR Confirmation)").first();
		Matcher matcher = confirmationCodePattern.matcher(e.text());
		matcher.find();
		return matcher.group("confirmation");
	}

	protected List<Flight> getFlights(Document doc, Date sentDate)
			throws ParseException {
		List<Flight> flights = new ArrayList<Flight>();
		Elements flightTables = doc.select("flightInfo + table");

		// store last date, for connections
		String lastDateString = null;

		for (Element flightTable : flightTables) {
			if (flightTable.hasText()) {
				Elements cells = flightTable.select("td");
				if (cells.size() < 4) {
					throw new ParseException("Not enough cells in flight table", 0);
				}
				String dateString = cells.get(1).text(), number = cells.get(2)
						.text(), itinerary = cells.get(3).text();
				if (!dateString.isEmpty()) {
					lastDateString = dateString;
				} else {
					dateString = lastDateString;
				}

				Flight flight = new Flight();
				flight.setNumber(Integer.parseInt(number));

				Matcher matcher = flightPattern.matcher(itinerary);
				System.out.println(itinerary);
				if (matcher.find()) {
					String depart = matcher.group("depart");
					System.out.println(depart);
					String arrive = matcher.group("arrive");
					Airport source = Ebean.find(Airport.class).where()
							.eq("code", depart).findUnique();
					Airport destination = Ebean.find(Airport.class).where()
							.eq("code", arrive).findUnique();
					Route route = Route.find(getAirline(), source, destination);
					flight.setRoute(route);
					
					String timeString = matcher.group("time");
					flight.setDate(getNextDate(flightDatePattern, dateString + ' ' + timeString, sentDate));
					
					flights.add(flight);
				}
			}
		}

		return flights;
	}

	// get the StartFlightInfo comment nodes
	private static Date getDate(Date sentDate, String dateString) {
		// TODO implement this
		return null;
	}
}
