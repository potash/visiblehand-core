package visiblehand.parser.air;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import lombok.Data;
import lombok.Getter;
import visiblehand.entity.air.AirReceipt;
import visiblehand.entity.air.Airline;
import visiblehand.entity.air.Flight;

import com.avaje.ebean.Ebean;

public @Data class DeltaParser extends AirParser {
	private final String fromString = "DeltaAirLines@e.delta.com";
	private final String[] subjectStrings = null;
	private final String  bodyString = "Delta Reservation Receipt";
	
	private final Date parserDate = null;
	private final Date searchDate = new Date(1387047326);	// December 14, 2013
	
	@Getter(lazy = true)
	private final Airline airline = Ebean.find(Airline.class).where().eq("name", "Delta Air Lines").findUnique();

	private DateFormat dateFormat = getGMTSimpleDateFormat("h:mm a EEE, MMM d, yyyy");
	
	public AirReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {

		AirReceipt receipt = new AirReceipt(message);
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
