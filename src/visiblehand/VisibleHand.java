package visiblehand;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.persistence.OptimisticLockException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import visiblehand.parser.AAParser;
import visiblehand.parser.AirParser;

import com.fasterxml.jackson.core.JsonProcessingException;

public class VisibleHand {

	private static final AirParser[] airParsers = {new AAParser()};

	public static Folder getInbox(String user, char[] password)
			throws MessagingException {
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "imaps");

		final String host = "imap.gmail.com";

		Session session = Session.getInstance(props, null);
		Store store = session.getStore();
		store.connect(host, user, new String(password));
		Folder inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_ONLY);

		return inbox;
	}

	public static void main(String[] args) throws OptimisticLockException,
			JsonProcessingException, IOException, MessagingException {

		Console console = System.console();
		System.out.print("Username:");
		String user = console.readLine();
		System.out.print("Password:");
		Folder inbox = getInbox(user, console.readPassword());
		
		List<Flight> flights = new ArrayList<Flight>();
		
		for (AirParser parser : airParsers) {
			for (Message message : inbox.search(parser.getSearchTerm())) {
				System.out.println(message.getSubject());
				flights.addAll(parser.getFlights(message));
			}
		}

		double fuel = 0;
		double nm = 0;
		DescriptiveStatistics sigma = new DescriptiveStatistics(),
				nmpg = new DescriptiveStatistics();
		
		for (Flight flight : flights) {
			System.out.println(flight.getRoute());
			DescriptiveStatistics fuelBurn = flight.getRoute().getFuelBurn();
			System.out.println(fuelBurn);
			fuel += fuelBurn.getMean();
			nm += flight.getRoute().getDistance();
			sigma.addValue(fuelBurn.getStandardDeviation()/fuelBurn.getMean());
			nmpg.addValue(flight.getRoute().getDistance() / fuelBurn.getMean());
		}
		System.out.println(fuel);
		System.out.println(nm);
		System.out.println(sigma);
		System.out.println(nmpg);
		System.out.println(nmpg.getMean()*1.151*.81*3.785); // miles per gallon!
	}
}
