package visiblehand.parser.utility;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;

import lombok.Data;
import lombok.Getter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import visiblehand.entity.utility.Electricity;
import visiblehand.entity.utility.ElectricityReceipt;
import visiblehand.entity.utility.Utility;

import com.avaje.ebean.Ebean;

// United Airlines email receipt parser

public @Data class ComEdParser extends ElectricityParser {
	private final String fromString = "mycheckfree@customercenter.net";
	private final String[] subjectStrings = {"You have a new bill from ComEd - Commonwealth Edison."};
	private final String  bodyString = "";
	
	private final Date parserDate = new Date(1387147448);	// December 15, 2013
	private final Date searchDate = new Date(1387047326);	// December 14, 2013
	
	@Getter(lazy = true)
	private final Utility utility = Ebean.find(Utility.class)
			.where().eq("name", "Commonwealth Edison").findUnique();

	public ElectricityReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {

		ElectricityReceipt receipt = new ElectricityReceipt();
		String content = getContent(message);
		receipt.setDate(message.getSentDate());
		receipt.setUtility(getUtility());
		
		Electricity electricity = new Electricity();
		electricity.setCost(getCost(content));
		electricity.setEmissionDate(subtractMonth(message.getSentDate()));
		
		receipt.setElectricity(electricity);
	
		return receipt;
	}
	
	protected static double getCost(String content) {
		Document doc = Jsoup.parse(content);
		Element td = doc.select("td:matches(^Amount Due) + td").first();
		return Double.parseDouble(td.text().substring(1));
	}
}
