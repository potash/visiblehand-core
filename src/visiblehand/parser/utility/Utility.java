package visiblehand.parser.utility;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import visiblehand.parser.MessageParserTest;

import com.avaje.ebean.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonView;

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
	@Id @JsonView(MessageParserTest.TestView.class)
	private int id;
	private String name;
	private Type type;
}