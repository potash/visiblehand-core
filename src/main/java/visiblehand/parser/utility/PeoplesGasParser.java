package visiblehand.parser.utility;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;

import lombok.Data;
import lombok.Getter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import visiblehand.entity.utility.NaturalGas;
import visiblehand.entity.utility.NaturalGasReceipt;
import visiblehand.entity.utility.Utility;

// United Airlines email receipt parser

public @Data class PeoplesGasParser extends NaturalGasParser {
	private final String fromString = "CustomerService@peoplesgas.ebillservice.net";
	private final String[] subjectStrings = {"Your latest Peoples Gas e-Bill is now available"};
	private final String  bodyString = "";
	
	private final Date parserDate = new Date(1387147448);	// December 15, 2013
	private final Date searchDate = new Date(1387047326);	// December 14, 2013
	
	@Getter(lazy = true)
	private final Utility utility = null;

	public NaturalGasReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {

		NaturalGasReceipt receipt = new NaturalGasReceipt();
		String content = getContent(message);
		
		NaturalGas gas = new NaturalGas();
		gas.setUtility(getUtility());
		gas.setDate(subtractMonth(message.getSentDate()));
		gas.setCost(getCost(content));
		receipt.setNaturalGas(gas);
	
		return receipt;
	}

	protected static double getCost(String content) {
		Document doc = Jsoup.parse(content);
		Element td = doc.select("td:containsOwn(Amount Due on) + td").first();
		
		return Double.parseDouble(td.text().substring(1));
	}
	
	
}
