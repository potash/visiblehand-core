package visiblehand;

import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.PasswordAuthentication;
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

import visiblehand.entity.Flight;
import visiblehand.parser.AAParser;
import visiblehand.parser.AirParser;
import visiblehand.parser.ContinentalParser;
import visiblehand.parser.DeltaParser;
import visiblehand.parser.JetBlueParser;
import visiblehand.parser.SouthwestParser;
import visiblehand.parser.UnitedParser;
import visiblehand.parser.UnitedParserOld;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlUpdate;

public class VisibleHand {

	// density of A-1 jet fuel (wikipedia)
	public static final double KG_FUEL_PER_LITER = .804;
	// emission factor of jet fuel
	// (http://www.eia.gov/oiaf/1605/coefficients.html)
	public static final double KG_CO2_PER_GALLON_FUEL = 9.57;
	// unit conversions
	public static final double MILES_PER_NM = 1.15078,
			LITERS_PER_GALLON = 3.78541;

	public static final AirParser[] airParsers = { new AAParser(),
			new UnitedParserOld(), new SouthwestParser(), new UnitedParser(),
			new DeltaParser(), new JetBlueParser(), new ContinentalParser() };

	public static Folder getFolder(Properties props, Session session,
			PasswordAuthentication auth) throws FileNotFoundException,
			MessagingException, IOException {
		Store store = session.getStore();
		int port = -1;
		if (props.getProperty("mail.port") != null) {
			try {
				port = Integer.parseInt(props.getProperty("mail.port"));
			} catch (NumberFormatException e) {
				System.err.println("NumberFormatException mail.port="
						+ props.getProperty("mail.port"));
			}
		}
		store.connect(null, port, auth.getUserName(),
				new String(auth.getPassword()));

		String name = props.getProperty("mail.folder");
		if (name == null)
			name = "Inbox";
		Folder folder = store.getFolder(name);
		folder.open(Folder.READ_ONLY);

		return folder;
	}

	public static Folder getInbox() throws MessagingException,
			FileNotFoundException, IOException {
		Properties props = getProperties();
		Session session = getSession(props);
		return getFolder(props, session, getPasswordAuthentication());
	}

	public static PasswordAuthentication getPasswordAuthentication() {
		Console console = System.console();

		System.out.print("Username:");
		final String user = console.readLine();
		System.out.print("Password:");
		final char[] pass = console.readPassword();

		return new java.net.PasswordAuthentication(user, pass);
	}

	public static Properties getProperties() throws IOException {
		Properties props = new Properties();

		InputStream stream = VisibleHand.class
				.getResourceAsStream("/mail.properties");
		if (stream == null)
			stream = new FileInputStream("mail.properties");
		props.load(stream);

		return props;
	}

	public static Session getSession(Properties props)
			throws MessagingException, FileNotFoundException, IOException {
		Session session = Session.getInstance(props);
		return session;
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

	public static String getSearchString() {
		String str = "";
		for (AirParser parser : airParsers) {
			str += parser.getSearchString() + " || ";
		}
		return str.substring(0, str.length() - 3);
	}

	public static void main(String[] args) throws MessagingException,
			ParseException, IOException {

		loadData();
		Folder inbox = getInbox();
		List<Flight> flights = new ArrayList<Flight>();

		for (AirParser parser : airParsers) {
			if (parser.isActive()) {
				for (Message message : inbox.search(parser.getSearchTerm())) {
					System.out.println(message.getSubject());
					flights.addAll(parser.parse(message).getFlights());
				}
			}
		}

		double fuel = 0;
		double nm = 0;
		DescriptiveStatistics sigma = new DescriptiveStatistics(), nmpkg = new DescriptiveStatistics();

		for (Flight flight : flights) {
			System.out.println(flight.getRoute());
			DescriptiveStatistics fuelBurn = flight.getRoute()
					.getFuelBurnStatistics();
			System.out.println(fuelBurn);
			fuel += fuelBurn.getMean();
			flight.setFuelBurn(fuelBurn.getMean());
			flight.setDistance(flight.getRoute().getDistance());
			nm += flight.getDistance();
			Ebean.save(flight);
			sigma.addValue(fuelBurn.getStandardDeviation() / fuelBurn.getMean());
			nmpkg.addValue(flight.getRoute().getDistance() / fuelBurn.getMean());
		}
		System.out.println("Fuel burned: " + fuel + " kg");
		System.out.println("Distance traveled: " + nm + " nm");
		
		System.out.println("Fuel economy: " + nmpkg.getMean() * MILES_PER_NM * KG_FUEL_PER_LITER
				* LITERS_PER_GALLON + " mpg");
		
		System.out.println("Carbon dioxide emissions: " + fuel / KG_FUEL_PER_LITER / LITERS_PER_GALLON * KG_CO2_PER_GALLON_FUEL + " kg");
		
		SqlQuery writeQuery = Ebean
				.createSqlQuery("call csvwrite('data/csv/flight.csv', 'SELECT * FROM FLIGHT')");
		writeQuery.findList();
	}
}
