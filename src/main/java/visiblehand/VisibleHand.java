package visiblehand;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import visiblehand.entity.Country;
import visiblehand.entity.Utility;
import visiblehand.entity.air.Airline;
import visiblehand.entity.air.Airport;
import visiblehand.entity.air.Equipment;
import visiblehand.entity.air.EquipmentAggregate;
import visiblehand.entity.air.Flight;
import visiblehand.entity.air.FuelData;
import visiblehand.entity.air.Route;
import visiblehand.entity.air.Seating;
import visiblehand.oauth2.OAuth2Authenticator;
import visiblehand.parser.MessageParser;
import visiblehand.parser.air.AAParser;
import visiblehand.parser.air.AirParser;
import visiblehand.parser.air.ContinentalParser;
import visiblehand.parser.air.DeltaParser;
import visiblehand.parser.air.JetBlueParser;
import visiblehand.parser.air.SouthwestParser;
import visiblehand.parser.air.UnitedParser;
import visiblehand.parser.air.UnitedParser2;
import visiblehand.parser.utility.ComEdParser;
import visiblehand.parser.utility.ElectricityParser;
import visiblehand.parser.utility.PeoplesGasParser;
import visiblehand.parser.utility.UtilityParser;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.text.csv.CsvReader;
import com.sun.mail.imap.IMAPStore;

public class VisibleHand {
	static final Logger logger = LoggerFactory.getLogger(VisibleHand.class);

	// A-1 jet fuel properties (wikipedia)
	public static final double KG_FUEL_PER_LITER = .804,
			MEGAJOULE_PER_LITER_FUEL = 34.7;
	// emission factor of jet fuel
	// (http://www.eia.gov/oiaf/1605/coefficients.html)
	public static final double KG_CO2_PER_GALLON_FUEL = 9.57;
	// unit conversions
	public static final double MILES_PER_NM = 1.15078,
			LITERS_PER_GALLON = 3.78541, BTU_PER_MEGAJOULE = 947.81712;

	private static boolean ebeanInitialized = false;

	public static final AirParser[] airParsers = { new AAParser(),
			new UnitedParser(), new SouthwestParser(), new UnitedParser2(),
			new DeltaParser(), new JetBlueParser(), new ContinentalParser() };

	public static final ElectricityParser[] electricParsers = { new ComEdParser() };
	public static final UtilityParser[] gasParsers = {	new PeoplesGasParser() };
	
	public static final List<MessageParser> parsers = new ArrayList<MessageParser>(); {
		parsers.addAll(Arrays.asList(airParsers));
		parsers.addAll(Arrays.asList(electricParsers));
		parsers.addAll(Arrays.asList(gasParsers));
	}

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

	public static Folder getGoogleInbox(String email, String oauthToken)
			throws MessagingException {

		OAuth2Authenticator.initialize();

		IMAPStore imapStore = OAuth2Authenticator.connectToImap(
				"imap.gmail.com", 993, email, oauthToken, false);
		Folder inbox = imapStore.getFolder("[Gmail]/All Mail");
		inbox.open(Folder.READ_ONLY);
		return inbox;
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
	public static Properties getProperties(String name) {
		Properties props = new Properties();

		InputStream stream = VisibleHand.class.getResourceAsStream("/" + name);
		try {
			props.load(stream);
		} catch (Exception e) {
			logger.warn("Error loading " + name);
		}

		return props;
	}

	public static Session getSession(Properties props)
			throws MessagingException, FileNotFoundException, IOException {
		Session session = Session.getInstance(props);
		return session;
	}

	// loads data from csv into in memory database
	public static void initEbean() throws IOException {
		if (!ebeanInitialized) {
			Properties props = getProperties("ebean.properties");
			String db = props.getProperty("datasource.default");
			if (db == null || db.equals("h2")) {
				ServerConfig c = new ServerConfig();
				c.setName("h2");
				c.loadFromProperties();
				c.setDdlGenerate(true);
				c.setDdlRun(true);
				c.setDefaultServer(true);

				DataSourceConfig config = new DataSourceConfig();
				config.setDriver("org.h2.Driver");
				config.setUsername("sa");
				config.setPassword("");
				config.setUrl("jdbc:h2:mem:visiblehand");
				c.setDataSourceConfig(config);
				c.addPackage("visiblehand.entity");

				EbeanServer h2 = EbeanServerFactory.create(c);
				SqlUpdate update = Ebean
						.createSqlUpdate("CREATE ALIAS LEVENSHTEIN FOR "
								+ "\"visiblehand.VisibleHand.getLevenshteinDistance\"");
				update.execute();

				loadCsvData();
			}
			ebeanInitialized = true;
		}
	}

	public static void loadCsvData() {
		String csvDirectory = "/csv/";

		Class<?>[] entities = new Class<?>[] { Airline.class, Airport.class,
				Equipment.class, EquipmentAggregate.class, FuelData.class,
				Route.class, Seating.class, Country.class, Utility.class };

		for (Class<?> entity : entities) {
			Reader reader = new InputStreamReader(
					VisibleHand.class.getResourceAsStream(csvDirectory
							+ entity.getSimpleName() + ".csv"));

			CsvReader<?> csv = Ebean.createCsvReader(entity);
			csv.setAddPropertiesFromHeader();
			try {
				csv.process(reader);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// h2 needs the method to take Strings, not CharSequences
	public static int getLevenshteinDistance(String s1, String s2) {
		return org.apache.commons.lang3.StringUtils.getLevenshteinDistance(s1,
				s2);
	}

	public static String getSearchString() {
		String str = "";
		for (AirParser parser : airParsers) {
			str += parser.getSearchString() + " || ";
		}
		return str.substring(0, str.length() - 3);
	}

	public static void printStatistics(List<Flight> flights) {
		double fuel = 0;
		double nm = 0;
		DescriptiveStatistics sigma = new DescriptiveStatistics(), nmpkg = new DescriptiveStatistics();

		for (Flight flight : flights) {
			logger.debug(flight.toString());
			DescriptiveStatistics fuelBurn = flight.getFuelBurnStatistics();
			logger.debug(fuelBurn.toString());
			if (fuelBurn.getValues().length > 0) {
				fuel += fuelBurn.getMean();
				nm += flight.getRoute().getDistance();
				sigma.addValue(fuelBurn.getStandardDeviation()
						/ fuelBurn.getMean());
				nmpkg.addValue(flight.getRoute().getDistance()
						/ fuelBurn.getMean());
			}
			Ebean.save(flight);

		}
		System.out.println("Fuel burned: " + fuel + " kg");
		System.out.println("Average std dev: " + (sigma.getMean() * 100) + "%");
		System.out.println("Distance traveled: " + nm + " nm");

		System.out.println("Fuel economy: " + nmpkg.getMean() * MILES_PER_NM
				* KG_FUEL_PER_LITER * LITERS_PER_GALLON + " mpg");

		System.out.println("Carbon dioxide emissions: " + fuel
				/ KG_FUEL_PER_LITER / LITERS_PER_GALLON
				* KG_CO2_PER_GALLON_FUEL + " kg");
	}
}
