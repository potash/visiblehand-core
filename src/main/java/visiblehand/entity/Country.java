package visiblehand.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@UniqueConstraint(columnNames={"name"})
public @Data class Country {
	@Id
	private String code;
	private String name;
}