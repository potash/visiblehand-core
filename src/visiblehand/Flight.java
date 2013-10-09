package visiblehand;

import java.util.Date;

import lombok.Data;
import visiblehand.entity.Route;
import visiblehand.entity.Seating;

public @Data class Flight {
	public Flight(Date date, Route route) {
		this.date = date;
		this.route = route;
	}
	private Route route;
	private Date date;
	// Route route?
}
