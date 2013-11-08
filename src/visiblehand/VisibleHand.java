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
import visiblehand.parser.air.AAParser;
import visiblehand.parser.air.AirParser;
import visiblehand.parser.air.ContinentalParser;
import visiblehand.parser.air.DeltaParser;
import visiblehand.parser.air.JetBlueParser;
import visiblehand.parser.air.SouthwestParser;
import visiblehand.parser.air.UnitedParser;
import visiblehand.parser.air.UnitedParserOld;
import visiblehand.parser.utility.ComEdParser;
import visiblehand.parser.utility.PeoplesGasParser;
import visiblehand.parser.utility.UtilityParser;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.config.ServerConfig;

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
	
	public static final UtilityParser[] utilityParsers = { new ComEdParser(), new PeoplesGasParser() };

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
		Properties props = getProperties("mail.properties");
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

	// get properties file either in classpath or current dir
	public static Properties getProperties(String name) throws IOException {
		Properties props = new Properties();

		InputStream stream = VisibleHand.class
				.getResourceAsStream("/" + name);
		if (stream == null)
			stream = new FileInputStream("" + name);
		props.load(stream);

		return props;
	}

	public static Session getSession(Properties props)
			throws MessagingException, FileNotFoundException, IOException {
		Session session = Session.getInstance(props);
		return session;
	}

	// loads data from csv into in memory database
	public static void initEbean() throws IOException {
		Properties props = getProperties("ebean.properties");
		String db = props.getProperty("datasource.default");
		if (db.equals("h2")) {
			ServerConfig c = new ServerConfig();
			c.setName("h2");
			c.loadFromProperties();
			c.setDdlGenerate(true);
			c.setDdlRun(true);
			c.setDefaultServer(true);
			EbeanServer h2 = EbeanServerFactory.create(c);
			SqlUpdate lev = Ebean.createSqlUpdate("CREATE ALIAS LEVENSHTEIN FOR \"visiblehand.VisibleHand.getLevenshteinDistance\"");
			lev.execute();
			String[] tables = new String[] { "airline", "airport", "equipment",
					"equipment_aggregate", "fuel_data", "route", "seating", "country" };

			for (String table : tables) {
				SqlUpdate update = Ebean.createSqlUpdate("insert into " + table
						+ " (select * from csvread('data/csv/" + table
						+ ".csv'))");
				h2.execute(update);
			}
		}
	}
	
	// h2 needs the method to take Strings, not CharSequences
	public static int getLevenshteinDistance(String s1, String s2) {
		return org.apache.commons.lang3.StringUtils.getLevenshteinDistance(s1, s2);
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

		initEbean();
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

		printStatistics(flights);
		SqlUpdate write = Ebean
				.createSqlUpdate("call csvwrite('data/csv/flight.csv', 'SELECT * FROM FLIGHT')");
		Ebean.execute(write);
	}

	public static void printStatistics(List<Flight> flights) {
		double fuel = 0;
		double nm = 0;
		DescriptiveStatistics sigma = new DescriptiveStatistics(), nmpkg = new DescriptiveStatistics();

		for (Flight flight : flights) {
			System.out.println(flight);
			DescriptiveStatistics fuelBurn = flight.getFuelBurnStatistics();
			System.out.println(fuelBurn);
			if (fuelBurn.getValues().length > 0) {
				fuel += fuelBurn.getMean();
				nm += flight.getRoute().getDistance();
				sigma.addValue(fuelBurn.getStandardDeviation() / fuelBurn.getMean());
				nmpkg.addValue(flight.getRoute().getDistance() / fuelBurn.getMean());
			}
			Ebean.save(flight);
			
		}
		System.out.println("Fuel burned: " + fuel + " kg");
		System.out.println("Average std dev: " + (sigma.getMean()*100) + "%");
		System.out.println("Distance traveled: " + nm + " nm");

		System.out.println("Fuel economy: " + nmpkg.getMean() * MILES_PER_NM
				* KG_FUEL_PER_LITER * LITERS_PER_GALLON + " mpg");

		System.out.println("Carbon dioxide emissions: " + fuel
				/ KG_FUEL_PER_LITER / LITERS_PER_GALLON
				* KG_CO2_PER_GALLON_FUEL + " kg");
	}
}
