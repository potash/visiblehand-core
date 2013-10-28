package visiblehand.parser;

import java.io.IOException;
import java.text.ParseException;
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

public @Data class JetBlueParser extends AirParser {
	private final  String fromString = "reservations@jetblue.com";
	private final String subjectString = "Itinerary for your upcoming trip";
	
	@Getter(lazy = true)
	private final Airline airline = Ebean.find(Airline.class, 3029);

	public AirReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {

		AirReceipt receipt = new AirReceipt();
		String content = getContent(message);
		receipt.setFlights(getFlights(getContent(message)));
		receipt.setAirline(getAirline());
		receipt.setConfirmation(getConfirmation(content));
		receipt.setDate(message.getSentDate());
	
		return receipt;
	}

	protected static String getConfirmation(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	protected List<Flight> getFlights(String content) throws ParseException {
		List<Flight> flights = new ArrayList<Flight>();
		// TODO implement this!
		return flights;
	}
}
