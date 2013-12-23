package visiblehand.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import visiblehand.entity.utility.EGridSubregion;

import com.avaje.ebean.Ebean;

@Entity
public @Data class ZipCode {
	@Id
	private Integer id;
	@ManyToOne
	private UnitedState state;
	
	@ManyToOne
	private EGridSubregion eGridSubregion;	// primary
	@ManyToOne
	private EGridSubregion eGridSubregion2;	// secondary
	@ManyToOne
	private EGridSubregion eGridSubregion3; // tertiary
	public static ZipCode find(int id) {
		return Ebean.find(ZipCode.class, id);
	}
}