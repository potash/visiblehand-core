package visiblehand.parser;

import java.io.IOException;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.ParseException;

import lombok.Data;
import visiblehand.entity.Airline;
import visiblehand.entity.Flight;

// A skeleton for an airline email receipt parser

public abstract @Data class AirParser extends MessageParser {
	public abstract Airline getAirline();

	public abstract AirReceipt parse(Message message) throws java.text.ParseException, MessagingException, IOException;
}