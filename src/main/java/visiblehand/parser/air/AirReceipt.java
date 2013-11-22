package visiblehand.parser.air;

import java.util.List;

import lombok.Data;
import visiblehand.entity.Airline;
import visiblehand.entity.Flight;
import visiblehand.parser.Receipt;

public @Data class AirReceipt extends Receipt {
	Airline airline;
	List<Flight> flights;
	String confirmation;
}
