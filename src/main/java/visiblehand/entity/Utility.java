package visiblehand.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.avaje.ebean.annotation.EnumValue;

@ToString(of = { "id", "name" })
@EqualsAndHashCode(of={"id"})
@Entity
public @Data
class Utility {
	public enum Type {
		@EnumValue("E")
		ELECTRIC,
		@EnumValue("G")
		GAS
	};
	@Id
	private int id;
	private String name;
	private Type type;
}