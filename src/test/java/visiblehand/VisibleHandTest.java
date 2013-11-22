package visiblehand;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.Test;

import visiblehand.entity.Flight;
import visiblehand.parser.MessageParserTest;
import visiblehand.parser.air.AirParser;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;

public class VisibleHandTest extends EbeanTest {

	@Test
	public void test() throws FileNotFoundException, MessagingException, ParseException, IOException {
		List<Flight> flights = new ArrayList<Flight>();
		for (AirParser parser : VisibleHand.airParsers) {
			if (parser.isActive()) {
				for (Message message : MessageParserTest.getTestMessages(parser)) {
					System.out.println(message.getSubject());
					flights.addAll(parser.parse(message).getFlights());
				}
			}
		}

		VisibleHand.printStatistics(flights);
		SqlUpdate write = Ebean
				.createSqlUpdate("call csvwrite('data/csv/flight.csv', 'SELECT * FROM FLIGHT')");
		Ebean.execute(write);
	}

}
