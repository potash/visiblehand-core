package visiblehand.entity;

import java.util.Date;

public interface Emission {
	//TODO should really be a date range
	public Date getEmissionDate();
	public Double getCO2();
	public Integer getSplit();
}
