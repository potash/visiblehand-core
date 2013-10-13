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

	public AirReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {
		
		AirReceipt receipt = new AirReceipt();
		receipt.setFlights(parse(getContent(message)));
		receipt.setAirline(getAirline());
		receipt.setConfirmation(message.getSubject().substring(48));
		
		return receipt;
	}

	public List<Flight> parse(String content) throws ParseException {
		List<Flight> flights = new ArrayList<Flight>();
		//TODO implement this!
		return flights;
	}
}
