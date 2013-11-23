package visiblehand.entity;

import javax.persistence.Entity;

import lombok.Data;

@Entity
public @Data class EquipmentAggregate {
	private String parentIATA;
	private String childIATA;
}
