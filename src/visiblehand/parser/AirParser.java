package visiblehand.parser;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;

import lombok.Data;
import visiblehand.entity.Airline;

// A skeleton for an airline email receipt parser

public abstract @Data class AirParser extends MessageParser {
	public abstract Airline getAirline();

	public abstract AirReceipt parse(Message message) throws java.text.ParseException, MessagingException, IOException;
}