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
	private final Airline airline = Ebean.find(Airline.class, 4547);
	
	private boolean active = true;

	private static final DateFormat flightDateFormat = getGMTSimpleDateFormat("EEE MMM d");
	private static final DateFormat confirmationDateFormat = getGMTSimpleDateFormat("M/d/yyyy");

	public AirReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {

		AirReceipt receipt = new AirReceipt();
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
		Matcher matcher = Pattern.compile("\\s*Confirmation Date:\\s*(?<date>\\d{2}/\\d{1,2}/\\d{4})\\s*")
				.matcher(e.text());
		matcher.find();
		return confirmationDateFormat.parse(matcher.group("date"));
	}

	protected static String getConfirmation(Document doc)
			throws ParseException {
		Element e =  doc.select(":containsOwn(AIR Confirmation)").first();
		Matcher matcher = Pattern.compile("\\s*AIR Confirmation:\\s*(?<confirmation>\\w*)\\s*")
				.matcher(e.text());
		matcher.find();
		return matcher.group("confirmation");
	}

	protected List<Flight> getFlights(Document doc, Date sentDate)
			throws ParseException {
		List<Flight> flights = new ArrayList<Flight>();
		Elements flightTables = doc.select("flightInfo + table");

		// store last date, for connections
		Date date = null;

		for (Element flightTable : flightTables) {
			if (flightTable.hasText()) {
				Elements cells = flightTable.select("td");
				if (cells.size() < 4) {
					throw new ParseException("Not enough cells in flight table", 0);
				}
				String dateString = cells.get(1).text(), number = cells.get(2)
						.text(), itinerary = cells.get(3).text();

				Flight flight = new Flight();
				flight.setAirline(getAirline());
				flight.setNumber(Integer.parseInt(number));
				if (dateString.matches("\\s*")) {
					flight.setDate(date);
				} else {
					date = getDate(sentDate, dateString);
					flight.setDate(date);
				}

				Matcher matcher = Pattern.compile(
						"[^(]*\\((\\w{3})\\)[^(]*\\((\\w{3})\\).*").matcher(
						itinerary);
				if (matcher.find()) {
					String depart = matcher.group(1);
					String arrive = matcher.group(2);
					Airport source = Ebean.find(Airport.class).where()
							.eq("code", depart).findUnique();
					Airport destination = Ebean.find(Airport.class).where()
							.eq("code", arrive).findUnique();
					Route route = Ebean.find(Route.class).where()
							.eq("airline", getAirline()).eq("source", source)
							.eq("destination", destination).findUnique();
					flight.setRoute(route);
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
