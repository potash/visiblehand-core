package visiblehand.entity;

import java.util.Date;

public interface Emission {
	//TODO should really be a date range
	public Date getDate();
	public double getCO2();
}
