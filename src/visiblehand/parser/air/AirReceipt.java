package visiblehand.parser.air;

import java.util.List;

import lombok.Data;
import visiblehand.entity.Airline;
import visiblehand.entity.Flight;
import visiblehand.parser.MessageParserTest;
import visiblehand.parser.Receipt;
import visiblehand.parser.MessageParserTest.TestView;

import com.fasterxml.jackson.annotation.JsonView;

public @Data class AirReceipt extends Receipt {
	@JsonView(MessageParserTest.TestView.class)
	Airline airline;
	@JsonView(MessageParserTest.TestView.class)
	List<Flight> flights;
	@JsonView(MessageParserTest.TestView.class)
	String confirmation;
}
