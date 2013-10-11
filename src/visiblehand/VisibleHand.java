package visiblehand;

import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import visiblehand.parser.AAParser;
import visiblehand.parser.AirParser;
import visiblehand.parser.UnitedParserOld;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlUpdate;

public class VisibleHand {

	public static final AirParser[] airParsers = {new AAParser(), new UnitedParserOld()};

	public static Folder getInbox(Properties props, String user, char[] password)
			throws MessagingException {
		
		Session session = Session.getInstance(props, null);
		Store store = session.getStore();
		store.connect(user, new String(password));
		Folder inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_ONLY);

		return inbox;
	}
	
	// loads data from csv into in memory database
	public static void loadData() {
		EbeanServer h2 = Ebean.getServer("h2");
		String[] tables = new String[] { "airline", "airport", "equipment",
				"equipment_aggregate", "fuel_data", "route", "seating" };

		for (String table : tables) {
			SqlUpdate update = Ebean.createSqlUpdate("insert into " + table
					+ " (select * from csvread('data/csv/" + table + ".csv'))");
			h2.execute(update);
		}
	}

	public static void main(String[] args) throws MessagingException, ParseException, IOException {
		loadData();
		Console console = System.console();
		System.out.print("Username:");
		String user = console.readLine();
		System.out.print("Password:");
		
		Properties props = new Properties();


		props.load(new FileInputStream("mail.properties"));
		Folder inbox = getInbox(props, user, console.readPassword());
		
		List<Flight> flights = new ArrayList<Flight>();
		
		for (AirParser parser : airParsers) {
			for (Message message : inbox.search(parser.getSearchTerm())) {
				System.out.println(message.getSubject());
				flights.addAll(parser.parse(message));
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
