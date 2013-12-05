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

import visiblehand.entity.AirReceipt;
import visiblehand.entity.Airline;
import visiblehand.entity.Airport;
import visiblehand.entity.Equipment;
import visiblehand.entity.Flight;
import visiblehand.entity.Route;

import com.avaje.ebean.Ebean;

// United Airlines email receipt parser

public @Data class UnitedParser2 extends AirParser {
	private final String fromString = "unitedairlines@united.com";
	private final String[] subjectStrings = {"eTicket Itinerary and Receipt for Confirmation"};
	private final String bodyString = "";

	@Getter(lazy = true)
	private final Airline airline = Ebean.find(Airline.class).where().eq("name", "United Airlines").findUnique();

	private static final DateFormat issueFormat = getGMTSimpleDateFormat("MMMM dd, yyyy"),
									dateFormat = getGMTSimpleDateFormat("EEE, ddMMMyyhh:mm a");
	
	private static final Pattern airportPattern = Pattern.compile("\\((?<code>[A-Z]*).*\\)"),
								 timePattern = Pattern.compile("(?<time>\\d{1,2}:\\d{2} (A|P)M)"),
						 		 issuePattern = Pattern.compile("(?i)(?<date>" + mmmmRegex + " \\d{2}, \\d{4})");
	public AirReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {

		AirReceipt receipt = new AirReceipt(message);
		String content = getContent(message);
		Document doc = Jsoup.parse(content);
		receipt.setFlights(getFlights(doc));
		receipt.setAirline(getAirline());
		receipt.setConfirmation(getConfirmation(doc));
		receipt.setDate(getIssueDate(doc));
		
		return receipt;
	}
	
	private static Date getIssueDate(Document doc) throws ParseException {
		Element e =  doc.select(":containsOwn(Issue Date:)").first();
		Matcher matcher = issuePattern.matcher(e.text());
		matcher.find();
		return issueFormat.parse(matcher.group("date"));
	}

	protected static String getConfirmation(Document doc) {
		Element e = doc.select(".eTicketConfirmation").first();
		return e.text();
	}
	protected List<Flight> getFlights(Document doc) throws ParseException {
		List<Flight> flights = new ArrayList<Flight>();
		Element flightTable = doc.select(":containsOwn(FLIGHT INFORMATION)").first().parent().parent();
		
		for (Element flightRow : flightTable.select("tr:gt(1):has(td:gt(0))")) {
			Elements cells = flightRow.select("td");
			
			Matcher matcher = flightCodePattern.matcher(cells.get(1).select("span").html());
			matcher.find();
			Integer number = Integer.parseInt(matcher.group("number"));
			Airline airline = Airline.findByIATA(matcher.group("airline"));
			
			matcher = airportPattern.matcher(cells.get(3).text());
			matcher.find();
			Airport source = Airport.findByCode(matcher.group("code"));
			
			matcher = timePattern.matcher(cells.get(3).text());
			matcher.find();
			String time = matcher.group("time");
			Date date = dateFormat.parse(cells.get(0).text() + time);
			
			matcher = airportPattern.matcher(cells.get(4).text());
			matcher.find();
			Airport destination = Airport.findByCode(matcher.group("code"));
			
			Route route = Route.find(getAirline(), source, destination);
			
			Equipment equipment = Equipment.findByName(cells.get(5).text());
			
			Flight flight = Flight.get(route, date, number, equipment);
			flights.add(flight);
		}
		return flights;
	}
}
