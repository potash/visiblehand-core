package visiblehand.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
public @Data
class Aircraft {
	@Id
	private Long id;
	private String manufacturer;
	private String type;
	private String model;
	private Integer year;
	private String engineManufacturer;
	private String engineModel;
	private Integer numEngines;
	//private int seats1,seats2, seats3, seatsAbreast;
	private Double volume;
	@Column(name="mtow")
	private Integer MTOW;
	@Column(name="mlw")
	private Integer MLW;
	@Column(name="mzfw")
	private Integer MZFW;
	private Integer fuel;
	@Column(name="mrc_speed")
	private Integer mrcSpeed;
	
	private Double mrcAltitude, mrcFuel;
	private Double lrcSpeed, lrcAltitude, lrcFuel;
	private Integer maxPayloadRange, designRange, maxFuelRange;
	private Double fuelPaxNm;
	private Integer seatsNm;
}
