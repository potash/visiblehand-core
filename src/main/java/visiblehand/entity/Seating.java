package visiblehand.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

@ToString(of={"id", "airline", "name", "seats"} )
@Entity
public @Data
class Seating {
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Id
	private int id;
	@ManyToOne
	@JoinColumn(name = "equipment_id")
	private Equipment equipment;
	@ManyToOne
	@JoinColumn(name = "airline_id")
	private Airline airline;
	String name;
	// names of seat classes
	private String class1, class2, class3, class4;
	// number of seats in each class
	private Integer seats1, seats2, seats3, seats4;

	
	@Transient
	@Getter(lazy = true)
	private final Integer seats = seats();
	
	private Integer seats() {
		Integer seats = getSeats1();
		if (getSeats2() != null)
			seats += getSeats2();
		if (getSeats3() != null)
			seats += getSeats3();
		if (getSeats4() != null)
			seats += getSeats4();
		return seats;
	}
	
	public void setClass(int index, String className) {
		switch (index) {
		case 1:
			setClass1(className);
			break;
		case 2:
			setClass2(className);
			break;
		case 3:
			setClass3(className);
			break;
		case 4:
			setClass4(className);
			break;
		default:
			break;
		}
	}

	public void setSeats(int index, int seats) {
		switch (index) {
		case 1:
			setSeats1(seats);
			break;
		case 2:
			setSeats2(seats);
			break;
		case 3:
			setSeats3(seats);
			break;
		case 4:
			setSeats4(seats);
			break;
		default:
			break;
		}
	}
	
	public static DescriptiveStatistics getSeatStatistics(List<Seating> seatings) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (Seating seating : seatings) {
			stats.addValue(seating.getSeats());
		}
		return stats;
	}
}
