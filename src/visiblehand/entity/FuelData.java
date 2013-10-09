package visiblehand.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;

import com.avaje.ebean.Ebean;

// data source : http://www.eea.europa.eu/publications/emep-eea-guidebook-2013/part-b-sectoral-guidance-chapters/1-energy/1-a-combustion/1-a-3-a-aviation-annex
// 				 http://www.team.aero/files/aviation_data/
@Entity
public @Data
class FuelData {
	@Id
	private int id;
	private String ICAO;
	private Double LTO;
	private Double cruise1, cruise2, cruise3, cruise4, cruise5, cruise6,
			cruise7, cruise8, cruise9, cruise10, cruise11, cruise12, cruise13,
			cruise14, cruise15, cruise16;
	private Integer distance1, distance2, distance3, distance4, distance5,
			distance6, distance7, distance8, distance9, distance10, distance11,
			distance12, distance13, distance14, distance15, distance16;

	private Double getCruise(int i) {
		switch (i) {
		case 0:
			return cruise1;
		case 1:
			return cruise2;
		case 2:
			return cruise3;
		case 3:
			return cruise4;
		case 4:
			return cruise5;
		case 5:
			return cruise6;
		case 6:
			return cruise7;
		case 7:
			return cruise8;
		case 8:
			return cruise9;
		case 9:
			return cruise10;
		case 10:
			return cruise11;
		case 11:
			return cruise12;
		case 12:
			return cruise13;
		case 13:
			return cruise14;
		case 14:
			return cruise15;
		case 15:
			return cruise16;
		default:
			throw new IllegalArgumentException("Bad cruise distance index: "
					+ i);
		}
	}

	private Integer getDistance(int i) {
		switch (i) {
		case 0:
			return distance1;
		case 1:
			return distance2;
		case 2:
			return distance3;
		case 3:
			return distance4;
		case 4:
			return distance5;
		case 5:
			return distance6;
		case 6:
			return distance7;
		case 7:
			return distance8;
		case 8:
			return distance9;
		case 9:
			return distance10;
		case 10:
			return distance11;
		case 11:
			return distance12;
		case 12:
			return distance13;
		case 13:
			return distance14;
		case 14:
			return distance15;
		case 15:
			return distance16;
		default:
			throw new IllegalArgumentException("Bad cruise distance index: "
					+ i);
		}
	}

	// per EEA document, interpolate linearly between the data points
	public double getCruiseBurn(double distance) {
		for (int i = 14; i >= 0; i--) {
			if (getDistance(i+1) != null && distance >= getDistance(i) && getCruise(i + 1) != null) {
				return getCruise(i) + (distance - getDistance(i))
						* (getCruise(i + 1) - getCruise(i))/(getDistance(i+1) - getDistance(i));
			}
		}
		if (distance >= 0) {
			return getCruise(0) + (distance - getDistance(0)) * getCruise(1)
					/ getCruise(0);
		}
		// distance must be negative
		throw new IllegalArgumentException(
				"Flight distance cannot be negative: " + distance);
	}

	// calculate fuel burn in kg for a given distance in nm
	public double getFuelBurn(double distance) {
		return getLTO() + getCruiseBurn(distance);
	}

	@Transient
	@Getter(lazy = true)
	private final List<Equipment> equipment = Ebean.find(Equipment.class)
			.where().eq("ICAO", getICAO()).findList();

	public static void main(String[] args) {
		System.out.println(Ebean.find(FuelData.class).findList().get(0)
				.getCruiseBurn(5000));
	}
}