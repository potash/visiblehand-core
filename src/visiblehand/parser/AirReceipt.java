package visiblehand.parser;

import java.util.List;

import lombok.Data;
import visiblehand.entity.Airline;
import visiblehand.entity.Flight;
import visiblehand.entity.JsonViews;

import com.fasterxml.jackson.annotation.JsonView;

public @Data class AirReceipt extends Receipt {
	@JsonView(JsonViews.Id.class)
	Airline airline;
	@JsonView(JsonViews.Id.class)
	List<Flight> flights;
	@JsonView(JsonViews.Id.class)
	String confirmation;
}
