package visiblehand.parser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import visiblehand.Flight;
import visiblehand.entity.Airline;
import visiblehand.entity.Airport;
import visiblehand.entity.Equipment;
import visiblehand.entity.Route;

import com.avaje.ebean.Ebean;

// United Airlines email receipt parser for older receipts
// subjects of the form "Your United flight confirmation..."

public @Data class UnitedParserOld extends AirParser {
	private final String fromString = "united-confirmation@united.com";
	private final String subjectString = "Your United flight confirmation";
	
	@Getter(lazy = true)
	private final Airline airline = Ebean.find(Airline.class, 5209);

	private static final DateFormat dateFormat = new SimpleDateFormat(
			"h:mm a EEE, MMM d, yyyy");


	public AirReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {

		AirReceipt receipt = new AirReceipt();
		String content = getContent(message);
		receipt.setFlights(getFlights(content));
		receipt.setAirline(getAirline());
		receipt.setConfirmation(getConfirmation(content));
		receipt.setDate(message.getSentDate());

		return receipt;
	}

	protected static String getConfirmation(String content) throws ParseException {
		Matcher matcher = Pattern.compile("(?s)Confirmation (?:#|number)[^\\w]*(\\w{6})")
				.matcher(content);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			throw new ParseException("Confirmation number not found.", 0);
		}
	}

	protected List<Flight> getFlights(String content) throws ParseException {
		List<Flight> flights = new ArrayList<Flight>();

		Document doc = Jsoup.parse(content);
		Elements flightTable = doc.select("#flightTable");

		if (flightTable.size() == 0) {
			throw new ParseException("No flight table found.", 0);
		}

		Elements flightRows = flightTable.get(0).select(
				"tr:has(td:contains(Depart))");
		for (Element flightRow : flightRows) {
			
			while( (flightRow = flightRow.nextElementSibling()) != null) {
			//System.out.println(flightRow);
			Elements cells = flightRow.select("td");
			if (cells.size() == 1) {
				if (cells.get(0).text().equals("<<< connecting to >>>")) {
					flightRow = flightRow.nextElementSibling();
					cells = flightRow.select("td");
				} else {
					break;
				}
			}
			String number = cells.get(0).text(), depart = cells.get(1).text(), arrive = cells
					.get(2).text(), seatClass = cells.get(3).text();

			Flight flight = new Flight();
			flight.setAirline(getAirline());
			try {
				flight.setNumber(new Integer(number.split("(United | )")[1]));
			} catch (NumberFormatException e) {
				throw new ParseException("Could not parse flight number: " + number, 0);
			}
			Airport source = Ebean.find(Airport.class).where()
					.eq("code", depart.substring(0, 3)).findUnique();
			Airport destination = Ebean.find(Airport.class).where()
					.eq("code", arrive.substring(0, 3)).findUnique();

			Date date = dateFormat.parse(depart.substring(4));
			flight.setDate(date);

			Route route = Ebean.find(Route.class).where()
					.eq("airline", getAirline()).eq("source", source)
					.eq("destination", destination).findUnique();
			flight.setRoute(route);
			// next line has equipment, duration, fare code, miles and meal
			// info
			flightRow = flightRow.nextElementSibling();
			String info = flightRow.select("td").get(0).text();
			String equipment = info.split("(Equipment:\\s*|\\W*\\|)")[1];
			List<Equipment> e = Ebean.find(Equipment.class).where()
					.like("name", equipment + "%").findList();
			if (e.size() > 0) {
				if (e.size() > 1) {
					System.out.println("More than one equipment match: " + equipment);
					// TODO if more than one pick the parent. or reference
					// with seatings. or route.getEquipment()
				}
				flight.setEquipment(e.get(0));
			}

			flights.add(flight);
			}
		}
		return flights;
	}
}
