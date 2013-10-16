package visiblehand.parser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.AndTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import lombok.Data;
import lombok.Getter;
import visiblehand.Flight;
import visiblehand.entity.Airline;

import com.avaje.ebean.Ebean;

// United Airlines email receipt parser

public @Data class DeltaParser extends AirParser {
	private final  String fromString = "DeltaAirLines@e.delta.com";
	private final String subjectString = "";
	
	@Getter(lazy = true)
	private final Airline airline = Ebean.find(Airline.class, 5209);

	private DateFormat dateFormat = new SimpleDateFormat(
			"h:mm a EEE, MMM d, yyyy");
	{
	searchTerm = new AndTerm(new FromStringTerm(getFromString()), new NotTerm(new SubjectTerm("Check-In")));
	}
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
	
	public String getSearchString() {
		return "(from:" + getFromString() + " and !subject:\"Check-In\")";
	}
}
