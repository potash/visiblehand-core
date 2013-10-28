package visiblehand;

import java.util.Date;

import lombok.Data;
import visiblehand.entity.Airline;
import visiblehand.entity.Equipment;
import visiblehand.entity.Route;

public @Data class Flight {
	private Route route;
	private Date date;
	private Integer number;
	private Equipment equipment;
	private Airline airline;
}
