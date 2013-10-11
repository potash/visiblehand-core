package visiblehand.parser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
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
class UnitedParser extends AirParser {
	private String fromString = "united.com";
	private String subjectString = "eTicket Itinerary and Receipt for Confirmation";

	private DateFormat dateFormat = new SimpleDateFormat(
			"h:mm a EEE, MMM d, yyyy");

	@Transient
	@Getter(lazy = true)
	private final Airline airline = Ebean.find(Airline.class, 24);

	public List<Flight> parse(Message message) throws ParseException,
			MessagingException, IOException {
		return parse(getContent(message));
	}

	public List<Flight> parse(String content) throws ParseException {
		List<Flight> flights = new ArrayList<Flight>();

		Document doc = Jsoup.parse(content);
		Elements flightTable = doc.select("#flightTable");

		if (flightTable.size() == 0) {
			throw new ParseException("No flight table found.", 0);
		}

		Elements flightRows = flightTable.get(0).select(
				"tr:has(td:contains(Depart))");
		for (Element flightRow : flightRows) {
			Elements cells = flightRow.nextElementSibling().select("td");
			String number = cells.get(0).text(), depart = cells.get(1).text(), arrive = cells
					.get(2).text(), seatClass = cells.get(3).text();

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
			flight.setRoute(route);
			// next line has equipment, duration, fare code, miles and meal
			// info
			String info = flightRow.nextElementSibling().nextElementSibling()
					.select("td").get(0).text();
			String equipment = info.split("(Equipment:\\s*|\\W*\\|)")[1];
			
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
	}
}
