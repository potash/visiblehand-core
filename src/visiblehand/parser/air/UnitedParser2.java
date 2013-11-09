package visiblehand.parser.air;

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
import visiblehand.entity.Airline;
import visiblehand.entity.Flight;

import com.avaje.ebean.Ebean;

// United Airlines email receipt parser

public @Data class UnitedParser2 extends AirParser {
	private final String fromString = "unitedairlines@united.com";
	private final String[] subjectStrings = {"eTicket Itinerary and Receipt for Confirmation"};
	private final String bodyString = "";

	@Getter(lazy = true)
	private final Airline airline = Ebean.find(Airline.class, 5209);

	private DateFormat dateFormat = new SimpleDateFormat(
			"h:mm a EEE, MMM d, yyyy");

	public AirReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {

		AirReceipt receipt = new AirReceipt();
		receipt.setFlights(getFlights(getContent(message)));
		receipt.setAirline(getAirline());
		receipt.setConfirmation(getConfirmation(message.getSubject()));
		receipt.setDate(message.getSentDate());
		
		return receipt;
	}

	protected static String getConfirmation(String subject) {
		return subject.substring(48);
	}
	protected List<Flight> getFlights(String content) throws ParseException {
		List<Flight> flights = new ArrayList<Flight>();
		// TODO implement this!
		return flights;
	}
}
