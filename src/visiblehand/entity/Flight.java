package visiblehand.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import visiblehand.parser.MessageParserTest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public @Data class Flight {
	@Id
	private int id;
	@ManyToOne
	@JoinColumn(name="airline_id")
	@JsonView(MessageParserTest.TestView.class)
	private Airline airline;
	@ManyToOne
	@JoinColumn(name="route_id")
	@JsonView(MessageParserTest.TestView.class)
	private Route route;
	@JsonView(MessageParserTest.TestView.class)
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm")
	private Date date;
	@JsonView(MessageParserTest.TestView.class)
	private Integer number;
	@ManyToOne
	@JoinColumn(name="equipment_id")
	@JsonView(MessageParserTest.TestView.class)
	private Equipment equipment;
	
	public DescriptiveStatistics getFuelBurnStatistics() {
		DescriptiveStatistics burn = null;
		if (getEquipment() != null) {
			burn = getRoute().getFuelBurnStatistics(getEquipment());
		}
		if (burn == null || burn.getValues().length == 0) {
			burn = getRoute().getFuelBurnStatistics();
		}
		return burn;
	}
}