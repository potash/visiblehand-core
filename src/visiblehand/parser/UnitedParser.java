package visiblehand.parser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import lombok.Data;
import lombok.Getter;
import visiblehand.Flight;
import visiblehand.entity.Airline;

import com.avaje.ebean.Ebean;

// United Airlines email receipt parser

public @Data class UnitedParser extends AirParser {
	private final  String fromString = "unitedairlines@united.com";
	private final String subjectString = "eTicket Itinerary and Receipt for Confirmation";
	@Getter(lazy = true)
	private final Airline airline = Ebean.find(Airline.class, 5209);

	private DateFormat dateFormat = new SimpleDateFormat(
			"h:mm a EEE, MMM d, yyyy");

	public AirReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {

		AirReceipt receipt = new AirReceipt();
		receipt.setFlights(parse(getContent(message)));
		receipt.setAirline(getAirline());
		receipt.setConfirmation(message.getSubject().substring(48));
		receipt.setDate(message.getSentDate());
		
		return receipt;
	}

	protected static List<Flight> parse(String content) throws ParseException {
		List<Flight> flights = new ArrayList<Flight>();
		// TODO implement this!
		return flights;
	}
}
