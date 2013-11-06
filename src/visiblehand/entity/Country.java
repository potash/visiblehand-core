package visiblehand.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
public @Data class Country {
	@Id
	private String code;
	private String name;
}