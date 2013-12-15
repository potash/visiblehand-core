package visiblehand.parser.utility;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import visiblehand.entity.UtilityReceipt;
import lombok.Data;
import lombok.Getter;

import com.sun.mail.imap.Utility;

// United Airlines email receipt parser

public @Data class ComEdParser extends UtilityParser {
	private final String fromString = "mycheckfree@customercenter.net";
	private final String[] subjectStrings = {"You have a new bill from ComEd - Commonwealth Edison."};
	private final String  bodyString = "";
	
	private final Date parserDate = new Date(1387147448);	// December 15, 2013
	private final Date searchDate = new Date(1387047326);	// December 14, 2013
	
	@Getter(lazy = true)
	private final Utility utility = null;

	public UtilityReceipt parse(Message message) throws ParseException,
			MessagingException, IOException {

		UtilityReceipt receipt = new UtilityReceipt();
		String content = getContent(message);
		receipt.setUtility(getUtility());
		receipt.setDate(message.getSentDate());
		receipt.setCost(getCost(content));
	
		return receipt;
	}
	
	protected static double getCost(String content) {
		Document doc = Jsoup.parse(content);
		Element td = doc.select("td:matches(^Amount Due) + td").first();
		return Double.parseDouble(td.text().substring(1));
	}
}
