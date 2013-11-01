package visiblehand.parser.air;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;

import lombok.Getter;
import visiblehand.entity.Airline;
import visiblehand.parser.MessageParser;

// A skeleton for an airline email receipt parser

public abstract class AirParser extends MessageParser {
	public abstract Airline getAirline();
	public abstract AirReceipt parse(Message message) throws java.text.ParseException, MessagingException, IOException;

	@Getter
	private final Class receiptClass = AirReceipt.class;
}