package visiblehand.parser;

import java.util.List;

import lombok.Data;
import visiblehand.entity.Airline;
import visiblehand.entity.Flight;

import com.fasterxml.jackson.annotation.JsonView;

public @Data class AirReceipt extends Receipt {
	@JsonView(MessageParserTest.TestView.class)
	Airline airline;
	@JsonView(MessageParserTest.TestView.class)
	List<Flight> flights;
	@JsonView(MessageParserTest.TestView.class)
	String confirmation;
}
