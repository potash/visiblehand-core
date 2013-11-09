package visiblehand.parser.air;

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

public @Data class ContinentalParser extends AirParser {
	private final  String fromString = "continentalairlines@continental.com";
	private final String[] subjectStrings = {"eTicket Itinerary and Receipt"};
	private final String bodyString = null;
	
	@Getter(lazy = true)
	private final Airline airline = Ebean.find(Airline.class, 1881);

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
