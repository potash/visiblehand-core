package visiblehand.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import visiblehand.entity.utility.EGridSubregion;

@Entity
public @Data class ZipCode {
	@Id
	private Integer id;
	private String state;
	
	@ManyToOne
	private EGridSubregion eGridSubregion;
	@ManyToOne
	private EGridSubregion eGridSubregion2;
	@ManyToOne
	private EGridSubregion eGridSubregion3;
}