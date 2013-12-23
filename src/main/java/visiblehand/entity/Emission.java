package visiblehand.entity;

import java.util.Date;

public interface Emission {
	//TODO should really be a date range
	public Date getDate();
	public Double getCO2();
	public Integer getSplit();
}
