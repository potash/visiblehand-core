package visiblehand.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
public @Data class UnitedState {
	@Id
	private String id;
	private String name;
}
