package visiblehand.parser;

import java.util.List;

import javax.mail.Message;
import javax.mail.search.AndTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import lombok.Data;
import lombok.Getter;
import visiblehand.Flight;
import visiblehand.entity.Airline;

// A skeleton for an airline email receipt parser

public abstract @Data class AirParser {
	public abstract String getFromString();
	public abstract String getSubjectString();
	public abstract Airline getAirline();
	
	@Getter(lazy=true)
	private final SearchTerm searchTerm = new AndTerm(new
		FromStringTerm(getFromString()),
		new SubjectTerm(getSubjectString()));
	
	public abstract List<Flight> getFlights(Message message);
}