package visiblehand.entity.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.UniqueConstraint;

import com.avaje.ebean.Ebean;

import lombok.Data;
import visiblehand.entity.UnitedState;

@Entity
@UniqueConstraint(columnNames={"state_id","period"})
public @Data class ElectricityPrice {
	public static final DateFormat periodFormat = new SimpleDateFormat("yyyyMM");
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	private UnitedState state;
	private Integer period;	// YYYYMM
	private Double price;	// cents / kWh
	
	public static Double find(UnitedState state, Date date) {
		ElectricityPrice price = Ebean.find(ElectricityPrice.class).where()
				.eq("state", state)
				.eq("period", Integer.parseInt(periodFormat.format(date))).findUnique();
		if (price != null) {
			return price.getPrice();
		} else {
			return null;
		}
	}
}
