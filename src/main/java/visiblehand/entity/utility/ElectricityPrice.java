package visiblehand.entity.utility;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import visiblehand.entity.UnitedState;

@Entity
@UniqueConstraint(columnNames={"state_id","period","price"})
public @Data class ElectricityPrice {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	private UnitedState state;
	private Integer period;
	private Double price;
}
