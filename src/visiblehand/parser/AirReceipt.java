package visiblehand.parser;

import java.util.List;

import lombok.Data;
import visiblehand.Flight;
import visiblehand.entity.Airline;

public @Data class AirReceipt extends Receipt {
	Airline airline;
	List<Flight> flights;
	String confirmation;
}
