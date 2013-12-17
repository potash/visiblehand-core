package visiblehand.entity.air;

import javax.persistence.Entity;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@UniqueConstraint(columnNames={"parent_iata","child_iata"})
public @Data class EquipmentAggregate {
	private String parentIATA;
	private String childIATA;
}
